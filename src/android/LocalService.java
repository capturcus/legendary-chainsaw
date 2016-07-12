package com.example.plugin;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;

import org.apache.cordova.*;

import java.util.List;

    public class LocalService extends Service {
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    BluetoothAdapter bluetoothAdapter;

    private static final int REQUEST_FINE_LOCATION = 0;
    
    public class LocalBinder extends Binder {
        LocalService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LocalService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void startDiscovery(Activity cordovaActivity, final CallbackContext callbackContext) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            throw new RuntimeException("bt adapter null");
        }
        if (!bluetoothAdapter.isEnabled()) {
            throw new RuntimeException("bt not enabled");
        }
        BluetoothLeScanner leScanner = bluetoothAdapter.getBluetoothLeScanner();
        ScanCallback scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                Log.e("mleko", "le found: " + result.toString());
                try {
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, result.getScanRecord().getServiceUuids().get(0).toString());
                    pluginResult.setKeepCallback(true);
                    callbackContext.sendPluginResult(pluginResult);
                } catch (Exception e) {
                    //just don't callback - no service uuid found, no reason to callback
                }
            }

            public void onBatchScanResults(List<ScanResult> results) {
                Log.e("mleko", "le found batch: " + results.toString());
            }

            public void onScanFailed(int errorCode) {
                Log.e("mleko", "le failed :( "+String.valueOf(errorCode));
            }
        };
        leScanner.startScan(scanCallback);
    }

    public void startAdvertising(String uuid, final CallbackContext callbackContext) {
        BluetoothLeAdvertiser advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build();

        final AdvertiseData data = new AdvertiseData.Builder()
                .addServiceUuid(ParcelUuid.fromString(uuid))
                .build();

        advertiser.startAdvertising(settings, data, new AdvertiseCallback() {
            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
                switch (errorCode) {
                    case ADVERTISE_FAILED_ALREADY_STARTED:
                        Log.e("mleko", "start failure already started");
                        break;
                    case ADVERTISE_FAILED_DATA_TOO_LARGE:
                        Log.e("mleko", "start failure data too large");
                        break;
                    case ADVERTISE_FAILED_FEATURE_UNSUPPORTED:
                        Log.e("mleko", "start failure feature unsupported");
                        break;
                    case ADVERTISE_FAILED_INTERNAL_ERROR:
                        Log.e("mleko", "start failure internal error");
                        break;
                    case ADVERTISE_FAILED_TOO_MANY_ADVERTISERS:
                        Log.e("mleko", "start failure too many advertisers");
                        break;
                }
                Log.e("mleko", "data: " + data.toString());
            }

            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                Log.e("mleko", "start success");
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "");
                pluginResult.setKeepCallback(false);
                callbackContext.sendPluginResult(pluginResult);
            }
        });
    }

    public void stopDiscovery() {
        bluetoothAdapter.cancelDiscovery();
    }
}
