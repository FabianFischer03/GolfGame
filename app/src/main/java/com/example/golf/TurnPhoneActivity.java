package com.example.golf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorAdditionalInfo;
import android.hardware.SensorEvent;
import android.hardware.SensorEventCallback;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class TurnPhoneActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor sensor;
    public int i = 0;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_phone);
        imageView = findViewById(R.id.imageview);
        Glide.with(this).load(R.drawable.turn_phone).into(imageView);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(new SensorEventCallback() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                super.onSensorChanged(event);

                Float z = event.values[2];
                if (i == 0 && z >=8.6){
                    i++;
                    changeView();
                }


            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                super.onAccuracyChanged(sensor, accuracy);
            }

            @Override
            public void onFlushCompleted(Sensor sensor) {
                super.onFlushCompleted(sensor);
            }

            @Override
            public void onSensorAdditionalInfo(SensorAdditionalInfo info) {
                super.onSensorAdditionalInfo(info);
            }
        }, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    public void changeView() {
        Intent intent = new Intent(getApplicationContext(), GolfActivity.class);
        startActivity(intent);
    }

}