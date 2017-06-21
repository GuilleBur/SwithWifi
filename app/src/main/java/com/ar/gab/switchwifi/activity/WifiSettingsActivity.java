package com.ar.gab.switchwifi.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.Preference;


import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ar.gab.switchwifi.R;
import com.ar.gab.switchwifi.services.SwitchWifiService;
import com.ar.gab.switchwifi.services.TestWifiInternetService;

public class WifiSettingsActivity extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {


    SwitchWifiService mService;
    TestWifiInternetService mTestWifiInternetService;
    boolean mBound = false;


    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(R.id.fragmentParentViewGroup, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
        }
    }
*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        addPreferencesFromResource(R.xml.preference);

        Intent intent = new Intent(getActivity(), SwitchWifiService.class);
        getActivity().bindService(intent, mConnection, getActivity().BIND_AUTO_CREATE);

        Intent intentInternet = new Intent(getActivity(), TestWifiInternetService.class);
        getActivity().bindService(intentInternet, mConnectionInternetCheck, getActivity().BIND_AUTO_CREATE);



    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


        if (key.equals(getString(R.string.setting_WifiTime_key))) {
            mService.resetTimeCheckWifi();

            MySeekBarPreference mySeekBarPreference = (MySeekBarPreference) findPreference(key);
            mySeekBarPreference.setSummary("100");

            /*Preference connectionPref = findPreference(key);
            MySeekBarPreference mySeekBarPreference = (MySeekBarPreference) findPreference(key);
            int i = sharedPreferences.getInt(key, 0);
            mySeekBarPreference.setSummary("Second:" + i);*/


            // Set summary to be the user-description for the selected value
            //connectionPref.setSummary(sharedPreferences.getString(key, ""));
            //connectionPref.setSummary(sharedPreferences.getInt(key, 0));
            //Toast.makeText(getActivity(), "Seek bar progress is :" +sharedPreferences.getInt(key, 0),
            //        Toast.LENGTH_SHORT).show();

        }
        if (key.equals(getString(R.string.setting_checkAppRun_key)) || key.equals(getString(R.string.setting_enableAppRun_key))) {
            mService.setAlarmTime();
        }
        if (key.equals(getString(R.string.setting_WifiInternetTime_key))) {
            mTestWifiInternetService.resetTimeInternetWifi();

        }


        if (key.equals(getString(R.string.setting_backButton_key))) {
            getFragmentManager().popBackStack();
        }
    }


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SwitchWifiService.LocalBinder binder = (SwitchWifiService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    private ServiceConnection mConnectionInternetCheck = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            TestWifiInternetService.LocalBinder binder = (TestWifiInternetService.LocalBinder) service;
            mTestWifiInternetService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub


        LinearLayout v = (LinearLayout) super.onCreateView(inflater, container, savedInstanceState);


        Button backbtn = new Button(getActivity().getApplicationContext());
        backbtn.setText(getString(R.string.setting_backButton));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(800, 0, 0, 10);
        backbtn.setLayoutParams(params);


        v.addView(backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        return v;
    }
}

