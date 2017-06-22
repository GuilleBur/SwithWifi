package com.ar.gab.switchwifi;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Guille on 4/19/2017.
 */

public class WifiFavoriteListAdapter extends ArrayAdapter<Wifi>{
    public ArrayList<Wifi> wifiItems = null;
    Context context;



    public WifiFavoriteListAdapter(Context context, ArrayList<Wifi> resource) {
        super(context,R.layout.wifi_fav_row,resource);
        this.wifiItems = resource;
        this.context = context;
        //this.wifiItems.addAll(resource);
    }

    private class ViewHolder {
        TextView name;
        CheckBox check;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.wifi_fav_row, null);


            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.textName1);
            holder.check = (CheckBox) convertView.findViewById(R.id.checkBox1);
            convertView.setTag(holder);
            holder.check.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v ;
                    Wifi wifi = wifiItems.get(position);
                    wifi.setChecked(cb.isChecked());
                    changeSelectWifi(context);
                }
            });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Wifi wifi = wifiItems.get(position);
        holder.name.setText(wifi.getName());
        holder.check.setChecked(wifi.isChecked());
        return convertView;
    }


    private void changeSelectWifi(Context context){
        StringBuffer responseText = new StringBuffer();
        //responseText.append("The following were selected...\n");
        ArrayList<Wifi> wifiList = wifiItems;
        Set<String> wifiSelected = new HashSet<String>();
        for(int i=0;i<wifiList.size();i++){
            Wifi wifi = wifiList.get(i);
            if(wifi.isChecked()){
               // responseText.append("\n" + wifi.getName());
                wifiSelected.add(wifi.getName());
            }
        }
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        sharedPref.edit().putStringSet( context.getString(R.string.wifiOK), wifiSelected).apply();

        Toast.makeText(context,
                responseText, Toast.LENGTH_LONG).show();

    }


}
