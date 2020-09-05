package com.firmground.evs.firmground.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.firmground.evs.firmground.database.MyDatabase;
import com.firmground.evs.firmground.R;
import com.firmground.evs.firmground.broadcastreceiver.WifiReceiver;
import com.firmground.evs.firmground.adapter.InListRecyclerViewAdapter;
import com.firmground.evs.firmground.adapter.Message_List_Adapter;
import com.firmground.evs.firmground.adapter.OutListRecyclerViewAdapter;
import com.firmground.evs.firmground.adapter.SquadListRecyclerViewAdapter;
import com.firmground.evs.firmground.circle_image.CircleImageView;
import com.firmground.evs.firmground.imagewebservice.JSONParser_Send_Chat_Image;
import com.firmground.evs.firmground.model.In_list;
import com.firmground.evs.firmground.model.Message_List;
import com.firmground.evs.firmground.model.Out_list;
import com.firmground.evs.firmground.model.Squad_list;
import com.firmground.evs.firmground.service.Incoming_Chat_Service;
import com.firmground.evs.firmground.imagewebservice.JSONParser_Upload_Group;
import com.firmground.evs.firmground.webservice.TaskDelegate;
import com.firmground.evs.firmground.webservice.WebRequestCall;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.squareup.picasso.Picasso;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.firmground.evs.firmground.activity.ChatPageActivity.PlaceholderFragment.message_ArrayList;
import static com.firmground.evs.firmground.activity.ChatPageActivity.PlaceholderFragment.service_chat;
import static com.firmground.evs.firmground.activity.MainActivity.activity;
import static com.firmground.evs.firmground.fragment.GroupFragment.wifi_checker;

public class ChatPageActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{


    public static SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    public static ViewPager mViewPager;
    SpringDotsIndicator springDotsIndicator;

    SharedPreferences sharedPreferences;

    public static Timer mTimer1_popup;
    public static TimerTask mTt1_popup;
    public static Handler mTimerHandler_popup = new Handler();
    public static TextView tv_toolbar_title;

    public String send_user_role(){
        Bundle bundle = getIntent().getExtras();
        final String memberRole = bundle.getString("memberRole");
        return memberRole;
    }
    public String send_game_Id()  {
        Bundle bundle = getIntent().getExtras();
        String game_Id= bundle.getString("game_Id"   );
        return game_Id;
    }
    public String send_group_Id() {
        Bundle bundle  = getIntent().getExtras();
        String group_Id= bundle.getString("group_Id"  );
        return group_Id;
    }
    public String send_group_name() {
        Bundle bundle  = getIntent().getExtras();
        String group_name= bundle.getString("group_name"  );
        return group_name;
    }
    public void call_calendar(String location,long date){

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");

        Calendar cal = Calendar.getInstance();
        long startTime_ = cal.getTimeInMillis();
        long endTime_ = cal.getTimeInMillis()  + 60 * 60 * 1000;

        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime_);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,date);
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

        intent.putExtra(CalendarContract.Events.TITLE, ""+tv_toolbar_title.getText().toString());
        intent.putExtra(CalendarContract.Events.DESCRIPTION,  "You have a match");
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, ""+location);
        intent.putExtra(CalendarContract.Events.RRULE, "FREQ=DAILY;COUNT=10");

        startActivity(intent);
    }
    public String send_member_role() {
        Bundle bundle  = getIntent().getExtras();
        String group_Id= bundle.getString("memberRole"  );
        return group_Id;
    }
    public String send_image() {
        Bundle bundle = getIntent().getExtras();
        String group_Image = bundle.getString("game_Image");
        return group_Image;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTimer_popup();
        //Toast.makeText(ChatPageActivity.this,"onResume",Toast.LENGTH_SHORT).show();
        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        tv_toolbar_title.setText(""+groupname);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer_popup();
        stopService(service_chat);
        SavePreferences("messageId"    ,"0");
        message_ArrayList.clear();
        //Toast.makeText(ChatPageActivity.this,"messageId = "+sharedPreferences.getString("messageId",""),Toast.LENGTH_SHORT).show();
    }

    private void SavePreferences(String key, String value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences((getApplicationContext()));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void startTimer_popup(){

        mTimer1_popup = new Timer();
        mTt1_popup = new TimerTask() {
            public void run() {
                mTimerHandler_popup.post(new Runnable() {
                    public void run(){
                        //call the API here inside a function
                        //Call_Api();
                    }
                });
            }
        };

        mTimer1_popup.schedule(mTt1_popup, 2*1000/*5*60*1000*/, 5*1000);
    }
    private void stopTimer_popup(){
        if(mTimer1_popup != null){
            mTimer1_popup.cancel();
            mTimer1_popup.purge();
        }
    }
    public void Call_Api(){


        //http://192.168.100.14/FirmGround/REST/groupDetail?gId=1&userID=2&gameId=23
        final String url = getResources().getString(R.string.url) + "groupDetail?"+
                "gId="+send_group_Id()+
                "&userID="+sharedPreferences.getString("userID","")+
                "&gameId="+send_game_Id();
        final String params = "";
        final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
            @Override
            public String TaskCompletionResult(String result) throws JSONException {

                JSONObject jsonObject = new JSONObject(result);

                if(jsonObject.getString("respCode").equals("200")) {
                    JSONArray chat = jsonObject.getJSONArray("chat");

                    if (chat.length()>0){

                        for(int i = 0; i < chat.length(); i++) {
                            JSONObject c = chat.getJSONObject(i);

                            String messageId = c.getString("messageId");
                            String gId = c.getString("gId");
                            String membershipId = c.getString("membershipId");
                            String message = c.getString("message")+"\n";
                            String sentTime = c.getString("sentTime");
                            String pendingView = c.getString("pendingView");
                            String status = c.getString("status");
                            //Toast.makeText(getActivity(),"GroupName = "+GroupName, Toast.LENGTH_SHORT).show();
                           // Toast.makeText(getApplicationContext(),"message = "+message, Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        //Toast.makeText(getApplicationContext(),"No Chat found ", Toast.LENGTH_SHORT).show();
                    }

                    JSONObject results = jsonObject.getJSONObject("matchDetail");

                    String gameId      = results.getString("gameId");
                    String LocationName= results.getString("locationName");
                    String image       = results.getString("image");
                    String DateAdded   = results.getString("DateAdded");
                    String matchDate   = results.getString("matchDateFormated");
                    String MatchTime   = results.getString("MatchTimeFormated");
                    String gId         = results.getString("gId");
                    String DayNight    = results.getString("DayNight");
                    String PitchNo     = results.getString("PitchNo");
                    String MatchCost   = results.getString("MatchCost");
                    String currency    = results.getString("currency");
                    String Boots       = results.getString("Boots");
                    String aSide       = results.getString("aSide");
                    String weather     = results.getString("weather");
                    String Turf        = results.getString("Turf");
                    String status      = results.getString("status");

                    //Toast.makeText(getApplicationContext(),"Turf = "+Turf, Toast.LENGTH_SHORT).show();

                    JSONArray getMatchParticipents = jsonObject.getJSONArray("getMatchParticipents");
                    for(int i = 0; i < getMatchParticipents.length(); i++) {
                        JSONObject c = getMatchParticipents.getJSONObject(i);

                        String participentId = c.getString("participentId");
                        String membershipId = c.getString("membershipId");
                        String Position = c.getString("Position");
                        String status_ = c.getString("status");
                        String UserID = c.getString("UserID");
                        String gId_ = c.getString("gId");
                        String memberRole = c.getString("memberRole");
                        String dateAdded = c.getString("dateAdded");
                        String fname = c.getString("fname");
                        String lname = c.getString("lname");
                        String full_name = c.getString("full_name");
                       // Toast.makeText(getApplicationContext(),"status_ = "+status_, Toast.LENGTH_SHORT).show();
                    }

                    JSONObject  MatchInfo = jsonObject.getJSONObject("matchData");
                    String gameId_     = MatchInfo.getString("gameId");
                    String LocationName_= MatchInfo.getString("locationName");
                    String image_       = MatchInfo.getString("image");
                    String DateAdded_   = MatchInfo.getString("DateAdded");
                    String matchDate_   = MatchInfo.getString("matchDate");
                    String MatchTime_   = MatchInfo.getString("MatchTime");
                    String gId_         = MatchInfo.getString("gId");
                    String DayNight_ = MatchInfo.getString("DayNight");
                    String PitchNo_  = MatchInfo.getString("PitchNo");
                    String MatchCost_= MatchInfo.getString("MatchCost");
                    String currency_ = MatchInfo.getString("currency");
                    String Boots_    = MatchInfo.getString("Boots");
                    String aSide_    = MatchInfo.getString("aSide");
                    String weather_  = MatchInfo.getString("weather");
                    String Turf_     = MatchInfo.getString("Turf");
                    String status_   = MatchInfo.getString("status");
                    //Toast.makeText(getApplicationContext(),"weather_ = "+weather_, Toast.LENGTH_SHORT).show();
                }
                return result;
            }
        });
        webRequestCall.execute(url,"GET",params);

    }

    public static String groupname="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*final EditText ed_search = activity.findViewById(R.id.ed_search);
        ed_search.setText("");
        ed_search.setVisibility(View.GONE);
        ImageView search_img = activity.findViewById(R.id.search_img);
        search_img .setVisibility(View.GONE);*/

        activity = ChatPageActivity.this;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String game_Id,group_Id,group_Image;

        ImageView message_img= findViewById(R.id.message_img);
        message_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*
        group_Id    = bundle.getString("group_Id"  );
        game_Id     = bundle.getString("game_Id"   );
        group_Image = bundle.getString("game_Image");
        */

        Bundle bundle  = getIntent().getExtras();
        String group_name= bundle.getString("group_name"  );
        groupname=bundle.getString("group_name"  );
        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
                 tv_toolbar_title.setText(""+groupname);
                /* tv_toolbar_title.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {

                         Toast.makeText(ChatPageActivity.this,"click",Toast.LENGTH_SHORT).show();

                        *//* Intent intent = new Intent(Intent.ACTION_INSERT);
                         intent.setType("vnd.android.cursor.item/event");

                         Calendar cal = Calendar.getInstance();
                         long startTime_ = cal.getTimeInMillis();
                         long endTime_ = cal.getTimeInMillis()  + 60 * 60 * 1000;

                         intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime_);
                         intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,endTime_);
                         intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

                         intent.putExtra(CalendarContract.Events.TITLE, "Neel Birthday TV");
                         intent.putExtra(CalendarContract.Events.DESCRIPTION,  "This is a sample description");
                         intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "My Guest House");
                         intent.putExtra(CalendarContract.Events.RRULE, "FREQ=YEARLY");

                         startActivity(intent);*//*
                     }
                 });*/

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        springDotsIndicator = (SpringDotsIndicator) findViewById(R.id.spring_dots_indicator);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        springDotsIndicator.setViewPager(mViewPager);
        mViewPager.setCurrentItem(1);

//        Toast.makeText(activity,"userID = "+sharedPreferences.getString("userID","")+
//                " group_ID = "+group_Id+" game_ID = "+game_Id+" status = out",Toast.LENGTH_SHORT).show();

    }
    public void alertDialogMatch_Status(final Context context) {

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.custom_prompt_match_status,
                null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

        alertDialogBuilder.setView(promptsView);


        final AlertDialog d = alertDialogBuilder.show();
        d.getWindow().setBackgroundDrawableResource(android.R.color.white);


        TextView tv_schedule   = promptsView.findViewById(R.id.tv_schedule);
        tv_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        TextView tv_reschedule = promptsView.findViewById(R.id.tv_reschedule);
        tv_reschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        TextView tv_completed  = promptsView.findViewById(R.id.tv_completed);
        tv_completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        final RadioButton radioButton1 = promptsView.findViewById(R.id.radioButton1);
        final RadioButton radioButton2 = promptsView.findViewById(R.id.radioButton2);
        final RadioButton radioButton3 = promptsView.findViewById(R.id.radioButton3);

        Button btn_submit = promptsView.findViewById(R.id.btn_submit);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//Create Group API
//http://192.168.100.14/FirmGround/REST/createGroup?users={"logIn":2, "groupName":"City Spiders",
//"users":[{"name":"Abc2","phonenumber":"122323407","id":1},{"name":"Abc3","phonenumber":"123456701","id":3}]}

                String status = "";

                if (radioButton1.isChecked()){
                    status = "schedule";
                }  if (radioButton2.isChecked()){
                    status = "reschedule";
                }  if (radioButton3.isChecked()){
                    status = "Completed";
                }

                if (status.equals("")){
                    Toast.makeText(ChatPageActivity.this,"Select an option",Toast.LENGTH_SHORT).show();
                }else {

                    //http://192.168.100.14/FirmGround/REST/gameStatusChange?gameId=8&status=Completed
                    String url    =  getResources().getString(R.string.url)+"gameStatusChange?gameId="+send_game_Id()+"&status="+status;

                    //Toast.makeText(getActivity(),"users="+prams.toString(),Toast.LENGTH_LONG).show();

                    final String params = "";

                    WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                        @Override
                        public String TaskCompletionResult(String result) {

                            try {

                                JSONObject jsonObject = new JSONObject(result);
                                if(jsonObject.getString("respCode").equals("200")) {

                                    Toast.makeText(ChatPageActivity.this,jsonObject.getString("message") , Toast.LENGTH_SHORT).show();
                                    d.dismiss();
                                }
                                else{
                                    Toast.makeText(ChatPageActivity.this,jsonObject.getString("message") , Toast.LENGTH_SHORT).show();
                                    d.dismiss();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return result;
                        }
                    });
                    webRequestCall.execute(url, "POST", params);

                }
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_chat_page, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    @SuppressLint("ValidFragment")
    public static class PlaceholderFragment extends Fragment  {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        ChatPageActivity mListener = null;

        ArrayList<Squad_list> squadLists=new ArrayList();
        ArrayList<In_list> in_lists     =new ArrayList();
        ArrayList<Out_list> out_lists   =new ArrayList();

        SharedPreferences sharedPreferences;

        CircleImageView img_group_icon;

        TextView  tv_name,tv_location,tv_date,tv_kickoff,tv_day_night,tv_pitch_no,tv_side,tv_truf,tv_condition,tv_cost,tv_match_type;
        ImageView imageView;

        SliderLayout sliderLayout;

        JSONParser_Upload_Group jsonParser_upload_group;
        JSONParser_Send_Chat_Image jsonParser_send_chat_image;
        ChatPageActivity chatPageActivity;
        UpdateGroupActivity updateGroupActivity;

        public String file_counter="0";
        FileOutputStream output = null;

        ListView mListView;

        public static ArrayList<Message_List> message_ArrayList =new ArrayList();
        public static Message_List_Adapter message_list_adapter;

        String gameId,stadiumName,LocationName,image,DateAdded,matchDate,MatchTime,gId,DayNight,PitchNo,MatchCost,match_type,currency,Boots,aSide,substitutes,weather,Turf,allowboots,status,lat,lng,real_time_to_pass;

        TextView tv_in,tv_out,tv_count,tv_plus_1,tv_minus_1,tv_day_detail,tv_goals_stadium,tv_boots,tv_facilities,tv_restriction,tv_match_referee,tv_gametype,tv_team_1,tv_team_2,tv_match_note;
        RecyclerView recyclerView_squad_list;
        RecyclerView recyclerView_in_list;
        RecyclerView recyclerView_out_list;
        View view_white;
        int referedCount=0;

        int in_count=0;

        Button btn_reminder,btn_cancel_game,btn_add_match;

        MyDatabase mydb;

        ImageButton attach_icon,attach_recording;

        public static BroadcastReceiver wifireciver;
        public static Context context;

        public static EditText messageArea;

        LinearLayout linearLayout_match_detail;

        int audio_check=0;

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

        private MediaRecorder mRecorder;

        private static final String LOG_TAG = "AudioRecording";

        private  String mFileName = "";
        int audio_file_count = 0;

        Timer T;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
            //Toast.makeText(getActivity(),getArguments().getInt(ARG_SECTION_NUMBER)+"",Toast.LENGTH_SHORT).show();
            final View rootView;
            chatPageActivity  = (ChatPageActivity) getActivity();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

            mydb = new MyDatabase(getActivity());
            context = getActivity().getApplicationContext();
            wifireciver = new WifiReceiver();
            registerNetworkBroadcastForNougat();

            if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                rootView = inflater.inflate(R.layout.pagerfragment_chat1, container, false);

                jsonParser_upload_group = new JSONParser_Upload_Group();

                 tv_in = rootView. findViewById(R.id.tv_in);
                 tv_out= rootView.findViewById(R.id.tv_out);
                 tv_count= rootView.findViewById(R.id.tv_count);
                 tv_plus_1= rootView.findViewById(R.id.tv_plus_1);
                 tv_minus_1= rootView.findViewById(R.id.tv_minus_1);
                 tv_day_detail= rootView.findViewById(R.id.tv_day_detail);
                 tv_goals_stadium= rootView.findViewById(R.id.tv_goals_stadium);

                img_group_icon = rootView.findViewById(R.id.img_group_icon);

                recyclerView_squad_list = rootView.findViewById(R.id.recyclerView_squad_list);
                recyclerView_squad_list.setLayoutManager(new GridLayoutManager(getActivity(), 5));

                recyclerView_in_list = rootView.findViewById(R.id.recyclerView_in_list);
                recyclerView_in_list.setLayoutManager(new GridLayoutManager(getActivity(), 5));

                recyclerView_out_list = rootView.findViewById(R.id.recyclerView_out_list);
                recyclerView_out_list.setLayoutManager(new GridLayoutManager(getActivity(), 5));

                view_white = rootView.findViewById(R.id.view_white);

                tv_in.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
                        //http://192.168.100.14/FirmGround/REST/userParticipationStatusChange?gameId=1&status=pending&gId=6&userID=2

                        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                       if (in_count==1){
                           Toast.makeText(activity,"You are already added to this game",Toast.LENGTH_SHORT).show();
                           activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                       }else {

                           String text = tv_count.getText().toString();
                           String[] array = text .split("/");

                           int temp_in_squad = Integer.parseInt(array[0]);
                           int temp_total = Integer.parseInt(array[1]);

                           if (temp_in_squad < temp_total){

                               final String url = getResources().getString(R.string.url) + "userParticipationStatusChange?" +
                                       "userID="+sharedPreferences.getString("userID","")+
                                       "&gId="+chatPageActivity.send_group_Id()+
                                       "&gameId="+chatPageActivity.send_game_Id()+
                                       "&status=in";
                               final String params = "";
                               final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                                   @Override
                                   public String TaskCompletionResult(String result) throws JSONException {

                                       JSONObject jsonObject = new JSONObject(result);

                                       if(jsonObject.getString("respCode").equals("200")) {

                                           in_count = 1;

                                           Toast.makeText(activity,"You have been successfully added to the game",Toast.LENGTH_SHORT).show();
                                           total_squad = 0;in_squad = 0;a_side=0;
                                           activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                           getmatchparticipents();
                                       }
                                       return result;
                                   }
                               });
                               webRequestCall.execute(url,"GET",params);

                           }else {
                               Toast.makeText(activity,"Players for the game is full",Toast.LENGTH_SHORT).show();
                               activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                           }
                       }
                    }
                });

                tv_out.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(activity,"userID = "+sharedPreferences.getString("userID","")+
//                                " group_ID = "+group_Id+" game_ID = "+game_Id+" status = out",Toast.LENGTH_SHORT).show();

                        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        if (out_buttom_check==0){

                            if (referedCount>0){

                                TextView textView_title = new TextView(getActivity());
                                textView_title.setText("Move All");
                                textView_title.setGravity(Gravity.START);
                                textView_title.setPadding(20,10,20,10);
                                textView_title.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
                                textView_title.setCompoundDrawablePadding(10);

                                //textView_title.setCompoundDrawables(null,null,mContext.getResources().getDrawable(R.drawable.ic_warning_colored_24dp),null);
                                textView_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_warning_colored_24dp, 0);

                                new AlertDialog.Builder(getActivity())
                                        .setCustomTitle(textView_title)
                                        .setMessage("Would you like to move other participants with you?")
                                        // Specifying a listener allows you to take an action before dismissing the dialog.
                                        // The dialog is automatically dismissed when a dialog button is clicked.
                                        .setPositiveButton(getActivity().getResources().getString(R.string.str_yes), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                                //http://192.168.100.14/FirmGround/REST/userParticipationStatusChange?gameId=1&status=pending&gId=6&userID=2
                                                final String url = getResources().getString(R.string.url) + "userParticipationStatusChange?" +
                                                        "userID="+sharedPreferences.getString("userID","")+
                                                        "&gId="+chatPageActivity.send_group_Id()+
                                                        "&gameId="+chatPageActivity.send_game_Id()+
                                                        "&ReferedPlayer=out"+
                                                        "&status=out";
                                                final String params = "";
                                                final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                                                    @Override
                                                    public String TaskCompletionResult(String result) throws JSONException {

                                                        JSONObject jsonObject = new JSONObject(result);

                                                        if(jsonObject.getString("respCode").equals("200")) {
                                                            Toast.makeText(activity,"You have been successfully removed from the game",Toast.LENGTH_SHORT).show();
                                                            total_squad = 0;in_squad = 0;a_side=0;
                                                            in_count=0;
                                                            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                            getmatchparticipents();
                                                        }
                                                        return result;
                                                    }
                                                });
                                                webRequestCall.execute(url,"GET",params);
                                            }
                                        })
                                        // A null listener allows the button to dismiss the dialog and take no further action.
                                        .setNegativeButton(getActivity().getResources().getString(R.string.str_no), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                                //http://192.168.100.14/FirmGround/REST/userParticipationStatusChange?gameId=1&status=pending&gId=6&userID=2
                                                final String url = getResources().getString(R.string.url) + "userParticipationStatusChange?" +
                                                        "userID="+sharedPreferences.getString("userID","")+
                                                        "&gId="+chatPageActivity.send_group_Id()+
                                                        "&gameId="+chatPageActivity.send_game_Id()+
                                                        "&status=partial";
                                                final String params = "";
                                                final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                                                    @Override
                                                    public String TaskCompletionResult(String result) throws JSONException {

                                                        JSONObject jsonObject = new JSONObject(result);

                                                        if(jsonObject.getString("respCode").equals("200")) {
                                                            Toast.makeText(activity,"You have been successfully removed from the game",Toast.LENGTH_SHORT).show();
                                                            total_squad = 0;in_squad = 0;a_side=0;in_count=0;
                                                            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                            getmatchparticipents();
                                                        }
                                                        return result;
                                                    }
                                                });
                                                webRequestCall.execute(url,"GET",params);
                                            }
                                        })
                                        //.setIcon(android.R.drawable.ic_dialog_alert)
                                        .setNeutralButton("Cancel",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Toast.makeText(activity,"Please have look on your referred players",Toast.LENGTH_LONG).show();
                                                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            }
                                        })
                                        .show();
                            }
                            else {

                                //http://192.168.100.14/FirmGround/REST/userParticipationStatusChange?gameId=1&status=pending&gId=6&userID=2
                                final String url = getResources().getString(R.string.url) + "userParticipationStatusChange?" +
                                        "userID="+sharedPreferences.getString("userID","")+
                                        "&gId="+chatPageActivity.send_group_Id()+
                                        "&gameId="+chatPageActivity.send_game_Id()+
                                        "&ReferedPlayer=0"+
                                        "&status=out";
                                final String params = "";
                                final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                                    @Override
                                    public String TaskCompletionResult(String result) throws JSONException {

                                        JSONObject jsonObject = new JSONObject(result);

                                        if(jsonObject.getString("respCode").equals("200")) {
                                            Toast.makeText(activity,"You have been successfully removed from the game",Toast.LENGTH_SHORT).show();
                                            total_squad = 0;in_squad = 0;a_side=0;in_count=0;
                                            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            getmatchparticipents();
                                        }
                                        return result;
                                    }
                                });
                                webRequestCall.execute(url,"GET",params);

                            }

                        }
                        else {
                            //Toast.makeText(activity,"You cannot remove yourself from the game without adding yourself first",Toast.LENGTH_SHORT).show();
                            Toast.makeText(activity,"You are already out",Toast.LENGTH_SHORT).show();
                            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    }
                });


                tv_plus_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Toast.makeText(activity,"in_squad = "+in_squad+"a_side = "+a_side,Toast.LENGTH_SHORT).show();

                        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        if (intercheck==1){

                            String text = tv_count.getText().toString();
                            String[] array = text .split("/");

                            int temp_in_squad = Integer.parseInt(array[0]);
                            int temp_total = Integer.parseInt(array[1]);

                            if (temp_in_squad < temp_total){

                                temp_in_squad++;
                                tv_count.setText(""+ temp_in_squad +"/"+ temp_total);

                                //http://192.168.100.14/FirmGround/REST/updateAdditionalParticipents?gameId=1
                                final String url = getResources().getString(R.string.url) + "updateAdditionalParticipents?" +
                                        "userID="+sharedPreferences.getString("userID","")+
                                        "&gId="+chatPageActivity.send_group_Id()+
                                        "&gameId="+chatPageActivity.send_game_Id()+
                                        "&status=plus";
                                final String params = "";
                                final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                                    @Override
                                    public String TaskCompletionResult(String result) throws JSONException {
                                        JSONObject jsonObject = new JSONObject(result);
                                        if(jsonObject.getString("respCode").equals("200")) {

                                            total_squad = 0;in_squad = 0;a_side=0;
                                            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            getmatchparticipents();
                                            //getmatchparticipents_1();
                                        }else {
                                        }
                                        return result;
                                    }
                                });
                                webRequestCall.execute(url,"GET",params);
                            }
                            else{
                                Toast.makeText(activity,"Players for the game is full",Toast.LENGTH_SHORT).show();
                                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }
                        }
                        else {
                            Toast.makeText(activity,"Internet not avaiable ",Toast.LENGTH_LONG).show();
                            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    }
                });

                tv_minus_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        if (referedCount>0){

                            //http://192.168.100.14/FirmGround/REST/updateAdditionalParticipents?gameId=1
                            final String url = getResources().getString(R.string.url) + "updateAdditionalParticipents?" +
                                    "userID="+sharedPreferences.getString("userID","")+
                                    "&gId="+chatPageActivity.send_group_Id()+
                                    "&gameId="+chatPageActivity.send_game_Id()+
                                    "&status=minus";
                            final String params = "";
                            final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                                @Override
                                public String TaskCompletionResult(String result) throws JSONException {

                                    JSONObject jsonObject = new JSONObject(result);

                                    if(jsonObject.getString("respCode").equals("200")) {

                                        total_squad = 0;in_squad = 0;a_side=0;
                                        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        getmatchparticipents();

                                    }else {
                                        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    }

                                    return result;
                                }
                            });
                            webRequestCall.execute(url,"GET",params);
                        }else {
                            Toast.makeText(activity,"You haven't added any players",Toast.LENGTH_SHORT).show();
                            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    }
                });

                total_squad = 0;in_squad = 0;a_side=0;
                getmatchparticipents();

                //=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                rootView = inflater.inflate(R.layout.pagerfragment_chat2, container, false);

                jsonParser_send_chat_image = new JSONParser_Send_Chat_Image();

                SavePreferences_int("audio_file_count",0);

                /*ImageView edit_icon = getActivity().findViewById(R.id.edit_icon);
                          edit_icon.setVisibility(View.GONE);*/

                /*Toast.makeText(getActivity(),""+global,Toast.LENGTH_SHORT).show();
                global++;*/

                //Toast.makeText(getActivity(),"fragment 2",Toast.LENGTH_SHORT).show();

                final LinearLayout layout;
                RelativeLayout layout_2;
                ImageView sendButton;
                //final EditText messageArea;
                final ScrollView scrollView;

                /*layout   = rootView.findViewById(R.id.layout1);
                layout_2   = rootView.findViewById(R.id.layout2);*/
                scrollView = rootView.findViewById(R.id.scrollView);
                sendButton = rootView.findViewById(R.id.sendButton);
                messageArea= rootView.findViewById(R.id.editText);
                attach_icon= rootView.findViewById(R.id.attach_icon);
                attach_recording= rootView.findViewById(R.id.attach_recording);

                attach_recording.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                requestAudio(v);

                                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                                // Vibrate for 500 milliseconds
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                                } else {
                                    //deprecated in API 26
                                    vibrator.vibrate(100);
                                }

                                mRecorder = new MediaRecorder();
                                mRecorder.reset();

                                if (audio_check==1){

                                    T=new Timer();

                                    final int[] count = {0};
                                    T.scheduleAtFixedRate(new TimerTask(){
                                        @Override
                                        public void run() {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run(){
                                                    //Toast.makeText(getActivity(), "counter = "+count[0], Toast.LENGTH_LONG).show();
                                                    messageArea.setText("00:"+ count[0]);
                                                    count[0]++;
                                                }
                                            });
                                        }
                                    }, 000, 1000);


                                    image_real_path="";
                                    mFileName="";

                                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                                    mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();

                                    mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
                                    //audio_file_count = sharedPreferences.getInt("audio_file_count",0);
                                    Long tsLong = System.currentTimeMillis()/1000;
                                    String ts = tsLong.toString();
                                    mFileName += "/AudioRecording_"+ts+".3gp";

                                     /* audio_file_count = sharedPreferences.getInt("audio_file_count",0);
                                      mFileName += "/AudioRecording_"+audio_file_count+".3gp";
                                      audio_file_count++;
                                      SavePreferences_int("audio_file_count",audio_file_count);*/

                                   // mFileName += "/AudioRecording.3gp";

                                    mRecorder.setOutputFile(mFileName);

                                    try {
                                        mRecorder.prepare();
                                    } catch (IOException e) {
                                        Log.e(LOG_TAG, "prepare() failed");
                                    }
                                    mRecorder.start();
                                    //Toast.makeText(getActivity(), "Recording Started", Toast.LENGTH_LONG).show();
                                }

                                break;
                            case MotionEvent.ACTION_UP:

                                messageArea.setText("");
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

                                    vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                                    // Vibrate for 500 milliseconds
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                                    } else {
                                        //deprecated in API 26
                                        vibrator.vibrate(100);
                                    }

                                }
                            default:
                                break;
                        }
                        return true;
                    }
                });


                ImageView  floatingButton;

                attach_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

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

                //comented here

                mListView = rootView.findViewById(R.id.chatsListView);
                floatingButton = rootView.findViewById(R.id.floatingButton);
                floatingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getActivity(),"floating Button Clicked",Toast.LENGTH_SHORT).show();

                        if (messageArea.getText().toString().equals("")){
                            messageArea.setError("Empty");
                        }else {

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

                                Message_List message_list = new Message_List(""+sb.toString(),"\uF017","",""+filename,""+messageArea.getText().toString(),
                                        "self","","",false);
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

                                /*  mydb.insertdata_chat(""+chatPageActivity.send_group_Id(),""+sharedPreferences.getString("userID",""),
                                        ""+chatPageActivity.send_group_name(),""+sb.toString(),"✓✓","",
                                        ""+filename,""+messageArea.getText().toString(),"self");*/

                                //http://192.168.100.14/FirmGround/REST/sendMessage?userId, gId, message
                                final String url = getResources().getString(R.string.url) + "sendMessage";

                                String dummy_id = sb.toString();

                                System.out.println("TAG Dummy = "+dummy_id);

                                final String params =
                                        "userId="+sharedPreferences.getString("userID","")+
                                                "&gId="+chatPageActivity.send_group_Id()+
                                                "&message="+messageArea.getText().toString()+
                                                "&messageId="+sb.toString();;

                                final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                                    @Override
                                    public String TaskCompletionResult(String result) throws JSONException {

                                        JSONObject jsonObject = new JSONObject(result);

                                        if(jsonObject.getString("respCode").equals("200")) {

                                            //Toast.makeText(getActivity(),""+jsonObject.getString("message"),Toast.LENGTH_SHORT).show();

                                            String messageId     = jsonObject.getString("messageId");
                                            String tempMessageId = jsonObject.getString("tempMessageId");
                                            String temp_message  = jsonObject.getString("message");

                                            mydb.insertdata_chat(""+chatPageActivity.send_group_Id(),
                                                    ""+sharedPreferences.getString("userID",""),
                                                    ""+messageId,"✓✓","",
                                                    ""+filename,""+temp_message,"self","read");

                                            System.out.println("TAG adapter count = "+message_list_adapter.getCount());

                                            if (message_list_adapter!=null){

                                                for (int i=0;i<message_list_adapter.getCount();i++){
                                                    if (message_list_adapter.getItem(i).getMessage_id().equals(""+tempMessageId)){

                                                        System.out.println("messageId = "+messageId+" item.getimagecheck = ");
                                                        //Toast.makeText(getActivity(),message_list_adapter.getItem(i).getMessage_id()+" = "+ sb.toString(), Toast.LENGTH_SHORT).show();

                                                        message_list_adapter.getItem(i).setMessage_id(""+messageId);
                                                        message_list_adapter.getItem(i).setMessage(""+temp_message);
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

                                        }else {
                                            //implement here databaseHelper class
                                            //Toast.makeText(getActivity(),""+jsonObject.getString("message"),Toast.LENGTH_SHORT).show();

                                            mydb.insertdata(""+chatPageActivity.send_group_Id(),//group_id
                                                    ""+sharedPreferences.getString("userID",""),//user_id
                                                    ""+sb.toString(),
                                                    ""+messageArea.getText().toString());
                                        }
                                        return result;
                                    }
                                });
                                webRequestCall.execute(url,"POST",params);
                            }else
                                {

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
                                       // mListView.setSelection(message_list_adapter.getCount());
                                }else {
                                    message_list_adapter.notifyDataSetChanged();
                                    if (mListView!=null){
                                        mListView.smoothScrollToPosition(message_list_adapter.getCount()-1);
                                        mListView.setSelection(message_list_adapter.getCount());
                                    }
                                }

                                mydb.insertdata(chatPageActivity.send_group_Id(),//group_id
                                        sharedPreferences.getString("userID",""),//user_id
                                        sb.toString(),
                                        messageArea.getText().toString());

                                    messageArea.setText("");
                            }
                        }
                    }
                });


                /*
                Toolbar toolbar = rootView.findViewById(R.id.toolbar);
                ImageView edit_icon = toolbar.findViewById(R.id.edit_icon);
                edit_icon.setVisibility(View.GONE);
                */

                EditText mEditText = (EditText) rootView.findViewById(R.id.editText);
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
                        if (s.length() < 0) {
                            //showAudioButton();
                        }
                    }
                });
            }
            else {
                rootView = inflater.inflate(R.layout.pagerfragment_chat3, container, false);

                linearLayout_match_detail = rootView.findViewById(R.id.linearLayout_match_detail);

                tv_name         = rootView.findViewById(R.id.tv_name);
                tv_cost         = rootView.findViewById(R.id.tv_cost);
                tv_date         = rootView.findViewById(R.id.tv_date);
                tv_side         = rootView.findViewById(R.id.tv_side);
                tv_truf         = rootView.findViewById(R.id.tv_truf);
                tv_boots        = rootView.findViewById(R.id.tv_boots);
                tv_team_1       = rootView.findViewById(R.id.tv_team_1);
                tv_team_2       = rootView.findViewById(R.id.tv_team_2);
                tv_kickoff      = rootView.findViewById(R.id.tv_kickoff);
                tv_location     = rootView.findViewById(R.id.tv_location);
                tv_pitch_no     = rootView.findViewById(R.id.tv_pitch_no);
                tv_gametype     = rootView.findViewById(R.id.tv_gametype);
                tv_day_night    = rootView.findViewById(R.id.tv_day_night);
                tv_condition    = rootView.findViewById(R.id.tv_condition);
                tv_match_type   = rootView.findViewById(R.id.tv_match_type);
                tv_match_note   = rootView.findViewById(R.id.tv_match_note);
                tv_facilities   = rootView.findViewById(R.id.tv_facilities);
                tv_restriction  = rootView.findViewById(R.id.tv_restriction);
                tv_match_referee= rootView.findViewById(R.id.tv_match_referee);
                //imageView     = rootView.findViewById(R.id.imageView);

                sliderLayout  = rootView.findViewById(R.id.sliderLayout);

                tv_location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                       /*
                         Uri navigationIntentUri = Uri.parse("google.navigation:q=51.656471,-0.139038");
                         Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
                         mapIntent.setPackage("com.google.android.apps.maps");
                         startActivity(mapIntent);
                         */

                       /*GPS_Tracker gps =new GPS_Tracker(getActivity());

                        final Double Lat = gps.getLatitude();
                        final Double Lng = gps.getLongitude();

                         Toast.makeText(getContext(),"Latitude = "+Lat,Toast.LENGTH_LONG).show();
                         Toast.makeText(getContext(),"Longitude = "+Lng,Toast.LENGTH_LONG).show();*/

                        //String uri = "http://maps.google.com/maps?q:hl=en&saddr=51.656471,-0.139038";/*"&daddr=51.5074,0.1278"*/

                        String uri = "http://maps.google.com/maps?hl=en&daddr="+lat+","+lng;
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        intent.setClassName("com.google.android.apps.maps",
                                "com.google.android.maps.MapsActivity");
                        try
                        {
                            startActivity(intent);
                        }
                        catch(ActivityNotFoundException ex)
                        {
                            try
                            {
                                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                startActivity(unrestrictedIntent);
                            }
                            catch(ActivityNotFoundException innerEx)
                            {
                                Toast.makeText(getActivity(), "Please install a maps application", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });


                btn_reminder    = rootView.findViewById(R.id.btn_reminder);
                btn_reminder.setVisibility(View.VISIBLE);
                btn_reminder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                     /*   Intent i = new Intent(Intent.ACTION_VIEW);
                        // Android 2.2+
                        i.setData(Uri.parse("content://com.android.calendar/time"));
                        // Before Android 2.2+
                        //i.setData(Uri.parse("content://calendar/time"));
                        startActivity(i);;*/

                      /*  Calendar cal = Calendar.getInstance();
                        Intent intent = new Intent(Intent.ACTION_EDIT);
                        intent.setType("vnd.android.cursor.item/event");
                        intent.putExtra("beginTime", cal.getTimeInMillis());
                        intent.putExtra("allDay", true);
                        intent.putExtra("rrule", "FREQ=YEARLY");
                        intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
                        intent.putExtra("title", "A Test Event from android app");
                        startActivity(intent);*/

                        //chatPageActivity.call_calendar();

                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMM yyyy");

                        String date_to_pass="";

                        date_to_pass =  tv_date.getText().toString().replace("st", "");
                        date_to_pass =  tv_date.getText().toString().replace("nd", "");
                        date_to_pass =  tv_date.getText().toString().replace("rd", "");
                        date_to_pass =  tv_date.getText().toString().replace("th", "");

                        try {
                            Date date = sdf.parse(date_to_pass);

                            Calendar cal   = Calendar.getInstance();
                            long startTime = cal.getTimeInMillis();
                            long endTime   =  date.getTime();

                            chatPageActivity.call_calendar(tv_name.getText().toString(),endTime);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
                btn_cancel_game = rootView.findViewById(R.id.btn_cancel_game);
                btn_cancel_game.setVisibility(View.VISIBLE);
                btn_cancel_game.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        TextView textView_title = new TextView(getActivity());
                        textView_title.setText("Cancel Game");
                        textView_title.setGravity(Gravity.START);
                        textView_title.setPadding(20,10,20,10);
                        textView_title.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
                        textView_title.setCompoundDrawablePadding(10);

                        //textView_title.setCompoundDrawables(null,null,mContext.getResources().getDrawable(R.drawable.ic_warning_colored_24dp),null);
                        textView_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_warning_colored_24dp, 0);

                        new AlertDialog.Builder(getActivity())
                                .setCustomTitle(textView_title)
                                .setMessage("Are you sure you want to cancel the game?")
                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(getActivity().getResources().getString(R.string.str_yes), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {


                                        //http://192.168.100.14/FirmGround/REST/gameStatusChange?gameId=8&status=Completed
                                        String url    =  getResources().getString(R.string.url)+"gameStatusChange?gameId="+chatPageActivity.send_game_Id()+"&status=cancel";

                                        //Toast.makeText(getActivity(),"users="+prams.toString(),Toast.LENGTH_LONG).show();

                                        final String params = "";

                                        WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                                            @Override
                                            public String TaskCompletionResult(String result) {

                                                try {

                                                    JSONObject jsonObject = new JSONObject(result);
                                                    if(jsonObject.getString("respCode").equals("200")) {

                                                        Toast.makeText(getActivity(),jsonObject.getString("message") , Toast.LENGTH_SHORT).show();
                                                        getActivity().finish();
                                                    }
                                                    else{
                                                        Toast.makeText(getActivity(),jsonObject.getString("message") , Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                return result;
                                            }
                                        });
                                        webRequestCall.execute(url, "POST", params);
                                    }
                                })
                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(getActivity().getResources().getString(R.string.str_no), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        //http://192.168.100.14/FirmGround/REST/userParticipationStatusChange?gameId=1&status=pending&gId=6&userID=2
                                    }
                                })

                     .show();
                    }
                });
            }

            return rootView;
        }
        private static void registerNetworkBroadcastForNougat() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.registerReceiver(wifireciver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.registerReceiver(wifireciver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            }else {
                context.registerReceiver(wifireciver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            }
        }
        protected void unregisterNetworkChanges() {
            try {
                context.unregisterReceiver(wifireciver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onDestroy() {
            super.onDestroy();
            unregisterNetworkChanges();
        }
        public static int intercheck=0;
        public  static Cursor c;
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

                                    intercheck=1;
                                    //Toast.makeText(context,"intercheck = "+intercheck,Toast.LENGTH_SHORT).show();

                                    MyDatabase mydb = new MyDatabase(activity);
                                    c = mydb.getdata();
                                    if (c.getCount()>=0){
                                        if (c.moveToFirst()){
                                            do{
                                                String id      = c.getString(0);
                                                String grp_id  = c.getString(1);
                                                String user_id = c.getString(2);
                                                String msg_id  = c.getString(3);
                                                String msg     = c.getString(4);

                                                Log.e("YYYY", "id = "+id+
                                                        " grp_id = "+grp_id+
                                                        " user_id = "+user_id+
                                                        " msg_id = "+msg_id+
                                                        " msg = "+msg);

                                                final String url_SEND = activity.getResources().getString(R.string.url)+"sendMessage";
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
                                                            /*
                                                              MyDatabase mydb = new MyDatabase(activity);
                                                              mydb.insertdata_chat(""+chatPageActivity.send_group_Id(),""+sharedPreferences.getString("userID",""),
                                                                    ""+chatPageActivity.send_group_name(),""+messageId,"✓✓","",
                                                                    ""+filename,""+messageArea.getText().toString(),"self","read");
                                                                    */

                                                            String messageId     = jsonObject.getString("messageId");
                                                            String tempMessageId = jsonObject.getString("tempMessageId");
                                                            String temp_message  = jsonObject.getString("message");
                                                            String cTime  = jsonObject.getString("cTime");



                                                            if (message_list_adapter!=null){
                                                                System.out.println("TAG adapter count = "+message_list_adapter.getCount());

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

            }
            else {
                intercheck=0;
                //Toast.makeText(context,"intercheck = "+intercheck,Toast.LENGTH_SHORT).show();
            }
        }
        int plus_button_checker =0;
        int out_buttom_check=0;
        int total_squad = 0;
        int in_squad = 0;
        int a_side = 0;
        public void getmatchparticipents(){

            //http://192.168.100.14/FirmGround/REST/getMatchParticipents?gameId=1
            /*final ProgressDialog progress = new ProgressDialog(activity);
              if (activity!=null)
                progress.setMessage("Loading");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setIndeterminate(true);
                progress.setProgress(0);
                progress.setCancelable(true);
                progress.show();*/

           final int[] loginUser = {0};
           if (wifi_checker==1){
               view_white.setVisibility(View.GONE);

               activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                       WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

               final String url = getResources().getString(R.string.url) + "getMatchParticipents?gameId="+chatPageActivity.send_game_Id();
               final String params = "";
               final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                   @Override
                   public String TaskCompletionResult(String result) throws JSONException {

                       JSONObject jsonObject = new JSONObject(result);

                       if(jsonObject.getString("respCode").equals("200")) {

                           //progress.dismiss();

                           JSONArray results = jsonObject.getJSONArray("results");

                           mydb.insertdata_squad_details(""+chatPageActivity.send_group_Id(),""+jsonObject.toString());

                           // Toast.makeText(getActivity(),"results = "+results.toString(),Toast.LENGTH_SHORT).show();
                           // total_squad = 0;
                           // in_squad = 0;
                           // int a_side=0;

                           squadLists.clear();
                           in_lists.clear();
                           out_lists.clear();

                           for(int i = 0; i < results.length(); i++) {
                               JSONObject c = results.getJSONObject(i);

                               String participentId= c.getString("participentId");
                               String membershipId = c.getString("membershipId");
                               String gameId       = c.getString("gameId");
                               String Position     = c.getString("Position");
                               String ReferedPlayer= c.getString("ReferedPlayer");
                               String added_date   = c.getString("added_date");
                               String status       = c.getString("status");
                               String userID       = c.getString("UserID");
                               String gId          = c.getString("gId");
                               String memberRole   = c.getString("memberRole");
                               String dateAdded    = c.getString("dateAdded");
                               String fname        = c.getString("fname");
                               String lname        = c.getString("lname");
                               String full_name    = c.getString("full_name");


                               if (sharedPreferences.getString("userID","").equals(""+userID)){
                                   loginUser[0] = Integer.parseInt(ReferedPlayer);
                                   referedCount = Integer.parseInt(ReferedPlayer);
                               }else {
                                   referedCount = Integer.parseInt(ReferedPlayer);
                               }

                               //referedCount = Integer.parseInt(ReferedPlayer);

                               if (fname.equals("")){

                                   if(full_name.contains(" ")){
                                       fname= full_name.substring(0, full_name.indexOf(" "));
                                       System.out.println(fname);
                                   }else
                                       fname=full_name;
                               }

                               if (status.equals("pending")) {
                                   if (sharedPreferences.getString("userID","").equals(""+userID)){
                                       plus_button_checker=0;
                                       out_buttom_check=1;// 1 Means out button will be disable
                                   }

                                   Squad_list squad_list = new Squad_list("","" + fname, "","","");
                                   squadLists.add(squad_list);
                                   /*Squad_list squad_list = new Squad_list("","" + fname, "","");
                                    squadLists.add(squad_list);*/
                               }
                               if (status.equals("in")) {

                                   if (sharedPreferences.getString("userID","").equals(""+userID)){
                                       plus_button_checker=1;
                                       out_buttom_check=0;// 0 Means out button will be able
                                       in_count=1;
                                   }

                                   In_list  in_list = new In_list("" + fname, "");
                                   in_lists.add(in_list);
                                   in_squad++;
                               }

                               if (status.equals("out")) {

                       /* for (int k=0;k<=Integer.parseInt(ReferedPlayer);k++){
                            if (k==0){
                                Out_list out_list = new Out_list("" + fname, "");
                                out_lists.add(out_list);
                            }else {
                                Out_list out_list = new Out_list(fname+"+"+k, "");
                                out_lists.add(out_list);
                            }
                        }*/
                                   if (sharedPreferences.getString("userID","").equals(""+userID)){
                                       plus_button_checker=0;
                                       out_buttom_check=1;// 1 Means out button will be disable
                                   }
                                   Out_list out_list = new Out_list("" + fname, "");
                                   out_lists.add(out_list);
                               }
                               if (status.equals("partial")){

                                   if (sharedPreferences.getString("userID","").equals(""+userID)){
                                       plus_button_checker=0;
                                       out_buttom_check=1;// 1 Means out button will be disable
                                   }

                                   Out_list out_list = new Out_list("" + fname, "");
                                   out_lists.add(out_list);

                               }

                               if (referedCount>0){
                                   for (int k=1;k<=Integer.parseInt(ReferedPlayer);k++){

                                       if (k==0){
                                           In_list  in_list = new In_list("" + fname, "");
                                           in_lists.add(in_list);
                                       }else {
                                           In_list  in_list = new In_list("" + fname+"+"+k, "");
                                           in_lists.add(in_list);
                                       }
                                       in_squad++;
                                   }
                               }

                   /* if (sharedPreferences.getString("userID","").equals(""+userID)){
                    }*/
                               total_squad = i + 1;
                           }

                           referedCount = loginUser[0];

                           JSONObject  MatchInfo = jsonObject.getJSONObject("MatchInfo");

                           String gameId      = MatchInfo.getString("gameId");
                           String LocationName= MatchInfo.getString("locationName");
                           String image       = MatchInfo.getString("image");
                           String DateAdded   = MatchInfo.getString("DateAdded");
                           String matchDate   = MatchInfo.getString("matchDate");
                           String MatchTime   = MatchInfo.getString("MatchTime");
                           String matchDateFormated   = MatchInfo.getString("matchDateFormated");
                           String gId         = MatchInfo.getString("gId");
                           String DayNight    = MatchInfo.getString("DayNight");
                           String PitchNo     = MatchInfo.getString("PitchNo");
                           String MatchCost   = MatchInfo.getString("MatchCost");
                           String currency    = MatchInfo.getString("currency");
                           String aSide       = MatchInfo.getString("aSide");
                           String weather     = MatchInfo.getString("weather");
                           String status      = MatchInfo.getString("status");
                           String MatchTimeFormated= MatchInfo.getString("MatchTimeFormated");

                           //Picasso.with( img_group_icon.getContext()).load("http://qa.monshiapp.com"+image).into( img_group_icon);
                           Picasso.with( img_group_icon.getContext()).load(""+sharedPreferences.getString("group_Image","")).into( img_group_icon);

                           a_side = Integer.parseInt(aSide);
                           a_side = a_side * 2;

                           SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
                           try {
                               Date date  = dateFormat.parse(MatchTime);
                               MatchTime = dateFormat.format(date);
                           } catch (ParseException e) {
                               e.printStackTrace();
                           }

                           String inputPattern = "yyyy-MM-dd";
                           String outputPattern = "EEEE, dd MMM yyyy";
                           SimpleDateFormat inputFormat  = new SimpleDateFormat(inputPattern);
                           SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

                           Date date = null;
                           try {
                               date = inputFormat.parse(matchDate);
                               matchDate = outputFormat.format(date);
                           } catch (ParseException e) {
                               e.printStackTrace();
                           }

                           //tv_day_detail.setText(""+matchDate+"\nKO - "+MatchTime );
                           //tv_day_detail.setText(""+matchDateFormated+"\nKO - "+MatchTime );
                           tv_day_detail.setText(""+matchDateFormated+"\nKO - "+MatchTimeFormated );
                           tv_goals_stadium.setText(""+LocationName);

                           if (in_squad<=a_side){
                               tv_count.setText(""+ in_squad +"/"+ a_side);
                           }

                           recyclerView_squad_list.setAdapter(new SquadListRecyclerViewAdapter(squadLists, mListener, getActivity()));
                           recyclerView_in_list.setAdapter(new InListRecyclerViewAdapter(in_lists, mListener, getActivity()));
                           recyclerView_out_list.setAdapter(new OutListRecyclerViewAdapter(out_lists, mListener, getActivity()));

                           activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                       }
                       else {
                       }

                       return result;
                       }
               });
               webRequestCall.execute(url,"GET",params);

           }
           else {
               MyDatabase mydb = new MyDatabase(activity);
               Cursor cursor = mydb.getdata_squad_details(chatPageActivity.send_group_Id());
               if (cursor.getCount()>0) {
                   if (cursor.moveToFirst()){
                       do {
                           String id          = cursor.getString(0);
                           String grp_id      = cursor.getString(1);
                           String squad_detail= cursor.getString(2);

                           Log.e("YYYY",
                                   " id          = "+id+
                                        " grp_id      = "+grp_id+
                                        " squad_detail= "+squad_detail);

                           try {
                               JSONObject jsonObject= new JSONObject(squad_detail);
                               JSONArray results = jsonObject.getJSONArray("results");

                               //mydb.insertdata_squad_details(""+chatPageActivity.send_group_Id(),""+jsonObject.toString());

                               // Toast.makeText(getActivity(),"results = "+results.toString(),Toast.LENGTH_SHORT).show();
                               // total_squad = 0;
                               // in_squad = 0;
                               // int a_side=0;

                               squadLists.clear();
                               in_lists.clear();
                               out_lists.clear();

                               for(int i = 0; i < results.length(); i++) {
                                   JSONObject c = results.getJSONObject(i);

                                   String participentId = c.getString("participentId");
                                   String membershipId = c.getString("membershipId");
                                   String gameId = c.getString("gameId");
                                   String Position = c.getString("Position");
                                   String ReferedPlayer = c.getString("ReferedPlayer");
                                   String added_date = c.getString("added_date");
                                   String status = c.getString("status");
                                   String userID = c.getString("UserID");
                                   String gId = c.getString("gId");
                                   String memberRole = c.getString("memberRole");
                                   String dateAdded = c.getString("dateAdded");
                                   String fname = c.getString("fname");
                                   String lname = c.getString("lname");
                                   String full_name = c.getString("full_name");


                                   if (sharedPreferences.getString("userID","").equals(""+userID)){
                                       loginUser[0] = Integer.parseInt(ReferedPlayer);
                                       referedCount = Integer.parseInt(ReferedPlayer);
                                   }else {
                                       referedCount = Integer.parseInt(ReferedPlayer);
                                   }

                                   //referedCount = Integer.parseInt(ReferedPlayer);

                                   if (fname.equals("")){

                                       if(full_name.contains(" ")){
                                           fname= full_name.substring(0, full_name.indexOf(" "));
                                           System.out.println(fname);
                                       }else
                                           fname=full_name;
                                   }

                                   if (status.equals("pending")) {
                                       if (sharedPreferences.getString("userID","").equals(""+userID)){
                                           plus_button_checker=0;
                                           out_buttom_check=1;// 1 Means out button will be disable
                                       }

                                       Squad_list squad_list = new Squad_list("","" + fname, "","","");
                                       squadLists.add(squad_list);
                                   /*Squad_list squad_list = new Squad_list("","" + fname, "","");
                                    squadLists.add(squad_list);*/
                                   }
                                   if (status.equals("in")) {

                                       if (sharedPreferences.getString("userID","").equals(""+userID)){
                                           plus_button_checker=1;
                                           out_buttom_check=0;// 0 Means out button will be able
                                           in_count=1;
                                       }

                                       In_list  in_list = new In_list("" + fname, "");
                                       in_lists.add(in_list);
                                       in_squad++;
                                   }

                                   if (status.equals("out")) {

                       /* for (int k=0;k<=Integer.parseInt(ReferedPlayer);k++){
                            if (k==0){
                                Out_list out_list = new Out_list("" + fname, "");
                                out_lists.add(out_list);
                            }else {
                                Out_list out_list = new Out_list(fname+"+"+k, "");
                                out_lists.add(out_list);
                            }
                        }*/
                                       if (sharedPreferences.getString("userID","").equals(""+userID)){
                                           plus_button_checker=0;
                                           out_buttom_check=1;// 1 Means out button will be disable
                                       }
                                       Out_list out_list = new Out_list("" + fname, "");
                                       out_lists.add(out_list);
                                   }
                                   if (status.equals("partial")){

                                       if (sharedPreferences.getString("userID","").equals(""+userID)){
                                           plus_button_checker=0;
                                           out_buttom_check=1;// 1 Means out button will be disable
                                       }

                                       Out_list out_list = new Out_list("" + fname, "");
                                       out_lists.add(out_list);

                                   }

                                   if (referedCount>0){
                                       for (int k=1;k<=Integer.parseInt(ReferedPlayer);k++){

                                           if (k==0){
                                               In_list  in_list = new In_list("" + fname, "");
                                               in_lists.add(in_list);
                                           }else {
                                               In_list  in_list = new In_list("" + fname+"+"+k, "");
                                               in_lists.add(in_list);
                                           }
                                           in_squad++;
                                       }
                                   }

                   /* if (sharedPreferences.getString("userID","").equals(""+userID)){
                    }*/
                                   total_squad = i + 1;
                               }

                               referedCount = loginUser[0];

                               JSONObject  MatchInfo = jsonObject.getJSONObject("MatchInfo");

                               String gameId      = MatchInfo.getString("gameId");
                               String LocationName= MatchInfo.getString("locationName");
                               String image       = MatchInfo.getString("image");
                               String DateAdded   = MatchInfo.getString("DateAdded");
                               String matchDate   = MatchInfo.getString("matchDate");
                               String MatchTime   = MatchInfo.getString("MatchTime");
                               String matchDateFormated   = MatchInfo.getString("matchDateFormated");
                               String gId         = MatchInfo.getString("gId");
                               String DayNight    = MatchInfo.getString("DayNight");
                               String PitchNo     = MatchInfo.getString("PitchNo");
                               String MatchCost   = MatchInfo.getString("MatchCost");
                               String currency    = MatchInfo.getString("currency");
                               String aSide       = MatchInfo.getString("aSide");
                               String weather     = MatchInfo.getString("weather");
                               String status      = MatchInfo.getString("status");
                               String MatchTimeFormated= MatchInfo.getString("MatchTimeFormated");

                               //Picasso.with( img_group_icon.getContext()).load("http://qa.monshiapp.com"+image).into( img_group_icon);
                               Picasso.with( img_group_icon.getContext()).load(""+sharedPreferences.getString("group_Image","")).into( img_group_icon);

                               a_side = Integer.parseInt(aSide);
                               a_side = a_side * 2;

                               SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
                               try {
                                   Date date  = dateFormat.parse(MatchTime);
                                   MatchTime = dateFormat.format(date);
                               } catch (ParseException e) {
                                   e.printStackTrace();
                               }

                               String inputPattern = "yyyy-MM-dd";
                               String outputPattern = "EEEE, dd MMM yyyy";
                               SimpleDateFormat inputFormat  = new SimpleDateFormat(inputPattern);
                               SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

                               Date date = null;
                               try {
                                   date = inputFormat.parse(matchDate);
                                   matchDate = outputFormat.format(date);
                               } catch (ParseException e) {
                                   e.printStackTrace();
                               }

                               //tv_day_detail.setText(""+matchDate+"\nKO - "+MatchTime );
                               //tv_day_detail.setText(""+matchDateFormated+"\nKO - "+MatchTime );
                               tv_day_detail.setText(""+matchDateFormated+"\nKO - "+MatchTimeFormated );
                               tv_goals_stadium.setText(""+LocationName);

                               if (in_squad<=a_side){
                                   tv_count.setText(""+ in_squad +"/"+ a_side);
                               }

                               recyclerView_squad_list.setAdapter(new SquadListRecyclerViewAdapter(squadLists, mListener, getActivity()));
                               recyclerView_in_list.setAdapter(new InListRecyclerViewAdapter(in_lists, mListener, getActivity()));
                               recyclerView_out_list.setAdapter(new OutListRecyclerViewAdapter(out_lists, mListener, getActivity()));

                           } catch (JSONException e) {
                               e.printStackTrace();
                           }

                       }while (cursor.moveToNext());
                       //mydb.delete_record_groups();
                   }
               }else {
                   view_white.setVisibility(View.VISIBLE);
                   Toast.makeText(getActivity(),"Connectivity failure !!!", Toast.LENGTH_SHORT).show();
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
        public void onResume() {
            super.onResume();
            //Toast.makeText(getActivity(),""+global,Toast.LENGTH_SHORT).show();
            activity = getActivity();

            if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                //Toast.makeText(getActivity(),"fragment 1 ",Toast.LENGTH_SHORT).show();
                if (!getUserVisibleHint()) {
                    return;
                }

                total_squad = 0;in_squad = 0;a_side=0;
                getmatchparticipents();

                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                Picasso.with( img_group_icon.getContext()).load(""+sharedPreferences.getString("group_Image","")).into( img_group_icon);
                ImageView edit_icon = getActivity().findViewById(R.id.edit_icon);
                edit_icon.setVisibility(View.VISIBLE);
                edit_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Toast.makeText(ChatPageActivity.this,"Click here",Toast.LENGTH_SHORT).show();
                        // alertDialogMatch_Status(ChatPageActivity.this);
                        if (intercheck==1){

                            if (chatPageActivity.send_user_role().equals("SuperAdmin") || chatPageActivity.send_user_role().equals("Admin") ){

                                Bundle bundle = new Bundle();
                                bundle.putString("group_Id",chatPageActivity.send_group_Id());
                                bundle.putString("gameId",chatPageActivity.send_game_Id());

                                Intent intent = new Intent(getActivity(),UpdateGroupActivity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);

                            }else {
                                Bundle bundle = new Bundle();
                                bundle.putString("group_Id",chatPageActivity.send_group_Id());
                                bundle.putString("gameId",chatPageActivity.send_game_Id());

                                Intent intent = new Intent(getActivity(),GroupDetailActivity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }

                        }else {
                            Toast.makeText(getActivity(),"Connectivity failure !!!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                //Toast.makeText(getActivity(),"fragment 2 is visible ",Toast.LENGTH_SHORT).show();
                if (!getUserVisibleHint()) {
                    return;
                }

                service_chat = new Intent(getActivity(), Incoming_Chat_Service.class);
                getActivity().stopService(service_chat);
                getActivity().startService(service_chat);

                IntentFilter filter = new IntentFilter();
                filter.addAction("android.intent.action.SmsReceiver");
                activity.registerReceiver(ReceivefromService, filter);

                ImageView edit_icon = getActivity().findViewById(R.id.edit_icon);
                edit_icon.setVisibility(View.GONE);

                if (wifi_checker==0){
                    message_ArrayList.clear();
                    //Toast.makeText(context,"inside the chat wifi_checker==0",Toast.LENGTH_SHORT).show();
                    MyDatabase mydb = new MyDatabase(activity);
                    Cursor c = mydb.getdata_chat(chatPageActivity.send_group_Id());
                    if (c.getCount()>0) {
                        if (c.moveToFirst()) {
                            do {
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
                    }else {
                        //Toast.makeText(getActivity(),"Connectivity failure !!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else {
                //Toast.makeText(getActivity(),"in onresume fragment 3 ",Toast.LENGTH_SHORT).show();
                if (!getUserVisibleHint()) {
                    return;
                }
                if (wifi_checker==1){

                    linearLayout_match_detail.setVisibility(View.VISIBLE);
                    final String url = getResources().getString(R.string.url) + "getGroupMatchDetails?gId="+chatPageActivity.send_group_Id();
                    final String params = "";
                    final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                        @Override
                        public String TaskCompletionResult(String result) throws JSONException {

                            JSONObject jsonObject = new JSONObject(result);

                            if(jsonObject.getString("respCode").equals("200")) {
                                JSONObject results = jsonObject.getJSONObject("results");

                                JSONArray locationImages      = jsonObject.getJSONArray("locationImages");
                                JSONArray boots               = jsonObject.getJSONArray("boots");
                                JSONArray additionalFacilities= jsonObject.getJSONArray("additionalFacilities");
                                JSONArray restrictions        = jsonObject.getJSONArray("restrictions");

                                gameId      = results.getString("gameId");

                                mydb.insertdata_match_details(""+chatPageActivity.send_group_Id(),
                                                              ""+gameId,""+results.toString());

                                stadiumName = results.getString("addressLane1");
                                stadiumName+= "\n"+results.getString("addressLane2");
                                LocationName= results.getString("locationName");
                                image       = results.getString("image");
                                DateAdded   = results.getString("DateAdded");
                                matchDate   = results.getString("matchDateFormated");
                                MatchTime   = results.getString("MatchTimeFormated");
                                gId         = results.getString("gId");
                                DayNight    = results.getString("DayNight");
                                PitchNo     = results.getString("PitchNo");
                                MatchCost   = results.getString("MatchCost");
                                match_type  = results.getString("match_type");
                                currency    = results.getString("currency");
                                aSide       = results.getString("aSide");
                                substitutes = results.getString("substitutes");
                                weather     = results.getString("weather");
                                Turf        = results.getString("ptichTurf");
                                allowboots  = results.getString("allowBoots");
                                status      = results.getString("status");
                                lat         = results.getString("lat");
                                lng         = results.getString("lng");
                                real_time_to_pass= results.getString("MatchTime");


                                String pitchName          = results.getString("pitchName");
                                String ptichTurf          = results.getString("ptichTurf");
                                String mrestrictions      = results.getString("restrictions");
                                String referee            = results.getString("referee");
                                String kitColors          = results.getString("kitColors");
                                String gameType           = results.getString("gameType");
                                String addtionalFacilities= results.getString("addtionalFacilities");
                                String note               = results.getString("note");
                                String recurringType      = results.getString("recurringType");

                                tv_name.         setText(""+LocationName);
                                tv_location.     setText(""+stadiumName);
                                tv_date.         setText(""+matchDate);
                                tv_kickoff.      setText(""+MatchTime);
                                tv_match_referee.setText(""+referee);
                                tv_gametype.     setText(""+gameType);
                                tv_match_note.   setText(""+note);

                                String[] separated = kitColors.split("\\^");

                                if (separated[0].equals("Skins")){
                                    tv_team_1.setBackgroundColor(getResources().getColor(R.color.color_skin));

                                }else if (separated[0].equals("Red")){
                                    tv_team_1.setBackgroundColor(getResources().getColor(R.color.color_red));
                                    tv_team_1.setTextColor(getResources().getColor(R.color.colorWhite));
                                }
                                else if (separated[0].equals("Orange")){
                                    tv_team_1.setBackgroundColor(getResources().getColor(R.color.colorOrange));

                                }else if (separated[0].equals("Yellow")){
                                    tv_team_1.setBackgroundColor(getResources().getColor(R.color.color_yellow));

                                }
                                else if (separated[0].equals("Green")){
                                    tv_team_1.setBackgroundColor(getResources().getColor(R.color.color_green));

                                }else if (separated[0].equals("Blue")){
                                    tv_team_1.setBackgroundColor(getResources().getColor(R.color.squadin));

                                }
                                else if (separated[0].equals("Purple")){
                                    tv_team_1.setBackgroundColor(getResources().getColor(R.color.color_purple));
                                    tv_team_1.setTextColor(getResources().getColor(R.color.colorWhite));
                                }else if (separated[0].equals("Pink")){
                                    tv_team_1.setBackgroundColor(getResources().getColor(R.color.color_pink));

                                }
                                else if (separated[0].equals("Brown")){
                                    tv_team_1.setBackgroundColor(getResources().getColor(R.color.color_brown));

                                }else if (separated[0].equals("Black")){
                                    tv_team_1.setBackgroundColor(getResources().getColor(R.color.color_black));
                                    tv_team_1.setTextColor(getResources().getColor(R.color.colorWhite));

                                }
                                else if (separated[0].equals("White")){
                                    tv_team_1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                                    tv_team_1.setTextColor(getResources().getColor(R.color.color_black));

                                }else if (separated[0].equals("Grey")){
                                    tv_team_1.setBackgroundColor(getResources().getColor(R.color.grey_light));
                                }
                                tv_team_1.setText(""+separated[0]);
                                if (separated[1].equals("Skins")){
                                    tv_team_2.setBackgroundColor(getResources().getColor(R.color.color_skin));

                                }
                                else if (separated[1].equals("Red")){
                                    tv_team_2.setBackgroundColor(getResources().getColor(R.color.color_red));
                                    tv_team_1.setTextColor(getResources().getColor(R.color.colorWhite));
                                }
                                else if (separated[1].equals("Orange")){
                                    tv_team_2.setBackgroundColor(getResources().getColor(R.color.colorOrange));

                                }
                                else if (separated[1].equals("Yellow")){
                                    tv_team_2.setBackgroundColor(getResources().getColor(R.color.color_yellow));

                                }
                                else if (separated[1].equals("Green")){
                                    tv_team_2.setBackgroundColor(getResources().getColor(R.color.color_green));

                                }
                                else if (separated[1].equals("Blue")){
                                    tv_team_2.setBackgroundColor(getResources().getColor(R.color.squadin));

                                }
                                else if (separated[1].equals("Purple")){
                                    tv_team_2.setBackgroundColor(getResources().getColor(R.color.color_purple));
                                    tv_team_1.setTextColor(getResources().getColor(R.color.colorWhite));
                                }
                                else if (separated[1].equals("Pink")){
                                    tv_team_2.setBackgroundColor(getResources().getColor(R.color.color_pink));

                                }
                                else if (separated[1].equals("Brown")){
                                    tv_team_2.setBackgroundColor(getResources().getColor(R.color.color_brown));

                                }
                                else if (separated[1].equals("Black")){
                                    tv_team_2.setBackgroundColor(getResources().getColor(R.color.color_black));
                                    tv_team_2.setTextColor(getResources().getColor(R.color.colorWhite));

                                }
                                else if (separated[1].equals("White")){
                                    tv_team_2.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                                    tv_team_2.setTextColor(getResources().getColor(R.color.color_black));

                                }
                                else if (separated[1].equals("Grey")){
                                    tv_team_2.setBackgroundColor(getResources().getColor(R.color.grey_light));
                                }
                                tv_team_2.setText(""+separated[1]);

                           /* if (DayNight.equals("day")){
                                DayNight="Day";
                            }else {
                                DayNight="Night";
                            }*/

                                String[] boot_Name = new String [allowboots.replaceAll("\\D", "").length()];
                                String[] boot_ID   = new String [allowboots.replaceAll("\\D", "").length()];

                                int counter=0;String str_boots="";
                                for (int i=0;i<boots.length();i++){

                                    JSONObject c = boots.getJSONObject(i);
                                    String bID = c.getString("bID");
                                    String bName = c.getString("bName");
                                    String bStatus = c.getString("bStatus");

                                    if (allowboots.contains(bID)){

                                        boot_ID[counter]  = bID;
                                        boot_Name[counter]= bName;
                                        str_boots += bName+", ";
                                        counter++;
                                        //Toast.makeText(MainActivity.this,"position = "+position+" bid = "+bID,Toast.LENGTH_LONG).show();
                                    }
                                }

                                tv_boots.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                                tv_boots.setHorizontallyScrolling(true);
                                tv_boots.setMarqueeRepeatLimit(-1);
                                tv_boots.setFocusable(true);
                                tv_boots.setFocusableInTouchMode(true);
                                tv_boots.setSelected(true);
                                tv_boots.setTextColor(getActivity().getResources().getColor(R.color.color_black));
                                tv_boots.setText(removeLastChar(removeLastChar(str_boots)));

                                String[] fact_Name = new String [addtionalFacilities.replaceAll("\\D", "").length()];
                                String[] fact_ID   = new String [addtionalFacilities.replaceAll("\\D", "").length()];

                                //Toast.makeText(MainActivity.this,"position = "+position,Toast.LENGTH_LONG).show();
                                counter = 0; String facilities="";

                                for(int i = 0; i < additionalFacilities.length(); i++) {
                                    JSONObject c = additionalFacilities.getJSONObject(i);

                                    String fID = c.getString("fID");
                                    String fName = c.getString("fName");
                                    String fStatus = c.getString("fStatus");

                                    if (addtionalFacilities.contains(fID)){

                                        fact_ID[counter]  = fID;
                                        fact_Name[counter]= fName;
                                        facilities+=fName+", ";
                                        counter++;
                                        //Toast.makeText(MainActivity.this,"position = "+position+" bid = "+bID,Toast.LENGTH_LONG).show();
                                    }
                                    //Toast.makeText(MainActivity.this,"bStatus = "+bStatus,Toast.LENGTH_LONG).show();
                                }

                                tv_facilities.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                                tv_facilities.setHorizontallyScrolling(true);
                                tv_facilities.setMarqueeRepeatLimit(-1);
                                tv_facilities.setFocusable(true);
                                tv_facilities.setFocusableInTouchMode(true);
                                tv_facilities.setSelected(true);
                                tv_facilities.setTextColor(getActivity().getResources().getColor(R.color.color_black));
                                tv_facilities.setText(removeLastChar(removeLastChar(facilities)));

                                String[] rest_Name = new String [mrestrictions.replaceAll("\\D", "").length()];
                                String[] rest_ID   = new String [mrestrictions.replaceAll("\\D", "").length()];

                                //Toast.makeText(MainActivity.this,"position = "+position,Toast.LENGTH_LONG).show();
                                counter = 0; String rest="";

                                for(int i = 0; i < restrictions.length(); i++) {
                                    JSONObject c = restrictions.getJSONObject(i);

                                    String id = c.getString("id");
                                    String restriction = c.getString("restriction");
                                    String resStatus = c.getString("resStatus");

                                    if (mrestrictions.contains(id)){

                                        rest_ID[counter]  = id;
                                        rest_Name[counter]= restriction;
                                        rest+=""+restriction+", ";
                                        counter++;
                                        //Toast.makeText(MainActivity.this,"position = "+position+" bid = "+bID,Toast.LENGTH_LONG).show();
                                    }
                                    //Toast.makeText(MainActivity.this,"bStatus = "+bStatus,Toast.LENGTH_LONG).show();
                                }

                                tv_restriction.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                                tv_restriction.setHorizontallyScrolling(true);
                                tv_restriction.setMarqueeRepeatLimit(-1);
                                tv_restriction.setFocusable(true);
                                tv_restriction.setFocusableInTouchMode(true);
                                tv_restriction.setSelected(true);
                                tv_restriction.setTextColor(getActivity().getResources().getColor(R.color.color_black));
                                tv_restriction.setText(removeLastChar(removeLastChar(rest)));

                                tv_day_night.setText(""+DayNight);
                                tv_pitch_no.setSingleLine(true);
                                tv_pitch_no.setText(""+pitchName);
                                tv_side.setText(""+aSide);
                                tv_truf.setText(""+ptichTurf);
                                tv_condition.setText(""+weather);
                                tv_cost.setText(""+MatchCost);
                                tv_match_type.setText(""+recurringType);

                                //Picasso.with(imageView.getContext()).load(""+getActivity().getResources().getString(R.string.imageBaseUrl)+image).into(imageView);

                                sliderLayout.removeAllSliders();

                                for(int i = 0; i < locationImages.length(); i++) {
                                    JSONObject c = locationImages.getJSONObject(i);

                                    Log.e("TAG","inside locationImage loop");

                                    String id = c.getString("id");
                                    String locationID = c.getString("locationID");
                                    String locationImage = c.getString("locationImage");
                                    String ImageType= c.getString("ImageType");
                                    String date = c.getString("date");

                                    locationImage = getActivity().getResources().getString(R.string.imageBaseUrl)+
                                            getActivity().getResources().getString(R.string.remaining_path)+locationImage;

                                    Log.e("TAG","imagepath = "+locationImage);

                                    TextSliderView textSliderView = new TextSliderView(getActivity());

                                    Log.e("TAG","ImageType = "+ImageType+" tv_day_night = "+tv_day_night.getText().toString());

                                    if (ImageType.equals("0") && tv_day_night.getText().toString().equals("Day")){
                                       // Toast.makeText(getActivity(),"0",Toast.LENGTH_SHORT).show();
                                        textSliderView
                                                .description("")
                                                .image(locationImage)
                                                .setScaleType(BaseSliderView.ScaleType.Fit)
                                                .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                                    @Override
                                                    public void onSliderClick(BaseSliderView slider) {

                                                    }
                                                });

                                        textSliderView.bundle(new Bundle());

                                        textSliderView.getBundle()
                                                .putString("extra","duumy");

                                        sliderLayout.addSlider(textSliderView);

                                    }
                                    else if(ImageType.equals("1") && tv_day_night.getText().toString().equals("Night")) {
                                        //Toast.makeText(getActivity(),"1",Toast.LENGTH_SHORT).show();
                                        textSliderView
                                                .description("")
                                                .image(locationImage)
                                                .setScaleType(BaseSliderView.ScaleType.Fit)
                                                .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                                    @Override
                                                    public void onSliderClick(BaseSliderView slider) {

                                                    }
                                                });

                                        textSliderView.bundle(new Bundle());

                                        textSliderView.getBundle()
                                                .putString("extra","duumy");

                                        sliderLayout.addSlider(textSliderView);
                                    }
                                }

                                sliderLayout.setPresetTransformer(SliderLayout.Transformer.DepthPage);

                                sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);

                                sliderLayout.setCustomAnimation(new DescriptionAnimation());

                                sliderLayout.setDuration(3000);

                                sliderLayout.addOnPageChangeListener(chatPageActivity);

                                matchDate =  matchDate.replace("Monday"   , "");
                                matchDate =  matchDate.replace("Tuesday"  , "");
                                matchDate =  matchDate.replace("Wednesday", "");
                                matchDate =  matchDate.replace("Thursday" , "");
                                matchDate =  matchDate.replace("Friday"   , "");
                                matchDate =  matchDate.replace("Saturday" , "");
                                matchDate =  matchDate.replace("Sunday"   , "");

                                matchDate =  matchDate.replace("st", "");
                                matchDate =  matchDate.replace("nd", "");
                                matchDate =  matchDate.replace("rd", "");
                                matchDate =  matchDate.replace("th", "");

                                Date c_date = null,c,date=null;
                                SimpleDateFormat dateFormat = new SimpleDateFormat("ddd MMM yyy",Locale.getDefault());
                                c = Calendar.getInstance().getTime();
                                System.out.println("matchDate => " + matchDate);
                                String str_c_date = dateFormat.format(c);

                                try {
                                    date = dateFormat.parse(matchDate);
                                    c_date = dateFormat.parse(str_c_date);
                                    System.out.println(date);
                                    System.out.println(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                System.out.println("date => " + date);
                                System.out.println("c_date => " + c_date);
                                //Toast.makeText(getActivity(),"serverdate = "+date.toString()+"  currentdate = "+c_date,Toast.LENGTH_LONG).show();

                                if (date.compareTo(c_date) < 0 && match_type.equals("Recurring")) {
                                    System.out.println("date2 is Greater than my date1");
                                    btn_reminder.setVisibility(View.VISIBLE);
                                    btn_reminder.setText("Reschedule");

                                    TextView textView_title = new TextView(getActivity());
                                    textView_title.setText("Recurring Match");
                                    textView_title.setGravity(Gravity.START);
                                    textView_title.setPadding(20,10,20,10);
                                    textView_title.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
                                    textView_title.setCompoundDrawablePadding(10);

                                    //textView_title.setCompoundDrawables(null,null,mContext.getResources().getDrawable(R.drawable.ic_warning_colored_24dp),null);
                                    textView_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_warning_colored_24dp, 0);

                                    new AlertDialog.Builder(getActivity())
                                            .setCustomTitle(textView_title)
                                            .setMessage("This is Recurring Match would you like to Reschedule it again? ")
                                            // Specifying a listener allows you to take an action before dismissing the dialog.
                                            // The dialog is automatically dismissed when a dialog button is clicked.
                                            .setPositiveButton(getActivity().getResources().getString(R.string.str_yes), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("group_Id"    ,chatPageActivity.send_group_Id());
                                                    bundle.putString("game_Id"     ,chatPageActivity.send_game_Id());
                                                    bundle.putString("stadiumName" ,stadiumName);
                                                    bundle.putString("LocationName",LocationName);
                                                    bundle.putString("image"       ,image);
                                                    bundle.putString("gametype"    ,tv_gametype.getText().toString());
                                                    bundle.putString("DateAdded"   ,tv_date.getText().toString());
                                                    bundle.putString("MatchTime"   ,tv_kickoff.getText().toString());
                                                    bundle.putString("DayNight"    ,DayNight);
                                                    bundle.putString("PitchNo"     ,tv_pitch_no.getText().toString());
                                                    bundle.putString("aSide"       ,aSide);
                                                    bundle.putString("cost"        ,tv_cost.getText().toString());
                                                    bundle.putString("Turf"        ,tv_truf.getText().toString());
                                                    bundle.putString("recursive"   ,tv_match_type.getText().toString());
                                                    bundle.putString("team_1"      ,tv_team_1.getText().toString());
                                                    bundle.putString("team_2"      ,tv_team_2.getText().toString());
                                                    bundle.putString("referee"     ,tv_match_referee.getText().toString());
                                                    bundle.putString("Boots"       ,Boots);
                                                    bundle.putString("weather"     ,weather);
                                                    bundle.putString("currency"    ,currency);
                                                    bundle.putString("substitutes" ,substitutes);
                                                    bundle.putString("note"        ,tv_match_note.getText().toString());

                                                    Intent intent = new Intent(getActivity(),UpdateMatchActivity.class);
                                                    intent.putExtras(bundle);
                                                    startActivity(intent);

                                                }
                                            })
                                            // A null listener allows the button to dismiss the dialog and take no further action.
                                            .setNegativeButton(getActivity().getResources().getString(R.string.str_no), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .show();


                                    btn_reminder.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Bundle bundle = new Bundle();

                                            bundle.putString("group_Id"    ,chatPageActivity.send_group_Id());
                                            bundle.putString("game_Id"     ,chatPageActivity.send_game_Id());
                                            bundle.putString("stadiumName" ,stadiumName);
                                            bundle.putString("LocationName",LocationName);
                                            bundle.putString("image"       ,image);
                                            bundle.putString("gametype"    ,tv_gametype.getText().toString());
                                            bundle.putString("DateAdded"   ,tv_date.getText().toString());
                                            bundle.putString("MatchTime"   ,tv_kickoff.getText().toString());
                                            bundle.putString("DayNight"    ,DayNight);
                                            bundle.putString("PitchNo"     ,tv_pitch_no.getText().toString());
                                            bundle.putString("aSide"       ,aSide);
                                            bundle.putString("cost"        ,tv_cost.getText().toString());
                                            bundle.putString("Turf"        ,tv_truf.getText().toString());
                                            bundle.putString("recursive"   ,tv_match_type.getText().toString());
                                            bundle.putString("team_1"      ,tv_team_1.getText().toString());
                                            bundle.putString("team_2"      ,tv_team_2.getText().toString());
                                            bundle.putString("referee"     ,tv_match_referee.getText().toString());
                                            bundle.putString("Boots"       ,Boots);
                                            bundle.putString("weather"     ,weather);
                                            bundle.putString("currency"    ,currency);
                                            bundle.putString("substitutes" ,substitutes);
                                            bundle.putString("note"        ,tv_match_note.getText().toString());

                                            Intent intent = new Intent(getActivity(),UpdateMatchActivity.class);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                    });
                                }
                                else {

                                    btn_reminder.setVisibility(View.VISIBLE);
                                    btn_reminder.setText("Set Reminder");
                                    btn_reminder.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMM yyyy");

                                            String date_to_pass="";

                                            date_to_pass =  tv_date.getText().toString().replace("st", "");
                                            date_to_pass =  tv_date.getText().toString().replace("nd", "");
                                            date_to_pass =  tv_date.getText().toString().replace("rd", "");
                                            date_to_pass =  tv_date.getText().toString().replace("th", "");

                                            //chatPageActivity.call_calendar(tv_name.getText().toString(),date_to_pass);

                                            System.out.println("date 1 = "+date_to_pass);

                                            try {
                                                Date date = sdf.parse(date_to_pass);

                                                long endTime   =  date.getTime();

                                                System.out.println("date 2 = "+endTime);

                                                chatPageActivity.call_calendar(tv_name.getText().toString(),endTime);

                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }

                            }
                            else{

                            }

                            return result;
                        }
                    });
                    webRequestCall.execute(url,"GET",params);

                    if (chatPageActivity.send_user_role().equals("SuperAdmin")){

                        ImageView edit_icon = getActivity().findViewById(R.id.edit_icon);
                        edit_icon.setVisibility(View.VISIBLE);
                        edit_icon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Toast.makeText(getActivity(),"Click here",Toast.LENGTH_SHORT).show();
                                //alertDialogMatch_Status(ChatPageActivity.this);

                                if (intercheck==1){

                                    Bundle bundle = new Bundle();

                                    bundle.putString("group_Id"    ,chatPageActivity.send_group_Id());
                                    bundle.putString("game_Id"     ,chatPageActivity.send_game_Id());
                                    bundle.putString("stadiumName" ,stadiumName);
                                    bundle.putString("LocationName",LocationName);
                                    bundle.putString("image"       ,image);
                                    bundle.putString("gametype"    ,tv_gametype.getText().toString());
                                    bundle.putString("DateAdded"   ,tv_date.getText().toString());
                                    bundle.putString("MatchTime"   ,tv_kickoff.getText().toString());
                                    bundle.putString("DayNight"    ,DayNight);
                                    bundle.putString("PitchNo"     ,tv_pitch_no.getText().toString());
                                    bundle.putString("aSide"       ,aSide);
                                    bundle.putString("cost"        ,tv_cost.getText().toString());
                                    bundle.putString("Turf"        ,tv_truf.getText().toString());
                                    bundle.putString("recursive"   ,tv_match_type.getText().toString());
                                    bundle.putString("team_1"      ,tv_team_1.getText().toString());
                                    bundle.putString("team_2"      ,tv_team_2.getText().toString());
                                    bundle.putString("referee"     ,tv_match_referee.getText().toString());
                                    bundle.putString("Boots"       ,Boots);
                                    bundle.putString("weather"     ,weather);
                                    bundle.putString("currency"    ,currency);
                                    bundle.putString("substitutes" ,substitutes);
                                    bundle.putString("note"        ,tv_match_note.getText().toString());

                                    Intent intent = new Intent(getActivity(),UpdateMatchActivity.class);
                                    intent.putExtras(bundle);
                                    startActivity(intent);

                                }
                                else {
                                    Toast.makeText(getActivity(),"Connectivity failure !!!",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }else {
                        ImageView edit_icon = getActivity().findViewById(R.id.edit_icon);
                        edit_icon.setVisibility(View.GONE);
                        btn_cancel_game.setVisibility(View.GONE);
                    }
                }
                else {

                    Log.i("wifi_checker","wifi_checker = "+wifi_checker);

                    MyDatabase mydb     = new MyDatabase(activity);

                    //===============================================
                    Cursor cursor_boots = mydb.getdata_boots();
                    final JSONArray jsonObject_boots   = new JSONArray();
                    if (cursor_boots.getCount()>0){
                        if (cursor_boots.moveToFirst()){
                            do {
                                JSONObject jsonobj   = new JSONObject();

                                String bID    = cursor_boots.getString(0);
                                String bName  = cursor_boots.getString(1);
                                String bStatus= cursor_boots.getString(2);

                                Log.e("YYYY",
                                           " bID          = "+bID+
                                                " bName       = "+bName+
                                                " bStatus= "+bStatus);

                                try {
                                    jsonobj.put("bID", ""+bID);
                                    jsonobj.put("bName", ""+bName);
                                    jsonobj.put("bStatus", ""+bStatus);
                                    jsonObject_boots.put(jsonobj);
                                    Log.i("wifi_checker","jsonObject_boots = "+jsonObject_boots.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } while (cursor_boots.moveToNext());
                            //mydb.delete_record_groups()
                        }
                    }
                    //===============================================

                    //===============================================
                    Cursor cursor_facilities = mydb.getdata_facilities();
                    final JSONArray jsonObject_facilities   = new JSONArray();
                    if (cursor_facilities.getCount()>0){
                        if (cursor_facilities.moveToFirst()){
                            do {
                                JSONObject jsonobj   = new JSONObject();

                                String fID    = cursor_facilities.getString(0);
                                String fName  = cursor_facilities.getString(1);
                                String fStatus= cursor_facilities.getString(2);

                                Log.e("YYYY",
                                          " fID     = "+fID+
                                                " fName  = "+fName+
                                                " fStatus= "+fStatus);

                                try {
                                    jsonobj.put("fID", ""+fID);
                                    jsonobj.put("fName", ""+fName);
                                    jsonobj.put("fStatus", ""+fStatus);
                                    jsonObject_facilities.put(jsonobj);
                                    Log.i("wifi_checker","jsonObject_facilities = "+jsonObject_facilities.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } while (cursor_facilities.moveToNext());
                            //mydb.delete_record_groups()
                        }
                    }
                    //===============================================

                    //===============================================
                    Cursor cursor_restriction = mydb.getdata_restriction();
                    final JSONArray jsonObject_restriction = new JSONArray();
                    if (cursor_restriction.getCount()>0){
                        if (cursor_restriction.moveToFirst()){
                            do {
                                JSONObject jsonobj   = new JSONObject();

                                String id    = cursor_restriction.getString(0);
                                String restriction  = cursor_restriction.getString(1);
                                String resStatus= cursor_restriction.getString(2);

                                Log.e("YYYY",
                                        " id     = "+id+
                                                " restriction  = "+restriction+
                                                " resStatus= "+resStatus);

                                try {
                                    jsonobj.put("id", ""+id);
                                    jsonobj.put("restriction", ""+restriction);
                                    jsonobj.put("resStatus", ""+resStatus);
                                    jsonObject_restriction.put(jsonobj);
                                    Log.i("wifi_checker","jsonObject_restriction = "+jsonObject_restriction.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } while (cursor_restriction.moveToNext());
                            //mydb.delete_record_groups()
                        }
                    }
                    //===============================================


                    //===============================================
                    Cursor cursor_locationImages = mydb.getdata_locationImages();
                    final JSONArray jsonObject_locationImages= new JSONArray();
                    if (cursor_locationImages.getCount()>0){
                        if (cursor_locationImages.moveToFirst()){
                            do {
                                JSONObject jsonobj   = new JSONObject();

                                String locationID   = cursor_locationImages.getString(0);
                                String locationImage= cursor_locationImages.getString(1);
                                String ImageType    = cursor_locationImages.getString(2);

                                Log.e("YYYY",
                                           " locationID   = "+locationID+
                                                " locationImage= "+locationImage+
                                                " ImageType= "+ImageType);

                                try {
                                    jsonobj.put("locationID", ""+locationID);
                                    jsonobj.put("locationImage", ""+locationImage);
                                    jsonobj.put("ImageType", ""+ImageType);
                                    jsonObject_locationImages.put(jsonobj);
                                    Log.i("wifi_checker","jsonObject_locationImages = "+jsonObject_locationImages.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } while (cursor_locationImages.moveToNext());
                            //mydb.delete_record_groups()
                        }
                    }
                    //===============================================

                    Log.i("wifi_checker","group_id = "+chatPageActivity.send_group_Id());
                    Cursor cursor       = mydb.getdata_match_details(chatPageActivity.send_group_Id());
                    if (cursor.getCount()>0){
                        if (cursor.moveToFirst()){
                            do {
                                String _id          = cursor.getString(0);
                                String grp_id       = cursor.getString(1);
                                String match_details= cursor.getString(2);

                                Log.e("YYYY","id      = "+_id+
                                        " grp_id        = "+grp_id+
                                        " match_details = "+match_details);

                                try {
                                    JSONObject jsonObject= new JSONObject();
                                    JSONObject match_details_= new JSONObject(match_details);

                                    jsonObject.put("results"             ,match_details_);
                                    jsonObject.put("boots"               ,jsonObject_boots);
                                    jsonObject.put("additionalFacilities",jsonObject_facilities);
                                    jsonObject.put("restrictions"        ,jsonObject_restriction);
                                    jsonObject.put("locationImages"      ,jsonObject_locationImages);

                                    Log.e("YYYY","jsonObject = "+jsonObject);

                                    JSONObject results = jsonObject.getJSONObject("results");

                                    JSONArray boots               = jsonObject.getJSONArray("boots");
                                    JSONArray additionalFacilities= jsonObject.getJSONArray("additionalFacilities");
                                    JSONArray restrictions        = jsonObject.getJSONArray("restrictions");
                                    JSONArray locationImages      = jsonObject.getJSONArray("locationImages");

                                    gameId           = results.getString("gameId");
                                    stadiumName      = results.getString("addressLane1");
                                    stadiumName     += "\n"+results.getString("addressLane2");
                                    LocationName     = results.getString("locationName");
                                    image            = results.getString("image");
                                    DateAdded        = results.getString("DateAdded");
                                    matchDate        = results.getString("matchDateFormated");
                                    MatchTime        = results.getString("MatchTimeFormated");
                                    gId              = results.getString("gId");
                                    DayNight         = results.getString("DayNight");
                                    PitchNo          = results.getString("PitchNo");
                                    MatchCost        = results.getString("MatchCost");
                                    match_type       = results.getString("match_type");
                                    currency         = results.getString("currency");
                                    aSide            = results.getString("aSide");
                                    substitutes      = results.getString("substitutes");
                                    weather          = results.getString("weather");
                                    Turf             = results.getString("ptichTurf");
                                    allowboots       = results.getString("allowBoots");
                                    status           = results.getString("status");
                                    lat              = results.getString("lat");
                                    lng              = results.getString("lng");
                                    real_time_to_pass= results.getString("MatchTime");

                                    String pitchName          = results.getString("pitchName");
                                    String ptichTurf          = results.getString("ptichTurf");
                                    String mrestrictions      = results.getString("restrictions");
                                    String referee            = results.getString("referee");
                                    String kitColors          = results.getString("kitColors");
                                    String gameType           = results.getString("gameType");
                                    String addtionalFacilities= results.getString("addtionalFacilities");
                                    String note               = results.getString("note");
                                    String recurringType      = results.getString("recurringType");

                                    tv_name.         setText(""+LocationName);
                                    tv_location.     setText(""+stadiumName);
                                    tv_date.         setText(""+matchDate);
                                    tv_kickoff.      setText(""+MatchTime);
                                    tv_match_referee.setText(""+referee);
                                    tv_gametype.     setText(""+gameType);
                                    tv_match_note.   setText(""+note);

                                    String[] separated = kitColors.split("\\^");

                                    if (separated[0].equals("Skins")){
                                        tv_team_1.setBackgroundColor(getResources().getColor(R.color.color_skin));

                                    }else if (separated[0].equals("Red")){
                                        tv_team_1.setBackgroundColor(getResources().getColor(R.color.color_red));

                                    }else if (separated[0].equals("Orange")){
                                        tv_team_1.setBackgroundColor(getResources().getColor(R.color.colorOrange));

                                    }else if (separated[0].equals("Yellow")){
                                        tv_team_1.setBackgroundColor(getResources().getColor(R.color.color_yellow));

                                    }else if (separated[0].equals("Green")){
                                        tv_team_1.setBackgroundColor(getResources().getColor(R.color.color_green));

                                    }else if (separated[0].equals("Blue")){
                                        tv_team_1.setBackgroundColor(getResources().getColor(R.color.squadin));

                                    }else if (separated[0].equals("Purple")){
                                        tv_team_1.setBackgroundColor(getResources().getColor(R.color.color_purple));

                                    }else if (separated[0].equals("Pink")){
                                        tv_team_1.setBackgroundColor(getResources().getColor(R.color.color_pink));

                                    }else if (separated[0].equals("Brown")){
                                        tv_team_1.setBackgroundColor(getResources().getColor(R.color.color_brown));

                                    }else if (separated[0].equals("Black")){
                                        tv_team_1.setBackgroundColor(getResources().getColor(R.color.color_black));
                                        tv_team_1.setTextColor(getResources().getColor(R.color.colorWhite));

                                    }else if (separated[0].equals("White")){
                                        tv_team_1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                                        tv_team_1.setTextColor(getResources().getColor(R.color.color_black));

                                    }else if (separated[0].equals("Grey")){
                                        tv_team_1.setBackgroundColor(getResources().getColor(R.color.grey_light));
                                    }
                                    tv_team_1.setText(""+separated[0]);

                                    if (separated[1].equals("Skins")){
                                        tv_team_2.setBackgroundColor(getResources().getColor(R.color.color_skin));

                                    }else if (separated[1].equals("Red")){
                                        tv_team_2.setBackgroundColor(getResources().getColor(R.color.color_red));

                                    }else if (separated[1].equals("Orange")){
                                        tv_team_2.setBackgroundColor(getResources().getColor(R.color.colorOrange));

                                    }else if (separated[1].equals("Yellow")){
                                        tv_team_2.setBackgroundColor(getResources().getColor(R.color.color_yellow));

                                    }else if (separated[1].equals("Green")){
                                        tv_team_2.setBackgroundColor(getResources().getColor(R.color.color_green));

                                    }else if (separated[1].equals("Blue")){
                                        tv_team_2.setBackgroundColor(getResources().getColor(R.color.squadin));

                                    }else if (separated[1].equals("Purple")){
                                        tv_team_2.setBackgroundColor(getResources().getColor(R.color.color_purple));

                                    }else if (separated[1].equals("Pink")){
                                        tv_team_2.setBackgroundColor(getResources().getColor(R.color.color_pink));

                                    }else if (separated[1].equals("Brown")){
                                        tv_team_2.setBackgroundColor(getResources().getColor(R.color.color_brown));

                                    }else if (separated[1].equals("Black")){
                                        tv_team_2.setBackgroundColor(getResources().getColor(R.color.color_black));
                                        tv_team_2.setTextColor(getResources().getColor(R.color.colorWhite));

                                    }else if (separated[1].equals("White")){
                                        tv_team_2.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                                        tv_team_2.setTextColor(getResources().getColor(R.color.color_black));

                                    }else if (separated[1].equals("Grey")){
                                        tv_team_2.setBackgroundColor(getResources().getColor(R.color.grey_light));
                                    }
                                    tv_team_2.setText(""+separated[1]);

                                   /*
                                   if (DayNight.equals("day")){
                                         DayNight="Day";
                                      }else {
                                         DayNight="Night";
                                      }
                                    */
                                    String[] boot_Name = new String [allowboots.replaceAll("\\D", "").length()];
                                    String[] boot_ID   = new String [allowboots.replaceAll("\\D", "").length()];

                                    int counter=0;String str_boots="";
                                    for (int i=0;i<boots.length();i++){

                                        JSONObject c = boots.getJSONObject(i);
                                        String bID = c.getString("bID");
                                        String bName = c.getString("bName");
                                        String bStatus = c.getString("bStatus");

                                        if (allowboots.contains(bID)){

                                            boot_ID[counter]  = bID;
                                            boot_Name[counter]= bName;
                                            str_boots += bName+", ";
                                            counter++;
                                            //Toast.makeText(MainActivity.this,"position = "+position+" bid = "+bID,Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    tv_boots.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                                    tv_boots.setHorizontallyScrolling(true);
                                    tv_boots.setMarqueeRepeatLimit(-1);
                                    tv_boots.setFocusable(true);
                                    tv_boots.setFocusableInTouchMode(true);
                                    tv_boots.setSelected(true);
                                    tv_boots.setTextColor(getActivity().getResources().getColor(R.color.color_black));
                                    tv_boots.setText(removeLastChar(removeLastChar(str_boots)));

                                    String[] fact_Name = new String [addtionalFacilities.replaceAll("\\D", "").length()];
                                    String[] fact_ID   = new String [addtionalFacilities.replaceAll("\\D", "").length()];

                                    //Toast.makeText(MainActivity.this,"position = "+position,Toast.LENGTH_LONG).show();
                                    counter = 0; String facilities="";

                                    for(int i = 0; i < additionalFacilities.length(); i++) {
                                        JSONObject c = additionalFacilities.getJSONObject(i);

                                        String fID = c.getString("fID");
                                        String fName = c.getString("fName");
                                        String fStatus = c.getString("fStatus");

                                        if (addtionalFacilities.contains(fID)){
                                            fact_ID[counter]  = fID;
                                            fact_Name[counter]= fName;
                                            facilities+=fName+", ";
                                            counter++;
                                            //Toast.makeText(MainActivity.this,"position = "+position+" bid = "+bID,Toast.LENGTH_LONG).show();
                                        }
                                        //Toast.makeText(MainActivity.this,"bStatus = "+bStatus,Toast.LENGTH_LONG).show();
                                    }

                                    tv_facilities.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                                    tv_facilities.setHorizontallyScrolling(true);
                                    tv_facilities.setMarqueeRepeatLimit(-1);
                                    tv_facilities.setFocusable(true);
                                    tv_facilities.setFocusableInTouchMode(true);
                                    tv_facilities.setSelected(true);
                                    tv_facilities.setTextColor(getActivity().getResources().getColor(R.color.color_black));
                                    tv_facilities.setText(removeLastChar(removeLastChar(facilities)));


                                    String[] rest_Name = new String [mrestrictions.replaceAll("\\D", "").length()];
                                    String[] rest_ID   = new String [mrestrictions.replaceAll("\\D", "").length()];

                                    //Toast.makeText(MainActivity.this,"position = "+position,Toast.LENGTH_LONG).show();
                                    counter = 0; String rest="";

                                    for(int i = 0; i < restrictions.length(); i++) {
                                        JSONObject c = restrictions.getJSONObject(i);

                                        String id = c.getString("id");
                                        String restriction = c.getString("restriction");
                                        String resStatus = c.getString("resStatus");

                                        if (mrestrictions.contains(id)){

                                            rest_ID[counter]  = id;
                                            rest_Name[counter]= restriction;
                                            rest+=" "+restriction+", ";
                                            counter++;
                                            //Toast.makeText(MainActivity.this,"position = "+position+" bid = "+bID,Toast.LENGTH_LONG).show();
                                        }
                                        //Toast.makeText(MainActivity.this,"bStatus = "+bStatus,Toast.LENGTH_LONG).show();
                                    }

                                    tv_restriction.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                                    tv_restriction.setHorizontallyScrolling(true);
                                    tv_restriction.setMarqueeRepeatLimit(-1);
                                    tv_restriction.setFocusable(true);
                                    tv_restriction.setFocusableInTouchMode(true);
                                    tv_restriction.setSelected(true);
                                    tv_restriction.setTextColor(getActivity().getResources().getColor(R.color.color_black));
                                    tv_restriction.setText(removeLastChar(removeLastChar(rest)));


                                    for(int i = 0; i < locationImages.length(); i++) {
                                        JSONObject c = locationImages.getJSONObject(i);

                                        //String id = c.getString("id");
                                        String locationID = c.getString("locationID");
                                        String locationImage = c.getString("locationImage");
                                        String ImageType= c.getString("ImageType");
                                        //String date = c.getString("date");

                                        locationImage = getActivity().getResources().getString(R.string.imageBaseUrl)+
                                                getActivity().getResources().getString(R.string.remaining_path)+locationImage;

                                        Log.i("locationImage","locationImage = "+locationImage);

                                        TextSliderView textSliderView = new TextSliderView(getActivity());

                                        if (ImageType.equals("0") && tv_day_night.getText().toString().equals("Day")){
                                            //Toast.makeText(getActivity(),"0",Toast.LENGTH_SHORT).show();
                                            textSliderView
                                                    .description("")
                                                    .image(locationImage)
                                                    .setScaleType(BaseSliderView.ScaleType.Fit)
                                                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                                        @Override
                                                        public void onSliderClick(BaseSliderView slider) {

                                                        }
                                                    });

                                            textSliderView.bundle(new Bundle());

                                            textSliderView.getBundle()
                                                    .putString("extra","duumy");

                                            sliderLayout.addSlider(textSliderView);

                                        }else if(ImageType.equals("1") && tv_day_night.getText().toString().equals("Night")) {
                                            //Toast.makeText(getActivity(),"1",Toast.LENGTH_SHORT).show();
                                            textSliderView
                                                    .description("")
                                                    .image(locationImage)
                                                    .setScaleType(BaseSliderView.ScaleType.Fit)
                                                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                                        @Override
                                                        public void onSliderClick(BaseSliderView slider) {

                                                        }
                                                    });

                                            textSliderView.bundle(new Bundle());

                                            textSliderView.getBundle()
                                                    .putString("extra","duumy");

                                            sliderLayout.addSlider(textSliderView);
                                        }
                                    }

                                    sliderLayout.setPresetTransformer(SliderLayout.Transformer.DepthPage);

                                    sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);

                                    sliderLayout.setCustomAnimation(new DescriptionAnimation());

                                    sliderLayout.setDuration(3000);

                                    sliderLayout.addOnPageChangeListener(chatPageActivity);

                                    tv_day_night.setText(""+DayNight);
                                    tv_pitch_no.setSingleLine(true);
                                    tv_pitch_no.setText(""+pitchName);
                                    tv_side.setText(""+aSide);
                                    tv_truf.setText(""+ptichTurf);
                                    tv_condition.setText(""+weather);
                                    tv_cost.setText(""+MatchCost);
                                    tv_match_type.setText(""+recurringType);

                                    //Picasso.with(imageView.getContext()).load(""+getActivity().getResources().getString(R.string.imageBaseUrl)+image).into(imageView);

                                    matchDate =  matchDate.replace("Monday", "");
                                    matchDate =  matchDate.replace("Tuesday", "");
                                    matchDate =  matchDate.replace("Wednesday", "");
                                    matchDate =  matchDate.replace("Thursday", "");
                                    matchDate =  matchDate.replace("Friday", "");
                                    matchDate =  matchDate.replace("Saturday", "");
                                    matchDate =  matchDate.replace("Sunday", "");

                                    matchDate =  matchDate.replace("st", "");
                                    matchDate =  matchDate.replace("nd", "");
                                    matchDate =  matchDate.replace("rd", "");
                                    matchDate =  matchDate.replace("th", "");

                                    Date c_date = null,c,date=null;
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("ddd MMM yyy",Locale.getDefault());
                                    c = Calendar.getInstance().getTime();
                                    System.out.println("matchDate => " + matchDate);
                                    String str_c_date = dateFormat.format(c);

                                    try {
                                        date = dateFormat.parse(matchDate);
                                        c_date = dateFormat.parse(str_c_date);
                                        System.out.println(date);
                                        System.out.println(date);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    System.out.println("date => " + date);
                                    System.out.println("c_date => " + c_date);
                                    //Toast.makeText(getActivity(),"serverdate = "+date.toString()+"  currentdate = "+c_date,Toast.LENGTH_LONG).show();

                                    if (date.compareTo(c_date) < 0 && match_type.equals("Recurring")) {
                                        System.out.println("date2 is Greater than my date1");
                                        btn_reminder.setVisibility(View.VISIBLE);
                                        btn_reminder.setText("Reschedule");

                                        TextView textView_title = new TextView(getActivity());
                                        textView_title.setText("Recurring Match");
                                        textView_title.setGravity(Gravity.START);
                                        textView_title.setPadding(20,10,20,10);
                                        textView_title.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
                                        textView_title.setCompoundDrawablePadding(10);

                                        //textView_title.setCompoundDrawables(null,null,mContext.getResources().getDrawable(R.drawable.ic_warning_colored_24dp),null);
                                        textView_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_warning_colored_24dp, 0);

                                        new AlertDialog.Builder(getActivity())
                                                .setCustomTitle(textView_title)
                                                .setMessage("This is Recurring Match would you like to Reschedule it again? ")
                                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                                // The dialog is automatically dismissed when a dialog button is clicked.
                                                .setPositiveButton(getActivity().getResources().getString(R.string.str_yes), new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("group_Id"    ,chatPageActivity.send_group_Id());
                                                        bundle.putString("game_Id"     ,chatPageActivity.send_game_Id());
                                                        bundle.putString("stadiumName" ,stadiumName);
                                                        bundle.putString("LocationName",LocationName);
                                                        bundle.putString("image"       ,image);
                                                        bundle.putString("gametype"    ,tv_gametype.getText().toString());
                                                        bundle.putString("DateAdded"   ,tv_date.getText().toString());
                                                        bundle.putString("MatchTime"   ,tv_kickoff.getText().toString());
                                                        bundle.putString("DayNight"    ,DayNight);
                                                        bundle.putString("PitchNo"     ,tv_pitch_no.getText().toString());
                                                        bundle.putString("aSide"       ,aSide);
                                                        bundle.putString("cost"        ,tv_cost.getText().toString());
                                                        bundle.putString("Turf"        ,tv_truf.getText().toString());
                                                        bundle.putString("recursive"   ,tv_match_type.getText().toString());
                                                        bundle.putString("team_1"      ,tv_team_1.getText().toString());
                                                        bundle.putString("team_2"      ,tv_team_2.getText().toString());
                                                        bundle.putString("referee"     ,tv_match_referee.getText().toString());
                                                        bundle.putString("Boots"       ,Boots);
                                                        bundle.putString("weather"     ,weather);
                                                        bundle.putString("currency"    ,currency);
                                                        bundle.putString("substitutes" ,substitutes);
                                                        bundle.putString("note"        ,tv_match_note.getText().toString());

                                                        Intent intent = new Intent(getActivity(),UpdateMatchActivity.class);
                                                        intent.putExtras(bundle);
                                                        startActivity(intent);

                                                    }
                                                })
                                                // A null listener allows the button to dismiss the dialog and take no further action.
                                                .setNegativeButton(getActivity().getResources().getString(R.string.str_no), new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                })
                                                .show();


                                        btn_reminder.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Bundle bundle = new Bundle();

                                                bundle.putString("group_Id"    ,chatPageActivity.send_group_Id());
                                                bundle.putString("game_Id"     ,chatPageActivity.send_game_Id());
                                                bundle.putString("stadiumName" ,stadiumName);
                                                bundle.putString("LocationName",LocationName);
                                                bundle.putString("image"       ,image);
                                                bundle.putString("gametype"    ,tv_gametype.getText().toString());
                                                bundle.putString("DateAdded"   ,tv_date.getText().toString());
                                                bundle.putString("MatchTime"   ,tv_kickoff.getText().toString());
                                                bundle.putString("DayNight"    ,DayNight);
                                                bundle.putString("PitchNo"     ,tv_pitch_no.getText().toString());
                                                bundle.putString("aSide"       ,aSide);
                                                bundle.putString("cost"        ,tv_cost.getText().toString());
                                                bundle.putString("Turf"        ,tv_truf.getText().toString());
                                                bundle.putString("recursive"   ,tv_match_type.getText().toString());
                                                bundle.putString("team_1"      ,tv_team_1.getText().toString());
                                                bundle.putString("team_2"      ,tv_team_2.getText().toString());
                                                bundle.putString("referee"     ,tv_match_referee.getText().toString());
                                                bundle.putString("Boots"       ,Boots);
                                                bundle.putString("weather"     ,weather);
                                                bundle.putString("currency"    ,currency);
                                                bundle.putString("substitutes" ,substitutes);
                                                bundle.putString("note"        ,tv_match_note.getText().toString());

                                                Intent intent = new Intent(getActivity(),UpdateMatchActivity.class);
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                            }
                                        });
                                    }else {

                                        btn_reminder.setVisibility(View.VISIBLE);
                                        btn_reminder.setText("Set Reminder");
                                        btn_reminder.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMM yyyy");
                                                String date_to_pass="";
                                                date_to_pass =  tv_date.getText().toString().replace("st", "");
                                                date_to_pass =  tv_date.getText().toString().replace("nd", "");
                                                date_to_pass =  tv_date.getText().toString().replace("rd", "");
                                                date_to_pass =  tv_date.getText().toString().replace("th", "");
                                                //chatPageActivity.call_calendar(tv_name.getText().toString(),date_to_pass);
                                                System.out.println("date 1 = "+date_to_pass);

                                                try {
                                                    Date date = sdf.parse(date_to_pass);
                                                    long endTime   =  date.getTime();
                                                    System.out.println("date 2 = "+endTime);
                                                    chatPageActivity.call_calendar(tv_name.getText().toString(),endTime);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }while (cursor.moveToNext());
                            //mydb.delete_record_groups()
                        }
                    }else {
                        linearLayout_match_detail.setVisibility(View.GONE);
                        Toast.makeText(getActivity(),"Connectivity failure !!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        String message ="",user_check="",api_call_chat_service="",else_check="",messageId="",sentTime="",full_name="",results="";
        int array_lenth=0;
        private void SavePreferences(String key, String value){
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.apply();
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

                //img_group_icon.setImageBitmap(scaledBitmap);


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
                    progressDialog.setCancelable(false);
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
                    }else {
                        message_list_adapter.notifyDataSetChanged();
                        if (mListView!=null){
                            mListView.smoothScrollToPosition(message_list_adapter.getCount()-1);
                            mListView.setSelection(message_list_adapter.getCount());
                        }
                            //mListView.setSelection(message_list_adapter.getCount());
                    }
                }

                @Override
                protected Boolean doInBackground(Void... params) {
                    try {

                        JSONObject jsonObject=jsonParser_send_chat_image.uploadImage(image_real_path,""+chatPageActivity.send_group_Id(),""+messageArea.getText().toString(),""+tempmsgid,"",getActivity());

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
                        "" + filename, "" + temp_message, "self","read");*/

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
        private void send_data_add(final String params_) {
            new AsyncTask<Void, Integer, Boolean>() {
                ProgressDialog progressDialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Save Data...");
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                }

                @Override
                protected Boolean doInBackground(Void... params) {


                    try {
//String result = jsonParser.uploadImage(imgFile.getAbsolutePath(),getActivity()).toString();
//Toast.makeText(getActivity(),result,Toast.LENGTH_SHORT).show();
//System.out.println("fdfdf"+imgFile.getAbsolutePath().toString());

//if(imgFile != null)
                        JSONObject jsonObject;
                        jsonObject=jsonParser_upload_group.uploadImage(image_real_path,""+params_,getActivity());

                        //Toast.makeText(getActivity(),"call = "+jsonObject.toString(),Toast.LENGTH_SHORT).show();
//else
//jsonObject =jsonParser_editProfile.uploadImage("",username,password,age,height,phone,weight,country,activity);

                        if (jsonObject != null) {
                            if(jsonObject.getString("respCode").equals("200")){
                                //Toast.makeText(getActivity(),"200 = "+jsonObject.toString(),Toast.LENGTH_SHORT).show();


                           /* SavePreferences("gameImage" ,getResources().getString(R.string.imageBaseUrl)+jsonObject.getString("gameImage" ));
                            Log.i("TAG", "gameImage : " +sharedPreferences.getString("gameImage",""));*/

                            }
                            //Toast.makeText(getActivity(),"201 = "+jsonObject.toString(),Toast.LENGTH_SHORT).show();
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
                    // if (progressDialog != null)
                    // progressDialog.dismiss();
                    try
                    {
                        if (aBoolean) {
                    //Toast.makeText(getActivity(), "Image Uploaded Successfully", Toast.LENGTH_LONG).show();
                            Toast.makeText(getActivity(),"Profile Saved!",Toast.LENGTH_SHORT).show();
                            getFragmentManager().popBackStack();
                        }
                        else{
                            Toast.makeText(getActivity(), "Failed To Save Data, Try Again!", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }finally {
                        progressDialog.dismiss();
                    }
//imagePath = "";
                }
            }.execute();
        }
        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);

            if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                //Toast.makeText(getActivity(),"fragment 1 ",Toast.LENGTH_SHORT).show();
                if(isVisibleToUser && isResumed()){
                    onResume();
                }
            }else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                if(isVisibleToUser && isResumed()){
                    onResume();
                }
            }else {
                //Toast.makeText(getActivity(),"fragment 3 ",Toast.LENGTH_SHORT).show();
                if(isVisibleToUser && isResumed()){
                    onResume();
                }
            }

        }
        public static Intent service_chat;

        private BroadcastReceiver ReceivefromService = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //get the data using the keys you entered at the service
                //Toast.makeText(getActivity(),"this received date = ",Toast.LENGTH_SHORT).show();

                results                = intent.getStringExtra("results"              );
               /*
                message                = intent.getStringExtra("message"              );
                sentTime               = intent.getStringExtra("sentTime"             );
                user_check             = intent.getStringExtra("user_check"           );
                full_name              = intent.getStringExtra("full_name"            );
                api_call_chat_service  = intent.getStringExtra("api_call_chat_service");
                else_check             = intent.getStringExtra("else_check"           );
                */

                try {
                    JSONArray results_array = new JSONArray(results);

                    for(int i = 0; i < results_array.length(); i++) {
                        JSONObject c = results_array.getJSONObject(i);

                        messageId = c.getString("messageId");
                        String message_type = c.getString("message_type");
                        String image="";
                        if (!c.getString("image").equals("")){
                            image = getActivity().getResources().getString(R.string.imageBaseUrl)+c.getString("image");
                        }
                        message          = c.getString("message");
                        gId              = c.getString("gId");
                        //membershipId     = c.getString("membershipId");
                        sentTime         = c.getString("sentTime_org");
                        //sentTime         = c.getString("sentTime");
                        status           = c.getString("status");

                        String fname;
                        JSONObject userNameData = c.getJSONObject("userNameData");
                        fname = userNameData.getString("fname");

                        // System.out.println("TAG"+fname);
                        // Toast.makeText(getActivity()," fname = "+fname,Toast.LENGTH_SHORT).show();

                        String pendingView      = c.getString("pendingView");
                        final String user_check = c.getString("user_check");

                        if (sharedPreferences.getString("messageId","").equals("0")){
                            Message_List message_list = new Message_List(""+messageId,"✓✓",""+fname,
                                    ""+sentTime,""+message,""+user_check,""+image,"",false);
                            message_ArrayList.add(message_list);

                            int msg_id = Integer.parseInt(mydb.getdata_chat_check_data(chatPageActivity.send_group_Id()));

                            if (Integer.parseInt(messageId)<=msg_id){

                            }else{
                             mydb.insertdata_chat(  ""+chatPageActivity.send_group_Id(),""+sharedPreferences.getString("userID",""),
                                                  ""+messageId,"✓✓",  ""+fname,
                                                    ""+sentTime, ""+message,""+user_check,"read");
                            }
                        }else {
                            if (user_check.equals("other")){

                                Message_List message_list = new Message_List(""+messageId,"",""+fname,
                                        ""+sentTime,""+message,""+user_check,""+image,"",false); //"false"
                                message_ArrayList.add(message_list);

                                int msg_id = Integer.parseInt(mydb.getdata_chat_check_data(chatPageActivity.send_group_Id()));

                                if (Integer.parseInt(messageId)<=msg_id){

                                }else{
                                    mydb.insertdata_chat(""+chatPageActivity.send_group_Id(),""+sharedPreferences.getString("userID",""),
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
                mydb.setdata_chat_msg_read_status(""+chatPageActivity.send_group_Id(),""+sharedPreferences.getString("userID",""));
            }
        };
        private String removeLastChar(String str) {

            if(str.equals("")){
                return "";
            }else
                return str.substring(0, str.length() - 1);
        }
        private void SavePreferences_int(String key, int value){
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(key, value);
            editor.apply();
        }
    }
    static class RealPathUtil_gp {

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
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position+1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
