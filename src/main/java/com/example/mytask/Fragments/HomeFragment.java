package com.example.mytask.Fragments;

import android.content.Context;
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
import android.widget.SearchView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mytask.Adapter.TaskAdapter;
import com.example.mytask.Constant;
import com.example.mytask.HomeActivity;
import com.example.mytask.Models.Task;
import com.example.mytask.Models.User;
import com.example.mytask.NewtaskActivity;
import com.example.mytask.R;
import com.example.mytask.UserInfoActivity;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class HomeFragment extends Fragment {
    private View view, view1;
    public static RecyclerView recyclerView;
    public static ArrayList<Task> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private TaskAdapter taskAdapter;
    private MaterialToolbar toolbar;
    private SharedPreferences sharedPreferences;
    private MenuItem MenuItem;



    public HomeFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_home_fragment, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerHome);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swipehome);
        toolbar = view.findViewById(R.id.toolbarHome);

        ((HomeActivity) getContext()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        getTask();


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(){
                getTask();
            }

        });
    }


    private void getTask(){
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        StringRequest request = new StringRequest(Request.Method.GET, Constant.TASK,response->{

            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    JSONArray array = new JSONArray(object.getString("task"));
                    for(int i = 0; i<array.length(); i++){
                        JSONObject taskObject = array.getJSONObject(i);
                        JSONObject userObject = taskObject.getJSONObject("user");

                        User user = new User();
                        user.setId(userObject.getInt("id"));
                        user.setUserName(userObject.getString("name")+" "+userObject.getString("email"));
                        //user.setPhoto(userObject.getString("file"));

                        Task task = new Task();
                        task.setId(taskObject.getInt("id"));
                        task.setUser(user);
                        //task.setComments(taskObject.getInt("commentCount"));
                        task.setDate(taskObject.getString("created_at"));
                        task.setDesc(taskObject.getString("desc"));
                        task.setPhoto(taskObject.getString("file"));

                        arrayList.add(task);
                    }

                    taskAdapter = new TaskAdapter(getContext(),arrayList);
                    recyclerView.setAdapter(taskAdapter);
                    }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            refreshLayout.setRefreshing(false);

        },error-> {
            error.printStackTrace();
            refreshLayout.setRefreshing(false);
        }){


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                String token = sharedPreferences.getString("token","");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization","Bearer"+token);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_search, menu);
//        MenuItem item = menu.findItem(R.id.search);
//        SearchView searchView = (SearchView)item.getActionView();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                taskAdapter.getFilter().filter(newText);
//                return false;
//            }
//        });
//        super.onCreateOptionsMenu(menu,inflater);
//    }
}