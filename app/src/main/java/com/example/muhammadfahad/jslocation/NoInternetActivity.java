package com.example.muhammadfahad.jslocation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class NoInternetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(NoInternetActivity.this, "Feature is disabled...", Toast.LENGTH_SHORT).show();
    }
}
