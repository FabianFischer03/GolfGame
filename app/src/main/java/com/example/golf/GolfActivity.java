package com.example.golf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

import java.util.Timer;
import java.util.TimerTask;

public class GolfActivity extends AppCompatActivity implements SurfaceHolder.Callback, SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magneticField;
    private Timer timer;
    private Paint player;
    private Paint goal;
    Double hMovement = 0.0;
    Double vMovement = 0.0;
    float[] mGravity;
    float[] mGeomagnetic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_golf);

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
                tryDrawing(holder, size);
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

    private void tryDrawing(SurfaceHolder holder, int ticks) {
        Canvas canvas = holder.lockCanvas();
        if(canvas != null) {
            draw(canvas, ticks);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void draw(final Canvas canvas, int ticks) {
        float h = Float.parseFloat(String.valueOf(hMovement));
        float v = Float.parseFloat(String.valueOf(vMovement));
        canvas.drawColor(getColor(android.R.color.white));
        canvas.drawCircle(canvas.getWidth()/2 + h, canvas.getHeight()/2 + v, ticks % 200, this.player);
        canvas.drawCircle(canvas.getWidth()/2 + 100, canvas.getHeight()/2 + 100, ticks % 200, this.goal);
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

    public void calculateMovement(double pitch, double roll){
        if (pitch >= -20 && pitch < 0){
            hMovement = 0.325;
        }
        if (pitch >= -40 && pitch < -20){
            hMovement = 0.65;
        }
        if (pitch >= -60 && pitch < -40){
            hMovement = 0.976;
        }
        if (pitch >= -80 && pitch < -60){
            hMovement = 1.3;
        }
        if (pitch >= 20 && pitch > 0){
            hMovement = -0.325;
        }
        if (pitch >= 40 && pitch > 20){
            hMovement = -0.65;
        }
        if (pitch >= 60 && pitch > 40){
            hMovement = -0.976;
        }
        if (pitch >= 80 && pitch > 60){
            hMovement = -1.3;
        }
        if (roll >= -20 && roll < 0){
            vMovement = 0.325;
        }
        if (roll >= -40 && roll < -20){
            vMovement = 0.65;
        }
        if (roll >= -60 && roll < -40){
            vMovement = 0.976;
        }
        if (roll >= -80 && roll < -60){
            vMovement = 1.3;
        }
        if (roll >= 20 && roll > 0){
            vMovement = -0.325;
        }
        if (roll >= 40 && roll > 20){
            vMovement = -0.65;
        }
        if (roll >= 60 && roll > 40){
            vMovement = -0.976;
        }
        if (roll >= 80 && roll > 60){
            vMovement = -1.3;
        }
    }
}