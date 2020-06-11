package com.example.bleconnectivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Space;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeviceConnectActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private int REQUEST_ENABLE_BT = 1;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGatt bluetoothGatt;
   // private ScanCallback scanCallback;
    private ScanSettings scanSettings;
    private UUID serviceUuid;
    private String selectedLED;
    //private UUID characteristicUuid = UUID.fromString()

    String[] names = new String[]{"LED"};
    List<ScanFilter> filters = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_connect);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.led_selection_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        selectedLED = "2";

        if(names != null) {
            filters = new ArrayList<>(); // CHANGE TO SEARCH BY BLE UUID INSTEAD OF NAME
            for (String name : names) {
                ScanFilter filter = new ScanFilter.Builder().setDeviceName(name).build();
                filters.add(filter);
            }
        }
        scanSettings = new ScanSettings.Builder() // CHANGE TO SEARCH BY BLE UUID INSTEAD OF NAME
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH)
                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                .setReportDelay(0L)
                .build();

        BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (bluetoothLeScanner != null) {
            bluetoothLeScanner.startScan(filters, scanSettings, new deviceFound());
        }


    }

    private View.OnClickListener handler = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            EditText textBoxTemp = findViewById(R.id.Red);
            String colorTemp = textBoxTemp.getText().toString();
            textBoxTemp.getText().clear();
            int len = colorTemp.length();
            byte[] data = new byte[1 + (len / 2)];

            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(colorTemp.charAt(i), 16) << 4)
                        + Character.digit(colorTemp.charAt(i+1), 16));
            }
            data[len / 2] = (byte) Character.digit(selectedLED.charAt(0), 16);
            BluetoothGattCharacteristic tempChar = bluetoothGattService.getCharacteristic(serviceUuid);

            if ( tempChar.setValue(data)) {
                bluetoothGatt.writeCharacteristic(bluetoothGattService.getCharacteristic(serviceUuid));
            }

        }
    };

    private Spinner.OnItemSelectedListener selectedListener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            selectedLED = parent.getItemAtPosition(pos).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //selectedLED = "";
        }
    };


    final class LEDControllerInstance extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            bluetoothGatt = gatt;
            serviceUuid  =  UUID.fromString("7f453f59-6477-40d8-8054-f5526953986a");
            gatt.discoverServices();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                bluetoothGattService = gatt.getService(serviceUuid);
                if (bluetoothGattService != null) {
                    Button btnConnect = (Button) findViewById(R.id.changeRedValue);
                    btnConnect.setOnClickListener(handler);
                    Spinner spinner = (Spinner) findViewById(R.id.spinner);
                    spinner.setOnItemSelectedListener(selectedListener);
                }
            }
        }
    }






    final class deviceFound extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            ScanRecord record = result.getScanRecord();
            if (record != null) {
                //if(record.getDeviceName() != null) {
                    bluetoothLeScanner.stopScan(this);
                    BluetoothDevice controller = result.getDevice();
                    controller.connectGatt(getApplicationContext(), true, new LEDControllerInstance());
                //}
            }
        }
    }
}