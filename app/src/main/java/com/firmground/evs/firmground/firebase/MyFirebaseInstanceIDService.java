package com.firmground.evs.firmground.firebase;


import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by HP on 4/4/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseInsIDService";

    @Override
    public void onTokenRefresh() {
        //get updated token
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,"Refresh Token: "+refreshToken);


        //you can save token into third party server to do anything you want
    }
}
