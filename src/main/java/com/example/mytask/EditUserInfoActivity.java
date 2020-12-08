package com.example.mytask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditUserInfoActivity extends AppCompatActivity {
    private TextInputLayout layoutfirst, layoutlast;
    private EditText txtfirst, txtlast, txtemail, txthp;
    private TextView txtSelectPhoto;
    private Button btnSave;
    private CircleImageView circleImageView;
    private static final int GALLERY_CHANGE_PROFILE = 5;
    private Bitmap bitmap = null;
    private SharedPreferences userPref;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);
        init();
    }
    private void init(){
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        userPref = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        txtfirst = findViewById(R.id.txtFirst);
        txtlast = findViewById(R.id.txtLast);
        txtemail = findViewById(R.id.txtEmailInfo);
        txthp = findViewById(R.id.txtEdithp);

        txtSelectPhoto = findViewById(R.id.txtSelectPhoto);
        btnSave = findViewById(R.id.btnSaveEdit);
        circleImageView = findViewById(R.id.imgUserInfo);

        //Picasso.get().load(getIntent().getStringExtra("imgUrl")).into(circleImageView);
        txtfirst.setText(userPref.getString("name",""));
        txtlast.setText(userPref.getString("last",""));
        txtemail.setText(userPref.getString("email",""));

        txtSelectPhoto.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, GALLERY_CHANGE_PROFILE);
        });

        btnSave.setOnClickListener(v -> {
            if (validate()){
                updateProfile();
            }
        });
    }

    private void updateProfile() {
        dialog.setMessage("Updating");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.SAVE_USER_INFO, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString("name", txtfirst.getText().toString().trim());
                    editor.putString("email", txtemail.getText().toString().trim());
                    editor.putString("no hp", txthp.getText().toString().trim());
                    editor.apply();

                    Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        }, error -> {
            error.printStackTrace();
            dialog.dismiss();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = userPref.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer"+token);
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("name", txtfirst.getText().toString().trim());
                map.put("email", txtemail.getText().toString().trim());
                map.put("no hp", txthp.getText().toString().trim());
                map.put("photo",bitmapToString(bitmap));
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(EditUserInfoActivity.this);
        queue.add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GALLERY_CHANGE_PROFILE && resultCode==RESULT_OK){
            Uri uri = data.getData();
            circleImageView.setImageURI(uri);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private boolean validate() {
        if(txtfirst.getText().toString().isEmpty()){
            layoutfirst.setErrorEnabled(true);
            layoutfirst.setError("First Name is Required");
            return false;
        }
        if(txtfirst.getText().toString().isEmpty()){
            layoutlast.setErrorEnabled(true);
            layoutlast.setError("Last Name is Reqiured");
            return false;
        }
        if(txtemail.getText().toString().isEmpty()){
            txtemail.getError().toString();
            txtemail.setError("Email is Required");
        }
        if(txthp.getText().toString().isEmpty()){
            txthp.getError().toString();
            txthp.setError("no. Hp is Required");
        }
        return true;
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
}