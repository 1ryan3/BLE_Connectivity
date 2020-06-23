package com.example.bleconnectivity;

import android.app.Service;
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
import android.os.Binder;
import android.os.IBinder;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BLEInterface extends Service {

    private final IBinder binder = new LocalBinder();
    private BluetoothAdapter bluetoothAdapter;

    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGatt gatt;

    private ScanSettings scanSettings;
    private UUID serviceUuid;
    private BluetoothGatt bluetoothGatt;
    String[] names = new String[]{"LED"};
    List<ScanFilter> filters = null;

    public class LocalBinder extends Binder {
        BLEInterface getService() {
            return BLEInterface.this;
        }
    }

    public BLEInterface() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public boolean connectBLE() {
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (bluetoothGatt != null) {
            bluetoothGatt.connect();
            return true;
        }
        else if (bluetoothLeScanner != null) {
            bluetoothLeScanner.startScan(filters, scanSettings, new deviceFound());
            return true;
        }
        else {
            return false;
        }
    }

    public void disconnectBLE() {
        bluetoothGatt.disconnect();
    }

    public void colorSet(byte[] data) {

        BluetoothGattCharacteristic tempChar = bluetoothGattService.getCharacteristic(serviceUuid);

        if ( tempChar.setValue(data)) {
            bluetoothGatt.writeCharacteristic(bluetoothGattService.getCharacteristic(serviceUuid));
        }

    }

    final class LEDControllerInstance extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            serviceUuid  =  UUID.fromString("7f453f59-6477-40d8-8054-f5526953986a");
            gatt.discoverServices();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            bluetoothGatt = gatt;
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //bleStatus = true;
                bluetoothGattService = gatt.getService(serviceUuid);
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

/*
EditText textBoxTemp = findViewById(R.id.Red);
            String colorTemp = textBoxTemp.getText().toString();
            textBoxTemp.getText().clear();
            int len = colorTemp.length();

            byte[] data = new byte[1 + (len / 2)]; //encode color and group

            for (int i = 0; i < len; i += 2) { //encode color and group
                data[i / 2] = (byte) ((Character.digit(colorTemp.charAt(i), 16) << 4)
                        + Character.digit(colorTemp.charAt(i+1), 16));
            }

            BluetoothGattCharacteristic tempChar = bluetoothGattService.getCharacteristic(serviceUuid);

            if ( tempChar.setValue(data)) {
                bluetoothGatt.writeCharacteristic(bluetoothGattService.getCharacteristic(serviceUuid));
            }

 */
