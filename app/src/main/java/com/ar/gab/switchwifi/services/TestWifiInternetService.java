package com.ar.gab.switchwifi.services;

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
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.ar.gab.switchwifi.R;
import com.ar.gab.switchwifi.WifiNotification;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by Guille on 5/12/2017.
 */

public class TestWifiInternetService extends Service {


    private Timer timer;
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private int intTimeToCheck;



    @Override
    public void onCreate() {
        // To avoid cpu-blocking, we create a background handler to run our service
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        /*mServiceLooper = thread.getLooper();
        // start the service using the background handler
        mServiceHandler = new ServiceHandler(mServiceLooper);*/
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
        showToast("onStartCommand TestWifiInternet ID: "+startId);

        Context context = getApplicationContext();
        resetTimeInternetWifi();


        /*WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        WifiNotification wifiNotification =  new WifiNotification();
        wifiNotification.notify(getApplicationContext(), R.drawable.ic_wifi_app_on,
                getApplicationContext().getString(R.string.wifi_on), wifiInfo.getSSID(), 3,
                getApplicationContext().getString(R.string.action_close), new Intent("com.ar.gab.switchwifi.CloseApp"));*/


        return START_STICKY;
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

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public TestWifiInternetService getService() {
            // Return this instance of LocalService so clients can call public methods
            return TestWifiInternetService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void resetTimeInternetWifi(){
        if(timer!=null) {
            timer.cancel();
        }
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        intTimeToCheck = SP.getInt(getString(R.string.setting_WifiInternetTime_key), 7000);
        //timer trigger wifiConnec  every time
        timer = new Timer();
        timer.scheduleAtFixedRate(new WifiInternetConnectTask(), 0, intTimeToCheck*1000);
    }




    private class WifiInternetConnectTask extends TimerTask
    {
        public void run()
        {
            //showToast("TestInternetWIFI");
            Context context = getApplicationContext();
            SharedPreferences sharedPref = context.getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            boolean noIternetInFavoriteWifi = false;

            Set<String> ssdiListFav = sharedPref.getStringSet(getString(R.string.wifiOK), null);
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            List<WifiConfiguration> listwifiManager = wifiManager.getConfiguredNetworks();

            List<ScanResult> list = wifiManager.getScanResults();
            String ssdiCurrent = wifiInfo.getSSID();
            String ssdiConnect = ssdiCurrent;


            //conocer si esta conectado a una wifi favorita y tiene internet
            Iterator<ScanResult> listIterator1 = list.iterator();
            while (listIterator1.hasNext()) {
                ScanResult sc = listIterator1.next();
                if (isWifiFavorite(sc.SSID, ssdiListFav)){

                    if(ssdiConnect.equals("\"" + sc.SSID + "\"") &&  ssdiConnect.equals(ssdiCurrent)  &&  !isConnected2()){
                        noIternetInFavoriteWifi = true;
                        showToast("Sorry, NO INTERNET!! on Wifi favorite:" + ssdiCurrent);
                        //ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                        //toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 7000); // 1
                    }
                }
            }

            //conectarse si hay que cambiar de wifi o si la wifi favorita no tiene wifi
            if (noIternetInFavoriteWifi ) {
                for (WifiConfiguration i : listwifiManager) {
                    if (i.SSID.equals(ssdiConnect)) {
                        //first time connect
                        //showToast("Sorry connect 1st, NO INTERNET!! on Wifi:" + i.SSID);
                        wifiManager.setWifiEnabled(false);
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        wifiManager.setWifiEnabled(true);
                        //second time connect
                        WifiNotification wifiNotification =  new WifiNotification();
                        if (!connectToWifiFav(i, wifiManager)) {
                            showToast("Sorry retry to connect, but is NOT INTERNET!! on Wifi:" + i.SSID);
                        }
                        break;
                    }
                }
            }

        }
    }




    private boolean connectToWifiFav(WifiConfiguration i, WifiManager wifiManager){
        wifiManager.disconnect();
        wifiManager.enableNetwork(i.networkId, true);
        wifiManager.reconnect();
        return isConnected2();
        /*if(isConnected2()){
            WifiNotification wifiNotification =  new WifiNotification();
            wifiNotification.notify(getApplicationContext(), R.drawable.ic_wifi_app_on,
                    getApplicationContext().getString(R.string.wifi_on), i.SSID, 4,
                    getApplicationContext().getString(R.string.action_close), new Intent("com.ar.gab.switchwifi.CloseApp"));

            showToast(i.SSID +" have internet connection!");
            return true;
        }
        //wifiManager.disconnect();
        return false;*/
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
            showToast("No connection for long-time");
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

