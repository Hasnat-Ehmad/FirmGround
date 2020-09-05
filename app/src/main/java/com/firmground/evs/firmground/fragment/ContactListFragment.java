package com.firmground.evs.firmground.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firmground.evs.firmground.R;
import com.firmground.evs.firmground.activity.MainActivity;
import com.firmground.evs.firmground.adapter.ContactsList_adapter;
import com.firmground.evs.firmground.circle_image.CircleImageView;
import com.firmground.evs.firmground.database.MyDatabase;
import com.firmground.evs.firmground.model.ContactsList;
import com.firmground.evs.firmground.imagewebservice.JSONParser_Upload_Create_Group;
import com.firmground.evs.firmground.model.Groups_list;
import com.firmground.evs.firmground.webservice.TaskDelegate;
import com.firmground.evs.firmground.webservice.WebRequestCall;
import static com.firmground.evs.firmground.fragment.GroupFragment.ground_Id;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static com.firmground.evs.firmground.activity.MainActivity.activity;
import static com.firmground.evs.firmground.activity.MainActivity.add_group_img;
import static com.firmground.evs.firmground.fragment.GroupFragment.wifi_checker;
//import static com.firmground.evs.firmground.activity.MainActivity.progressDialog;


public class ContactListFragment extends Fragment {

    ArrayList<String> StoreContacts ;
    Cursor cursor ;
    String name, phonenumber,contact_image_uri;

    ListView listView;
    ContactsList_adapter adapter;
    ArrayList<ContactsList> contactList=new ArrayList();

    String [] checkArray;

    SharedPreferences sharedPreferences;

    JSONParser_Upload_Create_Group jsonParser_upload_create_group;

    MyDatabase mydb;

    TextView title;
    static int contactfunctioncheck=0;

    ImageView add_group_img_;
    public static int titlebtncheck = 0;

    ContactsList_adapter.AdapterInterface listener;

    TextView selected_count;

    int select_contact_count = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_contact_list, container, false);

        selected_count = rootView.findViewById(R.id.selected_count);

        ImageView search_img = getActivity().findViewById(R.id.search_img);
        search_img .setVisibility(View.VISIBLE);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        add_group_img.setVisibility(View.GONE);


        add_group_img_ = getActivity().findViewById(R.id.add_group_img);
        add_group_img_.setVisibility(View.VISIBLE);
        add_group_img_.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_refresh_white_24dp));
        add_group_img_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetContactsIntoArrayList();
            }
        });

        mydb = new MyDatabase(getActivity());

        ground_Id = "";

        final EditText ed_search = getActivity().findViewById(R.id.ed_search);
        final LinearLayout toolbar_layout = getActivity().findViewById(R.id.toolbar_layout);
        ImageView cross_img = getActivity().findViewById(R.id.cross_img);
        cross_img.setVisibility(View.GONE);
        ed_search.setVisibility(View.GONE);
        ed_search.setHint("Contact Name");


        listener = new ContactsList_adapter.AdapterInterface()
        {
            @Override
            public void onClick(int value) {

                select_contact_count = value;
                if (value>0){
                    selected_count.setVisibility(View.VISIBLE);
                    selected_count.setText("Contact selected "+value);
                }else {
                    selected_count.setVisibility(View.GONE);
                }
            }
        };

        // Add Text Change Listener to EditText
        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Call back the Adapter with current character to Filter
                //adapter.getFilter().filter(s.toString());

                int textlength = s.length();
                ArrayList<ContactsList> tempArrayList = new ArrayList<ContactsList>();
                for(ContactsList c: contactList){
                    if (textlength <= c.getmContactName().length()) {
                        if (c.getmContactName().toLowerCase().contains(s.toString().toLowerCase())) {
                            tempArrayList.add(c);
                        }
                    }
                }
                if (getActivity()!=null)
                adapter = new ContactsList_adapter(getActivity(),tempArrayList,listener);
                listView.setAdapter(adapter);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        toolbar_layout.setVisibility(View.VISIBLE);

        jsonParser_upload_create_group = new JSONParser_Upload_Create_Group();

        listView = (ListView) rootView.findViewById(R.id.list_view);

        final Button btn_save = rootView.findViewById(R.id.btn_save);
        btn_save.setVisibility(View.GONE);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wifi_checker==1){
                    alertDialogShow_create_group(getActivity());
                }else {
                    Toast.makeText(getActivity(),"Connectivity failure !!!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        title = rootView.findViewById(R.id.title);
        titlebtncheck = 0;
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (titlebtncheck ==0){
                    titlebtncheck = 1;
                    adapter.notifyDataSetInvalidated();
                    btn_save.setVisibility(View.VISIBLE);
                    title.setBackground(getActivity().getResources().getDrawable(R.drawable.blue_border));
                    if(select_contact_count>0){
                        selected_count.setVisibility(View.VISIBLE);
                    }

                   // Toast.makeText(getActivity(),"value = "+titlebtncheck, Toast.LENGTH_SHORT).show();
                }else {
                    titlebtncheck = 0;
                    adapter.notifyDataSetInvalidated();
                    btn_save.setVisibility(View.GONE);
                    title.setBackground(getActivity().getResources().getDrawable(R.drawable.transparent_border));
                    selected_count.setVisibility(View.GONE);
                    //Toast.makeText(getActivity(),"value = "+titlebtncheck, Toast.LENGTH_SHORT).show();
                }
            }
        });


        /*
        if (contactfunctioncheck==0){
            contactfunctioncheck=1;
            }else{
           }
        */

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        add_group_img_.setVisibility(View.GONE);
        titlebtncheck = 0;
        SavePreferences_int("titlebtncheck",0);
    }

    @Override
    public void onStart() {
        super.onStart();
        GetContactsIntoArrayList();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void SavePreferences_int(String key, int value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }




    public void GetContactsIntoArrayList(){
        //Toast.makeText(getActivity(), "fucntion = ", Toast.LENGTH_LONG).show();

        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, ContactsContract.CommonDataKinds.Phone.PHONETIC_NAME+" ASC");

        //Toast.makeText(getActivity(), "phones.getCount() = "+phones.getCount(), Toast.LENGTH_LONG).show();
        cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, ContactsContract.CommonDataKinds.Phone.PHONETIC_NAME+" ASC");
        StoreContacts = new ArrayList<String>();//my contacts

        JSONArray  jsonArray  = new  JSONArray();
        JSONObject contactObj = new JSONObject();

        Cursor cursor_db = mydb.getdata_contact_user_id_wise_contact(sharedPreferences.getString("userID",""));

        Log.i("TAG", "cursor = "+cursor.getCount()+ " cursor_db = "+cursor_db.getCount());

        if (cursor_db.getCount()==0){
            //get Contact from phone memory

            Log.i("TAG", "Contact from phone");
            try
            {
                //Toast.makeText(getActivity(), "cursor.getCount() = "+cursor.getCount(), Toast.LENGTH_LONG).show();
                //mydb.delete_contact();
                checkArray = new String [cursor.getCount()];
                int counter=0;
                Log.i("cursor.getCount()", "cursor.getCount() = "+cursor.getCount());

                contactList.clear();

                while (cursor.moveToNext()) {
                    String contactId;
                    name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    contact_image_uri= cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                    contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.CONTACT_ID));

                    //Toast.makeText(getActivity(), "contactId in fragment = "+contactId, Toast.LENGTH_LONG).show();

                    phonenumber = phonenumber.replaceAll("\\(", "");
                    phonenumber = phonenumber.replaceAll("\\)","");
                    phonenumber = phonenumber.replaceAll("-"  ,"");
                    phonenumber = phonenumber.replaceAll(" "  ,"");
                    phonenumber = phonenumber.replaceAll("  " ,"");

                    phonenumber =  phonenumber.replaceAll(" ", "");

                    StoreContacts.add(name + " "  + ":" + " " + phonenumber);

                    String contact = name + ""  + ":" + "" + phonenumber +"\n";

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 10;

                    Bitmap bp = null;
                    //Bitmap bp = BitmapFactory.decodeResource(getResources(),R.drawable.default_person,options);
                  /*  try {

                        InputStream inputStream = null;

                        if (!contactId.equals(null)){
                            inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getActivity().getContentResolver(),
                                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactId)));
                        }
                        if (inputStream != null) {
                            bp = BitmapFactory.decodeStream(inputStream);
                        }
                        if(inputStream != null)
                            inputStream.close();

                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Drawable[] myImageList = new Drawable[]{getActivity().getApplicationContext().getResources().getDrawable(R.drawable.user)};
                    Drawable d = myImageList[0];*/

                    if (bp==null) {
                        //bp = ((BitmapDrawable)d).getBitmap();

                        //Log.i("TAG bp", "bp 1: "+bp);

                        ContactsList contactsList = new ContactsList("", "", ""+name,
                                ""+phonenumber, null, null, false, bp, null, null, null);

                        if (Arrays.asList(checkArray).contains(phonenumber)){
                            Log.i("donothing", "name: "+name+" phone: "+phonenumber);
                        }else {
                            contactList.add(contactsList);
                            checkArray[counter] = phonenumber;
                            counter++;

                            mydb.insertdata_contact(""+sharedPreferences.getString("userID",""),""+name,""+phonenumber,"","active");

                        }
                        //bp = ((BitmapDrawable)d).getBitmap();
                        //bp = (Bitmap) BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_person_grey_24dp);
                    }else {

                        //Log.i("TAG bp", "bp 2: "+bp);

                        ContactsList contactsList = new ContactsList("", "", ""+name,
                                ""+phonenumber, null, null, false, bp, null, null, null);

                        if (Arrays.asList(checkArray).contains(phonenumber)){
                            Log.i("donothing", "name: "+name+" phone: "+phonenumber);
                        }else {
                            contactList.add(contactsList);
                            checkArray[counter] = phonenumber;
                            counter++;

                            mydb.insertdata_contact(    ""+sharedPreferences.getString("userID",""),
                                                    ""+name,""+phonenumber,"","active");
                        }
                    }
                    Log.i("PhoneContact", "name: "+name+" phone: "+phonenumber);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
            adapter = new ContactsList_adapter(getActivity(),contactList,listener);
            listView.setAdapter(adapter);

        }
        else {
            //get Contact from sqllite database
            Log.i("TAG", "Contact from DB");
            if (cursor_db.getCount()>=0){
                if (cursor_db.moveToFirst()) {
                    contactList.clear();
                    do {
                        String user_id         = cursor_db.getString(0);
                        String contactname     = cursor_db.getString(1);
                        String contactnumber   = cursor_db.getString(2);
                        String contactdbuser_id= cursor_db.getString(3);
                        String contactstatus   = cursor_db.getString(4);

                        Log.e("YYYY",
                                " contactname  = " + contactname +
                                     " contactnumber = " + contactnumber +
                                     " contactstatus = " + contactstatus
                               );

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 6;

                        Bitmap bp = null;
                      /*  Bitmap bp = BitmapFactory.decodeResource(getResources(),R.drawable.default_person,options);
                        try {

                            InputStream inputStream = null;

                            if (inputStream != null) {
                                bp = BitmapFactory.decodeStream(inputStream);
                            }
                            if(inputStream != null)
                                inputStream.close();

                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/

                        if (getActivity()!=null){

                            Drawable[] myImageList = new Drawable[]{getActivity().getApplicationContext().getResources().getDrawable(R.drawable.user)};
                            Drawable d = myImageList[0];

                            /*
                            if (bp==null) {
                                bp = ((BitmapDrawable) d).getBitmap();
                            }
                            */

                            ContactsList contactsList = new ContactsList("", "", "" + contactname,
                                    "" + contactnumber, null, null, false, bp, null, null, null);

                            contactList.add(contactsList);

                        }
                    } while (cursor_db.moveToNext());

                    if (!activity.isFinishing()){
                        adapter = new ContactsList_adapter(activity,contactList,listener);
                        listView.setAdapter(adapter);

                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    CircleImageView img_group_icon;
    public void alertDialogShow_create_group(final Context context) {

        StringBuilder contacts = new StringBuilder();
        final JSONArray users = new  JSONArray();

        final JSONObject prams   = new JSONObject();

        for(int i=0;i<contactList.size();i++){
            if(contactList.get(i).isSelected()){
                contacts.append(contactList.get(i).getmContactId()).append(",");

                JSONObject jsonobj   = new JSONObject();
                try {
                    jsonobj.put("name", ""+contactList.get(i).getmContactName());
                    jsonobj.put("phonenumber", ""+contactList.get(i).getmContactNumber());
                    jsonobj.put("id", ""+contactList.get(i).getmContactId());

                    users.put(jsonobj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
       // Toast.makeText(getActivity(),users.toString(),Toast.LENGTH_LONG).show();

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.custom_prompt_crud_option,
                null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

        alertDialogBuilder.setView(promptsView);

        final AlertDialog d = alertDialogBuilder.show();
        d.getWindow().setBackgroundDrawableResource(android.R.color.white);

        TextView textView_label = promptsView.findViewById(R.id.tv_label_popup);

        img_group_icon = promptsView.findViewById(R.id.img_group_icon);
        img_group_icon.setOnClickListener(new View.OnClickListener() {
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

        final EditText ed_create_group = promptsView.findViewById(R.id.ed_create_group);
        Button btn_create_group = promptsView.findViewById(R.id.btn_create_group);
        btn_create_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//Create Group API
//http://192.168.100.14/FirmGround/REST/createGroup?users={"logIn":2, "groupName":"City Spiders",
//"users":[{"name":"Abc2","phonenumber":"122323407","id":1},{"name":"Abc3","phonenumber":"123456701","id":3}]}

                if (ed_create_group.getText().toString().equals("")){
                    ed_create_group.setError("Enter Group Name");
                }else {

                    if (image_real_path.equals("")){

                        String url = getResources().getString(R.string.url)+"createGroup";

                        try {
                            prams.put("logIn",sharedPreferences.getString("userID", ""));
                            prams.put("groupName",""+ed_create_group.getText().toString());
                            prams.put("users",users);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Toast.makeText(getActivity(),"users="+prams.toString(),Toast.LENGTH_LONG).show();
                        String tmp    = Base64.encodeToString(prams.toString().getBytes(),Base64.NO_WRAP);

                        final String params = "users="+tmp;

                        WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                            @Override
                            public String TaskCompletionResult(String result) {

                                try {

                                    JSONObject jsonObject = new JSONObject(result);
                                    if(jsonObject.getString("respCode").equals("200")) {

                                        JSONArray results = jsonObject.getJSONArray("results");
                                        //Toast.makeText(getActivity(),jsonObject.getString("respCode") , Toast.LENGTH_SHORT).show();

                                        String is_reg = "",phone="";
                                        String number="",group_id="",dateAdded="";
                                        int counter=0;

                                        for(int i = 0; i < results.length(); i++) {
                                            JSONObject c = results.getJSONObject(i);

                                            group_id  = c.getString("gId");
                                            dateAdded = c.getString("dateAdded");

                                            if (c.has("membershipId")){
                                                // Toast.makeText(getActivity(),"yes",Toast.LENGTH_SHORT).show();
                                            }
                                            is_reg = c.getString("is_reg");
                                            phone = c.getString("phone");

                                            if (is_reg.equals("0")){
                                                if (!c.getString("UserID").equals(""+sharedPreferences.getString("userID",""))){
                                                    number+=phone+",";
                                                    counter++;
                                                }
                                            }
                                                Log.d("TAG", "File...:::: number = " + number);
                                        }

                                        if (counter>0){

                                            TextView textView_title = new TextView(getActivity());
                                            textView_title.setText(getActivity().getResources().getString(R.string.str_title_message));
                                            textView_title.setGravity(Gravity.START);
                                            textView_title.setPadding(20,10,20,10);
                                            textView_title.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
                                            textView_title.setCompoundDrawablePadding(10);

                                            //textView_title.setCompoundDrawables(null,null,mContext.getResources().getDrawable(R.drawable.ic_warning_colored_24dp),null);
                                            textView_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_warning_colored_24dp, 0);
                                            final String finalNumber = number;
                                            new AlertDialog.Builder(getActivity())
                                                    .setCustomTitle(textView_title)
                                                    .setMessage(getActivity().getResources().getString(R.string.str_send_message))
                                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                                    .setPositiveButton(getActivity().getResources().getString(R.string.str_yes), new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            getActivity().getSupportFragmentManager().popBackStack();
                                                            d.dismiss();

                                                            Intent smsIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+removeLastChar(finalNumber)));
                                                            smsIntent.putExtra("sms_body", "http://evsoft.pk/FirmGround/FirmGround.apk  \n Download this from given link");
                                                            startActivity(smsIntent);

                                                        }
                                                    })
                                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                                    .setNegativeButton(getActivity().getResources().getString(R.string.str_no), new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            getActivity().getSupportFragmentManager().popBackStack();
                                                            d.dismiss();
                                                        }
                                                    })
                                                    //.setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                        }

                                        //http://192.168.100.14/FirmGround/REST/sendMessage?userId, gId, message
                                        final String url = getResources().getString(R.string.url) + "sendMessage";

                                        final String params ="userId="+sharedPreferences.getString("userID","")+
                                                "&gId="+group_id+
                                                "&message="+sharedPreferences.getString("fname","")+" created Group \""+ed_create_group.getText().toString()+"\""+
                                                "&messageId="+
                                                "&message_type=initial";

                                        final String finalGroup_id = group_id;
                                        final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                                            @Override
                                            public String TaskCompletionResult(String result) throws JSONException {

                                                JSONObject jsonObject = new JSONObject(result);

                                                if(jsonObject.getString("respCode").equals("200")) {

                                                    SimpleDateFormat timeStampFormat = new SimpleDateFormat("hh:mm");
                                                    Date myDate = new Date();
                                                    final String currenttime = timeStampFormat.format(myDate);

                                                    String messageId     = jsonObject.getString("messageId");
                                                    String tempMessageId = jsonObject.getString("tempMessageId");
                                                    String temp_message  = jsonObject.getString("message");

                                                    mydb.insertdata_chat(""+ finalGroup_id,""+sharedPreferences.getString("userID",""),
                                                            ""+messageId,"✓✓","",
                                                            ""+currenttime,""+temp_message,"self","read");

                                                    getActivity().getSupportFragmentManager().popBackStack();
                                                    d.dismiss();

                                                }else{
                                                }
                                                return result;
                                            }
                                        });
                                        webRequestCall.execute(url,"POST",params);

                                        /*
                                        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                        i.putExtra("address", "987385438; 750313; 971855;84393");
                                        i.putExtra("sms_body", "Testing you!");
                                        i.setType("vnd.android-dir/mms-sms");
                                        getActivity().startActivity(i);
                                        */
                                    }
                                    else{
                                        Toast.makeText(getActivity(),jsonObject.getString("status_alert") , Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                return result;
                            }
                        });
                        webRequestCall.execute(url, "POST", params);

                    }else {

                        try {
                            prams.put("logIn",sharedPreferences.getString("userID", ""));
                            prams.put("groupName",""+ed_create_group.getText().toString());
                            prams.put("users",users);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String tmp    = Base64.encodeToString(prams.toString().getBytes(),Base64.NO_WRAP);

                        final String params = ""+tmp;

                        d.dismiss();
                        send_data_add(""+params,ed_create_group.getText().toString());

                    }
                }
            }
        });

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
                realPath = RealPathUtil_cgp.getRealPathFromURI_BelowAPI11(getActivity(), data.getData());
                // SDK >= 11 && SDK < 19
            else if (Build.VERSION.SDK_INT < 19)
                realPath = RealPathUtil_cgp.getRealPathFromURI_API11to18(getActivity(), data.getData());
                // SDK > 19 (Android 4.4)
            else
                realPath = RealPathUtil_cgp.getRealPathFromURI_API19(getActivity(), data.getData());
            setmatchpic(Build.VERSION.SDK_INT, data.getData().getPath(), realPath);

        }
    }
    String image_real_path="";
    private void setmatchpic(int sdk, String uriPath, String realPath) {

        File file = new File(realPath);

        Uri uriFromPath = Uri.fromFile(file);

        // you have two ways to display selected image
        // ( 1 ) imageView.setImageURI(uriFromPath);
        // ( 2 ) imageView.setImageBitmap(bitmap);

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uriFromPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Log.d("HMKCODE", "Build.VERSION.SDK_INT:" + sdk);
        Log.d("HMKCODE", "URI Path:" + uriPath);
        Log.d("HMKCODE", "Real Path: " + realPath);

        image_real_path = file.getAbsolutePath();
        //img_group_icon.setImageBitmap(bitmap);

        compressImage(image_real_path);

        //send_data_add(""+sharedPreferences.getString("userID",""),"profileImage");
    }
    private void send_data_add(final String params_, final String groupname) {
        new AsyncTask<Void, Integer, Boolean>() {
            ProgressDialog progressDialog;
            JSONArray results;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Save Data...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {

                    JSONObject jsonObject;
                    jsonObject=jsonParser_upload_create_group.uploadImage(image_real_path,""+params_,getActivity());

                    if (jsonObject != null) {
                        if(jsonObject.getString("respCode").equals("200")){
                            results = jsonObject.getJSONArray("results");
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

                try {
                    if (aBoolean) {
                        Toast.makeText(getActivity(),"Group Created",Toast.LENGTH_SHORT).show();
                        dialog_fuction(results,groupname);
                    }
                    else{
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
    public void dialog_fuction(JSONArray results, final String groupname) {

       // Toast.makeText(getActivity(), "inside the image function", Toast.LENGTH_LONG).show();

        String is_reg = "",phone="";
        String number="",group_id="",dateAdded="";
        int counter=0;

        for(int i = 0; i < results.length(); i++) {
            JSONObject c = null;
            try {
                c = results.getJSONObject(i);


            group_id  = c.getString("gId");

            is_reg = c.getString("is_reg");
            phone = c.getString("phone");

            if (is_reg.equals("0")){
                if (!c.getString("UserID").equals(""+sharedPreferences.getString("userID",""))){
                    number+=phone+",";
                    counter++;
                }
            }
            Log.d("TAG", "File...:::: number = " + number);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (counter>0){

            Log.d("TAG", "counter = " + counter);
            TextView textView_title = new TextView(activity);
            textView_title.setText(getActivity().getResources().getString(R.string.str_title_message));
            textView_title.setGravity(Gravity.START);
            textView_title.setPadding(20,10,20,10);
            textView_title.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
            textView_title.setCompoundDrawablePadding(10);

            //textView_title.setCompoundDrawables(null,null,mContext.getResources().getDrawable(R.drawable.ic_warning_colored_24dp),null);
            textView_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_warning_colored_24dp, 0);
            final String finalNumber = number;

            Log.d("TAG", "finalNumber = " + finalNumber);
            final String finalGroup_id = group_id;
            new AlertDialog.Builder(activity)
                    .setCustomTitle(textView_title)
                    .setMessage(activity.getResources().getString(R.string.str_send_message))
                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(activity.getResources().getString(R.string.str_yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            Log.d("TAG", "getActi = " );

                            Intent smsIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+removeLastChar(finalNumber)));
                            smsIntent.putExtra("sms_body", "http://evsoft.pk/FirmGround/FirmGround.apk  \n Download this from given link");
                            activity.startActivity(smsIntent);

                            send_function(finalGroup_id,groupname);
                        }
                    })
                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(activity.getResources().getString(R.string.str_no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            send_function(finalGroup_id,groupname);

                        }
                    })
                    //.setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }else {
                send_function(group_id,groupname);
        }



    }
    public void send_function(final String group_id, String groupname){
        //http://192.168.100.14/FirmGround/REST/sendMessage?userId, gId, message
        final String url = getResources().getString(R.string.url) + "sendMessage";

        final String params_ ="userId="+sharedPreferences.getString("userID","")+
                "&gId="+group_id+
                "&message="+sharedPreferences.getString("fname","")+" created Group \""+groupname+"\""+
                "&messageId="+
                "&message_type=initial";

        final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
            @Override
            public String TaskCompletionResult(String result) throws JSONException {

                JSONObject jsonObject = new JSONObject(result);

                if(jsonObject.getString("respCode").equals("200")) {


                    SimpleDateFormat timeStampFormat = new SimpleDateFormat("hh:mm");
                    Date myDate = new Date();
                    final String currenttime = timeStampFormat.format(myDate);

                    String messageId     = jsonObject.getString("messageId");
                    String tempMessageId = jsonObject.getString("tempMessageId");
                    String temp_message  = jsonObject.getString("message");

                    mydb.insertdata_chat(""+ group_id,""+sharedPreferences.getString("userID",""),
                            ""+messageId,"✓✓","",
                            ""+currenttime,""+temp_message,"self","read");


                    getActivity().getSupportFragmentManager().popBackStack();


                }else{
                }
                return result;
            }
        });
        webRequestCall.execute(url,"POST",params_);

    }
    private static String removeLastChar(String str) {

        if(str.equals("")){
            return "";
        }else
            return str.substring(0, str.length() - 1);
    }

    //======================Image Compression====================================

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

            img_group_icon.setImageBitmap(scaledBitmap);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Toast.makeText(MainActivity.this, "filename = "+filename, Toast.LENGTH_LONG).show();
        image_real_path=filename;
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


}

class RealPathUtil_cgp {

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