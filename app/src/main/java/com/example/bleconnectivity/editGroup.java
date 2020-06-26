package com.example.bleconnectivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;


import com.google.android.material.slider.Slider;

public class editGroup extends AppCompatActivity {

    private Slider red;
    private Slider blue;
    private Slider green;
    private TextView rDisp;
    private TextView gDisp;
    private TextView bDisp;
    private View colorSquare;
    private float redValue = 255;
    private float greenValue = 255;
    private float blueValue = 255;
    private String groupNum;
    BLEInterface mService;
    boolean mIsBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        colorSquare = findViewById(R.id.colorSquare);

        red = findViewById(R.id.redSlider);
        red.setValue(redValue);
        red.addOnChangeListener(redHandler);

        green = findViewById(R.id.greenSlider);
        green.setValue(greenValue);
        green.addOnChangeListener(greenHandler);

        rDisp = findViewById(R.id.redDispVal);
        bDisp = findViewById(R.id.blueDispVal);
        gDisp = findViewById(R.id.greenDispVal);

        blue = findViewById(R.id.blueSlider);
        blue.setValue(blueValue);
        blue.addOnChangeListener(blueHandler);
        bindService();
        groupNum = getIntent().getStringExtra("groupNumber");
    }

    private Slider.OnChangeListener redHandler = new Slider.OnChangeListener() {
        @Override
        public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
            redValue = slider.getValue();
            rDisp.setText(Integer.toHexString((int)redValue).toUpperCase());
            setColor();
            updatePreview();
        }
    };

    private Slider.OnChangeListener greenHandler = new Slider.OnChangeListener() {
        @Override
        public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
            greenValue = slider.getValue();
            gDisp.setText(Integer.toHexString((int)greenValue).toUpperCase());
            setColor();
            updatePreview();
        }
    };

    private Slider.OnChangeListener blueHandler = new Slider.OnChangeListener() {
        @Override
        public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
            blueValue = slider.getValue();
            bDisp.setText(Integer.toHexString((int)blueValue).toUpperCase());
            setColor();
            updatePreview();
        }
    };

    private void setColor() {

        int temp = Character.getNumericValue(groupNum.charAt(groupNum.length() - 1)) - 1;

        byte[] data = new byte[4];
        data[0] = (byte) redValue;
        data[1] = (byte) greenValue;
        data[2] = (byte) blueValue;
        data[3] = (byte) temp;
        mService.colorSet(data);
    }

    private void updatePreview() {
        int color = (0xff) << 24 | ((int)redValue & 0xff) << 16 | ((int)greenValue & 0xff) << 8 | ((int)blueValue & 0xff);
        colorSquare.setBackgroundColor(color);
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

