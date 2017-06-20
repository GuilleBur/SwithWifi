package com.ar.gab.switchwifi.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.ar.gab.switchwifi.R;
import com.ar.gab.switchwifi.services.SwitchWifiService;
import com.ar.gab.switchwifi.services.TestWifiInternetService;

import utils.ServiceUtil;

/**
 * Created by Guille on 5/15/2017.
 */

public class SwitchWifiRestarterBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
       // Log.i(SwitchWifiRestarterBroadcastReceiver.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");
        SwitchWifiService mSwitchWifiService = new SwitchWifiService();
        TestWifiInternetService mTestWifiInternetService = new TestWifiInternetService();
        ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        boolean closeApp = sharedPref.getBoolean(context.getString(R.string.closeApp), false);

        //if user not turn off the application
        if(!closeApp && !ServiceUtil.isMyServiceRunning(mSwitchWifiService.getClass(), context)){
            context.startService(new Intent(context, SwitchWifiService.class));

        }
        if(!closeApp && !ServiceUtil.isMyServiceRunning(mTestWifiInternetService.getClass(), context)){
            context.startService(new Intent(context, TestWifiInternetService.class));
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
}
