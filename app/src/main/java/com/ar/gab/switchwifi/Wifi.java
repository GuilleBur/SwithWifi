package com.ar.gab.switchwifi;

/**
 * Created by Guille on 4/19/2017.
 */

public class Wifi {

    String strength;
    String name;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    boolean checked; /* 0 -&gt; checkbox disable, 1 -&gt; checkbox enable */
    String bssid;

    public Wifi(String name, String bssid, String strength, boolean check){
        setName(name);
        setStrength(strength);
        setChecked(check);
        setBssid(bssid);
    }



    public String getStrength() {
        return strength;
    }

    public String getName() {
        return name;
    }





    public void setStrength(String strength) {
        this.strength = strength;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }




}
