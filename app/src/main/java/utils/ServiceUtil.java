package utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by Guille on 15/6/2017.
 */

public class ServiceUtil {

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    public static void showToast(final String msg, final Context context){
        //gets the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // run this code in the main thread
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static boolean isWifiFavorite (String ssdi, Set<String> ssdiListFav){
        boolean isWifiFav = false;
        if(ssdiListFav!=null){
            for (String wifiFav : ssdiListFav) {
                if(wifiFav.equals(ssdi)){
                    isWifiFav = true;
                    break;
                }
            }
        }
        return isWifiFav;
    }

    public static String nBBSDI(String bbsdi){
        String out;
        if(bbsdi!=null){
            out = bbsdi.replaceFirst("BBSDI:", "");
        }else{
            out = "";
        }
        return out;
    }


}


