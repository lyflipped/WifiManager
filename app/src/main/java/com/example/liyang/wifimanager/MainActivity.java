package com.example.liyang.wifimanager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.test.suitebuilder.TestMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final int REFRESH_COMPLETE = 0X110;
    private SwipeRefreshLayout mSwipeLayout;
    private WifiManager wifiManager;
    List<ScanResult> list;
    Context context;
    private WifiInfo wifiInfo;
    private MyAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setLogo(R.drawable.back_24px);
        setSupportActionBar(toolbar);
        if(checkPermission()) {
            init();
        }
    }
    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case REFRESH_COMPLETE:
                    list=wifiManager.getScanResults();
                    adapter.refresh(list);
                    mSwipeLayout.setRefreshing(false);
                    break;

            }
        };
    };
    private  void init()
    {
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_ly);

        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeColors(Color.parseColor("#6495ED"), Color.parseColor("#32CD32"),
                Color.parseColor("#FF8C00"),Color.parseColor("#FF0000"));
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        openWifi();
        list = wifiManager.getScanResults();
        ListView wifilist = (ListView) findViewById(R.id.wifilist);
        if( list.size()==0)
        {
            Toast.makeText(this,"wifi未打开！",Toast.LENGTH_LONG).show();
        }else{
            Log.d("size",String.valueOf(list.size()));
            System.out.println("list的大小"+list.size());
            adapter = new MyAdapter(this,list);
            wifilist.setAdapter(adapter);
        }

        wifiInfo = wifiManager.getConnectionInfo();
        if(wifiInfo!=null) {
            TextView con_wifi = (TextView) findViewById(R.id.con_wifi);
            con_wifi.setText(wifiInfo.getSSID()+"   已连接");
            TextView con_level = (TextView) findViewById(R.id.con_level);
            int signal_strenth = 100 - Math.abs(wifiInfo.getRssi());
            con_level.setText(String.valueOf(signal_strenth));
            ImageView image = (ImageView) findViewById(R.id.con_wifi_icon);
            if (signal_strenth < 20) {
                image.setImageResource(R.drawable.ap_open_barely);
            } else if (signal_strenth < 40) {
                image.setImageResource(R.drawable.ap_open_weak);
            } else if (signal_strenth < 60) {
                image.setImageResource(R.drawable.ap_open_ok);
            } else {
                image.setImageResource(R.drawable.ap_open_max);
            }
            TextView con_IpAddress = (TextView) findViewById(R.id.con_IpAddress);
            con_IpAddress.setText("IP地址："+StringtoIp(wifiInfo.getIpAddress()));
            TextView con_LinkSpeed = (TextView) findViewById(R.id.con_LinkSpeed);
            con_LinkSpeed.setText("连接速度："+String.valueOf(wifiInfo.getLinkSpeed()));
        }

    }
    private void openWifi()
    {
        if(wifiManager.isWifiEnabled())
        {
            wifiManager.setWifiEnabled(true);
        }

    }
    private String StringtoIp(int ip)
    {
        return (ip & 0xFF)+"."+((ip>>8) & 0xFF)+"."+((ip>>16) & 0xFF)+"."+((ip>>24) & 0xFF);
    }
    public void onRefresh()
    {
        mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_item,menu);
        return true;
    }
    //检查位置权限  动态申请位置权限，然后调用回调函数。
    final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 111;
    private boolean checkPermission() {
        Log.d("hello","checkPermission");
        List<String> permissionsList = new ArrayList<String>();
        String[] permission = new String[2];
        if (checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permission[0] = Manifest.permission.ACCESS_FINE_LOCATION;
        }
        if (checkSelfPermission( Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permission[1] = Manifest.permission.ACCESS_COARSE_LOCATION;
        }
        if(permission[0] != null || permission[1] != null){
            Log.d("hello","regist Permission");
            requestPermissions(permission,REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        Log.d("hello","onRequestPermissionsResult");
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                        (permissions.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                                grantResults[1] == PackageManager.PERMISSION_GRANTED)){
                    Log.d("hello","permission allow");
                    Toast.makeText(this, "permission allow", Toast.LENGTH_LONG).show();
                    init();
                    //list is still empty
                }
                else {
                    // Permission Denied
                    Toast.makeText(this, "permission deny", Toast.LENGTH_LONG).show();
                    Log.d("hello","permission deny");
                }
                break;
        }
    }
    public boolean onOptionsItemSelected( MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_settings:break;
            case R.id.menu_about :break;
            case R.id.menu_quit:
                finish();
                break;
        }
        return true;
    }

}
