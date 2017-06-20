package utils;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ViewGroup;

/**
 * Created by Guille on 7/6/2017.
 */

public class RunnableWifi implements Runnable {
    private SwipeRefreshLayout swipeContainer;
    private Context context;
    private Activity activity;
    public RunnableWifi(SwipeRefreshLayout swipeRefreshLayout, Context context, Activity activity) {
        swipeContainer = swipeRefreshLayout;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public void run() {
        //FToast.makeText(MainActivity.this, "Starting Scan...", Toast.LENGTH_SHORT).show();
        CheckPermission checkPermission = new CheckPermission(context, activity);
        if(checkPermission.checkWifiPermission()){
            WifiManager mainWifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            mainWifi.startScan();
            swipeContainer.setRefreshing(false);
        }

    }

}