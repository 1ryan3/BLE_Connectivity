package com.example.bleconnectivity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class DeviceConnectedActivity extends AppCompatActivity {

    private String selectedLED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_connect);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.led_selection_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(selectedListener);

        Button btnConnect = (Button) findViewById(R.id.editGroup);
        btnConnect.setOnClickListener(editGroupHandler);

        Button btnDisconnect = (Button) findViewById(R.id.disconnect);
        btnDisconnect.setOnClickListener(disconnectHandler);



    }

    private View.OnClickListener editGroupHandler = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intentMain = new Intent(DeviceConnectedActivity.this, editGroup.class);
            intentMain.putExtra("groupNumber", selectedLED);
            DeviceConnectedActivity.this.startActivity(intentMain);
        }
    };

    private View.OnClickListener disconnectHandler = new View.OnClickListener(){
        @Override
        public void onClick(View v) {

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



}