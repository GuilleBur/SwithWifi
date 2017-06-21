package com.ar.gab.switchwifi;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import com.ar.gab.switchwifi.activity.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utils.WifiLogComparator;

/**
 * Helper class for showing and canceling wifi
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class WifiNotification {
    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "Wifi";



    /**
     * Shows the notification, or updates a previously shown notification of
     * this type, with the given parameters.
     * <p>
     * TODO: Customize this method's arguments to present relevant content in
     * the notification.
     * <p>
     * TODO: Customize the contents of this method to tweak the behavior and
     * presentation of wifi notifications. Make
     * sure to follow the
     * <a href="https://developer.android.com/design/patterns/notifications.html">
     * Notification design guidelines</a> when doing so.
     *
     *
     */
    public static void notify(final Context context, final int icon, final String onOrOff,
                                      final String currentWifiString, final int number, final String openOrCloseButton, final int iconOpenOrClose,
                                      final Intent actionIntent ) {
        final Resources res = context.getResources();

        // This image is used as the notification's large icon (thumbnail).
        // TODO: Remove this if your notification has no relevant thumbnail.
        final Bitmap picture = BitmapFactory.decodeResource(res, R.drawable.ic_stat_wifi);


        final String ticker = currentWifiString;
        final String title = res.getString(
                R.string.wifi_notification_title_template, onOrOff, currentWifiString);
        final String text = res.getString(
                R.string.wifi_notification_placeholder_text_template, currentWifiString);
        long[] vibrate = { 0, 50};

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)

                // Set appropriate defaults for the notification light, sound,
                // and vibration.

                .setVibrate(vibrate)

                // Set required fields, including the small icon, the
                // notification title, and text.
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text)

                // All fields below this line are optional.

                // Use a default priority (recognized on devices running Android
                // 4.1 or later)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                // Provide a large icon, shown with the notification in the
                // notification drawer on devices running Android 3.0 or later.
                .setLargeIcon(picture)

                // Set ticker text (preview) information for this notification.
                .setTicker(ticker)

                // Show a number. This is useful when stacking notifications of
                // a single type.
                .setNumber(number)

                // If this notification relates to a past or upcoming event, you
                // should set the relevant time information using the setWhen
                // method below. If this call is omitted, the notification's
                // timestamp will by set to the time at which it was shown.
                // TODO: Call setWhen if this notification relates to a past or
                // upcoming event. The sole argument to this method should be
                // the notification timestamp in milliseconds.
                //.setWhen(...)

                //CONTENT
                // Set the pending intent to be initiated when the user touches
                // the notification.
                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                new Intent(context, MainActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT))




                //SHARE
                // Example additional actions for this notification. These will
                // only show on devices running Android 4.1 or later, so you
                // should ensure that the activity in this notification's
                // content intent provides access to the same actions in
                // another way.
                .addAction(
                        iconOpenOrClose,
                        openOrCloseButton,
                        PendingIntent.getBroadcast(
                                context, 2,
                                actionIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction(
                        R.drawable.ic_sync_black_24dp,
                        res.getString(R.string.action_restart),
                        PendingIntent.getBroadcast(
                                context, 5,
                                new Intent("com.ar.gab.switchwifi.RestartApp"), PendingIntent.FLAG_UPDATE_CURRENT))

                .addAction(
                        R.drawable.ic_info_black_24dp,
                        res.getString(R.string.action_status),
                        PendingIntent.getBroadcast(
                                context, 5,
                                new Intent("com.ar.gab.switchwifi.StatusApp"), PendingIntent.FLAG_UPDATE_CURRENT))

                // Automatically dismiss the notification when it is touched.
                .setAutoCancel(true)
                .setOngoing(true);

/*

        PendingIntent.getBroadcast(
                context, 2,
                new Intent("closeApp"), PendingIntent.FLAG_UPDATE_CURRENT))*/



//LOG
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);


        Set<String> logApp = sharedPref.getStringSet(context.getString(R.string.logApp), null);
        if(logApp==null){
            logApp = new HashSet<String>();
        }else if (logApp.size()>4){
            List<String> listWifiLogOrder = new ArrayList<String>(logApp);
            Collections.sort(listWifiLogOrder, new WifiLogComparator());
            listWifiLogOrder.remove(logApp.remove(listWifiLogOrder.toArray()[4]));
        }


        String hora = WifiLogComparator.dateFormat.format(new Date());

        logApp.add(hora+" "+currentWifiString+" is "+onOrOff);
        sharedPref.edit().putStringSet(context.getString(R.string.logApp), logApp).apply();

        List<String> listWifiLog = new ArrayList<String>(logApp);
        Collections.sort(listWifiLog, new WifiLogComparator());
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        for (int i = 0; i < listWifiLog.size(); i++) {
            inboxStyle.addLine(listWifiLog.get(i));
        }
        inboxStyle.setBigContentTitle(title);
        builder.setStyle(inboxStyle);

        notify(context, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }

    /**
     * Cancels any notifications of this type previously shown using
     * {@link #notify(Context, String, int)}.
     */
    /*@TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }*/
}
