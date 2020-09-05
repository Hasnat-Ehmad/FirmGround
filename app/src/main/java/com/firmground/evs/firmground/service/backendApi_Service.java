package com.firmground.evs.firmground.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.firmground.evs.firmground.R;
import com.firmground.evs.firmground.activity.LoginActivity;
import com.firmground.evs.firmground.activity.MainActivity;
import com.firmground.evs.firmground.database.MyDatabase;
import com.firmground.evs.firmground.webservice.TaskDelegate;
import com.firmground.evs.firmground.webservice.WebRequestCall;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hp on 3/22/2018.
 */

    // Handler that receives messages from the thread
public class backendApi_Service extends Service {

    MyDatabase mydb;

    final static int REQUEST_LOCATION = 199;
    private GoogleApiClient googleApiClient;
    LocationManager manager;

    int counter=0;
    SharedPreferences sharedPreferences;
    public String check_notify_id="";

    String id="0",notification_="";

    private Handler mHandler;
    // default interval for syncing data
    final long DEFAULT_SYNC_INTERVAL =  60 * 60 * 1000;

    // task to be run here
    private Runnable runnableService = new Runnable() {
        @Override
        public void run() {

            // Todo Location Already on  ... start
                syncData();
                // counter++;
                //Toast.makeText(getApplication(),"40 = "+counter, Toast.LENGTH_SHORT).show();
                // Repeat this runnable code block again every ... min

            mHandler.postDelayed(runnableService, DEFAULT_SYNC_INTERVAL);

             /*syncData();
             //counter++;
             //Toast.makeText(getApplication(),"40 = "+counter, Toast.LENGTH_SHORT).show();

             //Repeat this runnable code block again every ... min
               mHandler.postDelayed(runnableService, DEFAULT_SYNC_INTERVAL);*/
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create the Handler object
        mydb = new MyDatabase(getApplicationContext());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            mHandler = new Handler();
           // Execute a runnable task as soon as possible
            mHandler.post(runnableService);
            //stopSelf();

          return  START_REDELIVER_INTENT;// tells the OS to restart the service when destroyed!!
          //return  START_NOT_STICKY ;//system will not try to restart the service
          //return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("LongLogTag")
    private synchronized void syncData() {

        getAll_Meges(mydb.getdata_last_msg_id_user_wise(sharedPreferences.getString("userID","")));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
         //mHandler.removeCallbacksAndMessages(runnableService);
         mHandler.removeCallbacks(runnableService);  // stopping handler ("this" is the handler runnable you are stopping)
         //Toast.makeText(getApplication(),"on destroy ", Toast.LENGTH_SHORT).show();
    }

    public void getAll_Meges(String last_msg_id){

        if (last_msg_id.equals("0")){
            last_msg_id="0";
        }

        String url = getResources().getString(R.string.url) + "groupAllChatAfter";
        String params = "userID="+sharedPreferences.getString("userID","")+"&lastID="+last_msg_id;
        WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
            @Override
            public String TaskCompletionResult(String result) throws JSONException {

                JSONObject jsonObject = new JSONObject(result);

                if(jsonObject.getString("respCode").equals("200")){

                    /*
                        mydb.delete_record_match_detail();
                        mydb.delete_record_squad_detail();
                    */
                    mydb.delete_boots();
                    mydb.delete_facilities();
                    mydb.delete_restriction();
                    mydb.delete_locationImages();

                    //mydb.delete_contact();

                    JSONArray boots = jsonObject.getJSONArray("boots");
                    for (int i=0 ; i<boots.length() ; i++){

                        JSONObject c = boots.getJSONObject(i);

                        String bID     = c.getString("bID");
                        String bName   = c.getString("bName");
                        String bStatus = c.getString("bStatus");

                        mydb.insertdata_boots(""+bID,""+bName,
                                ""+bStatus);

                        //Toast.makeText(getApplicationContext(),c.getString("bStatus"),Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "File...:::: bStatus = " + c.getString("bStatus"));
                    }

                    JSONArray additionalFacilities= jsonObject.getJSONArray("additionalFacilities");
                    for (int i=0 ; i<additionalFacilities.length() ; i++){

                        JSONObject c = additionalFacilities.getJSONObject(i);

                        String fID     = c.getString("fID");
                        String fName   = c.getString("fName");
                        String fStatus = c.getString("fStatus");

                        mydb.insertdata_facilities(""+fID,""+fName,
                                ""+fStatus);

                        //Toast.makeText(getApplicationContext(),c.getString("fStatus"),Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "File...:::: fStatus = " + c.getString("fStatus"));
                    }

                    JSONArray restrictions        = jsonObject.getJSONArray("restrictions");
                    for (int i=0 ; i<restrictions.length() ; i++){

                        JSONObject c = restrictions.getJSONObject(i);

                        String rid    = c.getString("id");
                        String rName  = c.getString("restriction");
                        String rStatus= c.getString("resStatus");

                        mydb.insertdata_restriction(""+rid,""+rName,
                                ""+rStatus);

                        //Toast.makeText(getApplicationContext(),c.getString("resStatus"),Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "File...:::: resStatus = " + c.getString("resStatus"));
                    }

                    JSONArray locationImages        = jsonObject.getJSONArray("locationImages");
                    for (int i=0 ; i<locationImages.length() ; i++){

                        JSONObject c = locationImages.getJSONObject(i);

                        String id           = c.getString("id");
                        String locationID   = c.getString("locationID");
                        String locationImage= c.getString("locationImage");
                        String ImageType    = c.getString("ImageType");
                        String date         = c.getString("date");

                        mydb.insertdata_locationImages(""+locationID,""+locationImage,
                                ""+ImageType);
                        //Toast.makeText(getApplicationContext(),c.getString("resStatus"),Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "File...:::: resStatus = " + c.getString("date"));
                    }

                    JSONArray matchDetails        = jsonObject.getJSONArray("matchDetails");
                    for (int i=0 ; i<matchDetails.length() ; i++){

                        JSONObject c = matchDetails.getJSONObject(i);

                        String gameId       = c.getString("gameId");
                        String locationID   = c.getString("locationID");
                        String image        = c.getString("image");
                        String DateAdded    = c.getString("DateAdded");
                        String matchDate    = c.getString("matchDate");
                        String MatchTime    = c.getString("MatchTime");
                        String gId          = c.getString("gId");
                        String DayNight     = c.getString("DayNight");
                        String PitchNo      = c.getString("PitchNo");
                        String MatchCost    = c.getString("MatchCost");
                        String match_type   = c.getString("match_type");
                        String recurringType= c.getString("recurringType");
                        String gameType     = c.getString("gameType");
                        String referee     = c.getString("referee");
                        String note        = c.getString("note");
                        String currency    = c.getString("currency");
                        String aSide       = c.getString("aSide");
                        String kitColors   = c.getString("kitColors");
                        String substitutes = c.getString("substitutes");
                        String weather     = c.getString("weather");
                        String status      = c.getString("status");
                        String locationName= c.getString("locationName");
                        String addressLane1= c.getString("addressLane1");
                        String addressLane2= c.getString("addressLane2");
                        String lat         = c.getString("lat");
                        String lng         = c.getString("lng");
                        String addtionalFacilities= c.getString("addtionalFacilities");
                        String mrestrictions      = c.getString("restrictions");
                        String lStatus            = c.getString("lStatus");
                        String id                 = c.getString("id");
                        String pitchName          = c.getString("pitchName");
                        String ptichTurf          = c.getString("ptichTurf");
                        String allowBoots         = c.getString("allowBoots");
                        String pitchStatus        = c.getString("pitchStatus");
                        String matchDateFormated  = c.getString("matchDateFormated");
                        String MatchTimeFormated  = c.getString("MatchTimeFormated");

                        mydb.insertdata_match_details(""+gId,
                                ""+gameId,""+c.toString());
                        //Toast.makeText(getApplicationContext(),c.getString("MatchTimeFormated"),Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "File...:::: MatchTimeFormated = " + c.getString("MatchTimeFormated"));
                    }

                }else{
                    Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                }
                return result;
            }
        });
        webRequestCall.execute(url,"POST",params);
    }

    private void SavePreferences(String key, String value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
}