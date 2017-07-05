package com.ar.gab.switchwifi.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ar.gab.switchwifi.R;
import com.ar.gab.switchwifi.Wifi;
import com.ar.gab.switchwifi.WifiListAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utils.ServiceUtil;

public class WifiListActivity extends Activity {

    private final int REQUEST_PERMISSION_PHONE_STATE=1;
    TextView mainText;
    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();
    ListView lv;
    ArrayList<Wifi> wifiItems;
    WifiListAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_list);

        //mainText = (TextView) findViewById(R.id.mainText2);
        mainWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        checkWifiPermission();
        checkButtonClick();

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
                    //Toast.makeText(WifiListActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WifiListActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void checkWifiPermission (){
        // Check for wifi is disabled
        if (mainWifi.isWifiEnabled() == false) {
            // If wifi disabled then enable it
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled",
                    Toast.LENGTH_LONG).show();
            mainWifi.setWifiEnabled(true);
        } else {
            checkPermission();
            // wifi scaned value broadcast receiver
            receiverWifi = new WifiReceiver();
            // Register broadcast receiver
            // Broacast receiver will automatically call when number of wifi connections changed
            registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            mainWifi.startScan();
            mainText.setText("Starting Scan...");
        }

    }

    private void checkPermission (){
        int permissionCheck1 = ContextCompat.checkSelfPermission(
                this, Manifest.permission.CHANGE_WIFI_STATE);
        int permissionCheck2 =ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionCheck2 != PackageManager.PERMISSION_GRANTED ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                showExplanation("Permission Needed", "Rationale", Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_PHONE_STATE);
            } else {
                requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_PHONE_STATE);
            }
        } else {
            //Toast.makeText(WifiListActivity.this, "Permission (already) Granted!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Refresh");
        return super.onCreateOptionsMenu(menu);
    }




    class WifiReceiver extends BroadcastReceiver {

        // This method call when number of wifi connections changed
        public void onReceive(Context c, Intent intent) {

            sb = new StringBuilder();
            wifiList = mainWifi.getScanResults();
            //sb.append("\n        Number Of Wifi connections :"+wifiList.size()+"\n\n");
            wifiItems = new ArrayList<Wifi>();
            //wifiItems.addAll(WifiMock.getWifiList());
            Context context = getApplicationContext();
            SharedPreferences sharedPref = context.getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            Set<String> wifiFavSet = sharedPref.getStringSet(getString(R.string.wifiOK), null);

            for(int i = 0; i < wifiList.size(); i++){
                wifiItems.add(new Wifi(wifiList.get(i).SSID,
                                        getString(R.string.bbsdi)+ServiceUtil.nBBSDI(wifiList.get(i).BSSID) ,
                        getString(R.string.strength)+String.valueOf(wifiList.get(i).level), isWifiFavorite(wifiList.get(i).SSID+"-"+ServiceUtil.nBBSDI(wifiList.get(i).BSSID), wifiFavSet)));
            }


            lv = (ListView) findViewById(R.id.listView1);
            adapter = new WifiListAdapter(c, wifiItems);
            lv.setAdapter(adapter);



            mainText.setText(sb);
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

    private void checkButtonClick() {
        Button myButton = (Button) findViewById(R.id.findSelected2);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeSelectWifi();
                finish();
            }
        });
    }


    private void changeSelectWifi(){
        StringBuffer responseText = new StringBuffer();
        responseText.append("The following were selected...\n");

        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Set<String> ssdiListFav = sharedPref.getStringSet(context.getString(R.string.wifiOK), null);

        ArrayList<Wifi> wifiList = adapter.wifiItems;
        Set<String> wifiSelected = new HashSet<String>();
        for(int i=0;i<wifiList.size();i++){
            Wifi wifi = wifiList.get(i);
            if(wifi.isChecked()){
                responseText.append("\n" + wifi.getName());
                wifiSelected.add(wifi.getName()+"-"+ServiceUtil.nBBSDI(wifi.getBssid()));
            }
            else if(ServiceUtil.isWifiFavorite(wifi.getName()+"-"+ServiceUtil.nBBSDI(wifi.getBssid()),ssdiListFav)){
                ssdiListFav.remove(wifi.getName()+"-"+ServiceUtil.nBBSDI(wifi.getBssid()));
            }

        }
        wifiSelected.addAll(ssdiListFav);

        sharedPref.edit().putStringSet( getString(R.string.wifiOK), wifiSelected).commit();

        Toast.makeText(getApplicationContext(),
                responseText, Toast.LENGTH_LONG).show();

    }






}