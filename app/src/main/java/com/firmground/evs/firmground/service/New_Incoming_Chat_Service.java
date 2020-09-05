package com.firmground.evs.firmground.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.firmground.evs.firmground.R;
import com.firmground.evs.firmground.activity.ChatPageActivity;
import com.firmground.evs.firmground.activity.LoginActivity;
import com.firmground.evs.firmground.activity.MainActivity;
import com.firmground.evs.firmground.database.MyDatabase;
import com.firmground.evs.firmground.webservice.TaskDelegate;
import com.firmground.evs.firmground.webservice.WebRequestCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import static com.firmground.evs.firmground.fragment.GroupFragment.ground_Id;


/**
 * Created by hp on 3/22/2018.
 */

    // Handler that receives messages from the thread
public class New_Incoming_Chat_Service extends Service {
   public static NotificationCompat.Builder mBuilder ; public static NotificationManager notifManager;

    int counter=0;
    String order_id;
    SharedPreferences sharedPreferences;
    public String file_counter="0";
    FileOutputStream output = null;
    ChatPageActivity chatPageActivity;
    String  messageId="0";

    public String api_call_chat_service="1";
    String else_check = "1";

    Context context;

    MyDatabase mydb;

    private Handler mHandler;
    // default interval for syncing data
    final long DEFAULT_SYNC_INTERVAL = 5 * 1000;

    // task to be run here
    private Runnable runnableService = new Runnable() {
        @Override
        public void run() {

            counter++;
            syncData();
            //Toast.makeText(getApplication(),"40 = "+counter, Toast.LENGTH_SHORT).show();
            // Repeat this runnable code block again every ... min
            mHandler.postDelayed(runnableService, DEFAULT_SYNC_INTERVAL);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create the Handler object
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        chatPageActivity = new ChatPageActivity();
        context = this;

        mydb = new MyDatabase(context);

            mHandler = new Handler();
           // Execute a runnable task as soon as possible
            mHandler.post(runnableService);
            //stopSelf();
        return  START_NOT_STICKY ;

       // return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private synchronized void syncData() {
          // call your rest service here
         //Toast.makeText(getApplicationContext()," order_id = "+order_id,Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext()," counter = "+counter,Toast.LENGTH_SHORT).show();

        String url = getResources().getString(R.string.url) + "getAllGroupsMessages";
        String params = "userID="+sharedPreferences.getString("userID","")+"&lastID="+mydb.getdata_last_msg_id_user_wise(sharedPreferences.getString("userID",""));
        WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
            @Override
            public String TaskCompletionResult(String result) throws JSONException {

                JSONObject jsonObject = new JSONObject(result);
                if(jsonObject.getString("respCode").equals("200")){

                    //Toast.makeText(LoginActivity.this,"Start Checking",Toast.LENGTH_SHORT).show();

                    JSONArray results = jsonObject.getJSONArray("results");
                    for (int i=0;i<results.length();i++){

                        JSONObject c = results.getJSONObject(i);

                        String messageId   = c.getString("messageId");
                        String gId         = c.getString("gId");
                        String membershipId= c.getString("membershipId");
                        String message     = c.getString("message");
                        String sentTime_org= c.getString("sentTime_org");
                        String sentTime    = c.getString("sentTime");
                        String pendingView = c.getString("pendingView");
                        String status      = c.getString("status");

                        JSONObject userNameData = c.getJSONObject("userNameData");

                        String UserID         = userNameData.getString("UserID");
                        String memberRole     = userNameData.getString("memberRole");
                        String dateAdded      = userNameData.getString("dateAdded");
                        String userGroupStatus= userNameData.getString("userGroupStatus");
                        String fname          = userNameData.getString("fname");
                        String lname          = userNameData.getString("lname");
                        String full_name      = userNameData.getString("full_name");
                        String username       = userNameData.getString("username");

                        String user_check = c.getString("user_check");

                        //Log.d("TAG", "File...:::: user_check = " + user_check);

                        mydb.insertdata_chat(""+gId,""+sharedPreferences.getString("userID",""),
                                ""+messageId,"✓✓",""+full_name,
                                ""+sentTime,""+message,""+user_check,"unread");

                    }

                    if (results.length()>0){
                        Intent intent = new Intent("android.intent.action.NewSmsReceiver");
                        getApplicationContext().sendBroadcast(intent);
                    }

                    //Toast.makeText(LoginActivity.this,"End Checking",Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                }
                return result;
            }
        });
        webRequestCall.execute(url,"POST",params);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnableService);

    }
    private void SavePreferences(String key, String value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences((getApplicationContext()));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
}