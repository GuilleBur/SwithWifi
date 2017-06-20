package utils;

import android.net.wifi.ScanResult;

import com.ar.gab.switchwifi.Wifi;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guille on 4/19/2017.
 */

public class WifiMock {

    private static  ArrayList<Wifi> wifiList;


    public static ArrayList<Wifi> getWifiList ()  {
        wifiList = new ArrayList<Wifi>();

        wifiList.add(new Wifi("angui", "BSSDI: 18:d6:c7:63:c9:05", "Strength: -40", false));
        wifiList.add(new Wifi("angui_ext","BSSDI: 18:d6:c7:63:c9:05", "Strength: -50", false));
        wifiList.add(new Wifi("angui-1","BSSDI: 18:d6:c7:63:c9:05", "Strength: -60", false));
        wifiList.add(new Wifi("angui-2","BSSDI: 18:d6:c7:63:c9:05", "Strength: -70", false));
        wifiList.add(new Wifi("angui-3","BSSDI: 18:d6:c7:63:c9:05", "Strength: -60", false));
        wifiList.add(new Wifi("angui-4","BSSDI: 18:d6:c7:63:c9:05", "Strength: -80", false));
        wifiList.add(new Wifi("angui-5","BSSDI: 18:d6:c7:63:c9:05", "Strength: -90", false));
        wifiList.add(new Wifi("angui-6","BSSDI: 18:d6:c7:63:c9:05", "Strength: -100", false));
        wifiList.add(new Wifi("angui-7","BSSDI: 18:d6:c7:63:c9:05", "Strength: -67", false));
        wifiList.add(new Wifi("angui-8","BSSDI: 18:d6:c7:63:c9:05", "Strength: -66", false));

        return  wifiList;
    }


}
