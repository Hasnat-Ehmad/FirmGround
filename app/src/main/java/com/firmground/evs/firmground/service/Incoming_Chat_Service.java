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

import com.firmground.evs.firmground.R;
import com.firmground.evs.firmground.activity.ChatPageActivity;
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
public class Incoming_Chat_Service extends Service {
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

    private Handler mHandler;
    // default interval for syncing data
    final long DEFAULT_SYNC_INTERVAL = 2 * 1000;

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
                            //Toast.makeText(getApplicationContext()," est_earning = "+est_earning,Toast.LENGTH_SHORT).show();

        if (sharedPreferences.contains("messageId")){
            messageId = sharedPreferences.getString("messageId","");
        }


        if (!ground_Id.equals("")){

            //http://192.168.100.14/FirmGround/REST/groupChat?gId=1
            final String url = getResources().getString(R.string.url) + "groupChat?gId="+ground_Id+"&lastID="+messageId+
                    "&userID="+sharedPreferences.getString("userID","");
            final String params = "";
            final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                @Override
                public String TaskCompletionResult(String result) throws JSONException {

                    JSONObject jsonObject = new JSONObject(result);

                    if(jsonObject.getString("respCode").equals("200")) {
                        JSONArray results = jsonObject.getJSONArray("results");

                        Intent intent = new Intent("android.intent.action.SmsReceiver");
                        intent.putExtra("results", results.toString());
                        getApplicationContext().sendBroadcast(intent);

                        //int array_lenth = results.length();

                    }else {
                        //Intent intent = new Intent("android.intent.action.SmsReceiver");
                        //intent.putExtra("message", jsonObject.getString("message"));
                        //getApplicationContext().sendBroadcast(intent);
                    }
                    return result;
                }
            });
            webRequestCall.execute(url,"GET",params);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnableService);

    }
    public void write1(String contact) {
        try
        {
            String mediaState = Environment.getExternalStorageState();
            if(mediaState.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {

                if (sharedPreferences.contains("file_counter")){
                    file_counter = sharedPreferences.getString("file_counter","0");

                }

                if (file_counter.equals("0")){

                    file_counter="1";

                    SavePreferences("file_counter"    ,file_counter);

                    output = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/FirmGround.txt"));

                    //Toast.makeText(getApplicationContext(),""+Environment.getExternalStorageDirectory() + "/Actifit_Contact_List.txt",Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(),"this is inside the mounted data",Toast.LENGTH_SHORT).show();
                    //output.write(contact.getBytes());
                    //output.flush();
                }else{
                    //output = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/Actifit_Contact_List.txt"));

                }
            }
            else
            {
                // error

            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (file_counter.equals("1")){

                    if (output != null){
                        // Toast.makeText(getApplicationContext(),"this is inside write data",Toast.LENGTH_SHORT).show();

                        output.write(contact.getBytes());
                        output.flush();
                    }
                }else {
                    File path = Environment.getExternalStorageDirectory();
                    File file = new File(path,"FirmGround.txt");

                    FileWriter fWriter = new FileWriter(file,true);

                    output = new FileOutputStream(file,true);
                    fWriter.append(contact);
                    fWriter.flush();
                    fWriter.close();
//                        fWriter.write(contact+"nn"+contact);//write data
//                        fWriter.flush();//flush writer
                    // fWriter.close();//close writer
                }

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    private void SavePreferences(String key, String value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences((getApplicationContext()));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
}