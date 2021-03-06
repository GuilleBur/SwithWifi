package com.ar.gab.switchwifi.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.ar.gab.switchwifi.receiver.AlarmWifiRestarterBroadcastReceiver;
import com.ar.gab.switchwifi.R;
import com.ar.gab.switchwifi.WifiNotification;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import utils.ServiceUtil;

/**
 * Created by Guille on 5/12/2017.
 */

public class SwitchWifiService extends Service {


    private Timer timer;
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private int intTimeToCheck;
    private int intAlarmHours;
    private boolean alarmIsOn;


    @Override
    public void onCreate() {
        // To avoid cpu-blocking, we create a background handler to run our service
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        /*mServiceLooper = thread.getLooper();
        // start the service using the background handler
        mServiceHandler = new ServiceHandler(mServiceLooper);*/

        setAlarmTime();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //stopForeground(false);
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent("com.ar.gab.switchwifi.RestartSwitchWifi");
        sendBroadcast(broadcastIntent);
        if(timer!=null) {
            timer.cancel();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        ServiceUtil.showToast("onStartCommand ID: "+startId, getApplicationContext());

        Context context = getApplicationContext();
        resetTimeCheckWifi();


        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        WifiNotification wifiNotification =  new WifiNotification();
        wifiNotification.notify(getApplicationContext(), R.drawable.ic_wifi_app_on,
                getApplicationContext().getString(R.string.wifi_on), wifiInfo.getSSID(), 3,
                getApplicationContext().getString(R.string.action_close), R.drawable.ic_stop_black_24px, new Intent("com.ar.gab.switchwifi.CloseApp"));


        return START_STICKY;
    }


    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public SwitchWifiService getService() {
            // Return this instance of LocalService so clients can call public methods
            return SwitchWifiService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void resetTimeCheckWifi(){
        if(timer!=null) {
            timer.cancel();
        }
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        intTimeToCheck = SP.getInt(getString(R.string.setting_WifiTime_key), 7000);
        //timer trigger wifiConnec  every time
        timer = new Timer();
        timer.scheduleAtFixedRate(new WifiConnectTask(), 0, intTimeToCheck*1000);
    }


    public void setAlarmTime(){
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        intAlarmHours = SP.getInt(getString(R.string.setting_checkAppRun_key), 3);
        alarmIsOn = SP.getBoolean(getString(R.string.setting_enableAppRun_key), false);


        setAlarm(alarmIsOn, intAlarmHours);
    }

    private class WifiConnectTask extends TimerTask
    {
        public void run()
        {
            //showToast("checkWIFI");
            Context context = getApplicationContext();
            SharedPreferences sharedPref = context.getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            boolean noIternetInFavoriteWifi = false;

            Set<String> ssdiListFav = sharedPref.getStringSet(getString(R.string.wifiOK), null);
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            List<WifiConfiguration> listwifiManager = wifiManager.getConfiguredNetworks();

            List<ScanResult> list = wifiManager.getScanResults();
            int level = -100;
            String ssdiCurrent = wifiInfo.getSSID();
            String bssdiCurrent = ServiceUtil.nBBSDI(wifiInfo.getBSSID());
            String ssdiConnect = ssdiCurrent;
            String bssdiConnect = bssdiCurrent;
            Iterator<ScanResult> listIterator = list.iterator();

            //conocer level actual de la wifi
            while (listIterator.hasNext()) {
                ScanResult sc = listIterator.next();
                if ((ssdiCurrent.equals("\"" + sc.SSID + "\"")  &&  bssdiCurrent.equals(ServiceUtil.nBBSDI(sc.BSSID)))
                        && isWifiFavorite(sc.SSID+"-"+ServiceUtil.nBBSDI(sc.BSSID), ssdiListFav)) {
                    level = sc.level;
                }
            }
            //conocer si hay que cambiar por otra wifi
            Iterator<ScanResult> listIterator1 = list.iterator();
            while (listIterator1.hasNext()) {
                ScanResult sc = listIterator1.next();
                if (isWifiFavorite(sc.SSID+"-"+ServiceUtil.nBBSDI(sc.BSSID), ssdiListFav)){
                    if (!(ssdiCurrent.equals("\"" + sc.SSID + "\"") && bssdiCurrent.equals(ServiceUtil.nBBSDI(sc.BSSID))) && sc.level > level) {
                        ssdiConnect = "\"" +sc.SSID+ "\"";
                        bssdiConnect = ServiceUtil.nBBSDI(sc.BSSID);
                        level = sc.level;
                    }

                }
            }

            //conectarse si hay que cambiar de wifi o si la wifi favorita no tiene wifi
            if (!(ssdiConnect.equals(ssdiCurrent) && bssdiConnect.equals(bssdiCurrent)) || noIternetInFavoriteWifi ) {
                for (WifiConfiguration i : listwifiManager) {
                    if (i.SSID.equals(ssdiConnect ) ) {
                        //first time connect
                        changeWifiConnect(i, wifiManager);
                        break;
                    }
                }
            }

        }
    }

    private void setAlarm(boolean isOn, int hours){

        AlarmManager alarmMgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intentAlarm = new Intent(getApplicationContext(), AlarmWifiRestarterBroadcastReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intentAlarm, 0);
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }
        if(isOn) {

            Intent intent = new Intent(getApplicationContext(), AlarmWifiRestarterBroadcastReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + (hours * 60 * 60 * 1000),
                    (hours * 60 * 60 * 1000), alarmIntent);
        }
    }

    private void changeWifiConnect(WifiConfiguration i, WifiManager wifiManager){
        wifiManager.disconnect();
        wifiManager.enableNetwork(i.networkId, true);
        wifiManager.reconnect();
        ServiceUtil.showToast("change wifi to :"+i.SSID, getApplicationContext());
       // if(isConnected2()){
            WifiNotification wifiNotification =  new WifiNotification();
            wifiNotification.notify(getApplicationContext(), R.drawable.ic_wifi_app_on,
                    getApplicationContext().getString(R.string.wifi_on), i.SSID, 3,
                    getApplicationContext().getString(R.string.action_close), R.drawable.ic_stop_black_24px, new Intent("com.ar.gab.switchwifi.CloseApp"));

            /*ServiceUtil.showToast(i.SSID +" have internet connection!", getApplicationContext());
            return true;
        }
        //wifiManager.disconnect();
        return false; */
    }


    private boolean isWifiFavorite (String ssdi, Set<String> ssdiListFav){
        boolean isWifiFav = false;
        if(ssdiListFav!=null){
            for (String wifiFav : ssdiListFav) {
                if(wifiFav.equals(ssdi)){
                    isWifiFav = true;
                    break;
                }
            }
        }
        return isWifiFav;
    }



    public boolean isConnected2() {
        boolean connected =false;
        try {
            connected = new NetTask().execute("www.google.com").get(6, TimeUnit.SECONDS);
            //connected = (Runtime.getRuntime().exec ("ping -c 1 google.com").waitFor() == 0);
            //showToast(String.valueOf(connected));
        }
        catch (Exception e1) {
            ServiceUtil.showToast("No connection for long-time", getApplicationContext());
            return false;
        }
        return connected;
    }



    public class NetTask extends AsyncTask<String, Integer, Boolean>
    {


        @Override
        protected Boolean doInBackground(String... params)
        {
            boolean connected = false;
            try {
                String command = "ping -c 1 google.com";
                connected = (Runtime.getRuntime().exec (command).waitFor() == 0);
            }
            catch (IOException|InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            return connected;
        }
    }





}

