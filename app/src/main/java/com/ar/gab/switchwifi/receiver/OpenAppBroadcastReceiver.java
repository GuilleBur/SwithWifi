package com.ar.gab.switchwifi.receiver;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.ar.gab.switchwifi.R;
import com.ar.gab.switchwifi.WifiNotification;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Guille on 5/15/2017.
 */

public class OpenAppBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
        nMgr.cancel(1);

        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean(context.getString(R.string.closeApp), false).apply();

        WifiNotification wifiNotification2 =  new WifiNotification();
        wifiNotification2.notify(context, R.drawable.ic_wifi_app_on, context.getString(R.string.wifi_on)
                ,((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getSSID(),
                3, context.getString(R.string.action_close), R.drawable.ic_stop_black_24px, new Intent("com.ar.gab.switchwifi.CloseApp"));

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);



        Intent broadcastIntent = new Intent("com.ar.gab.switchwifi.RestartSwitchWifi");
        context.sendBroadcast(broadcastIntent);
        //context.startActivity(new Intent(context, MainActivity.class));

    }




}
