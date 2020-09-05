package com.firmground.evs.firmground.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import static com.firmground.evs.firmground.activity.ChatPageActivity.PlaceholderFragment.dialog;
import static com.firmground.evs.firmground.fragment.GroupFragment.wifi_checker;


public class WifiReceiver extends BroadcastReceiver {

    String TAG = getClass().getSimpleName();
    Context mContext;



    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;
        try
        {
            if (isOnline(context)) {
                dialog(true);
                wifi_checker=1;
                Log.e("keshav", "Online Connect Intenet "+wifi_checker);
                //Toast.makeText(context,"Online Connect Intenet",Toast.LENGTH_SHORT).show();
            } else {
                dialog(false);
                wifi_checker=0;
                Log.e("keshav", "Conectivity Failure !!! "+wifi_checker);
                //Toast.makeText(context,"Conectivity Failure !!!",Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        }
    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
    }
