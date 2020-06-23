package com.example.bleconnectivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;

    private int REQUEST_ENABLE_BT = 1;

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
}
