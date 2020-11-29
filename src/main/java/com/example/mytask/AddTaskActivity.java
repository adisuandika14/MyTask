package com.example.mytask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.icu.text.UnicodeSetSpanner;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.UserDictionary;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mytask.Fragments.HomeFragment;
import com.example.mytask.Models.Task;
import com.example.mytask.Models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.*;

public class AddTaskActivity extends AppCompatActivity {
    private Button btnpost;
    private ImageView imgpost;
    private EditText txtdesc;
    private Bitmap bitmap = null;
    private static final int GALLERY_CHANGE_TASK =3;
    private ProgressDialog dialog;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        init();
    }

    private void init() {
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnpost = findViewById(R.id.btnpost);
        imgpost = findViewById(R.id.addTask);
        txtdesc = findViewById(R.id.txtdesc);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);


        imgpost.setImageURI(getIntent().getData());

        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),getIntent().getData());
        } catch (IOException e) {
            e.printStackTrace();
        }

        btnpost.setOnClickListener(v->{
            if(!txtdesc.getText().toString().isEmpty()){
                post();
            }
            else{
                Toast.makeText(this, "Post Description Is Required",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void post() {
        dialog.setMessage("Posting");
        dialog.show();

        StringRequest request = new StringRequest(Request.Method.POST,Constant.ADD_POST, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject postObject = object.getJSONObject("post");
                    JSONObject userObject = postObject.getJSONObject("user");

                    User user = new User();
                    user.setId(userObject.getInt("id"));
                    user.setUserName(userObject.getString("name") + " " + userObject.getString("email"));
                    //user.setPhoto(userObject.getString("file"));

                    Task task = new Task();
                    task.setId(postObject.getInt("id"));
                    task.setUser(user);
                    task.setComments(0);
                    task.setDate(postObject.getString("created_at"));
                    task.setDesc(postObject.getString("desc"));
                    task.setPhoto(postObject.getString("file"));


                    HomeFragment.arrayList.add(0,task);
                    HomeFragment.recyclerView.getAdapter().notifyItemInserted(0);
                    HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();

                    Toast.makeText(this,"Posted", Toast.LENGTH_SHORT).show();
                    finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();

            }
            dialog.dismiss();
        },error -> {
                error.printStackTrace();
                dialog.dismiss();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token", "");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearier" +token);
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("desc",txtdesc.getText().toString().trim());
                map.put("file", bitmapToString(bitmap));
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(AddTaskActivity.this);
        queue.add(request);
    }
    private String bitmapToString(Bitmap bitmap) {
        if(bitmap!=null){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            byte [] array = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(array,Base64.DEFAULT);
        }
        return "";
    }

    public void changePost(View view) {
        super.onBackPressed();
    }

    public void cancelpost(View view) {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i,GALLERY_CHANGE_TASK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_CHANGE_TASK && resultCode==RESULT_OK){
            Uri imgUri = data.getData();
            imgpost.setImageURI(imgUri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}