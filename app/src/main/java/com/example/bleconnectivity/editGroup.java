package com.example.bleconnectivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;


import com.google.android.material.slider.Slider;

public class editGroup extends AppCompatActivity {

    private Slider red;
    private Slider blue;
    private Slider green;
    private float redValue = 255;
    private float greenValue = 255;
    private float blueValue = 255;
    private String groupNum;
    private EditText test;
    BLEInterface mService;
    boolean mIsBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        red = findViewById(R.id.redSlider);
        red.setValue(redValue);
        red.addOnSliderTouchListener(redHandler);

        green = findViewById(R.id.greenSlider);
        green.setValue(greenValue);
        green.addOnSliderTouchListener(blueHandler);

        blue = findViewById(R.id.blueSlider);
        blue.setValue(blueValue);
        blue.addOnSliderTouchListener(greenHandler);
        bindService();
        groupNum = getIntent().getStringExtra("groupNumber");
    }

    private Slider.OnSliderTouchListener redHandler = new Slider.OnSliderTouchListener() {
        @Override
        public void onStartTrackingTouch(@NonNull Slider slider) {

        }

        @Override
        public void onStopTrackingTouch(@NonNull Slider slider) {
            redValue = slider.getValue();
            setColor();
            /*String temp = Float.toString(redValue);
            test.setText(temp);*/
        }
    };

    private Slider.OnSliderTouchListener greenHandler = new Slider.OnSliderTouchListener() {
        @Override
        public void onStartTrackingTouch(@NonNull Slider slider) {

        }

        @Override
        public void onStopTrackingTouch(@NonNull Slider slider) {
            greenValue = slider.getValue();
            setColor();
            /*String temp = Float.toString(redValue);
            test.setText(temp);*/
        }
    };

    private Slider.OnSliderTouchListener blueHandler = new Slider.OnSliderTouchListener() {
        @Override
        public void onStartTrackingTouch(@NonNull Slider slider) {

        }

        @Override
        public void onStopTrackingTouch(@NonNull Slider slider) {
            blueValue = slider.getValue();
            setColor();
            /*String temp = Float.toString(redValue);
            test.setText(temp);*/
        }
    };

    private void setColor() {

        int temp = Character.getNumericValue(groupNum.charAt(groupNum.length() - 1)) - 1;

        byte[] data = new byte[4];
        data[0] = (byte) redValue;
        data[1] = (byte) blueValue;
        data[2] = (byte) greenValue;
        data[3] = (byte) temp;
        mService.colorSet(data);
    }

    private void bindService(){
        Intent serviceBindIntent =  new Intent(this, BLEInterface.class);
        bindService(serviceBindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder iBinder) {

            // We've bound to MyService, cast the IBinder and get MyBinder instance
            BLEInterface.LocalBinder binder = (BLEInterface.LocalBinder) iBinder;
            mService = binder.getService();
            mIsBound = true;

        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {

            mIsBound = false;
        }
    };
}

