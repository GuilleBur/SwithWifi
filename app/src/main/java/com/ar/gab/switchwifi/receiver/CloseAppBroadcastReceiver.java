package com.ar.gab.switchwifi.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;

import com.ar.gab.switchwifi.activity.MainActivity;
import com.ar.gab.switchwifi.R;
import com.ar.gab.switchwifi.WifiNotification;

/**
 * Created by Guille on 5/15/2017.
 */

public class CloseAppBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
        nMgr.cancel(1);

        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean(context.getString(R.string.closeApp), true).apply();

        WifiNotification wifiNotification1 =  new WifiNotification();
        wifiNotification1.notify(context, R.drawable.ic_off_wifi, context.getString(R.string.wifi_off),
                ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getSSID()
                , 3, context.getString(R.string.action_open), new Intent("com.ar.gab.switchwifi.OpenApp"));


        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags (Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra("close_activity",true);
        context.startActivity(i);

    }




}
