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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserInfoActivity extends AppCompatActivity {

    private TextInputLayout layoutfirst, layoutlast;
    private EditText txtfirst, txtlast, txtemail, txthp;
    private TextView txtSelectPhoto;
    private Button btnContinue;
    private CircleImageView circleImageView;
    private static final int GALLERY_ADD_PROFILE = 1;
    private Bitmap bitmap = null;
    private SharedPreferences userPref;
    private ProgressDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        init();
    }

    private void init() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        userPref = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        txtfirst = findViewById(R.id.txtFirst);
        txtlast = findViewById(R.id.txtLast);
        txtemail = findViewById(R.id.txtEmailInfo);
        txthp = findViewById(R.id.txthp);

        txtSelectPhoto = findViewById(R.id.txtSelectPhoto);
        btnContinue = findViewById(R.id.btnContinue);
        circleImageView = findViewById(R.id.imgUserInfo);

        //pick photo from gallery
        txtSelectPhoto.setOnClickListener(v->{
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i,GALLERY_ADD_PROFILE);
        });

        btnContinue.setOnClickListener(v->{
            if(validate()){
                saveUserInfo();
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_ADD_PROFILE && resultCode==RESULT_OK){
            Uri imgUri = data.getData();
            circleImageView.setImageURI(imgUri);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
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


    private void saveUserInfo(){
        dialog.setMessage("Saving");
        dialog.show();

        String first = txtfirst.getText().toString().trim();
        String last = txtemail.getText().toString().trim();
        String email = txtemail.getText().toString().trim();
        String hp = txthp.getText().toString().trim();

        StringRequest request = new StringRequest(Request.Method.POST, Constant.SAVE_USER_INFO, response -> {

            try {
                JSONObject object = new JSONObject(response);
                    if(object.getBoolean("success")){
                        SharedPreferences.Editor editor = userPref.edit();
                        editor.putString("photo",object.getString("photo"));
                        editor.apply();
                        startActivity(new Intent(UserInfoActivity.this,HomeActivity.class));
                        finish();
                    }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            dialog.dismiss();

        },error->{
            error.printStackTrace();
            dialog.dismiss();
        }){
            //add token to header

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = userPref.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer"+token);
                return map;
            }


            //add params
            protected Map<String, String> getParams() throws AuthFailureError{
                HashMap<String, String> map = new HashMap<>();
                map.put("firstname",first);
                map.put("lastname",last);
                map.put("email",email);
                map.put("hp",hp);
                map.put("photo",bitmapToString(bitmap));
                return map;
            }

        };
        RequestQueue queue = Volley.newRequestQueue(UserInfoActivity.this);
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
}