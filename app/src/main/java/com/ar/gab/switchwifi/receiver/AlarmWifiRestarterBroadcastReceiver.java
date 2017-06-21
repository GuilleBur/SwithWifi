package com.ar.gab.switchwifi.receiver;

import android.app.ActivityManager;
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
import com.ar.gab.switchwifi.services.SwitchWifiService;
import com.ar.gab.switchwifi.services.TestWifiInternetService;

import java.util.Calendar;

import utils.ServiceUtil;

/**
 * Created by Guille on 5/15/2017.
 */

public class AlarmWifiRestarterBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SwitchWifiService mSwitchWifiService = new SwitchWifiService();
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        boolean closeApp = sharedPref.getBoolean(context.getString(R.string.closeApp), false);

        if(!closeApp && !ServiceUtil.isMyServiceRunning(mSwitchWifiService.getClass(), context)){
            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 7000); // 1

            context.startService(new Intent(context, SwitchWifiService.class));
            context.startService(new Intent(context, TestWifiInternetService.class));

        }else if (!closeApp){
            WifiNotification wifiNotification2 =  new WifiNotification();
            Calendar c = Calendar.getInstance();
            wifiNotification2.notify(context, R.drawable.ic_wifi_app_on , context.getString(R.string.wifi_on) +" "+Calendar.HOUR+":"+Calendar.MINUTE
                    ,((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getSSID(),
                    3, context.getString(R.string.action_close), R.drawable.ic_stop_black_24px, new Intent("com.ar.gab.switchwifi.CloseApp"));
        }



    }

}
