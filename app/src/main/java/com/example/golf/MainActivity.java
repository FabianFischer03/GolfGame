package com.example.golf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button play = (Button) findViewById(R.id.play);
        TextView highscore = (TextView) findViewById(R.id.highscore);
        Bundle extras = getIntent().getExtras();

        SharedPreferences mPrefs = getSharedPreferences("highscore", 0);
        String highscoreString = mPrefs.getString("highscore", "0");

        highscore.setText(highscoreString);

        highscore.setText(highscoreString);
        if (extras != null) {
            String scoreS = extras.getString("score");
            score = Integer.parseInt(scoreS);
        }


        play.setOnClickListener(v -> {
            changeView();
        });
    }
    public void changeView() {
        Intent intent = new Intent(getApplicationContext(), TurnPhoneActivity.class);
        startActivity(intent);
    }
}