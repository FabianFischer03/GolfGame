package com.example.golf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button play = (Button) findViewById(R.id.play);
        TextView highscore = (TextView) findViewById(R.id.highscore);
        play.setOnClickListener(v -> {
            changeView();
        });
    }
    public void changeView() {
        Intent intent = new Intent(getApplicationContext(), TurnPhoneActivity.class);
        startActivity(intent);
    }
}