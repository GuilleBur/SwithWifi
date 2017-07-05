package utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.ListView;
import android.widget.Toast;

import com.ar.gab.switchwifi.receiver.WifiReceiver;

/**
 * Created by Guille on 7/6/2017.
 */

public class CheckPermission {

    private WifiManager mainWifi;
    private Context context;
    private WifiReceiver receiverWifi;
    private Activity activity;
    private StringBuilder sb = new StringBuilder();



    private ListView lv;

    private final int REQUEST_PERMISSION_PHONE_STATE=1;


    public CheckPermission(Context context, Activity activity) {
        mainWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        this.context = context;
        this.activity = activity;

    }


    public boolean checkWifiPermission (){
        // Check for wifi is disabled
        if (mainWifi.isWifiEnabled() == false) {
            // If wifi disabled then enable it
            Toast.makeText(context, "wifi is disabled..making it enabled",
                    Toast.LENGTH_LONG).show();
            mainWifi.setWifiEnabled(true);
        }
        return checkPermission();

    }

    private boolean checkPermission (){
        boolean permission = false;
        int permissionCheck1 = ContextCompat.checkSelfPermission(
                context, Manifest.permission.CHANGE_WIFI_STATE);
        int permissionCheck2 =ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionCheck2 != PackageManager.PERMISSION_GRANTED ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                showExplanation("Permission Needed", "Rationale", Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_PHONE_STATE);
            } else {
                requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_PHONE_STATE);
            }

        } else {
            //Toast.makeText(context, "Permission (already) Granted!", Toast.LENGTH_SHORT).show();
            permission = true;
        }
        return permission;
    }


    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
        ActivityCompat.requestPermissions(activity,
                new String[]{permissionName}, permissionRequestCode);
    }






}
