package com.firmground.evs.firmground.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firmground.evs.firmground.R;
import com.firmground.evs.firmground.adapter.Message_List_Adapter;
import com.firmground.evs.firmground.adapter.MyChatRecyclerViewAdapter;
import com.firmground.evs.firmground.database.MyDatabase;
import com.firmground.evs.firmground.fragment.dummy.DummyContent.DummyItem;
import com.firmground.evs.firmground.model.Groups_list;
import com.firmground.evs.firmground.model.In_list;
import com.firmground.evs.firmground.model.Message_List;
import com.firmground.evs.firmground.model.Out_list;
import com.firmground.evs.firmground.model.Squad_list;
import com.firmground.evs.firmground.service.New_Incoming_Chat_Service;
import com.firmground.evs.firmground.webservice.TaskDelegate;
import com.firmground.evs.firmground.webservice.WebRequestCall;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.firmground.evs.firmground.broadcastreceiver.WifiReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.firmground.evs.firmground.activity.LoginActivity.static_user_Id;
import static com.firmground.evs.firmground.activity.MainActivity.activity;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class GroupFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    ArrayList<Groups_list> groupslists =new ArrayList<Groups_list>();
    MyChatRecyclerViewAdapter myChatRecyclerViewAdapter;
    ListView listView;
    BroadcastReceiver wifireciver;

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static GroupFragment newInstance(int columnCount) {
        GroupFragment fragment = new GroupFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    private FragmentActivity fragmentActivity;
    SharedPreferences sharedPreferences;
    View view;

    Cursor cursor;

    public Intent new_incoming_chat_message;

    public static int wifi_checker = 0;

    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    public void requestPhone(final View view) {
        Permissions.check(getActivity(), Manifest.permission.READ_CONTACTS, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                //Toast.makeText(getActivity(), "Phone granted.", Toast.LENGTH_SHORT).show();
                getContacts();
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);

                requestPhone(view);
            }
        });
    }
    private void getContacts() {
        //TODO get contacts code here
        //Toast.makeText(getActivity(), "Get contacts ....", Toast.LENGTH_LONG).show();

        //getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        final ProgressBar simpleProgressBar = (ProgressBar) view.findViewById(R.id.simpleProgressBar);
        simpleProgressBar.setVisibility(View.VISIBLE);

        View view1 = view.findViewById(R.id.view);
        view1.setVisibility(View.VISIBLE);

       /* ContactListFragment contactListFragment = new ContactListFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_layout, contactListFragment);
        ft.addToBackStack(null);
        ft.commit();*/

        ContactListFragment contactListFragment = new ContactListFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_layout, contactListFragment);
        ft.addToBackStack(null);
        ft.commit();

       /* int SPLASH_TIME_OUT = 1000;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                ContactListFragment contactListFragment = new ContactListFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.frame_layout, contactListFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        }, SPLASH_TIME_OUT);*/

    }
    public void requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        android.Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Read Contacts permission");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access to contacts.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.READ_CONTACTS}
                                    , PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    builder.show();

                    //Toast.makeText(getActivity(), "granted 2", Toast.LENGTH_LONG).show();

                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);

                    //Toast.makeText(getActivity(), "granted 3", Toast.LENGTH_LONG).show();

                }
            } else {
                getContacts();
                //Toast.makeText(getActivity(), "granted 4", Toast.LENGTH_LONG).show();
            }
        } else {
            getContacts();
            //Toast.makeText(getActivity(), "granted 5", Toast.LENGTH_LONG).show();
        }
    }

    private  void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getActivity().registerReceiver(wifireciver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getActivity().registerReceiver(wifireciver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }else {
            getActivity().registerReceiver(wifireciver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }
    protected void unregisterNetworkChanges() {
        try {
            getActivity().unregisterReceiver(wifireciver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "granted 1", Toast.LENGTH_LONG).show();
                    getContacts();
                } else {
                    Toast.makeText(getActivity(), "You have disabled a contacts permission", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        new_incoming_chat_message = new Intent(getActivity(), New_Incoming_Chat_Service.class);
        getActivity().stopService(new_incoming_chat_message);
        getActivity().startService(new_incoming_chat_message);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        wifireciver = new WifiReceiver();
        registerNetworkBroadcastForNougat();

        fragmentActivity = (FragmentActivity) activity;

        ImageView add_match_img = getActivity().findViewById(R.id.add_match_img);
        add_match_img.setVisibility(View.GONE);

        ImageView add_group_img = getActivity().findViewById(R.id.add_group_img);
        add_group_img.setVisibility(View.VISIBLE);
        add_group_img.setImageResource(R.drawable.ic_group_add_white_24dp);
        add_group_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //requestContactPermission();
                  requestPhone(v);
                // Toast.makeText(MainActivity.this,"click",Toast.LENGTH_SHORT).show();
            }
        });

        final ImageView cross_img = getActivity().findViewById(R.id.cross_img);
        cross_img.setVisibility(View.GONE);
        final EditText ed_search = getActivity().findViewById(R.id.ed_search);
        ed_search.setVisibility(View.GONE);
        ed_search.setHint("Group Name");
        final LinearLayout toolbar_layout = getActivity().findViewById(R.id.toolbar_layout);
        toolbar_layout.setVisibility(View.VISIBLE);

        ImageView search_img = getActivity().findViewById(R.id.search_img);
        search_img.setVisibility(View.VISIBLE);
        search_img.setImageResource(R.drawable.ic_search_white_24dp);
        search_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this,"click",Toast.LENGTH_SHORT).show();
                ed_search.setVisibility(View.VISIBLE);
                ed_search.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(ed_search, InputMethodManager.SHOW_IMPLICIT);
                cross_img.setVisibility(View.VISIBLE);
                toolbar_layout.setVisibility(View.GONE);
                cross_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toolbar_layout.setVisibility(View.VISIBLE);
                        ed_search.setText("");
                        ed_search.setVisibility(View.GONE);
                        cross_img.setVisibility(View.GONE);
                    }
                });
            }
        });


// Add Text Change Listener to EditText
        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter

                //myChatRecyclerViewAdapter.getFilter().filter(s.toString());

                int textlength = s.length();
                ArrayList<Groups_list> tempArrayList = new ArrayList<Groups_list>();
                for(Groups_list c: groupslists){
                    if (textlength <= c.getGroupName().length()) {
                        if (c.getGroupName().toLowerCase().contains(s.toString().toLowerCase())) {
                            tempArrayList.add(c);
                        }
                    }
                }
                if (activity!=null){
                    myChatRecyclerViewAdapter = new MyChatRecyclerViewAdapter(tempArrayList, mListener,activity);
                    if (recyclerView!=null)
                    recyclerView.setAdapter(myChatRecyclerViewAdapter);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return view;
    }

    RecyclerView recyclerView;
    MyDatabase mydb;
    public static String ground_Id="";
    @Override
    public void onResume() {
        super.onResume();
        // Set the adapter
        activity = getActivity();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.NewSmsReceiver");
        activity.registerReceiver(ReceivefromService, filter);
        ground_Id = "";
        if (view instanceof ConstraintLayout) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view.findViewById(R.id.list);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mydb = new MyDatabase(activity);

            final String[] second_user_id = {""};
            final String[] full_name = { "" };
            final String[] phone = { "" };

            int SPLASH_TIME_OUT = 500;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (wifi_checker==1){

                        String url = "";
                        if (getActivity()!=null){
                             url = getResources().getString(R.string.url) + "myGroups?userID="+sharedPreferences.getString("userID","");
                        }

                        final String params = "";
                        final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                            @Override
                            public String TaskCompletionResult(String result) throws JSONException {

                                JSONObject jsonObject = new JSONObject(result);

                                if(jsonObject.getString("respCode").equals("200")) {
                                    JSONArray results = jsonObject.getJSONArray("results");

                                    groupslists.clear();
                                    mydb.delete_record_groups();

                                    for(int i = 0; i < results.length(); i++) {
                                        JSONObject c = results.getJSONObject(i);

                                        String membershipId   = c.getString("membershipId");
                                        String UserID         = c.getString("UserID");
                                        String groupId        = c.getString("gId");
                                        String memberRole     = c.getString("memberRole");
                                        String dateAdded      = c.getString("dateAdded");
                                        String userGroupStatus= c.getString("userGroupStatus");
                                        String GroupName      = c.getString("GroupName");
                                        String groupType      = c.getString("groupType");
                                        String status         = c.getString("status");
                                        String CreatedDate    = c.getString("CreatedDate");
                                        String gameId         = c.getString("gameId");

                                        String participentStatus = "";
                                        if (c.has("participentStatus")){
                                            participentStatus=c.getString("participentStatus");
                                            //Toast.makeText(getActivity(),"participentStatus = "+participentStatus,Toast.LENGTH_SHORT).show();
                                        }

                                        JSONObject secondUserData = new JSONObject();

                                          if (c.has("secondUserData")){
                                            secondUserData = c.getJSONObject("secondUserData");

                                        }else {

                                        }

                                        String sentTime,last_message,last_message_type = "",messageId="";
                                        JSONObject lastConvRow = c.getJSONObject("lastConvRow");

                                        if (lastConvRow.length()>1){

                                            messageId         = lastConvRow.getString("messageId");
                                            String gId        = lastConvRow.getString("gId");
                                            last_message      = lastConvRow.getString("message");
                                            last_message_type = lastConvRow.getString("message_type");
                                            sentTime          = lastConvRow.getString("sentTime");
                                            String pendingView= lastConvRow.getString("pendingView");
                                            //String sentTimeOrg = lastConvRow.getString("sentTimeOrg");
                                            //Toast.makeText(getActivity(),"sentTimeOrg = "+sentTimeOrg,Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            sentTime = lastConvRow.getString("sentTime");
                                            last_message="No Conversion";
                                            //Toast.makeText(getActivity(),"sentTime = "+sentTime.toString(),Toast.LENGTH_SHORT).show();
                                        }

                                        String  image = "";
                                        if (getActivity()!=null){
                                            image = getActivity().getResources().getString(R.string.imageBaseUrl)+c.getString("image");
                                            //Toast.makeText(getActivity(),"GroupName = "+GroupName, Toast.LENGTH_SHORT).show();
                                            //Toast.makeText(getActivity(),"gameId = "+gameId, Toast.LENGTH_SHORT).show();
                                            SavePreferences("messageId"    ,"0");
                                            Groups_list groups_list = new Groups_list(""+membershipId,""+UserID,""+groupId,
                                                                                      ""+GroupName,""+groupType,""+memberRole,""+sentTime,
                                                                                      ""+image,"", "","",""+gameId,
                                                                                      ""+participentStatus,""+last_message,""+last_message_type,""+messageId);


                                            if (secondUserData!=null){

                                                Log.i("TAG",""+secondUserData.toString());
                                                mydb.insertdata_groups(""+groupId,""+sharedPreferences.getString("userID",""),
                                                        ""+GroupName,""+groupType,""+last_message,""+last_message_type,""+sentTime,
                                                        ""+memberRole,""+gameId,""+image,""+participentStatus,""+secondUserData.toString(),""+messageId);

                                            }else {
                                                Log.i("TAG",""+secondUserData.toString());
                                                mydb.insertdata_groups(""+groupId,""+sharedPreferences.getString("userID",""),
                                                        ""+GroupName,""+groupType,""+last_message,""+last_message_type,""+sentTime,
                                                        ""+memberRole,""+gameId,""+image,""+participentStatus,"",""+messageId);
                                            }

                                            //groupslists.add(groups_list);
                                        }
                                    }

                                    groupslists.clear();
                                    final MyDatabase mydb = new MyDatabase(activity);
                                    Cursor cursor = mydb.getdata_groups_msg_id();
                                    if (cursor.getCount()>=0){
                                        if (cursor.moveToFirst()) {
                                            do {

                                                Log.i("cursor = ",""+cursor.toString());

                                                String id            = cursor.getString(0);
                                                String grp_id        = cursor.getString(1);
                                                String user_id       = cursor.getString(2);
                                                String grp_name      = cursor.getString(3);
                                                String grp_type      = cursor.getString(4);
                                                String last_msg      = cursor.getString(5);
                                                String last_msg_type = cursor.getString(6);
                                                String date          = cursor.getString(7);
                                                String memberrole    = cursor.getString(8);
                                                String game_id       = cursor.getString(9);
                                                String image         = cursor.getString(10);
                                                String in_out_status = cursor.getString(11);
                                                String secondUserData= cursor.getString(12);
                                                String msg_id        = cursor.getString(13);
                                                String lastmsgid     = cursor.getString(14);
                                                String senttime      = cursor.getString(15);


                                                if (grp_type.equals("one2one")){
                                                    String[] separated = grp_name.split("-");
                                                    String frist_id  = separated[0]; // this will contain "Fruit"
                                                    String second_id =separated[1];

                                                    if (frist_id.equals(""+sharedPreferences.getString("userID",""))){
                                                        //Toast.makeText(getActivity(),"participentStatus = "+second_id,Toast.LENGTH_SHORT).show();
                                                        grp_name = mydb.getdata_contact_user_name(second_id);

                                                        if (grp_name.equals("")){
                                                            //Toast.makeText(getActivity(),"secondUserData = "+secondUserData.toString(),Toast.LENGTH_SHORT).show();
                                                            JSONObject secondUserData_= new JSONObject();
                                                            secondUserData_ = new JSONObject(secondUserData);
                                                            grp_name = secondUserData_.getString("full_name");

                                                            mydb.insertdata_contact(""+sharedPreferences.getString("userID",""),""+secondUserData_.getString("full_name"),""+secondUserData_.getString("phone"),""+secondUserData_.getString("userID"),"active");

                                                        }
                                                    }else {
                                                        grp_name = mydb.getdata_contact_user_name(second_id);
                                                        if (grp_name.equals("")){
                                                            //Toast.makeText(getActivity(),"secondUserData = "+secondUserData.toString(),Toast.LENGTH_SHORT).show();
                                                            JSONObject secondUserData_= new JSONObject();
                                                            secondUserData_ = new JSONObject(secondUserData);
                                                            grp_name = secondUserData_.getString("full_name");

                                                            mydb.insertdata_contact(""+sharedPreferences.getString("userID",""),""+secondUserData_.getString("full_name"),""+secondUserData_.getString("phone"),""+secondUserData_.getString("userID"),"active");

                                                        }
                                                    }
                                                }

                                                Log.e("YYYY","id      = " + id +
                                                        " grp_id       = " + grp_id +
                                                        " user_id      = " + user_id +
                                                        " grp_name     = " + grp_name +
                                                        " grp_type     = " + grp_type +
                                                        " last_msg     = " + last_msg +
                                                        " last_msg_type= " + last_msg_type +
                                                        " date         = " + date +
                                                        " memberrole   = " + memberrole +
                                                        " game_id      = " + game_id +
                                                        " image        = " + image +
                                                        " in_out_status= " + in_out_status+
                                                        " msg_id       = " + msg_id +
                                                        " senttime     = " + senttime+
                                                        " lastmsgid    = " + lastmsgid);

                                                        Groups_list groups_list = new Groups_list("", "", "" + grp_id,
                                                        "" + grp_name,""+grp_type,""+memberrole, "" + date,
                                                        "" + image, "", "", "", "" + game_id,
                                                        "" + in_out_status, "" + last_msg,""+last_msg_type,""+lastmsgid);

                                                groupslists.add(groups_list);

                                            } while (cursor.moveToNext());

                                            Cursor cursor_match_id = mydb.getdata_gameids();// getting game ids here
                                            if (cursor_match_id.getCount()>=0){
                                                if (cursor_match_id.moveToFirst()) {
                                                    String games_ids = "";
                                                    do {
                                                        String id           = cursor_match_id.getString(0);
                                                        games_ids += id+",";
                                                    } while (cursor_match_id.moveToNext());

                                                    //http://192.168.100.14/FirmGround/REST/updateAdditionalParticipents?gameId=1
                                                    final String url = getResources().getString(R.string.url) + "getAllMatchSquadDetails?"+
                                                            "userID="+sharedPreferences.getString("userID","")+
                                                            "&games_ids="+games_ids
                                                            ;
                                                    final String params = "";
                                                    final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                                                        @Override
                                                        public String TaskCompletionResult(String result) throws JSONException {

                                                            JSONObject jsonObject = new JSONObject(result);

                                                            if(jsonObject.getString("respCode").equals("200")) {
                                                                JSONArray results = jsonObject.getJSONArray("results");

                                                                mydb.delete_record_squad_detail();

                                                                for (int i=0;i<results.length();i++){

                                                                    JSONObject c = results.getJSONObject(i);
                                                                    String   gId = c.getString("gId");
                                                                    JSONArray gameParicpents = c.getJSONArray("gameParicpents");
                                                                    //Toast.makeText(getActivity(),"gameParicpents = "+gameParicpents.toString(),Toast.LENGTH_SHORT).show();
                                                                    JSONObject  MatchInfo = c.getJSONObject("MatchInfo");
                                                                    //Toast.makeText(getActivity(),"MatchInfo = "+MatchInfo.toString(),Toast.LENGTH_SHORT).show();

                                                                    JSONObject squadDetails= new JSONObject();

                                                                    squadDetails.put("results",gameParicpents);
                                                                    squadDetails.put("MatchInfo",MatchInfo);

                                                                    mydb.insertdata_squad_details(""+gId,""+squadDetails.toString());
                                                                }
                                                            }else {
                                                            }

                                                            return result;
                                                        }
                                                    });
                                                    webRequestCall.execute(url,"GET",params);
                                                }
                                            }
                                        }
                                    }

                                    myChatRecyclerViewAdapter = new MyChatRecyclerViewAdapter(groupslists, mListener, getActivity());
                                    recyclerView.setAdapter(myChatRecyclerViewAdapter);
                                }else{

                                }
                                return result;
                            }
                        });
                        webRequestCall.execute(url,"GET",params);
                        //recyclerView.setAdapter(new MyChatRecyclerViewAdapter(DummyContent.ITEMS, mListener, getActivity()));
                    }
                    else{
                        //Toast.makeText(context,"inside the dummyHit",Toast.LENGTH_SHORT).show();
                        groupslists.clear();
                        MyDatabase mydb = new MyDatabase(activity);
                        Cursor cursor = mydb.getdata_groups_msg_id();
                        if (cursor .getCount()>=0){
                            if (cursor .moveToFirst()){
                                do {
                                    String id            = cursor.getString(0);
                                    String grp_id        = cursor.getString(1);
                                    String user_id       = cursor.getString(2);
                                    String grp_name      = cursor.getString(3);
                                    String grp_type      = cursor.getString(4);
                                    String last_msg      = cursor.getString(5);
                                    String last_msg_type = cursor.getString(6);
                                    String date          = cursor.getString(7);
                                    String memberrole    = cursor.getString(8);
                                    String game_id       = cursor.getString(9);
                                    String image         = cursor.getString(10);
                                    String in_out_status = cursor.getString(11);
                                    String msg_id        = cursor.getString(12);
                                    String senttime      = cursor.getString(13);
                                    String secondUserData= cursor.getString(14);
                                    String lastmsgid     = cursor.getString(15);

                                    if (grp_type.equals("one2one")){
                                        String[] separated = grp_name.split("-");
                                        String frist_id  = separated[0]; // this will contain "Fruit"
                                        String second_id =separated[1];

                                        if (frist_id.equals(""+sharedPreferences.getString("userID",""))){
                                            //Toast.makeText(getActivity(),"participentStatus = "+second_id,Toast.LENGTH_SHORT).show();
                                            grp_name = mydb.getdata_contact_user_name(second_id);
                                            if (grp_name.equals("")){

                                                JSONObject secondUserData_= new JSONObject();
                                                try {
                                                    secondUserData_ = new JSONObject(secondUserData);
                                                    grp_name = secondUserData_.getString("full_name");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }else {
                                            grp_name = mydb.getdata_contact_user_name(second_id);
                                            if (grp_name.equals("")){
                                                JSONObject secondUserData_= new JSONObject();
                                                try {
                                                    secondUserData_ = new JSONObject(secondUserData);
                                                    grp_name = secondUserData_.getString("full_name");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }

                                    Log.e("YYYY","id= " + id +
                                            " grp_id          = " + grp_id +
                                            " user_id         = " + user_id +
                                            " grp_name      = " + grp_name +
                                            " grp_type      = " + grp_type +
                                            " last_msg      = " + last_msg +
                                            " last_msg_type = " + last_msg_type +
                                            " date          = " + date +
                                            " memberrole    = " + memberrole +
                                            " game_id       = " + game_id +
                                            " image         = " + image +
                                            " in_out_status = " + in_out_status+
                                            " msg_id        = " + msg_id +
                                            " senttime      = " + senttime+
                                            " secondUserData= " + secondUserData);

                                    Groups_list groups_list = new Groups_list("", "", "" + grp_id,
                                            "" + grp_name,""+grp_type, ""+memberrole, "" + date,
                                            "" + image, "", "", "", "" + game_id,
                                            "" + in_out_status, "" + last_msg,""+last_msg_type,""+lastmsgid);

                                    groupslists.add(groups_list);

                                }while (cursor .moveToNext());
                                //mydb.delete_record_groups();
                                myChatRecyclerViewAdapter = new MyChatRecyclerViewAdapter(groupslists, mListener, getActivity());
                                recyclerView.setAdapter(myChatRecyclerViewAdapter);
                            }
                          }
                        //Toast.makeText(context,""+jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                        }

                      }
                    }, SPLASH_TIME_OUT);
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

    }

    private BroadcastReceiver ReceivefromService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //get the data using the keys you entered at the service
            //Toast.makeText(getActivity(),"this-->",Toast.LENGTH_SHORT).show();

                String url = "";
                if (getActivity()!=null){
                    url = getResources().getString(R.string.url) + "myGroups?userID="+sharedPreferences.getString("userID","");
                }

                final String params = "";
                final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                    @Override
                    public String TaskCompletionResult(String result) throws JSONException {

                        JSONObject jsonObject = new JSONObject(result);

                        if(jsonObject.getString("respCode").equals("200")) {
                            JSONArray results = jsonObject.getJSONArray("results");

                            groupslists.clear();
                            mydb.delete_record_groups();

                            for(int i = 0; i < results.length(); i++) {
                                JSONObject c = results.getJSONObject(i);

                                String membershipId   = c.getString("membershipId");
                                String UserID         = c.getString("UserID");
                                String groupId        = c.getString("gId");
                                String memberRole     = c.getString("memberRole");
                                String dateAdded      = c.getString("dateAdded");
                                String userGroupStatus= c.getString("userGroupStatus");
                                String GroupName      = c.getString("GroupName");
                                String groupType      = c.getString("groupType");
                                String status         = c.getString("status");
                                String CreatedDate    = c.getString("CreatedDate");
                                String gameId         = c.getString("gameId");

                                String participentStatus = "";
                                if (c.has("participentStatus")){
                                    participentStatus=c.getString("participentStatus");
                                    //Toast.makeText(getActivity(),"participentStatus = "+participentStatus,Toast.LENGTH_SHORT).show();
                                }

                                JSONObject secondUserData = new JSONObject();

                                if (c.has("secondUserData")){
                                    secondUserData = c.getJSONObject("secondUserData");

                                }else {

                                }

                                String sentTime,last_message,last_message_type = "",messageId="";
                                JSONObject lastConvRow = c.getJSONObject("lastConvRow");

                                if (lastConvRow.length()>1){

                                     messageId  = lastConvRow.getString("messageId");
                                    String gId        = lastConvRow.getString("gId");
                                    last_message      = lastConvRow.getString("message");
                                    last_message_type = lastConvRow.getString("message_type");
                                    sentTime          = lastConvRow.getString("sentTime");
                                    String pendingView= lastConvRow.getString("pendingView");
                                    //String sentTimeOrg = lastConvRow.getString("sentTimeOrg");
                                    //Toast.makeText(getActivity(),"sentTimeOrg = "+sentTimeOrg,Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    sentTime = lastConvRow.getString("sentTime");
                                    last_message="No Conversion";
                                    //Toast.makeText(getActivity(),"sentTime = "+sentTime.toString(),Toast.LENGTH_SHORT).show();
                                }

                                String  image = "";
                                if (getActivity()!=null){
                                    image = getActivity().getResources().getString(R.string.imageBaseUrl)+c.getString("image");
                                    //Toast.makeText(getActivity(),"GroupName = "+GroupName, Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(getActivity(),"gameId = "+gameId, Toast.LENGTH_SHORT).show();
                                    SavePreferences("messageId"    ,"0");
                                    Groups_list groups_list = new Groups_list(""+membershipId,""+UserID,""+groupId,
                                            ""+GroupName,""+groupType,""+memberRole,""+sentTime,
                                            ""+image,"", "","",""+gameId,
                                            ""+participentStatus,""+last_message,""+last_message_type,""+messageId);


                                    if (secondUserData!=null){

                                        Log.i("TAG",""+secondUserData.toString());
                                        mydb.insertdata_groups(""+groupId,""+sharedPreferences.getString("userID",""),
                                                ""+GroupName,""+groupType,""+last_message,""+last_message_type,""+sentTime,
                                                ""+memberRole,""+gameId,""+image,""+participentStatus,""+secondUserData.toString(),""+messageId);

                                    }else {
                                        Log.i("TAG",""+secondUserData.toString());
                                        mydb.insertdata_groups(""+groupId,""+sharedPreferences.getString("userID",""),
                                                ""+GroupName,""+groupType,""+last_message,""+last_message_type,""+sentTime,
                                                ""+memberRole,""+gameId,""+image,""+participentStatus,"",""+messageId);
                                    }

                                    //groupslists.add(groups_list);
                                }
                            }

                            groupslists.clear();
                            final MyDatabase mydb = new MyDatabase(activity);
                            Cursor cursor = mydb.getdata_groups_msg_id();
                            if (cursor.getCount()>=0){
                                if (cursor.moveToFirst()) {
                                    do {

                                        Log.i("cursor = ",""+cursor.toString());

                                        String id            = cursor.getString(0);
                                        String grp_id        = cursor.getString(1);
                                        String user_id       = cursor.getString(2);
                                        String grp_name      = cursor.getString(3);
                                        String grp_type      = cursor.getString(4);
                                        String last_msg      = cursor.getString(5);
                                        String last_msg_type = cursor.getString(6);
                                        String date          = cursor.getString(7);
                                        String memberrole    = cursor.getString(8);
                                        String game_id       = cursor.getString(9);
                                        String image         = cursor.getString(10);
                                        String in_out_status = cursor.getString(11);
                                        String secondUserData= cursor.getString(12);
                                        String msg_id        = cursor.getString(13);
                                        String lastmsgid     = cursor.getString(14);
                                        String senttime      = cursor.getString(15);

                                        if (grp_type.equals("one2one")){
                                            String[] separated = grp_name.split("-");
                                            String frist_id  = separated[0]; // this will contain "Fruit"
                                            String second_id =separated[1];

                                            if (frist_id.equals(""+sharedPreferences.getString("userID",""))){
                                                //Toast.makeText(getActivity(),"participentStatus = "+second_id,Toast.LENGTH_SHORT).show();
                                                grp_name = mydb.getdata_contact_user_name(second_id);

                                                if (grp_name.equals("")){
                                                    //Toast.makeText(getActivity(),"secondUserData = "+secondUserData.toString(),Toast.LENGTH_SHORT).show();
                                                    JSONObject secondUserData_= new JSONObject();
                                                    secondUserData_ = new JSONObject(secondUserData);
                                                    grp_name = secondUserData_.getString("full_name");

                                                    mydb.insertdata_contact(""+sharedPreferences.getString("userID",""),""+secondUserData_.getString("full_name"),""+secondUserData_.getString("phone"),""+secondUserData_.getString("userID"),"active");

                                                }
                                            }else {
                                                grp_name = mydb.getdata_contact_user_name(second_id);
                                                if (grp_name.equals("")){
                                                    //Toast.makeText(getActivity(),"secondUserData = "+secondUserData.toString(),Toast.LENGTH_SHORT).show();
                                                    JSONObject secondUserData_= new JSONObject();
                                                    secondUserData_ = new JSONObject(secondUserData);
                                                    grp_name = secondUserData_.getString("full_name");

                                                    mydb.insertdata_contact(""+sharedPreferences.getString("userID",""),""+secondUserData_.getString("full_name"),""+secondUserData_.getString("phone"),""+secondUserData_.getString("userID"),"active");

                                                }
                                            }
                                        }

                                        Log.e("YYYY","id      = " + id +
                                                " grp_id       = " + grp_id +
                                                " user_id      = " + user_id +
                                                " grp_name     = " + grp_name +
                                                " grp_type     = " + grp_type +
                                                " last_msg     = " + last_msg +
                                                " last_msg_type= " + last_msg_type +
                                                " date         = " + date +
                                                " memberrole   = " + memberrole +
                                                " game_id      = " + game_id +
                                                " image        = " + image +
                                                " in_out_status= " + in_out_status+
                                                " msg_id       = " + msg_id +
                                                " senttime     = " + senttime+
                                                " lastmsgid    = " + lastmsgid);

                                        Groups_list groups_list = new Groups_list("", "", "" + grp_id,
                                                "" + grp_name,""+grp_type,""+memberrole, "" + date,
                                                "" + image, "", "", "", "" + game_id,
                                                "" + in_out_status, "" + last_msg,""+last_msg_type,""+lastmsgid);

                                        groupslists.add(groups_list);

                                    } while (cursor.moveToNext());

                                    Cursor cursor_match_id = mydb.getdata_gameids();// getting game ids here
                                    if (cursor_match_id.getCount()>=0){
                                        if (cursor_match_id.moveToFirst()) {
                                            String games_ids = "";
                                            do {
                                                String id           = cursor_match_id.getString(0);
                                                games_ids += id+",";
                                            } while (cursor_match_id.moveToNext());

                                            //http://192.168.100.14/FirmGround/REST/updateAdditionalParticipents?gameId=1
                                            final String url = getResources().getString(R.string.url) + "getAllMatchSquadDetails?"+
                                                    "userID="+sharedPreferences.getString("userID","")+
                                                    "&games_ids="+games_ids
                                                    ;
                                            final String params = "";
                                            final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                                                @Override
                                                public String TaskCompletionResult(String result) throws JSONException {

                                                    JSONObject jsonObject = new JSONObject(result);

                                                    if(jsonObject.getString("respCode").equals("200")) {
                                                        JSONArray results = jsonObject.getJSONArray("results");

                                                        mydb.delete_record_squad_detail();

                                                        for (int i=0;i<results.length();i++){

                                                            JSONObject c = results.getJSONObject(i);
                                                            String   gId = c.getString("gId");
                                                            JSONArray gameParicpents = c.getJSONArray("gameParicpents");
                                                            //Toast.makeText(getActivity(),"gameParicpents = "+gameParicpents.toString(),Toast.LENGTH_SHORT).show();
                                                            JSONObject  MatchInfo = c.getJSONObject("MatchInfo");
                                                            //Toast.makeText(getActivity(),"MatchInfo = "+MatchInfo.toString(),Toast.LENGTH_SHORT).show();

                                                            JSONObject squadDetails= new JSONObject();

                                                            squadDetails.put("results",gameParicpents);
                                                            squadDetails.put("MatchInfo",MatchInfo);

                                                            mydb.insertdata_squad_details(""+gId,""+squadDetails.toString());
                                                        }
                                                    }else {
                                                    }

                                                    return result;
                                                }
                                            });
                                            webRequestCall.execute(url,"GET",params);
                                        }
                                    }
                                }
                            }

                            myChatRecyclerViewAdapter = new MyChatRecyclerViewAdapter(groupslists, mListener, getActivity());
                            recyclerView.setAdapter(myChatRecyclerViewAdapter);
                        }else{

                        }
                        return result;
                    }
                });
                webRequestCall.execute(url,"GET",params);
                //recyclerView.setAdapter(new MyChatRecyclerViewAdapter(DummyContent.ITEMS, mListener, getActivity()));
        }
    };



    private void SavePreferences(String key, String value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences((getActivity().getApplicationContext()));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
