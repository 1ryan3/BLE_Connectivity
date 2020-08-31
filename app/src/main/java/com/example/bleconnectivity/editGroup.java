package com.example.bleconnectivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
    private String selectedLED;
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



        Spinner spinner = (Spinner) findViewById(R.id.spinner2);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.led_selection_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(selectedListener);

        Button btnTurnOff = findViewById(R.id.offButton);
        btnTurnOff.setOnClickListener(turnOffHandler);

        Button btnDisconnect = (Button) findViewById(R.id.disconnect2);
        btnDisconnect.setOnClickListener(disconnectHandler);

        bindService();
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

        int temp = Character.getNumericValue(selectedLED.charAt(selectedLED.length() - 1)) - 1;

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

    private View.OnClickListener disconnectHandler = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            mService.disconnectBLE();
            Intent intentMain = new Intent(editGroup.this, MainActivity.class);
            editGroup.this.startActivity(intentMain);
        }
    };

    private View.OnClickListener turnOffHandler = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            byte data[] = {0,0,0,0};
            int temp = Character.getNumericValue(selectedLED.charAt(selectedLED.length() - 1)) - 1;
            data[3] = (byte) (temp | 0xF0);
            mService.colorSet(data);
        }
    };

    private Spinner.OnItemSelectedListener selectedListener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            selectedLED = parent.getItemAtPosition(pos).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            selectedLED = "Group 1";
        }
    };

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

