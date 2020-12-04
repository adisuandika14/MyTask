package com.example.mytask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.mytask.Fragments.AccountFragment;
import com.example.mytask.Fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.LineNumberReader;

public class HomeActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FloatingActionButton fab;
    private static final int GALLERY_ADD_TASK = 2;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameHomeContainer,new HomeFragment(),HomeFragment.class.getSimpleName()).commit();
        init();
    }

    private void init() {
        navigationView = findViewById(R.id.bottom_nav);
        fab = findViewById(R.id.fab);


        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.item_home:{
                        Fragment account = fragmentManager.findFragmentByTag(AccountFragment.class.getSimpleName());
                        if(account!=null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(AccountFragment.class.getSimpleName())).commit();
                            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(HomeFragment.class.getSimpleName())).commit();
                        }
                        break;
                    }

                    case R.id.item_account:{
                        Fragment account = fragmentManager.findFragmentByTag(AccountFragment.class.getSimpleName());
                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(HomeFragment.class.getSimpleName())).commit();
                        if(account!=null){
                            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(AccountFragment.class.getSimpleName())).commit();

                        }else{
                            fragmentManager.beginTransaction().add(R.id.frameHomeContainer,new AccountFragment(),AccountFragment.class.getSimpleName()).commit();
                        }
                        break;
                    }
                }
                return true;
            }
        });

        fab.setOnClickListener(v->{
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i,GALLERY_ADD_TASK);
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_ADD_TASK && resultCode==RESULT_OK){
            Uri imgUri = data.getData();
            Intent i = new Intent(HomeActivity.this,AddTaskActivity.class);
            i.setData(imgUri);
            startActivity(i);
        }
    }
}