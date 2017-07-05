package com.ar.gab.switchwifi.activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Html;
import android.widget.NumberPicker;

import com.ar.gab.switchwifi.R;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;

import utils.ServiceUtil;

/**
 * Created by Guille on 7/6/2017.
 */

public class CurrentWifiFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.title_currentWifi)
                        .setPositiveButton(R.string.dismiss_about, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ServiceUtil.showToast("Bye, good luck!", getActivity());
            }
        });
        if(wifiInfo!=null){

            byte[] ipAddress = BigInteger.valueOf(wifiInfo.getIpAddress()).toByteArray();
            try {
                InetAddress myAddr = InetAddress.getByAddress(ipAddress);
                String ip = myAddr.getHostAddress();
                String mens = String.format(getString(R.string.wifiConnectionInfo), wifiInfo.getSSID(),
                        ip,wifiInfo.getBSSID(), getStrenghtCurrentWifi(wifiManager) );
                mens = mens.replaceAll("-b", "<b>" );
                mens = mens.replaceAll("-c", "</b>");
                mens = mens.replaceAll("-l", "<br>");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    builder.setMessage(Html.fromHtml(mens, Html.FROM_HTML_MODE_LEGACY));
                } else {
                    builder.setMessage(Html.fromHtml(mens));
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        // Create the AlertDialog object and return it
        return builder.create();
    }

    private int getStrenghtCurrentWifi(WifiManager wifiManager){

        List<ScanResult> wifiList = wifiManager.getScanResults();
        Iterator<ScanResult> listIterator = wifiList.iterator();

        int level = 0;
        //conocer level actual de la wifi
        while (listIterator.hasNext()) {
            ScanResult sc = listIterator.next();
            if ((wifiManager.getConnectionInfo().getSSID().equals("\"" + sc.SSID + "\"")  &&  wifiManager.getConnectionInfo().getBSSID().equals(ServiceUtil.nBBSDI(sc.BSSID)))) {
                level = sc.level;
            }
        }
        return level;
    }
}