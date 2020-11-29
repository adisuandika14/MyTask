package com.example.mytask;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mytask.Fragments.HomeFragment;
import com.example.mytask.Models.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditPostActivity extends AppCompatActivity {

    private int position = 0, id = 0;
    private EditText txtDesc;
    private Button btnSave;
    private ProgressDialog  dialog;
    private SharedPreferences sharedpreferences;

    
    
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        init();
    }

    private void init() {
        sharedpreferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        txtDesc = findViewById(R.id.txteditdesc);
        btnSave = findViewById(R.id.btnedit);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        position = getIntent().getIntExtra("position", 0);
        id = getIntent().getIntExtra("id", 0);
        txtDesc.setText(getIntent().getStringExtra("desc"));

        btnSave.setOnClickListener(v->{
            if(!txtDesc.getText().toString().isEmpty()){
                saveTask();
            }
        });
    }

    private void saveTask() {
        dialog.setMessage("Saving");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,Constant.UPDATE_POST,response -> {

            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    Task task = HomeFragment.arrayList.get(position);
                    task.setDesc(txtDesc.getText().toString());
                    HomeFragment.arrayList.set(position,task);
                    HomeFragment.recyclerView.getAdapter().notifyItemChanged(position);
                    HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        },error -> {

        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedpreferences.getString("token","");
                HashMap<String, String>map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String>map = new HashMap<>();
                map.put("id",id+" ");
                map.put("desc",txtDesc.getText().toString());
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(EditPostActivity.this);
        queue.add(request);

    }


    public void cancelEdit(View view) {

        super.onBackPressed();
    }
}