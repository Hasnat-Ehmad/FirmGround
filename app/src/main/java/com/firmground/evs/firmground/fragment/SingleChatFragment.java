package com.firmground.evs.firmground.fragment;

import android.Manifest;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firmground.evs.firmground.R;
import com.firmground.evs.firmground.adapter.Message_List_Adapter;
import com.firmground.evs.firmground.database.MyDatabase;
import com.firmground.evs.firmground.imagewebservice.JSONParser_Send_Chat_Image;
import com.firmground.evs.firmground.model.Message_List;
import com.firmground.evs.firmground.service.Incoming_Chat_Service;
import com.firmground.evs.firmground.webservice.TaskDelegate;
import com.firmground.evs.firmground.webservice.WebRequestCall;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.firmground.evs.firmground.R.*;
import static com.firmground.evs.firmground.activity.MainActivity.activity;
import static com.firmground.evs.firmground.fragment.GroupFragment.ground_Id;
import static com.firmground.evs.firmground.fragment.GroupFragment.wifi_checker;


public class SingleChatFragment extends Fragment {
    String contactname,contactnumber;

     ArrayList<Message_List> message_ArrayList =new ArrayList();
     Message_List_Adapter message_list_adapter;

    private FragmentActivity fragmentActivity;

    ListView mListView;

    SharedPreferences sharedPreferences;

    public Intent service_chat;

    MyDatabase mydb;

    EditText messageArea;

    ImageView floatingButton;

    int buttonclickcheck=0;

    LinearLayout single_chat,toolbar_layout;

    ImageButton attach_icon,attach_recording;

    JSONParser_Send_Chat_Image jsonParser_send_chat_image;

    private  String mFileName = null;

    int audio_check=0;

    private MediaRecorder mRecorder;

    private final String LOG_TAG = "AudioRecording";

    int singlechatscreen=0;

    Timer T;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        //View rootView = inflater.inflate(R.layout.fragment_single_chat, container, false);
        View rootView = inflater.inflate(layout.pagerfragment_chat2, container, false);

        final EditText ed_search = getActivity().findViewById(R.id.ed_search);
        ed_search.setText("");
        ed_search.setVisibility(View.GONE);
        ImageView search_img = getActivity().findViewById(R.id.search_img);
        search_img .setVisibility(View.GONE);

        jsonParser_send_chat_image = new JSONParser_Send_Chat_Image();

        contactname = getArguments().getString("contactname");
        contactnumber = getArguments().getString("contactnumber");

        single_chat =  getActivity().findViewById(id.single_chat);
        single_chat.setVisibility(View.VISIBLE);

        toolbar_layout = getActivity().findViewById(R.id.toolbar_layout);
        toolbar_layout.setVisibility(View.GONE);

        final ImageView persone_image = getActivity().findViewById(id.persone_image);
        persone_image.setBackground(null);
        persone_image.setImageDrawable(getActivity().getResources().getDrawable(drawable.user));

        final TextView tv_name= getActivity().findViewById(id.tv_name);
        tv_name.setVisibility(View.VISIBLE);
        tv_name.setText(""+contactname);

        final ImageView back_image = getActivity().findViewById(id.back_image);
        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        mydb = new MyDatabase(getActivity());

        service_chat = new Intent(getActivity(), Incoming_Chat_Service.class);
        getActivity().stopService(service_chat);
        getActivity().startService(service_chat);

        messageArea= rootView.findViewById(R.id.editText);
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
                    //create group api call here and get the group id here
                    if (ground_Id.equals("")){
                        buttonclickcheck=1;
                        create_group();
                    }else {
                        send_message();
                    }
                }
            }
        });

        //=========================================================================

        attach_icon= rootView.findViewById(R.id.attach_icon);
        attach_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(),"attach Button Clicked",Toast.LENGTH_SHORT).show();
                //create group api call here and get the group id here
                if (ground_Id.equals("")){
                    buttonclickcheck=2;
                    create_group();
                }else {
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
            }
        });
        attach_recording = rootView.findViewById(R.id.attach_recording);
        attach_recording.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        requestAudio(v);

                        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                        // Vibrate for 500 milliseconds
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                               }else{
                                //deprecated in API 26
                                vibrator.vibrate(100);
                           }
                        mRecorder = new MediaRecorder();
                        mRecorder.reset();

                        if (audio_check==1){

                            if (ground_Id.equals("")){
                                buttonclickcheck=3;
                                create_group();
                            }

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
                                            messageArea.setText("00:"+ count[0]);
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

                            if (buttonclickcheck==3){
                                send_data_image_single(""+sb.toString(),""+time, "initial");
                            }else {
                                send_data_image_single(""+sb.toString(),""+time, "");
                            }

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
        //==========================================================================

        return rootView;
    }

    public void create_group(){

        final JSONArray users = new  JSONArray();
        final JSONObject prams   = new JSONObject();

        JSONObject jsonobj   = new JSONObject();
        try {
            jsonobj.put("name", ""+contactname);
            jsonobj.put("phonenumber", ""+contactnumber);
            jsonobj.put("id", "");

            users.put(jsonobj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = getResources().getString(R.string.url)+"createGroup";

        try {
            prams.put("logIn",sharedPreferences.getString("userID", ""));
            prams.put("groupName",""+contactname);
            prams.put("users",users);
            prams.put("groupType","one2one");

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
                        String number="",dateAdded="";
                        int counter=0;

                        for(int i = 0; i < results.length(); i++) {
                            JSONObject c = results.getJSONObject(i);
                            ground_Id  = c.getString("gId");
                            dateAdded  = c.getString("dateAdded");

                            if (c.getString("phone").equals(""+contactnumber)){

                                 mydb.setdata_contact_user_id(""+contactname,""+contactnumber,""+c.getString("UserID"));
                                 Log.d("TAG", "File...:::: UserID = " + c.getString("UserID"));
                            }

                        }
                        if (buttonclickcheck==1){
                            send_message_first_time();
                        }else if (buttonclickcheck==2){
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
                    }else{
                        Toast.makeText(getActivity(),jsonObject.getString("status_alert") , Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return result;
            }
        });
        webRequestCall.execute(url, "POST", params);
    }
    //===========================================================

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

    //===========================================================
            //IMAGE WORK START HERE

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

        if (buttonclickcheck==2){

            send_data_image_single(""+sb.toString(),""+time,"initial");


        }else {
            send_data_image_single(""+sb.toString(),""+time,"");
        }
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

    private void send_data_image_single(final String tempmsgid, final String senttime, final String message_type) {
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
                }
            }
            @Override
            protected Boolean doInBackground(Void... params) {
                try {

                    JSONObject jsonObject=jsonParser_send_chat_image.uploadImage(image_real_path,""+ground_Id,""+messageArea.getText().toString(),""+tempmsgid,""+message_type,getActivity());

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

                        if (buttonclickcheck==2 || buttonclickcheck==3){
                            mydb.insertdata_chat(""+ground_Id,""+sharedPreferences.getString("userID",""),
                                    ""+messageId,"✓✓","",
                                    ""+sentTime,""+message,""+user_check,"read");
                        }
                        /*
                          Message_List message_list = new Message_List(""+messageId,""+path.toString(),"",""+filename,""+messageArea.getText().toString(),"self","true");
                          message_ArrayList.set(i,message_list);
                          */
                        //message_list_adapter.notifyDataSetChanged();
                        //adapter state has change

                    }
                }
            }
            messageArea.setFocusable(true);
            messageArea.setText("");
        }
    }

          //IMAGE WORK ENDS HERE
   //============================================================

    public void send_message(){
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
        String params="";

        params ="userId="+sharedPreferences.getString("userID","")+
                "&gId="+ground_Id+
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

                    mydb.insertdata_chat(""+ground_Id,""+sharedPreferences.getString("userID",""),
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

                    mydb.insertdata(ground_Id,//ground_Id
                            sharedPreferences.getString("userID",""),//user_id
                            sb.toString(),
                            messageArea.getText().toString());

                }
                return result;
            }
        });
        webRequestCall.execute(url,"POST",params);
    }

    public void send_message_first_time(){
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

      /*  Message_List message_list = new Message_List(""+sb.toString(),"\uF017","",""+filename,
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
        }*/

        //http://192.168.100.14/FirmGround/REST/sendMessage?userId, gId, message
        final String url = getResources().getString(R.string.url) + "sendMessage";

        //String dummy_id = sb.toString();

        //System.out.println("TAG Dummy = "+dummy_id);
        String params="";

        params ="userId="+sharedPreferences.getString("userID","")+
                "&gId="+ground_Id+
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

                    //System.out.println("TAG adapter count = "+message_list_adapter.getCount());

                    mydb.insertdata_chat(""+ground_Id,""+sharedPreferences.getString("userID",""),
                            ""+messageId,"✓✓","",
                            ""+filename,""+temp_message,"self","read");

                   /* if (message_list_adapter!=null){

                        for (int i=0;i<message_list_adapter.getCount();i++){
                            if (message_list_adapter.getItem(i).getMessage_id().equals(""+tempMessageId)){

                                System.out.println("messageId = "+messageId+" item.getimagecheck = ");
                                //Toast.makeText(getActivity(),message_list_adapter.getItem(i).getMessage_id()+" = "+ sb.toString(), Toast.LENGTH_SHORT).show();

                                message_list_adapter.getItem(i).setMessage_id(""+messageId);
                                message_list_adapter.getItem(i).setMessage(""+temp_message);
                                Uri path = Uri.parse("" + R.drawable.ic_check_black_24dp);

                                message_list_adapter.getItem(i).setImage("✓");

                                                   *//* Message_List message_list = new Message_List(""+messageId,""+path.toString(),"",""+filename,""+messageArea.getText().toString(),"self","true");
                                                    message_ArrayList.set(i,message_list);*//*
                                //message_list_adapter.notifyDataSetChanged();//adapter state has change
                            }
                        }
                    }*/

                    messageArea.setFocusable(true);
                    messageArea.setText("");

                }else {
                    //implement here databaseHelper class
                    Toast.makeText(getActivity(),""+jsonObject.getString("message"),Toast.LENGTH_SHORT).show();

                    mydb.insertdata(ground_Id,//ground_Id
                            sharedPreferences.getString("userID",""),//user_id
                            sb.toString(),
                            messageArea.getText().toString());

                }
                return result;
            }
        });
        webRequestCall.execute(url,"POST",params);
    }

    private void SavePreferences(String key, String value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences((getActivity().getApplicationContext()));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
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
            Cursor c = mydb.getdata_chat(ground_Id);
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
                        if (mListView!=null){
                            mListView.setAdapter(message_list_adapter);
                            mListView.smoothScrollToPosition(message_list_adapter.getCount()-1);
                            mListView.setSelection(message_list_adapter.getCount());
                        }
                    } else {
                        if (message_list_adapter==null){
                            message_list_adapter = new Message_List_Adapter(getActivity(),message_ArrayList);
                            if (mListView!=null){
                                mListView.setAdapter(message_list_adapter);
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        message_ArrayList.clear();
        getActivity().stopService(service_chat);
        SavePreferences("messageId"    ,"0");

        toolbar_layout.setVisibility(View.VISIBLE);
        single_chat.setVisibility(View.GONE);
    }
    @Override
    public void onDetach() {
        super.onDetach();
        message_ArrayList.clear();
        getActivity().stopService(service_chat);
        SavePreferences("messageId"    ,"0");
        toolbar_layout.setVisibility(View.VISIBLE);
        single_chat.setVisibility(View.GONE);
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        message_ArrayList.clear();
        getActivity().stopService(service_chat);
        SavePreferences("messageId"    ,"0");
        toolbar_layout.setVisibility(View.VISIBLE);
        single_chat.setVisibility(View.GONE);
    }

    String message ="",user_check="",api_call_chat_service="",else_check="",messageId="",sentTime="",full_name="",results="",message_type="";
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
                         message_type         = c.getString("message_type");

                        String fname;
                        JSONObject userNameData = c.getJSONObject("userNameData");
                        fname = userNameData.getString("fname");

                        //System.out.println("TAG"+fname);
                        //Toast.makeText(getApplicationContext()," fname = "+fname,Toast.LENGTH_SHORT).show();

                        String pendingView      = c.getString("pendingView");
                        final String user_check = c.getString("user_check");


                        if (sharedPreferences.getString("messageId","").equals("0")){

                            if ((message_type.equals("initial") || image.contains(".3gp")) && (buttonclickcheck==3 || buttonclickcheck==2 || buttonclickcheck==1)){
                                //do noting
                                buttonclickcheck=0;
                            }else {
                                Message_List message_list = new Message_List(""+messageId,"✓✓",""+fname,""+sentTime,
                                        ""+message,""+user_check,""+image,"",false);
                                message_ArrayList.add(message_list);

                                int msg_id = Integer.parseInt(mydb.getdata_chat_check_data(ground_Id));

                                if (Integer.parseInt(messageId)<=msg_id){

                                }else{
                                    mydb.insertdata_chat(""+ground_Id,""+sharedPreferences.getString("userID",""),
                                            ""+messageId,"✓✓",""+fname,
                                            ""+sentTime,""+message,""+user_check,"read");
                                }
                            }
                        }else {
                            if (user_check.equals("other")){
                                if ((message_type.equals("initial") || image.contains(".3gp")) && (buttonclickcheck==3 || buttonclickcheck==2 || buttonclickcheck==1)){
                                    //do noting
                                    buttonclickcheck=0;
                                }else {

                                    Message_List message_list = new Message_List(""+messageId,"",""+fname,""+sentTime,
                                            ""+message,""+user_check,""+image,"",false);
                                    message_ArrayList.add(message_list);

                                    int msg_id = Integer.parseInt(mydb.getdata_chat_check_data(ground_Id));

                                    if (Integer.parseInt(messageId)<=msg_id){

                                    }else{
                                        mydb.insertdata_chat(""+ground_Id,""+sharedPreferences.getString("userID",""),
                                                ""+messageId,"✓✓",""+fname,
                                                ""+sentTime,""+message,""+user_check,"read");
                                    }
                                }
                            }
                        }
                    }

                    if (sharedPreferences.getString("messageId","").equals("0")){
                        message_list_adapter = new Message_List_Adapter(getActivity(),message_ArrayList);
                        if (mListView!=null){
                            mListView.setAdapter(message_list_adapter);
                            mListView.smoothScrollToPosition(message_list_adapter.getCount()-1);
                            mListView.setSelection(message_list_adapter.getCount());
                        }
                    } else {
                        message_list_adapter.notifyDataSetChanged();
                        if (mListView!=null){
                            mListView.smoothScrollToPosition(message_list_adapter.getCount()-1);
                            mListView.setSelection(message_list_adapter.getCount());
                        }
                    }

                    SavePreferences("messageId"    ,messageId);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mydb.setdata_chat_msg_read_status(""+ground_Id,""+sharedPreferences.getString("userID",""));
            }
        }
    };
}
