package com.ar.gab.switchwifi.activity;


import android.app.DialogFragment;
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

import android.widget.CompoundButton;
import android.widget.Switch;
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
                    .add(R.id.your_placeholder, new MainFragment(), getString(R.string.mainFragment))
                    .commit();
        }


//Start Main Services
        mSwitchWifiService = new SwitchWifiService();
        mServiceIntent = new Intent(this, mSwitchWifiService.getClass());
        if (!ServiceUtil.isMyServiceRunning(mSwitchWifiService.getClass(), this)) {
            startService(mServiceIntent);
        }


//get notification
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        WifiNotification wifiNotification =  new WifiNotification();
        wifiNotification.notify(getApplicationContext(), R.drawable.ic_wifi_app_on,
                getApplicationContext().getString(R.string.wifi_on), wifiInfo.getSSID(), 3,
                getApplicationContext().getString(R.string.action_close), R.drawable.ic_stop_black_24px, new Intent("com.ar.gab.switchwifi.CloseApp"));




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

        Switch mSwitchShowSecure;
        mSwitchShowSecure = (Switch) menu.findItem(R.id.show_secure).getActionView().findViewById(R.id.switchOnOff);
        mSwitchShowSecure.setChecked(true);
        mSwitchShowSecure.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    Intent openAppIntent = new Intent("com.ar.gab.switchwifi.OpenApp");
                    sendBroadcast(openAppIntent);
                    Toast.makeText(getApplication(), "ON", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Intent closeAppIntent = new Intent("com.ar.gab.switchwifi.CloseApp");
                    sendBroadcast(closeAppIntent);
                    Toast.makeText(getApplication(), "OFF", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });


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




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            DialogFragment aboutFragment = new AboutFragment();
            aboutFragment.show(getFragmentManager(), "about");
            return true;
        }
        if(id == R.id.list_fav_wifi){
            DialogFragment listFavWifiFragment = new ListWifiFavoriteFragment();
            listFavWifiFragment.show(getFragmentManager(), "fav");
            return true;
        }
        if(id == R.id.current_wifi){
            DialogFragment currentWifiInfo = new CurrentWifiFragment();
            currentWifiInfo.show(getFragmentManager(), "currentWifiInfo");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    }