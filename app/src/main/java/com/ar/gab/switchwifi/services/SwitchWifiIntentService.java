package com.ar.gab.switchwifi.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.ar.gab.switchwifi.R;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Guille on 5/11/2017.
 */

public class SwitchWifiIntentService extends IntentService {



    public SwitchWifiIntentService() {
        this(SwitchWifiIntentService.class.getName());
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SwitchWifiIntentService(String name) {
        super(name);
    }


    @Override
    protected void onHandleIntent(Intent workIntent) {
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new wifiConnectTask(), 0, 60000);
    }

   /* @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }*/

    private class wifiConnectTask extends TimerTask
    {
        public void run()
        {

            Context context = getApplicationContext();
            SharedPreferences sharedPref = context.getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);

            Set<String> ssdiListFav = sharedPref.getStringSet(getString(R.string.wifiOK), null);

            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            List<WifiConfiguration> listwifiManager = wifiManager.getConfiguredNetworks();

            List<ScanResult> list =wifiManager.getScanResults();
            int level = -100;
            String ssdiCurrent = wifiInfo.getSSID();
            String ssdiConnect = ssdiCurrent;
            Iterator<ScanResult> listIterator = list.iterator();
            //conocer level actual
            while (listIterator.hasNext()) {
                ScanResult sc = listIterator.next();
                if(ssdiCurrent.equals("\"" + sc.SSID + "\"") && isWifiFavorite(sc.SSID, ssdiListFav)){
                    level = sc.level;
                }

            }
            //conocer si hay que cambiar por otra wifi
            Iterator<ScanResult> listIterator1 = list.iterator();
            while (listIterator1.hasNext()) {
                ScanResult sc = listIterator1.next();
                if( isWifiFavorite(sc.SSID, ssdiListFav) && !ssdiCurrent.equals("\"" + sc.SSID + "\"") && sc.level>level ) {
                     ssdiConnect = sc.SSID;
                    //showToast("current wifi:"+ssdiConnect+" level:"+sc.level);
                    //Toast.makeText(getApplicationContext(), "current wifi:"+ssdiConnect+" level:"+sc.level, Toast.LENGTH_LONG).show();
                }
            }
            //conectarse si hay que cambiar de wifi[
            if(!ssdiConnect.equals(ssdiCurrent)) {
                for (WifiConfiguration i : listwifiManager) {
                    if (i.SSID != null && i.SSID.equals("\"" + ssdiConnect + "\"")) {
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(i.networkId, true);
                        wifiManager.reconnect();
                        showToast("change wifi to :"+ssdiConnect);
                        //Toast.makeText(getApplicationContext(),"change wifi to :"+ssdiConnect, Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            }
        }
    }


    private boolean isWifiFavorite (String ssdi, Set<String> ssdiListFav){
        boolean isWifiFav = false;
        if(ssdiListFav!=null){
            for (String wifiFav : ssdiListFav) {
                if(wifiFav.equals(ssdi)) isWifiFav = true;
            }
        }
        return isWifiFav;
    }

    protected void showToast(final String msg){
        //gets the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // run this code in the main thread
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }





}