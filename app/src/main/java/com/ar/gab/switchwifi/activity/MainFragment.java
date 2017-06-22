package com.ar.gab.switchwifi.activity;


import android.app.Fragment;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;


import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ar.gab.switchwifi.R;
import com.ar.gab.switchwifi.receiver.WifiReceiver;
import com.ar.gab.switchwifi.services.TestWifiInternetService;

import utils.CheckPermission;
import utils.RunnableWifi;


/**
 * Created by Guille on 7/6/2017.
 */

public class MainFragment extends Fragment {

    SwipeRefreshLayout swipeContainer;
    private TestWifiInternetService mTestWifiInternetService;


    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    //This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
//Check wifi permission
        CheckPermission checkPermission = new CheckPermission(view.getContext(), getActivity());
        boolean permission = checkPermission.checkWifiPermission();
//Get wifi list
        WifiManager mainWifi = (WifiManager) view.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        ListView listView = (ListView) getActivity().findViewById(R.id.listView1);
        TextView textView = new TextView(view.getContext());


        SpannableString spanString = new SpannableString(getString(R.string.selectFavWifi));
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
        textView.setTextSize(19);
        textView.setText(spanString);

        textView.setText(spanString);
        listView.addHeaderView(textView);
        WifiReceiver receiverWifi = new WifiReceiver(listView);
        // Register broadcast receiver
        // Broacast receiver will automatically call when number of wifi connections changed
        view.getContext().registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        if(permission) {
            mainWifi.startScan();
        }

//Refresh wifi connections
        swipeContainer = (SwipeRefreshLayout) getView().findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final boolean b = new Handler().postDelayed(new RunnableWifi(swipeContainer, view.getContext(), getActivity()), 1000);
            }

        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


//Settings button
        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager()
                        .beginTransaction().replace(R.id.your_placeholder, new WifiSettingsActivity())
                        .addToBackStack(null).commit();


            }
        });




    }







}
