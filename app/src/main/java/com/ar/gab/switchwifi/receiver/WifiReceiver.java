package com.ar.gab.switchwifi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.ListView;

import com.ar.gab.switchwifi.R;
import com.ar.gab.switchwifi.Wifi;
import com.ar.gab.switchwifi.WifiListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import utils.ServiceUtil;

/**
 * Created by Guille on 7/6/2017.
 */

public class WifiReceiver extends BroadcastReceiver {
    private ListView lv;
    private List<ScanResult> wifiList;
    private WifiManager mainWifi;
    private ArrayList<Wifi> wifiItems;
    private WifiListAdapter adapter = null;

    public WifiReceiver(ListView lv){
        this.lv = lv;
    }

    // This method call when number of wifi connections changed
    public void onReceive(Context c, Intent intent) {


        mainWifi = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        wifiList = mainWifi.getScanResults();
        wifiItems = new ArrayList<Wifi>();

        SharedPreferences sharedPref = c.getSharedPreferences(
                c.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Set<String> wifiFavSet = sharedPref.getStringSet(c.getString(R.string.wifiOK), null);

        for(int i = 0; i < wifiList.size(); i++){
            wifiItems.add(new Wifi(wifiList.get(i).SSID,
                    c.getString(R.string.bbsdi)+ServiceUtil.nBBSDI(wifiList.get(i).BSSID) ,
                    c.getString(R.string.strength)+String.valueOf(wifiList.get(i).level), isWifiFavorite(wifiList.get(i).SSID+"-"+ ServiceUtil.nBBSDI(wifiList.get(i).BSSID), wifiFavSet)));
        }


        //lv = (ListView) activity.findViewById(R.id.listView1);
        adapter = new WifiListAdapter(c, wifiItems);
        lv.setAdapter(adapter);
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

}
