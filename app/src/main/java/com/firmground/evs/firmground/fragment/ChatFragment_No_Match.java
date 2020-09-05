package com.firmground.evs.firmground.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.firmground.evs.firmground.activity.LoginActivity;
import com.firmground.evs.firmground.activity.MainActivity;
import com.firmground.evs.firmground.activity.UpdateMatchActivity;
import com.firmground.evs.firmground.database.MyDatabase;
import com.firmground.evs.firmground.R;
import com.firmground.evs.firmground.broadcastreceiver.WifiReceiver_no_Match;
import com.firmground.evs.firmground.activity.GroupDetailActivity;
import com.firmground.evs.firmground.activity.UpdateGroupActivity;
import com.firmground.evs.firmground.adapter.Message_List_Adapter;
import com.firmground.evs.firmground.imagewebservice.JSONParser_Send_Chat_Image;
import com.firmground.evs.firmground.model.Message_List;
import com.firmground.evs.firmground.service.Incoming_Chat_Service;
import com.firmground.evs.firmground.splashscreen.SplashScreen;
import com.firmground.evs.firmground.webservice.TaskDelegate;
import com.firmground.evs.firmground.webservice.WebRequestCall;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.firmground.evs.firmground.activity.MainActivity.activity;
import static com.firmground.evs.firmground.fragment.GroupFragment.wifi_checker;


public class ChatFragment_No_Match extends Fragment {

     static ArrayList<Message_List> message_ArrayList =new ArrayList();
     static Message_List_Adapter message_list_adapter;

    private FragmentActivity fragmentActivity;

    ListView mListView;

    SharedPreferences sharedPreferences;

    public Intent service_chat;

    MyDatabase mydb;

    public static BroadcastReceiver wifireciver;

    public static int intercheck=0;

    public  static Cursor c;

    String group_Id,memberRole,group_name;
    ImageButton attach_icon,attach_recording;
    EditText messageArea;
    JSONParser_Send_Chat_Image jsonParser_send_chat_image;

    int audio_check=0;

    private MediaRecorder mRecorder;

    private  final String LOG_TAG = "AudioRecording";

    private  String mFileName = null;

    Timer T;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View rootView = inflater.inflate(R.layout.fragment_chat_fragment_no_match, container, false);
        View rootView = inflater.inflate(R.layout.pagerfragment_chat2, container, false); //calling the second fragment from the chatActivity

        final EditText ed_search = getActivity().findViewById(R.id.ed_search);
        final EditText mEditText = (EditText) rootView.findViewById(R.id.editText);
        ed_search.setText("");
        ed_search.setVisibility(View.GONE);
        ImageView search_img = getActivity().findViewById(R.id.search_img);
        search_img .setVisibility(View.GONE);

        jsonParser_send_chat_image = new JSONParser_Send_Chat_Image();

        attach_icon= rootView.findViewById(R.id.attach_icon);
        attach_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(),"attach Button Clicked",Toast.LENGTH_SHORT).show();
                if (checkPermissionForReadExtertalStorage()){
                    // 1. on Upload click call ACTION_GET_CONTENT intent
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    // 2. pick image only
                    intent.setType("image/*");
                    // 3. start activity
                    startActivityForResult(intent, 0);

                }else {
                    try {
                        requestPermissionForReadExtertalStorage();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        SavePreferences_int("audio_file_count",0);

        attach_recording= rootView.findViewById(R.id.attach_recording);
        attach_recording.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        requestAudio(v);

                            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                            //Vibrate for 500 milliseconds
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                //deprecated in API 26
                                vibrator.vibrate(100);
                            }


                        int SPLASH_TIME_OUT = 200;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                mRecorder = new MediaRecorder();
                                mRecorder.reset();

                                if (audio_check==1){

                                    T=new Timer();

                                    final int[] count = {0};
                                    T.scheduleAtFixedRate(new TimerTask() {
                                        @Override
                                        public void run() {
                                            getActivity().runOnUiThread(new Runnable()
                                            {
                                                @Override
                                                public void run()
                                                {

                                                    //Toast.makeText(getActivity(), "counter = "+count[0], Toast.LENGTH_LONG).show();
                                                    mEditText.setText("00:"+ count[0]);
                                                    count[0]++;
                                                }
                                            });
                                        }
                                    }, 000, 1000);

                                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                                    mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
                                    //audio_file_count = sharedPreferences.getInt("audio_file_count",0);
                                    Long tsLong = System.currentTimeMillis()/1000;
                                    String ts = tsLong.toString();
                                    mFileName += "/AudioRecording_"+ts+".3gp";
                                    // audio_file_count++;
                                    //SavePreferences_int("audio_file_count",audio_file_count);
                                    //mFileName += "/AudioRecording.3gp";

                                    mRecorder.setOutputFile(mFileName);
                                    try {
                                        mRecorder.prepare();
                                    } catch (IOException e) {
                                        Log.e(LOG_TAG, "prepare() failed");
                                    }
                                    mRecorder.start();
                                    //Toast.makeText(getActivity(), "Recording Started", Toast.LENGTH_LONG).show();
                                }
                            }
                        }, SPLASH_TIME_OUT);

                        break;
                    case MotionEvent.ACTION_UP:
                        mEditText.setText("");
                        T.cancel();

                        if (audio_check==1){
                            try{
                                mRecorder.stop();
                            }catch(RuntimeException stopException){
                                //handle cleanup here
                            }
                            mRecorder.release();
                            mRecorder = null;

                            image_real_path = mFileName;

                            //Toast.makeText(getActivity(), "Recording Stopped", Toast.LENGTH_LONG).show();

                            final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

                            final Random random=new Random();
                            final StringBuilder sb=new StringBuilder(4);

                            for(int i=0;i<4;++i)
                                sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));

                            SimpleDateFormat timeStampFormat = new SimpleDateFormat("hh:mm");
                            Date myDate = new Date();
                            final String time = timeStampFormat.format(myDate);

                            send_data_image_single(""+sb.toString(),""+time);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                                    //Vibrate for 500 milliseconds
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                                    } else {
                                        //deprecated in API 26
                                        vibrator.vibrate(100);
                                    }
                                }
                            }, 200);

                        }
                    default:
                        break;
                }
                return true;
            }
        });


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        mydb = new MyDatabase(getActivity());

        wifireciver = new WifiReceiver_no_Match();
        registerNetworkBroadcastForNougat();

         group_Id = getArguments().getString("group_Id");
         memberRole = getArguments().getString("memberRole");
         group_name = getArguments().getString("group_name");

        //Toast.makeText(getActivity(),"grounp_id = "+group_Id,Toast.LENGTH_SHORT).show();

        fragmentActivity = (FragmentActivity) activity;

        mListView = rootView.findViewById(R.id.chatsListView);

        message_ArrayList.clear();

        Toolbar toolbar         = rootView.findViewById(R.id.toolbar);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        ImageView add_group_detail = getActivity().findViewById(R.id.search_img);
        add_group_detail.setVisibility(View.VISIBLE);
        add_group_detail.setImageResource(R.drawable.ic_edit_black_18dp);
        add_group_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(activity,"click",Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                bundle.putString("group_Id",group_Id);

                if (memberRole.equals("SuperAdmin") || memberRole.equals("Admin")){
                    Intent intent = new Intent(getActivity(), UpdateGroupActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getActivity(), GroupDetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        ImageView add_group_img = getActivity().findViewById(R.id.add_group_img);
        add_group_img.setVisibility(View.GONE);


        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {//showSendButton();
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    //showAudioButton();
                }
            }
        });

        //=-=-=-=-=-=-=-=-=


        messageArea= rootView.findViewById(R.id.editText);
        ImageView floatingButton;

        //comented here

        mListView = rootView.findViewById(R.id.chatsListView);

        floatingButton = rootView.findViewById(R.id.floatingButton);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(),"floating Button Clicked",Toast.LENGTH_SHORT).show();
                if (messageArea.getText().toString().equals("")){
                    messageArea.setError("Empty");
                }
                else {

                    final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

                    final Random random=new Random();
                    final StringBuilder sb=new StringBuilder(4);

                    for(int i=0;i<4;++i)
                        sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));

                    SimpleDateFormat timeStampFormat = new SimpleDateFormat("hh:mm");
                    Date myDate = new Date();
                    final String filename = timeStampFormat.format(myDate);
                    Date currentTime = Calendar.getInstance().getTime();

                    Uri path = Uri.parse("" + R.drawable.ic_access_time_grey_24dp);

                    //Toast.makeText(context,"intercheck = "+intercheck,Toast.LENGTH_SHORT).show();

                    if (intercheck==1){

                        Message_List message_list = new Message_List(""+sb.toString(),"\uF017","",""+filename,
                                ""+messageArea.getText().toString(),"self","","",false);
                        message_ArrayList.add(message_list);

                        if (message_list_adapter == null){
                            message_list_adapter = new Message_List_Adapter(getActivity(),message_ArrayList);
                            mListView.setAdapter(message_list_adapter);
                            if (mListView!=null){
                                mListView.smoothScrollToPosition(message_list_adapter.getCount()-1);
                                mListView.setSelection(message_list_adapter.getCount());

                            }
                        }else {
                            message_list_adapter.notifyDataSetChanged();
                            if (mListView!=null){
                                mListView.smoothScrollToPosition(message_list_adapter.getCount()-1);
                                mListView.setSelection(message_list_adapter.getCount());
                            }
                        }

                        //http://192.168.100.14/FirmGround/REST/sendMessage?userId, gId, message
                        final String url = getResources().getString(R.string.url) + "sendMessage";

                        String dummy_id = sb.toString();

                        System.out.println("TAG Dummy = "+dummy_id);

                        final String params ="userId="+sharedPreferences.getString("userID","")+
                                "&gId="+group_Id+
                                "&message="+messageArea.getText().toString()+
                                "&messageId="+sb.toString();

                        final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                            @Override
                            public String TaskCompletionResult(String result) throws JSONException {

                                JSONObject jsonObject = new JSONObject(result);

                                if(jsonObject.getString("respCode").equals("200")) {

                                    //Toast.makeText(getActivity(),""+jsonObject.getString("message"),Toast.LENGTH_SHORT).show();

                                    String messageId     = jsonObject.getString("messageId");
                                    String tempMessageId = jsonObject.getString("tempMessageId");
                                    String temp_message  = jsonObject.getString("message");

                                    System.out.println("TAG adapter count = "+message_list_adapter.getCount());

                                    mydb.insertdata_chat(""+group_Id,""+sharedPreferences.getString("userID",""),
                                            ""+messageId,"✓✓","",
                                            ""+filename,""+temp_message,"self","read");

                                    if (message_list_adapter!=null){

                                        for (int i=0;i<message_list_adapter.getCount();i++){
                                            if (message_list_adapter.getItem(i).getMessage_id().equals(""+tempMessageId)){

                                                System.out.println("messageId = "+messageId+" item.getimagecheck = ");
                                                //Toast.makeText(getActivity(),message_list_adapter.getItem(i).getMessage_id()+" = "+ sb.toString(), Toast.LENGTH_SHORT).show();

                                                message_list_adapter.getItem(i).setMessage_id(""+messageId);
                                                message_list_adapter.getItem(i).setMessage(""+temp_message);
                                                Uri path = Uri.parse("" + R.drawable.ic_check_black_24dp);

                                                message_list_adapter.getItem(i).setImage("✓");

                                                   /* Message_List message_list = new Message_List(""+messageId,""+path.toString(),"",""+filename,""+messageArea.getText().toString(),"self","true");
                                                    message_ArrayList.set(i,message_list);*/
                                                //message_list_adapter.notifyDataSetChanged();//adapter state has change
                                            }
                                        }
                                    }

                                    messageArea.setFocusable(true);
                                    messageArea.setText("");

                                }else {
                                    //implement here databaseHelper class
                                    Toast.makeText(getActivity(),""+jsonObject.getString("message"),Toast.LENGTH_SHORT).show();

                                    mydb.insertdata(group_Id,//group_id
                                            sharedPreferences.getString("userID",""),//user_id
                                            sb.toString(),
                                            messageArea.getText().toString());

                                }
                                return result;
                            }
                        });
                        webRequestCall.execute(url,"POST",params);
                    }else {

                        Message_List message_list = new Message_List(""+sb.toString(),"\uF017","",""+filename,
                                ""+messageArea.getText().toString(),"self","","",false);
                        message_ArrayList.add(message_list);

                        if (message_list_adapter == null){
                            message_list_adapter = new Message_List_Adapter(getActivity(),message_ArrayList);
                            mListView.setAdapter(message_list_adapter);
                            if (mListView!=null)
                                mListView.setSelection(message_list_adapter.getCount());
                        }else {
                            message_list_adapter.notifyDataSetChanged();
                            if (mListView!=null)
                                mListView.setSelection(message_list_adapter.getCount());
                        }

                        mydb.insertdata(group_Id,//group_id
                                sharedPreferences.getString("userID",""),//user_id
                                sb.toString(),
                                messageArea.getText().toString());

                        messageArea.setText("");
                    }
                }
            }
        });


        service_chat = new Intent(getActivity(), Incoming_Chat_Service.class);
        getActivity().stopService(service_chat);
        getActivity().startService(service_chat);


        /*int SPLASH_TIME_OUT = 1000;
        final int[] i = {0};
        final Handler handler = new Handler();
        Runnable runnable1  = null;
        final Runnable finalRunnable = runnable1;
        runnable1 = new Runnable() {

            @Override
            public void run() {
                mListView.smoothScrollToPosition(message_list_adapter.getCount() - 1);
                if(i[0] > 0){
                    handler.removeCallbacks(finalRunnable);
                }
                i[0]++;
            }
        };
        handler.postDelayed(runnable1, SPLASH_TIME_OUT);*/


        //=-=-=-=-=-==-=-=-


        //ListView mRecyclerView = (ListView) rootView.findViewById(R.id.chatsListView);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        //linearLayoutManager.setStackFromEnd(true);
        //mRecyclerView.setLayoutManager(linearLayoutManager);
        //loadChats();

       /* message_ArrayList.clear();

        for(int i=0; i<10;i++){
            String message = "Message "+i;
            mEditText.setText("");
            Log.d("send msg", message);
            String image ="";
            String name = "";
            String date_time ="";
            String message1 = message;
            String sender;
            if(i%2 == 0) {
                sender  = "own";
            }else{
                sender = "other";
            }

            Message_List message_list = new Message_List(""+image,""+name,""+date_time,""+message1,""+sender);
            message_ArrayList.add(message_list);
        }
        message_list_adapter = new Message_List_Adapter(getActivity(),message_ArrayList);
        mListView.setAdapter(message_list_adapter);*/

        final ImageView add_match_img = getActivity().findViewById(R.id.add_match_img);
        if (memberRole.equals("SuperAdmin") || memberRole.equals("Admin")){
            add_match_img.setVisibility(View.VISIBLE);
            add_match_img.setImageResource(R.drawable.grey_player_icon);
            add_match_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(activity,"click",Toast.LENGTH_SHORT).show();

                    Bundle bundle = new Bundle();

                    bundle.putString("group_Id"   , group_Id);
                    bundle.putString("memberRole" , memberRole);
                    bundle.putString("group_name" , group_name);

                    AddMatchFragment addMatchFragment = new AddMatchFragment();
                    addMatchFragment.setArguments(bundle);

                          /*FragmentManager fm = getFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.replace(R.id.frame_layout, addMatchFragment);
                            ft.addToBackStack(null);
                            ft.commit();*/

                    if (wifi_checker==1){
                        Intent intent = new Intent(activity, UpdateMatchActivity.class);
                        intent.putExtras(bundle);
                        activity.startActivity(intent);
                    }else {
                        Toast.makeText(getActivity(),"Connectivity failure !!!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            add_match_img.setVisibility(View.GONE);
        }
        return rootView;
    }

    public void requestAudio(final View view) {
        Permissions.check(getActivity(), Manifest.permission.RECORD_AUDIO, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                //Toast.makeText(getActivity(), "Phone granted.", Toast.LENGTH_SHORT).show();
                audio_check=1;
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);

                requestAudio(view);
            }
        });
    }

    public static void dialog(boolean value){
        if(value){
            int SPLASH_TIME_OUT = 2000;
            new Handler().postDelayed(new Runnable() {

                /*
                 * Showing splash screen with a timer. This will be useful when you
                 * want to show case your app logo / company
                 */

                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    // Toast.makeText(context,"inside the function",Toast.LENGTH_SHORT).show();
                    final String url = activity.getResources().getString(R.string.url) +"dummyHit";
                    final String params = "";
                    final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                        @Override
                        public String TaskCompletionResult(String result) throws JSONException {

                            JSONObject jsonObject = new JSONObject(result);

                            if(jsonObject.getString("respCode").equals("200")) {
                                //Toast.makeText(context,"inside the dummyHit",Toast.LENGTH_SHORT).show();
                                intercheck=1;

                                MyDatabase mydb = new MyDatabase(activity);
                                c = mydb.getdata();
                                if (c.getCount()>=0)
                                {
                                    if (c.moveToFirst())
                                    {
                                        do
                                        {
                                            String id     = c.getString(0);
                                            String grp_id = c.getString(1);
                                            String user_id= c.getString(2);
                                            String msg_id = c.getString(3);
                                            String msg=c.getString(4);

                                            Log.e("YYYY", "id = "+id+
                                                    " grp_id = "+grp_id+
                                                    " user_id = "+user_id+
                                                    " msg_id = "+msg_id+
                                                    " msg = "+msg);

                                            final String url_SEND = activity.getResources().getString(R.string.url) +"sendMessage";
                                            final String params ="userId="+user_id+
                                                    "&gId="+grp_id+
                                                    "&message="+msg+
                                                    "&messageId="+msg_id;

                                            final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                                                @Override
                                                public String TaskCompletionResult(String result) throws JSONException {

                                                    JSONObject jsonObject = new JSONObject(result);

                                                    if(jsonObject.getString("respCode").equals("200")) {
                                                        //Toast.makeText(context,"inside the sendMessage",Toast.LENGTH_SHORT).show();
                                                        //Toast.makeText(getActivity(),""+jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                                                        String messageId     = jsonObject.getString("messageId");
                                                        String tempMessageId = jsonObject.getString("tempMessageId");
                                                        String temp_message  = jsonObject.getString("message");
                                                        String cTime  = jsonObject.getString("cTime");

                                                        System.out.println("TAG adapter count = "+message_list_adapter.getCount());

                                                        if (message_list_adapter!=null){

                                                            for (int i=0;i<message_list_adapter.getCount();i++){
                                                                if (message_list_adapter.getItem(i).getMessage_id().equals(""+tempMessageId)){

                                                                    //Toast.makeText(getActivity(),message_list_adapter.getItem(i).getMessage_id()+" = "+ sb.toString(), Toast.LENGTH_SHORT).show();

                                                                    message_list_adapter.getItem(i).setMessage_id(""+messageId);
                                                                    message_list_adapter.getItem(i).setMessage(""+temp_message);
                                                                    Uri path = Uri.parse("" + R.drawable.ic_check_black_24dp);

                                                                    message_list_adapter.getItem(i).setImage("✓");
                                                                }
                                                            }
                                                        }
                                                        //messageArea.setFocusable(true);
                                                        //messageArea.setText("");
                                                    }else {
                                                        //implement here databaseHelper class
                                                    }
                                                    return result;
                                                }
                                            });
                                            webRequestCall.execute(url_SEND,"POST",params);

                                        }while (c.moveToNext());
                                        mydb.delete_record();
                                    }
                                }
                                //Toast.makeText(context,""+jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                            }else {
                                intercheck=0;
                            }
                            return result;
                        }
                    });
                    webRequestCall.execute(url,"GET",params);
                    // close this activity
                }
            }, SPLASH_TIME_OUT);
        }else {
            intercheck=0;
        }
    }
    private static void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            activity.registerReceiver(wifireciver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.registerReceiver(wifireciver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }else {
            activity.registerReceiver(wifireciver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }
    protected void unregisterNetworkChanges() {
        try {
            activity.unregisterReceiver(wifireciver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            int result = getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }else {
            //int result = getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return true;
        }
    }
    int READ_STORAGE_PERMISSION_REQUEST_CODE=1;
    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == Activity.RESULT_OK && data != null) {
            String realPath;
            // SDK < API11
            if (Build.VERSION.SDK_INT < 11)
                realPath = RealPathUtil_gp.getRealPathFromURI_BelowAPI11(getActivity(), data.getData());

                // SDK >= 11 && SDK < 19
            else if (Build.VERSION.SDK_INT < 19)
                realPath = RealPathUtil_gp.getRealPathFromURI_API11to18(getActivity(), data.getData());
                // SDK > 19 (Android 4.4)
            else
                realPath = RealPathUtil_gp.getRealPathFromURI_API19(getActivity(), data.getData());

            setmatchpic(Build.VERSION.SDK_INT, data.getData().getPath(), realPath);

        }
    }
    String image_real_path="";
    Bitmap bitmap = null;
    Uri uriFromPath;
    private void setmatchpic(int sdk, String uriPath, String realPath) {

        File file = new File(realPath);

        uriFromPath = Uri.fromFile(file);

        // you have two ways to display selected image
        // ( 1 ) imageView.setImageURI(uriFromPath);
        // ( 2 ) imageView.setImageBitmap(bitmap);

        try {
            bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uriFromPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //img_group_icon.setImageBitmap(bitmap);

        Log.d("HMKCODE", "Build.VERSION.SDK_INT:" + sdk);
        Log.d("HMKCODE", "URI Path:" + uriPath);
        Log.d("HMKCODE", "Real Path: " + realPath);

        image_real_path = file.getAbsolutePath();
        compressImage(image_real_path);
    }
    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth  = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth  = (int) (imgRatio * actualWidth);
                actualHeight = (int)  maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth  = (int)  maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth  = (int) maxWidth;
            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX  = actualWidth / (float) options.outWidth;
        float ratioY  = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);
            //write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Toast.makeText(MainActivity.this, "filename = "+filename, Toast.LENGTH_LONG).show();
        image_real_path=filename;

        final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(4);

        for(int i=0;i<4;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));

        SimpleDateFormat timeStampFormat = new SimpleDateFormat("hh:mm");
        Date myDate = new Date();
        final String time = timeStampFormat.format(myDate);

        send_data_image_single(sb.toString(),time);

        return filename;
    }
    public String getFilename() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "FirmGround/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }
    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getActivity().getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
    private void send_data_image_single(final String tempmsgid,final String senttime) {
        new AsyncTask<Void, Integer, Boolean>() {
            ProgressDialog progressDialog;
            JSONObject results;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Save Data...");
                progressDialog.setCancelable(true);
                progressDialog.show();

                Message_List message_list = new Message_List(
                        ""+tempmsgid,"\uF017",
                        "",""+senttime,
                        ""+messageArea.getText().toString(),
                        "self",""+uriFromPath,""+image_real_path,false);
                message_ArrayList.add(message_list);

                if (message_list_adapter == null){
                    message_list_adapter = new Message_List_Adapter(getActivity(),message_ArrayList);
                    mListView.setAdapter(message_list_adapter);
                    if (mListView!=null){
                        mListView.smoothScrollToPosition(message_list_adapter.getCount()-1);
                        mListView.setSelection(message_list_adapter.getCount());
                    }
                        //mListView.smoothScrollToPosition(message_list_adapter.getCount()-1);
                }else {
                    message_list_adapter.notifyDataSetChanged();
                    if (mListView!=null){
                        mListView.smoothScrollToPosition(message_list_adapter.getCount()-1);
                        mListView.setSelection(message_list_adapter.getCount());
                    }
                        //mListView.smoothScrollToPosition(message_list_adapter.getCount()-1);
                }
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {

                    JSONObject jsonObject=jsonParser_send_chat_image.uploadImage(image_real_path,""+group_Id,""+messageArea.getText().toString(),""+tempmsgid,"",getActivity());

                    image_real_path="";
                    mFileName="";
                    uriFromPath=null;
                    if (jsonObject != null) {
                        if(jsonObject.getString("respCode").equals("200")){
                            results = jsonObject;
                        }
                        return jsonObject.getString("respCode").equals("200");
                    }
                } catch (JSONException e) {
                    Log.i("TAG", "Error : " + e.getLocalizedMessage());
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                try
                {
                    if (aBoolean) {
                        image_real_path="";
                        dialog_fuction(results);
                        //Toast.makeText(getActivity(),"Profile Saved!",Toast.LENGTH_SHORT).show();
                        //getFragmentManager().popBackStack();
                    }
                    else{
                        image_real_path="";
                        Toast.makeText(getActivity(), "Failed To Save Data, Try Again!", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }finally {
                    progressDialog.dismiss();
                }
            }
        }.execute();
    }
    public void dialog_fuction(JSONObject results) throws JSONException {
        // Toast.makeText(getActivity(), "inside the image function", Toast.LENGTH_LONG).show();

        JSONObject jsonObject = new JSONObject(String.valueOf(results));

        if(jsonObject.getString("respCode").equals("200")) {

            //Toast.makeText(getActivity(),""+jsonObject.getString("message"),Toast.LENGTH_SHORT).show();

            String messageId = jsonObject.getString("messageId");
            String tempMessageId = jsonObject.getString("tempMessageId");
            String temp_message = jsonObject.getString("message");

               /* mydb.insertdata_chat("" + chatPageActivity.send_group_Id(),
                        "" + sharedPreferences.getString("userID", ""),
                        "" + messageId, "✓✓", "",
                        "" + filename, "" + temp_message, "self");*/

            System.out.println("TAG adapter count = " + message_list_adapter.getCount());

            if (message_list_adapter != null) {

                for (int i = 0; i < message_list_adapter.getCount(); i++) {
                    if (message_list_adapter.getItem(i).getMessage_id().equals("" + tempMessageId)) {

                        System.out.println("messageId = " + messageId + " item.getimagecheck = ");
                        //Toast.makeText(getActivity(),message_list_adapter.getItem(i).getMessage_id()+" = "+ sb.toString(), Toast.LENGTH_SHORT).show();

                        message_list_adapter.getItem(i).setMessage_id("" + messageId);
                        message_list_adapter.getItem(i).setMessage("" + temp_message);
                        Uri path = Uri.parse("" + R.drawable.ic_check_black_24dp);

                        message_list_adapter.getItem(i).setImage("✓");

                                                    /*Message_List message_list = new Message_List(""+messageId,""+path.toString(),"",""+filename,""+messageArea.getText().toString(),"self","true");
                                                      message_ArrayList.set(i,message_list);*/
                        //message_list_adapter.notifyDataSetChanged();//adapter state has change
                    }
                }
            }
            messageArea.setFocusable(true);
            messageArea.setText("");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SmsReceiver");
        activity.registerReceiver(ReceivefromService, filter);
       if (wifi_checker==0){

           message_ArrayList.clear();
           //Toast.makeText(context,"inside the dummyHit",Toast.LENGTH_SHORT).show();

           MyDatabase mydb = new MyDatabase(activity);
           Cursor c = mydb.getdata_chat(group_Id);
           if (c.getCount()>=0)
           {
               if (c.moveToFirst())
               {
                   do
                   {
                       String grp_id         = c.getString(0);
                       String user_id        = c.getString(1);
                       String msg_id         = c.getString(2);
                       String msg_image      = c.getString(3);
                       String fullname       = c.getString(4);
                       String senttime       = c.getString(5);
                       String chat_msg       = c.getString(6);
                       String chat_user_check= c.getString(7);

                       Log.e("YYYY", " grp_id         = "+grp_id+
                               " user_id        = "+user_id+
                               " msg_id         = "+msg_id+
                               " msg_image      = "+msg_image+
                               " fullname       = "+fullname+
                               " senttime       = "+senttime+
                               " chat_msg       = "+chat_msg+
                               " chat_user_check= "+chat_user_check);


                       Message_List message_list = new Message_List(""+msg_id,"✓✓",""+fullname,""+senttime,
                               ""+chat_msg,""+chat_user_check,"","",false);
                       message_ArrayList.add(message_list);

                   }while (c.moveToNext());
                   //mydb.delete_record_groups();
                   if (sharedPreferences.getString("messageId","").equals("0")){
                       message_list_adapter = new Message_List_Adapter(getActivity(),message_ArrayList);
                       if (mListView!=null)
                           mListView.setAdapter(message_list_adapter);
                       mListView.setSelection(message_list_adapter.getCount());
                   } else {
                       if (message_list_adapter==null){
                           message_list_adapter = new Message_List_Adapter(getActivity(),message_ArrayList);
                           if (mListView!=null)
                               mListView.setAdapter(message_list_adapter);
                           mListView.setSelection(message_list_adapter.getCount());
                       }else {
                           message_list_adapter.notifyDataSetChanged();
                           if (mListView!=null)
                               mListView.setSelection(message_list_adapter.getCount());
                       }
                   }

                   SavePreferences("messageId"    ,messageId);
               }
           }

       }
    }
    @Override
    public void onPause() {
        super.onPause();
        try {
            activity.unregisterReceiver(ReceivefromService);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Receiver not registered")) {
                Log.i("TAG","Tried to unregister the reciver when it's not registered");
            }
            else
            {
                throw e;
            }
        }
        //Toast.makeText(getActivity(),"onpause ",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        message_ArrayList.clear();
        getActivity().stopService(service_chat);
        SavePreferences("messageId"    ,"0");

        unregisterNetworkChanges();
    }
    @Override
    public void onDetach() {
        super.onDetach();
        message_ArrayList.clear();
        getActivity().stopService(service_chat);
        SavePreferences("messageId"    ,"0");
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        message_ArrayList.clear();
        getActivity().stopService(service_chat);
        SavePreferences("messageId"    ,"0");
    }

    private void SavePreferences(String key, String value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences((getActivity().getApplicationContext()));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void SavePreferences_int(String key, int value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    String message ="",user_check="",api_call_chat_service="",else_check="",messageId="",sentTime="",full_name="",results="";
    int array_lenth=0;
    private BroadcastReceiver ReceivefromService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //get the data using the keys you entered at the service
            //Toast.makeText(getActivity(),"this received date = ",Toast.LENGTH_SHORT).show();

            if (wifi_checker==1){

                 results               = intent.getStringExtra("results"        );
                 /*
                 message               = intent.getStringExtra("message"              );
                 sentTime              = intent.getStringExtra("sentTime"             );
                 user_check            = intent.getStringExtra("user_check"           );
                 full_name             = intent.getStringExtra("full_name"            );
                 api_call_chat_service = intent.getStringExtra("api_call_chat_service");
                 else_check            = intent.getStringExtra("else_check"           );
                                                                                      */
                try {
                    JSONArray results_array = new JSONArray(results);

                    for(int i = 0; i < results_array.length(); i++) {
                        JSONObject c = results_array.getJSONObject(i);

                        messageId = c.getString("messageId");
                        String image="";
                        if (!c.getString("image").equals("")){
                            image = getActivity().getResources().getString(R.string.imageBaseUrl)+c.getString("image");
                        }
                        message    = c.getString("message");
                        // gId              = c.getString("gId");
                        //membershipId     = c.getString("membershipId");

                        sentTime         = c.getString("sentTime_org");
                        //sentTime         = c.getString("sentTime");
                        //status           = c.getString("status");

                        String fname;
                        JSONObject userNameData = c.getJSONObject("userNameData");
                        fname = userNameData.getString("fname");

                        //System.out.println("TAG"+fname);
                        //Toast.makeText(getApplicationContext()," fname = "+fname,Toast.LENGTH_SHORT).show();

                        String pendingView      = c.getString("pendingView");
                        final String user_check = c.getString("user_check");

                        if (sharedPreferences.getString("messageId","").equals("0")){

                            Message_List message_list = new Message_List(""+messageId,"✓✓",""+fname,""+sentTime,
                                    ""+message,""+user_check,""+image,"",false);
                            message_ArrayList.add(message_list);

                            int msg_id = Integer.parseInt(mydb.getdata_chat_check_data(group_Id));

                            if (Integer.parseInt(messageId)<=msg_id){

                            }else{
                                mydb.insertdata_chat(""+group_Id,""+sharedPreferences.getString("userID",""),
                                        ""+messageId,"✓✓",""+fname,
                                        ""+sentTime,""+message,""+user_check,"read");
                            }
                        }else {
                            if (user_check.equals("other")){

                                Message_List message_list = new Message_List(""+messageId,"",""+fname,""+sentTime,
                                        ""+message,""+user_check,""+image,"",false);
                                message_ArrayList.add(message_list);

                                int msg_id = Integer.parseInt(mydb.getdata_chat_check_data(group_Id));

                                if (Integer.parseInt(messageId)<=msg_id){

                                }else{
                                    mydb.insertdata_chat(""+group_Id,""+sharedPreferences.getString("userID",""),
                                            ""+messageId,"✓✓",""+fname,
                                            ""+sentTime,""+message,""+user_check,"read");
                                }

                            }
                        }
                    }

                    if (sharedPreferences.getString("messageId","").equals("0")){
                        message_list_adapter = new Message_List_Adapter(getActivity(),message_ArrayList);
                        if (mListView!=null)
                            mListView.setAdapter(message_list_adapter);
                        mListView.setSelection(message_list_adapter.getCount());
                    } else {
                        message_list_adapter.notifyDataSetChanged();
                        if (mListView!=null)
                            mListView.setSelection(message_list_adapter.getCount());
                    }

                    SavePreferences("messageId"    ,messageId);

                    int SPLASH_TIME_OUT = 0000;
                    final int[] i = {0};
                    final Handler handler = new Handler();
                    Runnable runnable1  = null;
                    final Runnable finalRunnable = runnable1;
                    runnable1 = new Runnable() {

                        @Override
                        public void run() {
                            message_list_adapter.notifyDataSetChanged();
                            if (mListView!=null)
                                mListView.setSelection(message_list_adapter.getCount());
                            if(i[0] > 0){
                                handler.removeCallbacks(finalRunnable);
                            }
                            i[0]++;
                        }
                    };
                    handler.postDelayed(runnable1, SPLASH_TIME_OUT);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mydb.setdata_chat_msg_read_status(""+group_Id,""+sharedPreferences.getString("userID",""));

            }
        }
    };

}

class RealPathUtil_gp {

    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }
    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if(cursor != null){
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri){
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index
                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
