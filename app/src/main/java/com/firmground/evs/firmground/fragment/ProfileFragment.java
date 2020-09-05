package com.firmground.evs.firmground.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.firmground.evs.firmground.R;
import com.firmground.evs.firmground.circle_image.CircleImageView;
import com.firmground.evs.firmground.imagewebservice.JSONParser_Upload_Profile;
import com.firmground.evs.firmground.webservice.TaskDelegate;
import com.firmground.evs.firmground.webservice.WebRequestCall;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.firmground.evs.firmground.activity.MainActivity.activity;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    SharedPreferences sharedPreferences;

    private FragmentActivity fragmentActivity;

    Calendar myCalendar;

    TextView ed_profile_date;
    CircleImageView img_profile;
    ImageView profile_bg;

    int pic_check=0;

    String image_real_path;

    JSONParser_Upload_Profile jsonParser_upload_profile;

    ProgressBar simpleProgressBar;

    View view1;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview =  inflater.inflate(R.layout.fragment_profile, container, false);

        simpleProgressBar = (ProgressBar) rootview.findViewById(R.id.simpleProgressBar);

        view1 = rootview.findViewById(R.id.view);

        ImageView search_img = getActivity().findViewById(R.id.search_img);
        search_img.setVisibility(View.GONE);

        final EditText ed_search = getActivity().findViewById(R.id.ed_search);
        ed_search.setText("");
        ed_search.setVisibility(View.GONE);

        final ImageView cross_img = getActivity().findViewById(R.id.cross_img);
        cross_img.setVisibility(View.GONE);

        jsonParser_upload_profile = new JSONParser_Upload_Profile();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        myCalendar = Calendar.getInstance();

        final TextView tv_name = rootview.findViewById(R.id.tv_name);
        tv_name.setText(""+sharedPreferences.getString("fname",""));

        final TextView tv_address = rootview.findViewById(R.id.tv_address);
        tv_address.setText(""+sharedPreferences.getString("address",""));

        //================================================================================

        final EditText ed_password   = rootview.findViewById(R.id.ed_password);

        final EditText ed_profile_name   = rootview.findViewById(R.id.ed_profile_name);
        ed_profile_name.setText(""+sharedPreferences.getString("fname",""));

        final EditText ed_profile_last_name   = rootview.findViewById(R.id.ed_profile_last_name);
        ed_profile_last_name.setText(""+sharedPreferences.getString("lname",""));

        final EditText ed_profile_email  = rootview.findViewById(R.id.ed_profile_email);
        ed_profile_email.setText(""+sharedPreferences.getString("email",""));

        ed_profile_date   = rootview.findViewById(R.id.ed_profile_date);
        ed_profile_date.setText(""+sharedPreferences.getString("birth_date",""));

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateLabel();
            }

        };

        ed_profile_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                SimpleDateFormat formatter =
                        new SimpleDateFormat("dd MMM yyy");

                Date date_ = null;
                try {
                    date_ = formatter.parse(ed_profile_date.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                myCalendar.setTime(date_);

                DatePickerDialog datePickerDialog=new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                //following line to restrict future date selection
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        final EditText ed_user_support   = rootview.findViewById(R.id.ed_user_support);
        ed_user_support.setText(""+sharedPreferences.getString("team_user_supports",""));

        final EditText ed_pref_play_position   = rootview.findViewById(R.id.ed_pref_play_position);
        ed_pref_play_position.setText(""+sharedPreferences.getString("preferred_position_play",""));

        final EditText ed_profile_phone  = rootview.findViewById(R.id.ed_profile_phone);
        ed_profile_phone.setText(""+sharedPreferences.getString("phone",""));

        final EditText ed_profile_address= rootview.findViewById(R.id.ed_profile_address);
        ed_profile_address.setText(""+sharedPreferences.getString("address",""));

        final EditText ed_emergency_name = rootview.findViewById(R.id.ed_emergency_name);
        ed_emergency_name.setText(""+sharedPreferences.getString("emerg_contact",""));

        final EditText ed_emergency_number = rootview.findViewById(R.id.ed_emergency_number);
        ed_emergency_number.setText(""+sharedPreferences.getString("emerg_contact_num",""));

        Button btn_update_profile= rootview.findViewById(R.id.btn_update_profile);
        btn_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (      ed_profile_name.getText().toString().equals("")){
                          ed_profile_name.setError("Empty");

                }else if (ed_profile_last_name.getText().toString().equals("")){
                          ed_profile_last_name.setError("Empty");

                }else if (ed_profile_email.getText().toString().equals("")){
                          ed_profile_email.setError("Empty");

                }else if (ed_profile_date.getText().toString().equals("")){
                          ed_profile_date.setError("Empty");

                }else if (ed_user_support.getText().toString().equals("")){
                          ed_user_support.setError("Empty");

                }else if (ed_pref_play_position.getText().toString().equals("")){
                          ed_pref_play_position.setError("Empty");

                }else if (ed_profile_phone.getText().toString().equals("")){
                          ed_profile_phone.setError("Empty");

                }else if (ed_emergency_name.getText().toString().equals("")){
                          ed_emergency_name.setError("Empty");

                }else if (ed_emergency_number.getText().toString().equals("")){
                          ed_emergency_number.setError("Empty");

                }else if(!ed_password.getText().toString().equals("") && ed_password.getText().length()<6){
                    ed_password.setError("Password length short");
                }
                else {

                    JSONObject param = new JSONObject();
                    try {
                        param.put("fname",  ""+ed_profile_name.getText().toString());
                        param.put("lname",  ""+ed_profile_last_name.getText().toString());
                        param.put("contact",""+ed_profile_phone.getText().toString());
                        param.put("email",  ""+ed_profile_email.getText().toString());
                        param.put("birth_date", ""+ed_profile_date.getText().toString());
                        param.put("team_user_supports", ""+ed_user_support.getText().toString());
                        param.put("preferred_position_play", ""+ed_pref_play_position.getText().toString());
                        param.put("address", ""+ed_profile_address.getText().toString());
                       /* param.put("user_star", ""+ed_profile_star.getText().toString());*/
                        param.put("emerg_contact", ""+ed_emergency_name.getText().toString());
                        param.put("emerg_contact_num", ""+ed_emergency_number.getText().toString());

                        if (!ed_password.getText().toString().equals("")){
                            param.put("password", ""+ed_password.getText().toString());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    String tmp    = Base64.encodeToString(param.toString().getBytes(),Base64.NO_WRAP);

// http://192.168.100.14/FirmGround/REST/updateProfile?
// inputData={"fname":"asdasd","lname":"asdasd","contact":"273273273","password":"123456",
// "email":"umair.evs@gmaisl.com","team_user_supports":"Pakistani","play_position":"asdasda",
// "emerg_contact":"asdasdasd","emerg_contact_num":"123123"}&userID=2
                    String url    =  getResources().getString(R.string.url)+"updateProfile";

                    String params = "inputData="+tmp+"&userID="+sharedPreferences.getString("userID","");

                    WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                        @Override
                        public String TaskCompletionResult(String result) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                if(jsonObject.getString("respCode").equals("200")) {

                                    SavePreferences("fname"      ,ed_profile_name.getText().toString());
                                    SavePreferences("lname"      ,ed_profile_last_name.getText().toString());
                                    SavePreferences("email"      ,ed_profile_email.getText().toString());
                                    SavePreferences("birth_date"             ,ed_profile_date.getText().toString());
                                    SavePreferences("team_user_supports"     ,ed_user_support.getText().toString());
                                    SavePreferences("preferred_position_play"     ,ed_pref_play_position.getText().toString());
                                    SavePreferences("phone"       ,ed_profile_phone.getText().toString());
                                    SavePreferences("address"     ,ed_profile_address.getText().toString());
                                    SavePreferences("emerg_contact"     ,ed_emergency_name.getText().toString());
                                    SavePreferences("emerg_contact_num"     ,ed_emergency_number.getText().toString());

                                    Toast.makeText(getActivity(),""+jsonObject.getString("message"),Toast.LENGTH_SHORT).show();

                                    tv_name.setText(""+sharedPreferences.getString("fname",""));
                                    tv_address.setText(""+sharedPreferences.getString("address",""));

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
                }
            }
        });


        fragmentActivity = (FragmentActivity) activity;

        ImageView add_match_img = getActivity().findViewById(R.id.add_match_img);
        add_match_img.setVisibility(View.GONE);
        ImageView add_group_img = getActivity().findViewById(R.id.add_group_img);
        add_group_img.setVisibility(View.GONE);
        add_group_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPhone(v);
            }
        });

        profile_bg = (ImageView) rootview.findViewById(R.id.profile_bg);
        if (sharedPreferences.contains("coverImage")){
            Log.i("coverImage", "coverImage = " +sharedPreferences.getString("coverImage",""));
            Picasso.with(profile_bg.getContext()).load(""+sharedPreferences.getString("coverImage", "")).into(profile_bg);
        }
        profile_bg.setElevation(0);
        profile_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pic_check=2;
                //Toast.makeText(getActivity(),"pic_check = "+pic_check,Toast.LENGTH_SHORT).show();

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

        img_profile = (CircleImageView) rootview.findViewById(R.id.img_profile);
        Log.i("profileImage", "profileImage = " +sharedPreferences.getString("profileImage",""));
        if (sharedPreferences.contains("profileImage") &&
                !sharedPreferences.getString("profileImage","").equals("")){
            Picasso.with(img_profile.getContext()).load(""+sharedPreferences.getString("profileImage", "")).into(img_profile);
        }
        img_profile.setElevation(100);
        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pic_check = 1;
               // Toast.makeText(getActivity(),"pic_check = "+pic_check,Toast.LENGTH_SHORT).show();
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

        return  rootview;
    }

    private void getContacts() {
        //TODO get contacts code here

        simpleProgressBar.setVisibility(View.VISIBLE);

        view1.setVisibility(View.VISIBLE);

        int SPLASH_TIME_OUT = 1000;
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
        }, SPLASH_TIME_OUT);

    }

    public void requestPhone(final View view) {
        Permissions.check(getActivity(), Manifest.permission.READ_CONTACTS, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                getContacts();
            }
            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                requestPhone(view);
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
    public  void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == Activity.RESULT_OK && data != null) {
            String realPath;
            // SDK < API11
            if (Build.VERSION.SDK_INT < 11)
                realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(getActivity(), data.getData());

                // SDK >= 11 && SDK < 19
            else if (Build.VERSION.SDK_INT < 19)
                realPath = RealPathUtil.getRealPathFromURI_API11to18(getActivity(), data.getData());

                // SDK > 19 (Android 4.4)
            else
                realPath = RealPathUtil.getRealPathFromURI_API19(getActivity(), data.getData());


            if(pic_check==1){
                setprofilepic(Build.VERSION.SDK_INT, data.getData().getPath(), realPath);
            }else {
                setbgimage(Build.VERSION.SDK_INT, data.getData().getPath(), realPath);
            }
        }
    }
    private void setprofilepic(int sdk, String uriPath, String realPath) {

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
        compressImage(image_real_path,"profileImage");
    }
    private void setbgimage(int sdk, String uriPath, String realPath) {

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

        compressImage(image_real_path,"profileCover");
    }

    public String compressImage(String imageUri,String function_call_check) {

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
            if (function_call_check.equals("profileImage"))
                img_profile.setImageBitmap(scaledBitmap);
            else
                profile_bg.setImageBitmap(scaledBitmap);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Toast.makeText(MainActivity.this, "filename = "+filename, Toast.LENGTH_LONG).show();
        image_real_path=filename;

        if (function_call_check.equals("profileImage"))
            send_data_add(""+sharedPreferences.getString("userID",""),"profileImage");
        else
            send_data_add(sharedPreferences.getString("userID",""),"profileCover");

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

    private void send_data_add(final String user_id, final String imageCheck) {
        //
        new AsyncTask<Void, Integer, Boolean>() {
            ProgressDialog progressDialog;

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
            //String result = jsonParser.uploadImage(imgFile.getAbsolutePath(),getActivity()).toString();
            //Toast.makeText(getActivity(),result,Toast.LENGTH_SHORT).show();
            //System.out.println("fdfdf"+imgFile.getAbsolutePath().toString());
            //if(imgFile != null)
                    JSONObject jsonObject;
                    jsonObject=jsonParser_upload_profile.uploadImage(image_real_path,""+user_id,""+imageCheck,getActivity());

                    //Toast.makeText(getActivity(),"call = "+jsonObject.toString(),Toast.LENGTH_SHORT).show();
                    //else
                    //jsonObject =jsonParser_editProfile.uploadImage("",username,password,age,height,phone,weight,country,activity);

                    if (jsonObject != null) {
                        if(jsonObject.getString("respCode").equals("200")){
                            //Toast.makeText(getActivity(),"200 = "+jsonObject.toString(),Toast.LENGTH_SHORT).show();
                           if (pic_check==1){
                               SavePreferences("profileImage" ,getResources().getString(R.string.imageBaseUrl)+jsonObject.getString("profileImage" ));
                               Log.i("TAG", "pic_check 1: " +pic_check);
                           }else {
                               SavePreferences("coverImage" ,getResources().getString(R.string.imageBaseUrl)+jsonObject.getString("coverImage" ));
                               Log.i("TAG", "pic_check 2: " +pic_check);
                           }
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
    private void SavePreferences(String key, String value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    private void updateLabel() {
        String myFormat = "dd MMM yyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        ed_profile_date.setText(sdf.format(myCalendar.getTime()));
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
class RealPathUtil {

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