package com.ar.gab.switchwifi.activity;

import android.app.ActivityManager;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ar.gab.switchwifi.R;
import com.ar.gab.switchwifi.WifiNotification;
import com.ar.gab.switchwifi.services.SwitchWifiService;
import com.ar.gab.switchwifi.services.TestWifiInternetService;

import utils.ServiceUtil;

public class MainActivity extends AppCompatActivity {


    private final int REQUEST_PERMISSION_PHONE_STATE=1;
    Intent mServiceIntent;
    Intent mServiceInternetIntent;
    private SwitchWifiService mSwitchWifiService;
    private TestWifiInternetService mTestWifiInternetService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//Put the main fragment
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.your_placeholder, new MainFragment())
                    .commit();
        }


//Start Main Services
        mSwitchWifiService = new SwitchWifiService();
        mServiceIntent = new Intent(this, mSwitchWifiService.getClass());
        if (!ServiceUtil.isMyServiceRunning(mSwitchWifiService.getClass(), this)) {
            startService(mServiceIntent);
        }

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        WifiNotification wifiNotification =  new WifiNotification();
        wifiNotification.notify(getApplicationContext(), R.drawable.ic_wifi_app_on,
                getApplicationContext().getString(R.string.wifi_on), wifiInfo.getSSID(), 3,
                getApplicationContext().getString(R.string.action_close), new Intent("com.ar.gab.switchwifi.CloseApp"));




//Start Test Internet Services
        mTestWifiInternetService = new TestWifiInternetService();
        mServiceInternetIntent = new Intent(this, mTestWifiInternetService.getClass());
        if (!ServiceUtil.isMyServiceRunning(mTestWifiInternetService.getClass(), this)) {
            startService(mServiceInternetIntent);
        }



//Set closeApp false by default
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean(getString(R.string.closeApp), false).apply();


//SetToolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);






    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }





    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_PHONE_STATE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        stopService(mServiceInternetIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

    }

//Close Aplication Intent
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getBooleanExtra("close_activity",false)){
            this.finish();
            //System.exit(0);
        }
    }



    /*private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





















   /* class RunnableWifi implements Runnable {

        @Override
        public void run() {
            //FToast.makeText(MainActivity.this, "Starting Scan...", Toast.LENGTH_SHORT).show();
            checkWifiPermission();
            swipeContainer.setRefreshing(false);
        }

    }*/



    }
