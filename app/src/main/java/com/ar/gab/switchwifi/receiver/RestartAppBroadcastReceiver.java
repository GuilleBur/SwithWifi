package com.ar.gab.switchwifi.receiver;

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

public class RestartAppBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String ns = Context.NOTIFICATION_SERVICE;

        //CLOSE
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean(context.getString(R.string.closeApp), true).apply();

        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags (Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra("close_activity",true);
        context.startActivity(i);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ///OPEN
        sharedPref.edit().putBoolean(context.getString(R.string.closeApp), false).apply();

        WifiNotification wifiNotification2 =  new WifiNotification();
        wifiNotification2.notify(context, R.drawable.ic_wifi_app_on, context.getString(R.string.wifi_on)
                ,((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getSSID(),
                3, context.getString(R.string.action_close), new Intent("com.ar.gab.switchwifi.CloseApp"));

        Intent broadcastIntent = new Intent("com.ar.gab.switchwifi.RestartSwitchWifi");
        context.sendBroadcast(broadcastIntent);


    }




}
