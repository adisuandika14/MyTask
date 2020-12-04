package com.example.mytask.Fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mytask.Adapter.AccountTaskAdapter;
import com.example.mytask.AuthActivity;
import com.example.mytask.Constant;
import com.example.mytask.HomeActivity;
import com.example.mytask.Models.Task;
import com.example.mytask.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {
    private View view;
    private MaterialToolbar toolbar;
    private CircleImageView imgProfile;
    private TextView txtName, txtTaskCount;
    private Button btnEditAccount;
    private RecyclerView recyclerView;
    private ArrayList<Task> arrayList;
    private AccountTaskAdapter adapter;

    private SharedPreferences preferences;

    public AccountFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_account, container, false);
        init();
        return view;
    }

    private void init() {
        preferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        toolbar = view.findViewById(R.id.toolbarAccount);
        ((HomeActivity)getContext()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        imgProfile = view.findViewById(R.id.imgAccountProfile);
        txtName = view.findViewById(R.id.txtAccountName);
        txtTaskCount = view.findViewById(R.id.txtAccounTaskCount);
        recyclerView = view.findViewById(R.id.recyclerAccount);
        btnEditAccount = view.findViewById(R.id.btnEditAccount);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        getData( );
    }

    private void getData() {
        arrayList = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.GET, Constant.MY_TASK, res->{

            try {
                JSONObject object = new JSONObject(res);
                if (object.getBoolean("success")){
                    JSONArray tasks = object.getJSONArray("tasks");
                    for (int i = 0; i < tasks.length(); i++){
                        JSONObject p = tasks.getJSONObject(i);

                        Task task = new Task();
                        task.setPhoto(Constant.URL+"storage/task/"+p.getString("file"));
                        arrayList.add(task);
                    }
                    JSONObject user = object.getJSONObject("user");
                    txtName.setText(user.getString("name"));
                    txtTaskCount.setText(arrayList.size()+"");
                    Picasso.get().load(Constant.URL+"storage/profiles/"+user.getString("photo")).into(imgProfile);
                    adapter = new AccountTaskAdapter(getContext(),arrayList);
                    recyclerView.setAdapter(adapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        },error->{
            error.printStackTrace();
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_account, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.item_logout:{
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Do You Want To Loggo Out ?");
                builder.setPositiveButton("Loggout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout(){
        StringRequest request = new StringRequest(Request.Method.GET, Constant.LOGOUT, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();
                    startActivity(new Intent(((HomeActivity)getContext()), AuthActivity.class));
                    ((HomeActivity)getContext()).finish();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        },error->{
            error.printStackTrace();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
}