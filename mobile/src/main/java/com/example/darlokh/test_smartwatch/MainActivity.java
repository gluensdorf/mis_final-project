package com.example.darlokh.test_smartwatch;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);

        final Button buttonFoo = findViewById(R.id.button);
        final postQuery mPostQuery = new postQuery();
        buttonFoo.setOnClickListener(mPostQuery.handleClick);
    }
}
