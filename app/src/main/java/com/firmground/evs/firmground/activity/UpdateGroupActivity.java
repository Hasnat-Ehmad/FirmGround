package com.firmground.evs.firmground.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
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
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.firmground.evs.firmground.R;
import com.firmground.evs.firmground.adapter.ContactsListupdateGroup_adapter;
import com.firmground.evs.firmground.adapter.SquadList_adapter;
import com.firmground.evs.firmground.model.ContactsList_updateGroup;
import com.firmground.evs.firmground.model.Squad_list;
import com.firmground.evs.firmground.imagewebservice.JSONParser_Update_Group;
import com.firmground.evs.firmground.webservice.TaskDelegate;
import com.firmground.evs.firmground.webservice.WebRequestCall;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import static com.firmground.evs.firmground.activity.ChatPageActivity.groupname;
import static com.firmground.evs.firmground.activity.MainActivity.activity;

public class UpdateGroupActivity extends AppCompatActivity {

    ListView listView;
    SquadList_adapter adapter;

    SharedPreferences sharedPreferences;
    ImageView img_group_icon,img_add_participant,img_cross;
    Button btn_update_group;
    JSONParser_Update_Group jsonParser_update_group;
    Cursor cursor ;
    ArrayList<String> StoreContacts ;
    String name, phonenumber,contact_image_uri,contactId;

    ListView list_view_contact;
    ContactsListupdateGroup_adapter contactsListupdateGroup_adapter;

    ArrayList<Squad_list> All_squad =new ArrayList();
    ArrayList<Squad_list> Selected_squad =new ArrayList();
    ArrayList<ContactsList_updateGroup> contactList=new ArrayList();

    String [] checkArray;

    String  group_Id,gameId;

    ProgressBar simpleProgressBar;

    EditText ed_search,ed_group_name ;

    ScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_group);

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        simpleProgressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);

        ed_search = findViewById(R.id.ed_search);

        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter

                //myChatRecyclerViewAdapter.getFilter().filter(s.toString());

                int textlength = s.length();
                ArrayList<ContactsList_updateGroup> tempArrayList = new ArrayList<ContactsList_updateGroup>();
                for(ContactsList_updateGroup c: contactList){
                    if (textlength <= c.getmContactName().length()) {
                        if (c.getmContactName().toLowerCase().contains(s.toString().toLowerCase())) {
                            tempArrayList.add(c);
                        }
                    }
                }
                if (activity!=null)
                    contactsListupdateGroup_adapter = new ContactsListupdateGroup_adapter(UpdateGroupActivity.this,tempArrayList);
                list_view_contact.setAdapter(contactsListupdateGroup_adapter);

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        listView = findViewById(R.id.list_view);
        list_view_contact = findViewById(R.id.list_view_contact);

        Bundle bundle = getIntent().getExtras();

        group_Id    = bundle.getString("group_Id"  );

        if (bundle.containsKey("gameId")){
            gameId = bundle.getString("gameId"  );
        }else {
            gameId="";
        }

        jsonParser_update_group = new JSONParser_Update_Group();

        img_group_icon      = findViewById(R.id.img_group_icon);
        img_add_participant = findViewById(R.id.img_add_participant);
        btn_update_group    = findViewById(R.id.btn_update_group);
        img_cross           = findViewById(R.id.img_cross);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

         ed_group_name = findViewById(R.id.ed_group_name);
        ed_group_name.setSelection(ed_group_name.getText().length());
        InputMethodManager imm = (InputMethodManager) UpdateGroupActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(ed_group_name, InputMethodManager.SHOW_IMPLICIT);

        //http://192.168.100.14/FirmGround/REST/groupsMembers?gId=2
        final String url = getResources().getString(R.string.url) + "groupsMembers?gId="+group_Id;
        final String params = "";
        final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
            @Override
            public String TaskCompletionResult(String result) throws JSONException {

                JSONObject jsonObject = new JSONObject(result);

                if(jsonObject.getString("respCode").equals("200")) {
                    JSONArray results = jsonObject.getJSONArray("results");
                    All_squad.clear();
                    for(int i = 0; i < results.length(); i++) {
                        JSONObject c = results.getJSONObject(i);

                        String membershipId = c.getString("membershipId");
                        String UserID = c.getString("UserID");
                        String groupId = c.getString("gId");
                        String memberRole = c.getString("memberRole");
                        String dateAdded = c.getString("dateAdded");
                        String full_name = c.getString("full_name");
                        String fname = c.getString("fname");
                        String lname = c.getString("lname");
                        String phone = c.getString("phone");
                        String image = c.getString("image");

                        image = getResources().getString(R.string.imageBaseUrl)+image;

                        //Toast.makeText(UpdateGroupActivity.this,fname+" "+UserID,Toast.LENGTH_SHORT).show();
                        if (fname.equals("")){

                            if(full_name.contains(" ")){
                                fname= full_name.substring(0, full_name.indexOf(" "));
                                System.out.println(fname);
                            }else
                                fname=full_name;
                        }

                        Squad_list squad_list = new Squad_list(""+membershipId,"" + fname, ""+memberRole,""+phone,""+image);
                        All_squad.add(squad_list);
                    }

                    adapter = new SquadList_adapter(UpdateGroupActivity.this,All_squad);
                    listView.setAdapter(adapter);

                        JSONObject GroupInfo = jsonObject.getJSONObject("GroupInfo");

                            String gId        = GroupInfo.getString("gId");
                            String GroupName  = GroupInfo.getString("GroupName");
                            ed_group_name.setText(""+GroupName);
                            String UserID     = GroupInfo.getString("UserID");
                            String image      = getResources().getString(R.string.imageBaseUrl)+GroupInfo.getString("image");
                            Picasso.with(img_group_icon.getContext()).load(""+image).into(img_group_icon);
                            String CreatedDate= GroupInfo.getString("CreatedDate");
                            String status     = GroupInfo.getString("status");
                     }

                return result;
            }
        });
        webRequestCall.execute(url,"GET",params);


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


        btn_update_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringBuilder contacts = new StringBuilder();
                final JSONArray users = new  JSONArray();

                for(int i=0;i<contactList.size();i++){
                    if(contactList.get(i).isSelected()){
                        contacts.append(contactList.get(i).getmContactId()).append(",");

                        Squad_list squad_list = new Squad_list("",""+contactList.get(i).getmContactName()
                                ,""+contactList.get(i).getmContactId(),
                                ""+contactList.get(i).getmContactNumber(),"");

                        Selected_squad.add(squad_list);

                    }
                }

                ArrayList<Integer> results = new ArrayList<>();

                // Loop arrayList2 items
                int counter=0,counter2=0;
                for ( Squad_list person2 : Selected_squad) {
                    // Loop arrayList1 items
                   // Toast.makeText(UpdateGroupActivity.this,"Selected Squad = "+Selected_squad.get(counter),Toast.LENGTH_LONG).show();
                    Log.e("mainToPost", "Selected_squad = " + Selected_squad.get(counter).getNumber());
                    counter++;
                    counter2=0;
                    boolean found = false;
                    for (Squad_list person1 : All_squad) {
                     //   Toast.makeText(UpdateGroupActivity.this,"All Squad = "+All_squad.get(counter2),Toast.LENGTH_LONG).show();
                        Log.e("mainToPost", "All_squad = " + All_squad.get(counter2).getNumber());
                        counter2++;

                        if (person2.getNumber().equals(person1.getNumber())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        //results.add(person2.getNumber());

                        JSONObject jsonobj   = new JSONObject();
                        try {
                            jsonobj.put("name", ""+person2.getName());
                            jsonobj.put("phonenumber", ""+person2.getNumber());
                            jsonobj.put("id", "");
                            users.put(jsonobj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                //Toast.makeText(UpdateGroupActivity.this,users.toString(),Toast.LENGTH_LONG).show();

                final JSONObject prams   = new JSONObject();

                if (ed_group_name.getText().toString().equals("")){
                    ed_group_name.setError("Enter Group Name");
                }else {
                    if (image_real_path.equals("")){
                        String url    =  getResources().getString(R.string.url)+"updateGroup";
                        try {
                            prams.put("logIn",sharedPreferences.getString("userID", ""));
                            prams.put("groupName",""+ed_group_name.getText().toString());
                            prams.put("users",users);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Toast.makeText(getActivity(),"users="+prams.toString(),Toast.LENGTH_LONG).show();
                        //String tmp    = Base64.encodeBytes(prams.toString().getBytes());

                        final String params = "users="+prams.toString()+"&gId="+group_Id+"&gameId="+gameId;

                        WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                            @Override
                            public String TaskCompletionResult(String result) {
                                try {
                                    JSONObject jsonObject = new JSONObject(result);
                                    if(jsonObject.getString("respCode").equals("200")) {
                                        //Toast.makeText(UpdateGroupActivity.this,jsonObject.getString("me") , Toast.LENGTH_SHORT).show();
                                        groupname=ed_group_name.getText().toString();
                                        finish();
                                        //startActivity(getIntent());
                                    }
                                    else{
                                        Toast.makeText(UpdateGroupActivity.this,jsonObject.getString("respCode") , Toast.LENGTH_SHORT).show();
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
                            prams.put("groupName",""+ed_group_name.getText().toString());
                            prams.put("users",users);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        send_data_add(""+prams.toString());

                    }
                }
            }
        });

        img_add_participant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestPhone(v);
            }
        });

        img_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listView.setVisibility(View.VISIBLE);
                list_view_contact.setVisibility(View.GONE);
                img_add_participant.setVisibility(View.VISIBLE);
                img_cross.setVisibility(View.GONE);
                ed_search.setText("");
                ed_search.setVisibility(View.INVISIBLE);
            }
        });

    }

    public void requestPhone(final View view) {
        Permissions.check(UpdateGroupActivity.this, Manifest.permission.READ_CONTACTS, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                //Toast.makeText(getActivity(), "Phone granted.", Toast.LENGTH_SHORT).show();
                ed_search.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                contactList.clear();
                simpleProgressBar.setVisibility(View.VISIBLE);

                int SPLASH_TIME_OUT = 200;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GetContactsIntoArrayList();
                    }
                }, SPLASH_TIME_OUT);

                list_view_contact.setVisibility(View.VISIBLE);
                img_add_participant.setVisibility(View.GONE);
                img_cross.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);

                requestPhone(view);
            }
        });
    }
    public void setDynamicHeight(ListView listView) {
        int height = 0;
        System.out.println("++++order_list_adapter.getCount() = " +contactsListupdateGroup_adapter.getCount());
        if (contactsListupdateGroup_adapter.getCount() == 0) {
            return;
        }
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < contactsListupdateGroup_adapter.getCount(); i++) {
            View listItem = contactsListupdateGroup_adapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            height += listItem.getMeasuredHeight();

            height += 40;
            System.out.println("++++item height1 = " +height);
            //height += 10;
            System.out.println("++++item height2 = " +height);
        }
        ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
        layoutParams.height = height + ((contactsListupdateGroup_adapter.getCount() - 1));
        listView.setLayoutParams(layoutParams);
        listView.requestLayout();

    }
    public void GetContactsIntoArrayList(){

        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);
        StoreContacts = new ArrayList<String>();//my contacts

        JSONArray jsonArray = new  JSONArray();
        JSONObject contactObj= new JSONObject();

        checkArray = new String [cursor.getCount()];

        contactList.clear();

        int counter=0;

        while (cursor.moveToNext()) {

            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contact_image_uri= cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.CONTACT_ID));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

            Bitmap bp = BitmapFactory.decodeResource(getResources(),R.drawable.default_person,options);

            try {

                InputStream inputStream = null;

                if (!contactId.equals(null)){
                    inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
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


            phonenumber = phonenumber.replaceAll("\\(", "");
            phonenumber = phonenumber.replaceAll("\\)","");
            phonenumber = phonenumber.replaceAll("-"  ,"");
            phonenumber = phonenumber.replaceAll(" "  ,"");
            phonenumber = phonenumber.replaceAll("  " ,"");
            //Toast.makeText(getApplicationContext(),temp+"   "+phonenumber,Toast.LENGTH_SHORT).show();
            //Toast.makeText(getActivity(),"bp = "+bp.toString(),Toast.LENGTH_SHORT).show();

            StoreContacts.add(name + " "  + ":" + " " + phonenumber);

            String contact = name + ""  + ":" + "" + phonenumber +"\n";

            Drawable[] myImageList = new Drawable[]{getApplicationContext().getResources().getDrawable(R.drawable.user)};
            Drawable d = myImageList[0];

            if (bp==null) {
                bp = ((BitmapDrawable)d).getBitmap();
                //bp = (Bitmap) BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_person_grey_24dp);
            }

            ContactsList_updateGroup contactsList = new ContactsList_updateGroup("", "", ""+name,
                    ""+phonenumber, null, null, false, bp, null, null, null);

            if (Arrays.asList(checkArray).contains(phonenumber)){

                Log.i("donothing", "name: "+name+" phone: "+phonenumber);
            }else {
                contactList.add(contactsList);
                checkArray[counter] = phonenumber;
                counter++;
            }

            try {
                // making kson data here for sending this on api.
                JSONObject jsonobj   = new JSONObject();
                jsonobj.put("name", ""+name);
                jsonobj.put("phonenumber", ""+phonenumber);

                jsonArray.put(jsonobj);

                contactObj.put("contacts",jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Log.e("mainToPost", "contactObj = " + contactObj.toString());

            /*if (count>50){
                break;
            }
            count++;*/

            //Toast.makeText(MainActivity.this,""+contact,Toast.LENGTH_SHORT).show();
            //write1(contact);//here calling the write function
        }

        contactsListupdateGroup_adapter = new ContactsListupdateGroup_adapter(UpdateGroupActivity.this,contactList);
        list_view_contact.setAdapter(contactsListupdateGroup_adapter);
        setDynamicHeight(list_view_contact);
        cursor.close();

        simpleProgressBar.setVisibility(View.GONE);

        //http://192.168.100.14/FirmGround/REST/getRegisteredContacts
       /* String url    =  getResources().getString(R.string.url)+"getRegisteredContacts";
          String params = "contacts="+contactObj.toString();
          WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
            @Override
            public String TaskCompletionResult(String result) {

                try {

                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.getString("respCode").equals("200")) {
                        JSONArray respData = jsonObject.getJSONArray("respData");
                        for(int i = 0; i < respData.length(); i++) {
                            JSONObject c = respData.getJSONObject(i);
                            String id = c.getString("id");
                            String image = c.getString("image");
                            String phonenumber = c.getString("phonenumber");
                            String name = c.getString("name");
                            //Toast.makeText(getActivity(),name, Toast.LENGTH_SHORT).show();
                            ContactsList contactsList = new ContactsList(null, ""+id, ""+name,
                                            ""+phonenumber, null, null, false, null, null, null, null);
                            contactList.add(contactsList);
                        }
                        adapter = new ContactsList_adapter(getActivity(),contactList);
                        listView.setAdapter(adapter);
                    }
                    else{
                   }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return result;
            }
        });
        webRequestCall.execute(url, "POST", params);
        */
    }
    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            int result = UpdateGroupActivity.this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = UpdateGroupActivity.this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
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
            ActivityCompat.requestPermissions((UpdateGroupActivity.this), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    public String returnImagePath(){
        return image_real_path;
    }
    private void send_data_add(final String params_) {

        new AsyncTask<Void, Integer, Boolean>() {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(UpdateGroupActivity.this);
                progressDialog.setMessage("Save Data...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {

                    JSONObject jsonObject;
                    jsonObject=jsonParser_update_group.uploadImage(image_real_path,""+params_,""+group_Id,UpdateGroupActivity.this);

                    if (jsonObject != null) {
                        if(jsonObject.getString("respCode").equals("200")){

                            SavePreferences("group_Image" ,getResources().getString(R.string.imageBaseUrl)+jsonObject.getString("group_Image" ));
                            groupname=ed_group_name.getText().toString();
                            Log.i("TAG", "group_Image : " +sharedPreferences.getString("group_Image",""));
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
                try
                {
                    if (aBoolean) {
                        Toast.makeText(UpdateGroupActivity.this,"Profile Saved!",Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(UpdateGroupActivity.this, "Failed To Save Data, Try Again!", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }finally {
                    progressDialog.dismiss();
                }
            }
        }.execute();

    }
    private void SavePreferences(String key, String value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == Activity.RESULT_OK && data != null) {
            String realPath;
            // SDK < API11
            if (Build.VERSION.SDK_INT < 11)
                realPath = RealPathUtil_match.getRealPathFromURI_BelowAPI11(UpdateGroupActivity.this, data.getData());

                // SDK >= 11 && SDK < 19
            else if (Build.VERSION.SDK_INT < 19)
                realPath = RealPathUtil_match.getRealPathFromURI_API11to18(UpdateGroupActivity.this, data.getData());

                // SDK > 19 (Android 4.4)
            else
                realPath = RealPathUtil_match.getRealPathFromURI_API19(UpdateGroupActivity.this, data.getData());


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
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uriFromPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //img_group_icon.setImageBitmap(bitmap);

        Log.d("HMKCODE", "Build.VERSION.SDK_INT:" + sdk);
        Log.d("HMKCODE", "URI Path:" + uriPath);
        Log.d("HMKCODE", "Real Path: " + realPath);

        image_real_path = file.getAbsolutePath();
        compressImage(image_real_path);
        //send_data_add(""+sharedPreferences.getString("userID",""),"profileImage");
    }


    //==========================================================

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
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
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
