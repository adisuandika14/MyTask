package com.example.mytask.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mytask.CommentActivity;
import com.example.mytask.Constant;
import com.example.mytask.EditPostActivity;
import com.example.mytask.Fragments.HomeFragment;
import com.example.mytask.HomeActivity;
import com.example.mytask.Models.Task;
import com.example.mytask.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class TaskAdapter extends RecyclerView.Adapter <TaskAdapter.TaskHolder>{

    private Context context;
    private ArrayList<Task> list;
    private ArrayList<Task> listAll;
    private SharedPreferences preferences;



    public TaskAdapter(Context context, ArrayList<Task> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        preferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_task,parent,false);
        return new TaskHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
        Task task = list.get(position);
        Picasso.get().load(Constant.URL+"storage/profiles/"+task.getUser().getPhoto()).into(holder.imgProfile);
        Picasso.get().load(Constant.URL+"storage/task/"+task.getPhoto()).into(holder.imgTask);
        holder.txtName.setText(task.getUser().getUserName());
        holder.txtComment.setText("View All  "+task.getComments()+ " Comment");
        holder.txtDate.setText(task.getDate());
        holder.txtDesc.setText(task.getDesc());

        if (task.getUser().getId() == preferences.getInt("id",0)){
            holder.btnTaskOption.setVisibility(View.VISIBLE);
        }else{
            holder.btnTaskOption.setVisibility(View.GONE);
        }

        holder.btnTaskOption.setOnClickListener(V->{
            PopupMenu popupMenu = new PopupMenu(context, holder.btnTaskOption);
            popupMenu.inflate(R.menu.menu_task_option);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.item_edit:{
                            Intent i = new Intent(((HomeActivity)context), EditPostActivity.class);
                            i.putExtra("id", task.getId());
                            i.putExtra("position", position);
                            i.putExtra("desc",task.getDesc());
                            context.startActivity(i);
                            return true;
                        }
                        case R.id.item_delete:{
                            deleteTask(task.getId(), position);
                            return true;
                        }
                    }
                    return false;
                }

                private void deleteTask(int task_id, int position) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Confirm");
                    builder.setMessage("Delete Post ?");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StringRequest request = new StringRequest(Request.Method.POST, Constant.DELETE_POST, response -> {
                                try {
                                    JSONObject object = new JSONObject(response);
                                    if(object.getBoolean("success")) {
                                        list.remove(position);
                                        notifyItemRemoved(position);
                                        notifyDataSetChanged();
                                        listAll.clear();
                                        listAll.addAll(list);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }, error -> {
                            }){
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    String token = preferences.getString("token", "");
                                    HashMap<String,String> map = new HashMap<>();
                                    map.put("Authorization", "Bearer"+token);
                                    return map;
                                }

                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    HashMap<String,String> map = new HashMap<>();
                                    map.put("id", task_id+"");
                                    return map;
                                }
                            };
                            RequestQueue queue = Volley.newRequestQueue(context);
                            queue.add(request);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }
            });
            popupMenu.show();
        });

        holder.txtComment.setOnClickListener(v->{
            Intent i = new Intent(((HomeActivity)context), CommentActivity.class);
            i.putExtra("task_id",task.getId());
            i.putExtra("taskPosition",position);
            context.startActivity(i);
        });

        holder.btnComment.setOnClickListener(v->{
            Intent i = new Intent(((HomeActivity)context),CommentActivity.class);
            i.putExtra("task_id",task.getId());
            i.putExtra("taskPosition",position);
            context.startActivity(i);
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<Task> filteredlist = new ArrayList<>();
            if(constraint.toString().isEmpty()){
                filteredlist.addAll(listAll);
            }else{
                for (Task task : listAll){
                    if(task.getDesc().toLowerCase().contains(constraint.toString().toLowerCase())
                        ||task.getUser().getUserName().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredlist.add(task);
                    }
                }

            }
            FilterResults results = new FilterResults();
            results.values = filteredlist;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((Collection<? extends Task>) results.values);
                    notifyDataSetChanged();
        }
    };

    public Filter getFilter() {
        return filter;
    }

    static class TaskHolder extends RecyclerView.ViewHolder{

        private TextView txtName, txtDate,txtDesc, txtComment;
        private CircleImageView imgProfile;
        private ImageView imgTask;
        private ImageButton btnTaskOption, btnComment;


        public TaskHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtTaskName);
            txtDate = itemView.findViewById(R.id.txtTaskDate);
            txtDesc = itemView.findViewById(R.id.txtTaskDesc);
            txtComment = itemView.findViewById(R.id.txtTaskComment);
            imgProfile = itemView.findViewById(R.id.imgTaskProfile);
            imgTask = itemView.findViewById(R.id.imgTaskPhoto);
            btnTaskOption = itemView.findViewById(R.id.btnTaskOption);
            btnComment = itemView.findViewById(R.id.btnTaskComment);
            btnTaskOption.setVisibility(View.GONE);
        }
    }

}
