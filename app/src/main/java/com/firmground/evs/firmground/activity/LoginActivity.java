package com.firmground.evs.firmground.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.firmground.evs.firmground.R;
import com.firmground.evs.firmground.database.MyDatabase;
import com.firmground.evs.firmground.service.InternetConnection;
import com.firmground.evs.firmground.webservice.TaskDelegate;
import com.firmground.evs.firmground.webservice.WebRequestCall;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "fg@evs.pk:123456", "evs@gmail.com:123456"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private RequestQueue mRequestQueue;

    String user, pass;

    private static int PERMISSION_ALL = 1;

    SharedPreferences sharedPreferences;

    TextView textView;
    NetworkInfo wifiCheck;

    final static int REQUEST_LOCATION = 199;
    private GoogleApiClient googleApiClient;
    LocationManager manager;
    String token_id;
    MyDatabase mydb;
    ProgressDialog progress;

    public static String static_user_Id="";

    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(LoginActivity.this," onResume ",Toast.LENGTH_SHORT).show();
        //token_id = FirebaseInstanceId.getInstance().getToken();
        //Toast.makeText(LoginActivity.this," onResume token_id "+token_id,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progress = new ProgressDialog(LoginActivity.this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        //Toast.makeText(LoginActivity.this," onCreate ",Toast.LENGTH_SHORT).show();
        token_id = FirebaseInstanceId.getInstance().getToken();
        //Toast.makeText(LoginActivity.this," onCreate token_id "+token_id,Toast.LENGTH_SHORT).show();


      /*  FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MyActivity.this,
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String newToken = instanceIdResult.getToken();
                        Log.e("newToken", newToken);
                    }
                });
*/
        SavePreferences("token_id",token_id);

        System.out.println("token_id = "+token_id);

        //Toast.makeText(LoginActivity.this,"token_id = "+token_id,Toast.LENGTH_SHORT).show();

        mRequestQueue = Volley.newRequestQueue(this);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.ed_email);
        //populateAutoComplete();

        TextView tv_signup = findViewById(R.id.tv_signup);
        TextPaint paint = tv_signup.getPaint();
        float width = paint.measureText(getResources().getString(R.string.login));
        Shader textShader = new LinearGradient(0, 0, width, tv_signup.getTextSize(),
                new int[]{
                        Color.parseColor("#26AADA"),
                        Color.parseColor("#26AADA"),
                        Color.parseColor("#26AADA"),
                        Color.parseColor("#26ae90"),
                        Color.parseColor("#26ae90"),
                }, null, Shader.TileMode.CLAMP);
        tv_signup.getPaint().setShader(textShader);
        tv_signup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
                mEmailView.setText("");
                mPasswordView.setText("");
            }
        });

        TextView tv_label_forgot_password = findViewById(R.id.tv_label_forgot_password);
        tv_label_forgot_password.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ForgetPassWordActivity.class);
                startActivity(intent);
            }
        });
                        /*
                        Color.parseColor("#F97C3C"),
                        Color.parseColor("#FDB54E"),
                        Color.parseColor("#64B678"),
                        Color.parseColor("#478AEA"),
                        Color.parseColor("#8446CC"),
                        */

        TextView tv_app_logo = findViewById(R.id.tv_app_logo);
        TextPaint paint1 = tv_app_logo.getPaint();
        float width1 = paint1.measureText(getResources().getString(R.string.app_name));
        Shader textShader_1 = new LinearGradient(0, 0, width1, tv_app_logo.getTextSize(),
                new int[]{
                        Color.parseColor("#26AADA"),
                        Color.parseColor("#26AADA"),
                        Color.parseColor("#26AADA"),
                        Color.parseColor("#26ae90"),
                        Color.parseColor("#26ae90"),
                }, null, Shader.TileMode.CLAMP);
        tv_app_logo.getPaint().setShader(textShader_1);


        ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiCheck = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


        mPasswordView = (EditText) findViewById(R.id.ed_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {

                    if (InternetConnection.checkConnection(LoginActivity.this)) {
                        // Do whatever here
                        attemptLogin(textView);
                       // textView.setText("WiFi is Connected");
                    } else {
                        //textView.setText("WiFi is not Connected");
                        Toast.makeText(getApplicationContext(),"Check your wifi", Toast.LENGTH_SHORT).show();
                    }

                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

//        Shader textShader=new LinearGradient(0, 0, 0, 20,
//                new int[]{Color.GREEN,Color.BLUE},
//                new float[]{0, 1}, Shader.TileMode.CLAMP);
//        mEmailSignInButton.getPaint().setShader(textShader);

        //mEmailSignInButton.setTextColor(textShader);

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (InternetConnection.checkConnection(LoginActivity.this)) {
                    // Do whatever here
                    attemptLogin(view);
                    // textView.setText("WiFi is Connected");
                } else {
                    //textView.setText("WiFi is not Connected");
                    Toast.makeText(getApplicationContext(),"Connectivity failure !!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }*/


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */


    public void requestPhone(final View view) {
        Permissions.check(LoginActivity.this, Manifest.permission.READ_CONTACTS, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                //Toast.makeText(getActivity(), "Phone granted.", Toast.LENGTH_SHORT).show();


                if (LoginActivity.this!=null)
                    progress.setMessage("Loading");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setIndeterminate(true);
                progress.setProgress(0);
                progress.setCancelable(true);
                progress.show();

                String url = getResources().getString(R.string.url) + "auth";
                String params = "email="+mEmailView.getText().toString()+"&password="+mPasswordView.getText().toString()+"&token_id="+token_id;
                WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                    @Override
                    public String TaskCompletionResult(String result) throws JSONException {

                        //System.out.println("ReSult = "+result);
                        //
                        JSONObject jsonObject = new JSONObject(result);

                        if(jsonObject.getString("respCode").equals("200")){

                         /*   Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();*/

                            String token = jsonObject.getString("Token");

                            SavePreferences("Expiry"    ,jsonObject.getString("Expiry"));

                            JSONObject jsonObject_userData=jsonObject.getJSONObject("UserData");
                            static_user_Id = jsonObject_userData.getString("id");
                            SavePreferences("userID"    ,jsonObject_userData.getString("id"));
                            SavePreferences("fname"     ,jsonObject_userData.getString("fname"     ));
                            SavePreferences("lname"     ,jsonObject_userData.getString("lname"     ));
                            SavePreferences("full_name" ,jsonObject_userData.getString("full_name" ));
                            SavePreferences("username"  ,jsonObject_userData.getString("username"  ));
                            SavePreferences("phone"     ,jsonObject_userData.getString("phone"     ));
                            SavePreferences("email"     ,jsonObject_userData.getString("email"     ));
                            SavePreferences("password"  ,jsonObject_userData.getString("password"  ));
                            SavePreferences("coverImage",jsonObject_userData.getString("coverImage"));
                            SavePreferences("team_user_supports"     ,jsonObject_userData.getString("team_user_supports"     ));
                            SavePreferences("preferred_position_play",jsonObject_userData.getString("preferred_position_play"));
                            SavePreferences("birth_date"             ,jsonObject_userData.getString("birthDay"             ));
                            SavePreferences("address_type"           ,jsonObject_userData.getString("address_type"           ));
                            SavePreferences("building_name",jsonObject_userData.getString("building_name"       ));
                            SavePreferences("unit_number"  ,jsonObject_userData.getString("unit_number"       ));
                            SavePreferences("address"    ,jsonObject_userData.getString("address"        ));
                            SavePreferences("city"       ,jsonObject_userData.getString("city"           ));
                            SavePreferences("zipcode"    ,jsonObject_userData.getString("zipcode"        ));
                            SavePreferences("state"      ,jsonObject_userData.getString("state"          ));
                            SavePreferences("country"    ,jsonObject_userData.getString("country"        ));
                            SavePreferences("status"     ,jsonObject_userData.getString("status"         ));
                            SavePreferences("change_pass_uniq",jsonObject_userData.getString("change_pass_uniq" ));
                            SavePreferences("activation_code" ,jsonObject_userData.getString("activation_code"  ));
                            SavePreferences("profileImage"    ,getResources().getString(R.string.imageBaseUrl)+jsonObject_userData.getString("image"));
                            SavePreferences("coverImage"      ,getResources().getString(R.string.imageBaseUrl)+jsonObject_userData.getString("coverImage"));
                            SavePreferences("emerg_contact"   ,jsonObject_userData.getString("emerg_contact"     ));
                            SavePreferences("emerg_contact_num",jsonObject_userData.getString("emerg_contact_num"));
                            SavePreferences("fb_id"            ,jsonObject_userData.getString("fb_id"            ));
                            SavePreferences("add_date"         ,jsonObject_userData.getString("add_date"         ));
                            SavePreferences("lastmodified"     ,jsonObject_userData.getString("lastmodified"     ));

                            SavePreferences("yes_login"     ,"yes");

                            mydb = new MyDatabase(LoginActivity.this);

                            //mydb.delete_record_chat();
                            mydb.delete_record_match_detail();
                            mydb.delete_record_squad_detail();
                            //mydb.delete_contact();

                            getAll_Meges(mydb.getdata_last_msg_id_user_wise(jsonObject_userData.getString("id")));
                            //progress.dismiss();

                        }else{
                            Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                            progress.dismiss();

                        }
                        return result;
                    }
                });
                webRequestCall.execute(url,"POST",params);

            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);

                requestPhone(view);
            }
        });
    }


    public void requestStorage(final View view) {
        Permissions.check(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                //Toast.makeText(getActivity(), "Phone granted.", Toast.LENGTH_SHORT).show();
                requestPhone(view);
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);

                requestStorage(view);
            }
        });
    }


    private boolean attemptLogin(View view) {
        if (mAuthTask != null) {
            return false;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } /*else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }*/

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.v("TAG","Permission is granted");


                    if (LoginActivity.this!=null)
                        progress.setMessage("Loading");
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.setIndeterminate(true);
                        progress.setProgress(0);
                        progress.setCancelable(true);
                        progress.show();


                    String url = getResources().getString(R.string.url) + "auth";
                    String params = "email="+mEmailView.getText().toString()+"&password="+mPasswordView.getText().toString()+"&token_id="+token_id;
                    WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                        @Override
                        public String TaskCompletionResult(String result) throws JSONException {

                            //System.out.println("ReSult = "+result);
                            //
                            JSONObject jsonObject = new JSONObject(result);

                            if(jsonObject.getString("respCode").equals("200")){

                                String token = jsonObject.getString("Token");

                                /*
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                                */

                                SavePreferences("Expiry"    ,jsonObject.getString("Expiry"));

                                JSONObject jsonObject_userData=jsonObject.getJSONObject("UserData");
                                static_user_Id = jsonObject_userData.getString("id");
                                SavePreferences("userID"    ,jsonObject_userData.getString("id"));
                                SavePreferences("fname"     ,jsonObject_userData.getString("fname"     ));
                                SavePreferences("lname"     ,jsonObject_userData.getString("lname"     ));
                                SavePreferences("full_name" ,jsonObject_userData.getString("full_name" ));
                                SavePreferences("username"  ,jsonObject_userData.getString("username"  ));
                                SavePreferences("phone"     ,jsonObject_userData.getString("phone"     ));
                                SavePreferences("email"     ,jsonObject_userData.getString("email"     ));
                                SavePreferences("password"  ,jsonObject_userData.getString("password"  ));
                                SavePreferences("coverImage",jsonObject_userData.getString("coverImage"));
                                SavePreferences("team_user_supports"     ,jsonObject_userData.getString("team_user_supports"     ));
                                SavePreferences("preferred_position_play",jsonObject_userData.getString("preferred_position_play"));
                                SavePreferences("birth_date"             ,jsonObject_userData.getString("birthDay"             ));
                                SavePreferences("address_type"           ,jsonObject_userData.getString("address_type"           ));
                                SavePreferences("building_name",jsonObject_userData.getString("building_name"       ));
                                SavePreferences("unit_number"  ,jsonObject_userData.getString("unit_number"       ));
                                SavePreferences("address"    ,jsonObject_userData.getString("address"        ));
                                SavePreferences("city"       ,jsonObject_userData.getString("city"           ));
                                SavePreferences("zipcode"    ,jsonObject_userData.getString("zipcode"        ));
                                SavePreferences("state"      ,jsonObject_userData.getString("state"          ));
                                SavePreferences("country"    ,jsonObject_userData.getString("country"        ));
                                SavePreferences("status"     ,jsonObject_userData.getString("status"         ));
                                SavePreferences("change_pass_uniq",jsonObject_userData.getString("change_pass_uniq" ));
                                SavePreferences("activation_code" ,jsonObject_userData.getString("activation_code"  ));
                                SavePreferences("profileImage"    ,getResources().getString(R.string.imageBaseUrl)+jsonObject_userData.getString("image"));
                                SavePreferences("coverImage"      ,getResources().getString(R.string.imageBaseUrl)+jsonObject_userData.getString("coverImage"));
                                SavePreferences("emerg_contact"   ,jsonObject_userData.getString("emerg_contact"     ));
                                SavePreferences("emerg_contact_num",jsonObject_userData.getString("emerg_contact_num"));
                                SavePreferences("fb_id"            ,jsonObject_userData.getString("fb_id"            ));
                                SavePreferences("add_date"         ,jsonObject_userData.getString("add_date"         ));
                                SavePreferences("lastmodified"     ,jsonObject_userData.getString("lastmodified"     ));

                                SavePreferences("yes_login"     ,"yes");

                                mydb = new MyDatabase(LoginActivity.this);

                                //mydb.delete_record_chat();
                                mydb.delete_record_match_detail();
                                mydb.delete_record_squad_detail();
                                //mydb.delete_contact();
                                getAll_Meges(mydb.getdata_last_msg_id_user_wise(jsonObject_userData.getString("id")));
                                //progress.dismiss();

                            }else{
                                Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                                progress.dismiss();
                            }
                            return result;
                        }
                    });
                    webRequestCall.execute(url,"POST",params);

                } else {

                    Log.v("TAG","Permission is revoked");
                    requestStorage(view);
                }
            }
            else { //permission is automatically granted on sdk<23 upon installation
                Log.v("TAG","Permission is granted");

                if (LoginActivity.this!=null)
                    progress.setMessage("Loading");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.setIndeterminate(true);
                    progress.setProgress(0);
                    progress.setCancelable(true);
                    progress.show();


                String url = getResources().getString(R.string.url) + "auth";
                String params = "email="+mEmailView.getText().toString()+"&password="+mPasswordView.getText().toString()+"&token_id="+token_id;
                WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                    @Override
                    public String TaskCompletionResult(String result) throws JSONException {

                        //System.out.println("ReSult = "+result);
                        //
                        JSONObject jsonObject = new JSONObject(result);

                        if(jsonObject.getString("respCode").equals("200")){

                            String token = jsonObject.getString("Token");

                            SavePreferences("Expiry"    ,jsonObject.getString("Expiry"));

                            JSONObject jsonObject_userData=jsonObject.getJSONObject("UserData");
                            static_user_Id = jsonObject_userData.getString("id");
                            SavePreferences("userID"    ,jsonObject_userData.getString("id"));
                            SavePreferences("fname"     ,jsonObject_userData.getString("fname"     ));
                            SavePreferences("lname"     ,jsonObject_userData.getString("lname"     ));
                            SavePreferences("full_name" ,jsonObject_userData.getString("full_name" ));
                            SavePreferences("username"  ,jsonObject_userData.getString("username"  ));
                            SavePreferences("phone"     ,jsonObject_userData.getString("phone"     ));
                            SavePreferences("email"     ,jsonObject_userData.getString("email"     ));
                            SavePreferences("password"  ,jsonObject_userData.getString("password"  ));
                            SavePreferences("coverImage",jsonObject_userData.getString("coverImage"));
                            SavePreferences("team_user_supports"     ,jsonObject_userData.getString("team_user_supports"     ));
                            SavePreferences("preferred_position_play",jsonObject_userData.getString("preferred_position_play"));
                            SavePreferences("birth_date"             ,jsonObject_userData.getString("birthDay"             ));
                            SavePreferences("address_type"           ,jsonObject_userData.getString("address_type"           ));
                            SavePreferences("building_name",jsonObject_userData.getString("birthDay"       ));
                            SavePreferences("unit_number"  ,jsonObject_userData.getString("unit_number"    ));
                            SavePreferences("address"      ,jsonObject_userData.getString("address"        ));
                            SavePreferences("city"         ,jsonObject_userData.getString("city"           ));
                            SavePreferences("zipcode"      ,jsonObject_userData.getString("zipcode"        ));
                            SavePreferences("state"        ,jsonObject_userData.getString("state"          ));
                            SavePreferences("country"      ,jsonObject_userData.getString("country"        ));
                            SavePreferences("status"       ,jsonObject_userData.getString("status"         ));
                            SavePreferences("change_pass_uniq",jsonObject_userData.getString("change_pass_uniq" ));
                            SavePreferences("activation_code" ,jsonObject_userData.getString("activation_code"  ));
                            SavePreferences("profileImage"    ,getResources().getString(R.string.imageBaseUrl)+jsonObject_userData.getString("image"));
                            SavePreferences("coverImage"      ,getResources().getString(R.string.imageBaseUrl)+jsonObject_userData.getString("coverImage"));
                            SavePreferences("emerg_contact"   ,jsonObject_userData.getString("emerg_contact"     ));
                            SavePreferences("emerg_contact_num",jsonObject_userData.getString("emerg_contact_num"));
                            SavePreferences("fb_id"            ,jsonObject_userData.getString("fb_id"            ));
                            SavePreferences("add_date"         ,jsonObject_userData.getString("add_date"         ));
                            SavePreferences("lastmodified"     ,jsonObject_userData.getString("lastmodified"     ));

                            SavePreferences("yes_login"     ,"yes");

                            mydb = new MyDatabase(LoginActivity.this);

                            //mydb.delete_record_chat();
                            mydb.delete_record_match_detail();
                            mydb.delete_record_squad_detail();
                            //mydb.delete_contact();

                            getAll_Meges(mydb.getdata_last_msg_id_user_wise(jsonObject_userData.getString("id")));

                        }else{
                            Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }
                        return result;
                    }
                });
                webRequestCall.execute(url,"POST",params);
            }

            //Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            //startActivity(intent);
            //finish();
            //mAuthTask = new UserLoginTask(email, password);
            //mAuthTask.execute((Void) null);
        }
        return cancel;
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
                                ""+sentTime,""+message,""+user_check,"read");

                    }

                    //Toast.makeText(LoginActivity.this,"End Checking",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                }else{
                    Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                }
                return result;
            }
        });
        webRequestCall.execute(url,"POST",params);

    }

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                        }
                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error","Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(LoginActivity.this, REQUEST_LOCATION);

                                //finish();
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }

    private void SavePreferences(String key, String value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    /*private void fetchJsonResponse() {
        String url = getResources().getString(R.string.url) + "auth";
        String params = "username="+mEmailView.getText().toString()+"&password="+mPasswordView.getText().toString()+"&token_id="+token_id;
        // Pass second argument as "null" for GET requests
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String result = "Your respCode is " + response.getString("respCode");
                            Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        // Add your Requests to the RequestQueue to execute
        mRequestQueue.add(req);
    }*/
}

