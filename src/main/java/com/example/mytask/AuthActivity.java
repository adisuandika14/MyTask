package com.example.mytask;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.mytask.Fragments.SignInFragment;
import com.example.mytask.Fragments.SignUpFragment;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer,new SignInFragment()).commit();
    }
}