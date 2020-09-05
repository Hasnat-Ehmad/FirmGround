package com.firmground.evs.firmground.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import static com.firmground.evs.firmground.fragment.ChatFragment_No_Match.dialog;


public class WifiReceiver_no_Match extends BroadcastReceiver {

    String TAG = getClass().getSimpleName();
    Context mContext;


    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;
        try
        {
            if (isOnline(context)) {
                dialog(true);
                Log.e("keshav", "Online Connect Intenet ");
                //Toast.makeText(context,"Online Connect Intenet",Toast.LENGTH_SHORT).show();
            } else {
                dialog(false);
                Log.e("keshav", "Conectivity Failure !!! ");
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
