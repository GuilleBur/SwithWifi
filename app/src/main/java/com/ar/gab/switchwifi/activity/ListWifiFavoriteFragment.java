package com.ar.gab.switchwifi.activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;

import com.ar.gab.switchwifi.R;
import com.ar.gab.switchwifi.Wifi;
import com.ar.gab.switchwifi.WifiFavoriteListAdapter;
import com.ar.gab.switchwifi.WifiListAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import utils.RunnableWifi;
import utils.ServiceUtil;

/**
 * Created by Guille on 7/6/2017.
 */

public class ListWifiFavoriteFragment extends DialogFragment {


    private ArrayList<Wifi> wifiItems;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        wifiItems = new ArrayList<Wifi>();

        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                getActivity().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Set<String> wifiFavSet = sharedPref.getStringSet(getActivity().getString(R.string.wifiOK), null);
        if(wifiFavSet!=null) {
            Iterator<String> iterator = wifiFavSet.iterator();
            while (iterator.hasNext()) {
                wifiItems.add(new Wifi(iterator.next(), true));
            }
        }
        WifiFavoriteListAdapter adapter = new WifiFavoriteListAdapter(getActivity(), wifiItems);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.listFavWifi)
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                })
                .setPositiveButton(R.string.ready, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeContainer);
                        final boolean b = new Handler().postDelayed(new RunnableWifi(swipeContainer, getActivity(), getActivity()), 100);
                    }
                });
        return builder.create();
    }
}