package com.example.golf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Timer;
import java.util.TimerTask;

public class GolfActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private SensorManager sensorManager;
    private Sensor sensor;
    private Timer timer;
    private Paint player;
    private Paint goal;
    int i = 0;

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
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            int size = 100;
            @Override
            public void run() {
                tryDrawing(holder, size);
                i++;
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
        canvas.drawColor(getColor(android.R.color.white));
        canvas.drawCircle(canvas.getWidth()/2 + i, canvas.getHeight()/2 + i, ticks % 200, this.player);
        canvas.drawCircle(canvas.getWidth()/2 + 100, canvas.getHeight()/2 + 100, ticks % 200, this.goal);
    }
}