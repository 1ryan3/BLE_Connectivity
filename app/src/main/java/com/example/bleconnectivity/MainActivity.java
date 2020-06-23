package com.example.bleconnectivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;

    private int REQUEST_ENABLE_BT = 1;/*
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGatt gatt;
    // private ScanCallback scanCallback;
    private ScanSettings scanSettings;
    private UUID serviceUuid;
    String[] names = new String[]{"LED"};
    List<ScanFilter> filters = null;*/

    BLEInterface mService;
    boolean mIsBound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, "The permission to get BLE location data is required", Toast.LENGTH_SHORT).show();
            }else{
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }else{
            Toast.makeText(this, "Location permissions already granted", Toast.LENGTH_SHORT).show();
        }

       /* if(names != null) {
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
        */

        BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


        Button btnConnect = (Button) findViewById(R.id.ConnectButton);
        btnConnect.setOnClickListener(handler);
    }

    private View.OnClickListener handler = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
           /* bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            if (bluetoothLeScanner != null) {
                bluetoothLeScanner.startScan(filters, scanSettings, new deviceFound());
            }*/
            if(mService.connectBLE()) {
                Intent deviceConnected = new Intent(MainActivity.this, DeviceConnectedActivity.class);
                MainActivity.this.startActivity(deviceConnected);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        startService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mIsBound){
            unbindService(serviceConnection);
            mIsBound = false;
        }
    }

    private void startService(){
        bluetoothEnable();
        Intent serviceIntent = new Intent(this, BLEInterface.class);
        startService(serviceIntent);
        bindService();
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

    private boolean bluetoothEnable() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        return true;
    }



/*    final class LEDControllerInstance extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            serviceUuid  =  UUID.fromString("7f453f59-6477-40d8-8054-f5526953986a");
            gatt.discoverServices();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                bluetoothGattService = gatt.getService(serviceUuid);
                if (bluetoothGattService != null) {
                    Intent intentMain = new Intent(MainActivity.this, DeviceConnectedActivity.class);
                    MainActivity.this.startActivity(intentMain);
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
    }*/

}
