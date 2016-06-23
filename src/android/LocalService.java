package com.example.plugin;

import org.apache.cordova.*;
import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class LocalService extends Service {
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    BluetoothAdapter bluetoothAdapter;
    CallbackContext callbackContext;

    private static final int REQUEST_FINE_LOCATION = 0;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.e("mleko", "no znalazlem "+device.getName()+" "+device.getAddress());
                PluginResult result = new PluginResult(PluginResult.Status.OK, "{\"name\":\""+device.getName()+"\",\"address\":\""+device.getAddress()+"\"}");
                result.setKeepCallback(true);
                callbackContext.sendPluginResult(result);
            }
        }
    };

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
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

    public void startBt(Activity cordovaActivity, CallbackContext cc) {
        Log.e("mleko", "starting bt");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            throw new RuntimeException("bt adapter null");
        }
        if (!bluetoothAdapter.isEnabled()) {
            throw new RuntimeException("bt not enabled");
        }
        callbackContext = cc;
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        bluetoothAdapter.startDiscovery();
    }

    public void stopBt() {
        unregisterReceiver(receiver);
        bluetoothAdapter.cancelDiscovery();
    }
}
