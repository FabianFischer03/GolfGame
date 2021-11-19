package com.example.golf;

import static java.util.Collections.max;
import static java.util.Collections.min;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorAdditionalInfo;
import android.hardware.SensorEvent;
import android.hardware.SensorEventCallback;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GolfActivity extends AppCompatActivity implements SurfaceHolder.Callback, SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magneticField;
    private Timer timer;
    private Paint player;
    private Paint time;
    private Paint goal;
    Double hMovement = 0.0;
    Double vMovement = 0.0;
    float[] mGravity;
    float[] mGeomagnetic;
    float testv = 0;
    float testh = 0;
    boolean noGoal = true;
    float hGoal = 0;
    float vGoal = 0;
    int score = 0;
    int frameCounter = 0;
    int actualTime = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_golf);

        //Time
        this.time = new Paint();
        time.setTextSize(60f);
        time.setColor(getColor(R.color.black));

        //Player
        this.player = new Paint();
        player.setColor(getColor(R.color.red));

        //Goal
        this.goal = new Paint();
        goal.setColor(getColor(R.color.green));
        SurfaceView surfaceView = new SurfaceView(this);

        setContentView(surfaceView);
        surfaceView.getHolder().addCallback(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            int size = 100;

            @Override
            public void run() {
                frameCounter++;
                if(frameCounter % 62.5 < 1) {
                    actualTime--;
                }
                float h = Float.parseFloat(String.valueOf(hMovement));
                float v = Float.parseFloat(String.valueOf(vMovement));
                tryDrawing(holder, size, h, v);
            }
        }, 0, 16); // 1000ms / 16ms = 62.5 frames per second
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        this.timer.cancel();
    }

    private void tryDrawing(SurfaceHolder holder, int ticks, float h, float v) {
        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            draw(canvas, ticks, h, v);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void draw(final Canvas canvas, int ticks, float h, float v) {
        testv = testv + v;
        testh = testh + h;

        canvas.drawColor(getColor(android.R.color.white));
        canvas.drawCircle(canvas.getWidth() / 2 + testh, canvas.getHeight() / 2 + testv, ticks % 200, this.player);
        canvas.drawText(String.valueOf(actualTime), canvas.getWidth() - 100, canvas.getHeight() - 100, this.time);
        canvas.drawText(String.valueOf(score), canvas.getWidth() + 100, canvas.getHeight() - 100, this.time);

        //check for collision
        double dx = (Double.parseDouble(String.valueOf(canvas.getWidth() / 2 + vGoal)) + (ticks % 200)) - (Double.parseDouble(String.valueOf(canvas.getWidth() / 2 + testh)) + (ticks % 200));
        double dy = (Double.parseDouble(String.valueOf(canvas.getHeight() / 2 + hGoal)) + (ticks % 200)) - (Double.parseDouble(String.valueOf(canvas.getHeight() / 2 + testv)) + (ticks % 200));
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < (ticks % 200) + (ticks % 200)) {
            noGoal = true;
            actualTime = 10;
            score++;
            System.out.println(score);
        }


        if (isGameOver()){
            SharedPreferences mPrefs = getSharedPreferences("highscore", 0);
            String highscoreString = mPrefs.getString("highscore", "0");
            if (Integer.parseInt(highscoreString) < score){
                SharedPreferences.Editor mEditor = mPrefs.edit();
                mEditor.putString("highscore", Integer.toString(score)).apply();
            }
            changeView(Integer.toString(score));
        }

        if (noGoal){
            hGoal = Float.parseFloat(Integer.toString(hOfGoal()));
            vGoal = Float.parseFloat(Integer.toString(vOfGoal()));
            noGoal = false;
        }
        if (!noGoal){
            canvas.drawCircle(canvas.getWidth() / 2 + vGoal, canvas.getHeight() / 2 + hGoal, ticks % 200, this.goal);
        }

    }
    public boolean isGameOver(){
        return actualTime == 0;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Float x = event.values[0];
        Float y = event.values[1];


        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;


        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;

        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                float azimuth = orientation[0];
                float pitch = orientation[1];
                float roll = orientation[2];

                calculateMovement(Math.toDegrees(pitch), Math.toDegrees(roll));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void calculateMovement(double pitch, double roll) {
        if (pitch == 0 && roll == 0) {
            hMovement = 0.0;
            vMovement = 0.0;
        }
        if (pitch >= -20 && pitch < 0) {
            hMovement = 0.325;
        }
        if (pitch >= -40 && pitch < -20) {
            hMovement = 0.65;
        }
        if (pitch >= -60 && pitch < -40) {
            hMovement = 0.976;
        }
        if (pitch >= -80 && pitch < -60) {
            hMovement = 1.3;
        }
        if (pitch >= 20 && pitch > 0) {
            hMovement = -0.325;
        }
        if (pitch >= 40 && pitch > 20) {
            hMovement = -0.65;
        }
        if (pitch >= 60 && pitch > 40) {
            hMovement = -0.976;
        }
        if (pitch >= 80 && pitch > 60) {
            hMovement = -1.3;
        }
        if (roll >= -20 && roll < 0) {
            vMovement = 0.325;
        }
        if (roll >= -40 && roll < -20) {
            vMovement = 0.65;
        }
        if (roll >= -60 && roll < -40) {
            vMovement = 0.976;
        }
        if (roll >= -80 && roll < -60) {
            vMovement = 1.3;
        }
        if (roll >= 20 && roll > 0) {
            vMovement = -0.325;
        }
        if (roll >= 40 && roll > 20) {
            vMovement = -0.65;
        }
        if (roll >= 60 && roll > 40) {
            vMovement = -0.976;
        }
        if (roll >= 80 && roll > 60) {
            vMovement = -1.3;
        }
    }

    public int vOfGoal() {
        Integer[] integers = {100, 150, 200, 250, 300, 350, 400, 450,  500, 550,  600, 650, 700, 750, -100, -150, -200, -250, -300, -350, -400, -450,  -500, -550,  -600, -650, -700, -750};
        Random rand = new Random();
        int upperbound = integers.length;
        int v = rand.nextInt(upperbound);
        return integers[v];

    }
    public int hOfGoal() {
        Integer[] integers = {25, 50, 75, 100, 125, 150, 175, 200, 225, 250, 275, 300, 325, -25, -50, -75, -100, -125, -150, -175, -200, -225, -250, -275, -300, -325};
        Random rand = new Random();
        int upperbound = integers.length;
        int h = rand.nextInt(upperbound);
        return integers[h];
    }
    public void changeView(String score) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("score",score);
        startActivity(intent);
    }
}