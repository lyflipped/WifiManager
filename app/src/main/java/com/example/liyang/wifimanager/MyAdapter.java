package com.example.liyang.wifimanager;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by liyang on 2017/8/8.
 */

public class MyAdapter extends BaseAdapter {
    LayoutInflater inflater;
    List<ScanResult> list;
    private static final String WIFI_AUTH_OPEN="";
    private static final String WIFI_AUTH_ROAM="[ESS]";
    public MyAdapter(Context context,List<ScanResult> list)
    {
        this.inflater=LayoutInflater.from(context);
        this.list=list;
    }
    @Override
    public int getCount()
    {
        return list.size();
    }
    public void refresh(List<ScanResult> list)
    {
        this.list=list;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position)
    {
        return position;
    }
    @Override
    public long getItemId(int position)
    {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = null;
        view = inflater.inflate(R.layout.item_wifilist,null);
        ScanResult scanResult = list.get(position);
        TextView ssid = (TextView) view.findViewById(R.id.ssid);
        ssid.setText(scanResult.SSID);
        TextView level = (TextView) view.findViewById(R.id.level);
        int signal_strenth = 100-Math.abs(scanResult.level);
        Log.d("level",String.valueOf(signal_strenth));
        level.setText(String.valueOf(signal_strenth));
        ImageView wifi_icon = (ImageView) view.findViewById(R.id.wifi_icon);
        if(scanResult.capabilities!=null && (scanResult.capabilities.equals(WIFI_AUTH_OPEN))||(scanResult.capabilities.equals(WIFI_AUTH_ROAM)))
        {
            //wifi no password
            if(signal_strenth<20)
            {
                wifi_icon.setImageResource(R.drawable.ap_open_barely);
            }else if(signal_strenth<40)
            {
                wifi_icon.setImageResource(R.drawable.ap_open_weak);
            }else if(signal_strenth<60)
            {
                wifi_icon.setImageResource(R.drawable.ap_open_ok);
            }else
            {
                wifi_icon.setImageResource(R.drawable.ap_open_max);
            }
        }else {
            if (signal_strenth < 20) {
                wifi_icon.setImageResource(R.drawable.ap_lock_barely);
            } else if (signal_strenth < 40) {
                wifi_icon.setImageResource(R.drawable.ap_lock_weak);
            } else if (signal_strenth < 60) {
                wifi_icon.setImageResource(R.drawable.ap_lock_ok);
            } else {
                wifi_icon.setImageResource(R.drawable.ap_lock_max);
            }
        }
        return view;

    }




}
