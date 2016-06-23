package com.example.plugin;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class CordovaBt extends CordovaPlugin {

	private boolean mBound = false;
	LocalService mService;
	CallbackContext mCallbackContext;

	/** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.e("mleko","on connected");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalService.LocalBinder binder = (LocalService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.startBt(cordova.getActivity(), mCallbackContext);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.e("mleko","on disconnected");
            mBound = false;
        }
    };

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        if (action.equals("greet")) {
            String name = data.getString(0);
            String message = "Hello, " + name;
            for (int i = 0; i < 10; i++) {
                PluginResult result = new PluginResult(PluginResult.Status.OK, "no siema "+String.valueOf(i));
                result.setKeepCallback(true);
                callbackContext.sendPluginResult(result);
            }
            return true;
        } else if (action.equals("servicetest")) {
        	Intent intent = new Intent(cordova.getActivity(), LocalService.class);
        	cordova.getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        	Log.e("mleko","called servicetest");
        	mCallbackContext = callbackContext;
        	PluginResult result = new PluginResult(PluginResult.Status.OK, "bindservice test");
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
        	return true;
        } else {
            return false;
        }
    }
}
