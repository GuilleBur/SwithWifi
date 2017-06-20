package com.ar.gab.switchwifi.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.ar.gab.switchwifi.R;
import com.ar.gab.switchwifi.services.SwitchWifiService;
import com.ar.gab.switchwifi.services.TestWifiInternetService;

import utils.ServiceUtil;

/**
 * Created by Guille on 5/15/2017.
 */

public class StatusAppBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
// Log.i(SwitchWifiRestarterBroadcastReceiver.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");
        SwitchWifiService mSwitchWifiService = new SwitchWifiService();
        TestWifiInternetService mTestWifiInternetService = new TestWifiInternetService();
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        boolean closeApp = sharedPref.getBoolean(context.getString(R.string.closeApp), false);

        //if user not turn off the application
        if(ServiceUtil.isMyServiceRunning(mSwitchWifiService.getClass(), context) && ServiceUtil.isMyServiceRunning(mTestWifiInternetService.getClass(), context) ){
            showToast("Alive", context);
        }else{
            if(!ServiceUtil.isMyServiceRunning(mSwitchWifiService.getClass(), context)) {
                showToast("Switch Wifi Service's Dead X^X", context);
            }
            if(!ServiceUtil.isMyServiceRunning(mTestWifiInternetService.getClass(), context)) {
                showToast("Test Wifi Internet's Dead X^X", context);
            }

        }

    }

    /*private boolean isMyServiceRunning(Class<?> serviceClass, ActivityManager manager) {
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }*/

    protected void showToast(final String msg , final Context context){
        //gets the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // run this code in the main thread
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }




}
