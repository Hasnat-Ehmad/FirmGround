package com.firmground.evs.firmground.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
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
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.firmground.evs.firmground.R;
import com.firmground.evs.firmground.imagewebservice.JSONParser_Update_Match;
import com.firmground.evs.firmground.imagewebservice.JSONParser_Upload_Match;
import com.firmground.evs.firmground.splashscreen.SplashScreen;
import com.firmground.evs.firmground.webservice.TaskDelegate;
import com.firmground.evs.firmground.webservice.WebRequestCall;
import com.squareup.picasso.Picasso;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.firmground.evs.firmground.activity.MainActivity.activity;

public class UpdateMatchActivity extends AppCompatActivity {

    String game_Id,group_Id,gametype,stadiumName,LocationName,image,DateAdded,MatchTime,DayNight,PitchNo,aSide,Turf,Boots,weather,currency,substitutes;
    SharedPreferences sharedPreferences;
    EditText ed_truf,ed_condition,ed_cost,ed_note;
    TextView tv_location,tv_date,tv_condition,tv_day_night,tv_label_pitch_number,tv_truf,tv_boots,tv_facilities,tv_restriction;
    Spinner spinner_kickoff,spinner_match,spinner_pitch_number,spinner_location_name,spinner_game_type,spinner_a_side,spinner_recurring_game,spinner_team_1,spinner_team_2,spinner_referee;
    ImageView imageView,img_edited_stadium;
    JSONParser_Update_Match jsonParser_update_match;
    JSONParser_Upload_Match jsonParser_upload_match;
    Calendar myCalendar;

    JSONArray results,
              addotionalFacilities,
              boots,
              restrictions;

    String[] location_Name, location_ID,location_lat,location_lng,additonal_fact,address_lane1,address_lane2,mrestrictions;


    String location_id,lat,lng,pitch_id="",match_type="Normal",referee="No", group_name;

    RelativeLayout relativespinnerlayout1,relativespinnerlayout2;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences       = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        jsonParser_update_match = new JSONParser_Update_Match();
        jsonParser_upload_match = new JSONParser_Upload_Match();

        final Bundle bundle = getIntent().getExtras();

        setContentView(R.layout.activity_update_match);

        relativespinnerlayout1 = findViewById(R.id.relativespinnerlayout1);
        relativespinnerlayout2 = findViewById(R.id.relativespinnerlayout2);

        if(bundle!=null){
            if (bundle.containsKey("game_Id")) {

                ImageView message_img= findViewById(R.id.message_img);
                message_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

                TextView tv_toolbar_title= findViewById(R.id.tv_toolbar_title);
                tv_toolbar_title.setText("Update Match");

                group_Id    = bundle.getString("group_Id"  );
                game_Id     = bundle.getString("game_Id"   );

                tv_location           = findViewById(R.id.tv_location);
                tv_date               = findViewById(R.id.tv_date);
                tv_condition          = findViewById(R.id.tv_condition);
                tv_day_night          = findViewById(R.id.tv_day_night);
                tv_truf               = findViewById(R.id.tv_truf);
                tv_boots              = findViewById(R.id.tv_boots);
                tv_facilities         = findViewById(R.id.tv_facilities);
                tv_restriction        = findViewById(R.id.tv_restriction);
                tv_label_pitch_number = findViewById(R.id.tv_label_pitch_number);
                spinner_location_name = findViewById(R.id.spinner_location_name);

               /* try {
                    Field popup = Spinner.class.getDeclaredField("mPopup");
                    popup.setAccessible(true);

                    // Get private mPopup member variable and try cast to ListPopupWindow
                    android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinner_location_name);

                    // Set popupWindow height to 500px
                    popupWindow.setHeight(200);
                }
                catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
                    // silently fail...
                }*/

                spinner_kickoff  = findViewById(R.id.spinner_kickoff);
                spinner_a_side   = findViewById(R.id.spinner_a_side);
                spinner_recurring_game= findViewById(R.id.spinner_recurring_game);
                spinner_team_1 = findViewById(R.id.spinner_team_1);
                spinner_team_2 = findViewById(R.id.spinner_team_2);
                spinner_referee= findViewById(R.id.spinner_referee);

                String[] recurring_game = getResources().getStringArray(R.array.recurring_game);

                ArrayAdapter<String> adapter_recurring_game = new ArrayAdapter<String>(activity,
                        R.layout.my_spinner_style, recurring_game) {

                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);
                        return v;
                    }

                    public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                        View v =super.getDropDownView(position, convertView, parent);
                        //((TextView) v).setBackgroundResource(R.drawable.grey_border);
                        return v;
                    }
                };

                spinner_recurring_game.setAdapter(adapter_recurring_game);

                String recursiveType="";
                if (bundle.getString("recursive").equals("Weekly")){
                    recursiveType="Yes";
                }else if (bundle.getString("recursive").equals("Monthly")){
                    recursiveType="Yes";
                }else {
                    recursiveType="No";
                }


                spinner_match = findViewById(R.id.spinner_match);

                String[] match_type_array = getResources().getStringArray(R.array.match_type);

                ArrayAdapter<String> adapter_match_type = new ArrayAdapter<String>(activity,
                        R.layout.my_spinner_style, match_type_array) {

                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);

                        return v;
                    }

                    public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                        View v =super.getDropDownView(position, convertView, parent);
                        //((TextView) v).setBackgroundResource(R.drawable.grey_border);
                        return v;
                    }
                };

                spinner_match.setAdapter(adapter_match_type);

                spinner_recurring_game.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        if (spinner_recurring_game.getSelectedItem().toString().equals("Yes")){
                            match_type = "Recurring";
                            spinner_match.setVisibility(View.VISIBLE);
                            if (bundle.getString("recursive").equals("Weekly")){
                                spinner_match.setSelection(0);
                            }else {
                                spinner_match.setSelection(1);
                            }
                        }else {
                            match_type = "Normal";
                            spinner_match.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                spinner_recurring_game.setSelection(adapter_recurring_game.getPosition(recursiveType));

       /* ArrayAdapter<CharSequence> adapter_team_1= ArrayAdapter.createFromResource(
                UpdateMatchActivity.this, R.array.team_1,
                    android.R.layout.simple_spinner_item);
        adapter_team_1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

                String[] foo_array = UpdateMatchActivity.this.getResources().getStringArray(R.array.team_1);

                ArrayAdapter<String> adapter_team_1 = new ArrayAdapter<String>(UpdateMatchActivity.this,
                        R.layout.my_spinner_style, foo_array) {

                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);

                        return v;
                    }

                    public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                        View v =super.getDropDownView(position, convertView, parent);
                        //((TextView) v).setBackgroundResource(R.drawable.grey_border);
                        //Toast.makeText(UpdateMatchActivity.this,((TextView) v).getText().toString(),Toast.LENGTH_SHORT).show();
                        if (((TextView) v).getText().equals("Non-Bibs")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            ((TextView) v).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        if (((TextView) v).getText().equals("-")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            ((TextView) v).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if (((TextView) v).getText().equals("Skins")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.color_skin));

                        }else if (((TextView) v).getText().equals("Red")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.color_red));
                            ((TextView) v).setTextColor(getResources().getColor(R.color.colorWhite));

                        }else if (((TextView) v).getText().equals("Orange")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.colorOrange));

                        }else if (((TextView) v).getText().equals("Yellow")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.color_yellow));

                        }
                        else if (((TextView) v).getText().equals("Green")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.color_green));

                        }
                        else if (((TextView) v).getText().equals("Blue")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.squadin));

                        }
                        else if (((TextView) v).getText().equals("Purple")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.color_purple));
                            ((TextView) v).setTextColor(getResources().getColor(R.color.colorWhite));
                        }
                        else if (((TextView) v).getText().equals("Pink")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.color_pink));

                        }
                        else if (((TextView) v).getText().equals("Brown")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.color_brown));

                        }
                        else if (((TextView) v).getText().equals("Black")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.color_black));
                            ((TextView) v).setTextColor(getResources().getColor(R.color.colorWhite));

                        }
                        else if (((TextView) v).getText().equals("White")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            ((TextView) v).setTextColor(getResources().getColor(R.color.color_black));

                        }else if (((TextView) v).getText().equals("Grey")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.grey_light));
                        }

                        return v;
                    }
                };


                spinner_team_1.setAdapter(adapter_team_1);
                spinner_team_1.setSelection(adapter_team_1.getPosition(bundle.getString("team_1")));

                spinner_team_1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //Toast.makeText(UpdateMatchActivity.this,""+spinner_team_1.getSelectedItem().toString(),Toast.LENGTH_SHORT).show();

                        if ((spinner_team_1.getSelectedItem().toString().equals("Non-Bibs"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            view.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        if ((spinner_team_1.getSelectedItem().toString().equals("-"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            view.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_1.getSelectedItem().toString().equals("Skins"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_skin));
                            view.setBackgroundColor(getResources().getColor(R.color.color_skin));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_1.getSelectedItem().toString().equals("Red"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_red));
                            view.setBackgroundColor(getResources().getColor(R.color.color_red));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.colorWhite));
                        }
                        else if ((spinner_team_1.getSelectedItem().toString().equals("Orange"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                            view.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_1.getSelectedItem().toString().equals("Yellow"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_yellow));
                            view.setBackgroundColor(getResources().getColor(R.color.color_yellow));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_1.getSelectedItem().toString().equals("Green"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_green));
                            view.setBackgroundColor(getResources().getColor(R.color.color_green));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_1.getSelectedItem().toString().equals("Blue"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.squadin));
                            view.setBackgroundColor(getResources().getColor(R.color.squadin));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_1.getSelectedItem().toString().equals("Purple"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_purple));
                            view.setBackgroundColor(getResources().getColor(R.color.color_purple));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.colorWhite));
                        }
                        else if ((spinner_team_1.getSelectedItem().toString().equals("Pink"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_pink));
                            view.setBackgroundColor(getResources().getColor(R.color.color_pink));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_1.getSelectedItem().toString().equals("Brown"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_brown));
                            view.setBackgroundColor(getResources().getColor(R.color.color_brown));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_1.getSelectedItem().toString().equals("Black"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_black));
                            view.setBackgroundColor(getResources().getColor(R.color.color_black));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.colorWhite));
                        }
                        else if ((spinner_team_1.getSelectedItem().toString().equals("White"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            view.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));

                        }else if ((spinner_team_1.getSelectedItem().toString().equals("Grey"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.grey_light));
                            view.setBackgroundColor(getResources().getColor(R.color.grey_light));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        int spinner_counter=0;
                        if (spinner_team_1.getSelectedItem().toString().equals(spinner_team_2.getSelectedItem().toString())){
                            Toast.makeText(UpdateMatchActivity.this,"Both team Kits color cannot be same",Toast.LENGTH_LONG).show();
                            spinner_team_1.setSelection(0);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                spinner_team_2.setAdapter(adapter_team_1);
                spinner_team_2.setSelection(adapter_team_1.getPosition(bundle.getString("team_2")));

                spinner_team_2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //Toast.makeText(UpdateMatchActivity.this,""+spinner_team_2.getSelectedItem().toString(),Toast.LENGTH_SHORT).show();

                        if ((spinner_team_2.getSelectedItem().toString().equals("Non-Bibs"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            view.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        if ((spinner_team_2.getSelectedItem().toString().equals("-"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                             view.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        if ((spinner_team_2.getSelectedItem().toString().equals("-"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            view.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        if ((spinner_team_2.getSelectedItem().toString().equals("-"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            view.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_2.getSelectedItem().toString().equals("Skins"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_skin));
                            view.setBackgroundColor(getResources().getColor(R.color.color_skin));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_2.getSelectedItem().toString().equals("Red"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_red));
                            view.setBackgroundColor(getResources().getColor(R.color.color_red));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.colorWhite));
                        }
                        else if ((spinner_team_2.getSelectedItem().toString().equals("Orange"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                            view.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_2.getSelectedItem().toString().equals("Yellow"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_yellow));
                            view.setBackgroundColor(getResources().getColor(R.color.color_yellow));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_2.getSelectedItem().toString().equals("Green"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_green));
                            view.setBackgroundColor(getResources().getColor(R.color.color_green));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_2.getSelectedItem().toString().equals("Blue"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.squadin));
                            view.setBackgroundColor(getResources().getColor(R.color.squadin));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_2.getSelectedItem().toString().equals("Purple"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_purple));
                            view.setBackgroundColor(getResources().getColor(R.color.color_purple));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.colorWhite));
                        }
                        else if ((spinner_team_2.getSelectedItem().toString().equals("Pink"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_pink));
                            view.setBackgroundColor(getResources().getColor(R.color.color_pink));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_2.getSelectedItem().toString().equals("Brown"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_brown));
                            view.setBackgroundColor(getResources().getColor(R.color.color_brown));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_2.getSelectedItem().toString().equals("Black"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_black));
                            view.setBackgroundColor(getResources().getColor(R.color.color_black));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.colorWhite));
                        }
                        else if ((spinner_team_2.getSelectedItem().toString().equals("White"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            view.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));

                        }else if ((spinner_team_2.getSelectedItem().toString().equals("Grey"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.grey_light));
                            view.setBackgroundColor(getResources().getColor(R.color.grey_light));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }

                        int spinner_counter=0;
                        if (spinner_team_2.getSelectedItem().toString().equals(spinner_team_1.getSelectedItem().toString())){
                            Toast.makeText(UpdateMatchActivity.this,"Both team Kits color cannot be same",Toast.LENGTH_LONG).show();
                            spinner_team_2.setSelection(0);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


                String[] referee_ = getResources().getStringArray(R.array.recurring_game);
                ArrayAdapter<String> adapter_referee= new ArrayAdapter<String>(activity,
                        R.layout.my_spinner_style, referee_) {

                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);

                        return v;
                    }

                    public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                        View v =super.getDropDownView(position, convertView, parent);
                        //((TextView) v).setBackgroundResource(R.drawable.grey_border);
                        return v;
                    }
                };

                spinner_referee.setAdapter(adapter_referee);


                spinner_referee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (spinner_referee.getSelectedItem().toString().equals("Yes")){
                            referee="Yes";
                        }else {
                            referee="No";
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                spinner_referee.setSelection(adapter_referee.getPosition(bundle.getString("referee")));

                final String url = getResources().getString(R.string.url)+"getGameLocations";
                final String params = "";
                final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                    @Override
                    public String TaskCompletionResult(String result) throws JSONException {

                        JSONObject jsonObject = new JSONObject(result);

                        if(jsonObject.getString("respCode").equals("200")) {

                            results             = jsonObject.getJSONArray("results");
                            boots               = jsonObject.getJSONArray("boots");
                            addotionalFacilities= jsonObject.getJSONArray("addotionalFacilities");
                            restrictions        = jsonObject.getJSONArray("restrictions");

                            location_Name   = new String [results.length()+1];
                            location_Name[0]="Select Location";

                            location_ID   = new String [results.length()+1];
                            location_ID[0]="0";

                            address_lane1   = new String [results.length()+1];
                            address_lane1[0]="-";

                            address_lane2   = new String [results.length()+1];
                            address_lane2[0]="";

                            additonal_fact   = new String [results.length()+1];
                            additonal_fact[0]="";

                            mrestrictions    = new String [results.length()+1];
                            mrestrictions [0]="";

                            location_lat   = new String [results.length()+1];
                            location_lat[0]="";

                            location_lng   = new String [results.length()+1];
                            location_lng[0]="";

                            for(int i = 0; i < results.length(); i++) {
                                JSONObject c = results.getJSONObject(i);

                                String locationID   = c.getString("locationID");
                                String locationName = c.getString("locationName");
                                String addressLane1 = c.getString("addressLane1");
                                String addressLane2 = c.getString("addressLane2");
                                String lat          = c.getString("lat");
                                String lng          = c.getString("lng");
                                String addtionalFacilities = c.getString("addtionalFacilities");
                                String lStatus             = c.getString("lStatus");
                                String restrictions        = c.getString("restrictions");

                                //Toast.makeText(MainActivity.this,"lStatus = "+lStatus,Toast.LENGTH_LONG).show();
                                location_ID   [i+1] = locationID;
                                location_Name [i+1] = locationName;
                                additonal_fact[i+1] = addtionalFacilities;
                                address_lane1 [i+1] = addressLane1;
                                address_lane2 [i+1] = addressLane2;
                                mrestrictions [i+1] = restrictions;
                                location_lat  [i+1] = lat;
                                location_lng  [i+1] = lng;
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(UpdateMatchActivity.this,R.layout.my_spinner_style, location_Name) {

                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View v = super.getView(position, convertView, parent);

                                    return v;
                                }

                                public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                                    View v =super.getDropDownView(position, convertView, parent);
                                    //((TextView) v).setBackgroundResource(R.drawable.grey_border);
                                    return v;
                                }
                            };
                            spinner_location_name.setAdapter(adapter);

                            String myString = ""+LocationName; //the value you want the position for
                            System.out.println("LocationName = "+LocationName);
                            int spinnerPosition = adapter.getPosition(myString);

                            spinner_location_name.setSelection(spinnerPosition);

                            spinner_location_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                    try {
                                        location_id = location_ID[position];

                                        tv_date.setText(""+bundle.getString("DateAdded"   ));
                                        DateAdded     = bundle.getString("DateAdded"   );

                                        if (position!=0)
                                            getWeather();

                                        lat=location_lat[position];
                                        lng=location_lng[position];

                                        pitch(Integer.parseInt(location_ID[position]));System.out.println("location_id = "+location_ID[position]);
                                        addotionalFacilities(additonal_fact[position]);
                                        restrictions(mrestrictions[position]);

                                        if (address_lane1[position].equals("-")){
                                            tv_location.setText(""+address_lane1[position]);
                                            tv_location.setSingleLine(true);
                                            tv_location.setOnClickListener(null);

                                        }else {
                                            tv_location.setText(""+address_lane1[position]+"\n"+address_lane2[position]);
                                            tv_location.setSingleLine(false);

                                            tv_location.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    String uri = "http://maps.google.com/maps?hl=en&daddr="+lat+","+lng;
                                                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                            & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                                    intent.setClassName("com.google.android.apps.maps",
                                                            "com.google.android.maps.MapsActivity");
                                                    try{
                                                        startActivity(intent);
                                                    }
                                                    catch(ActivityNotFoundException ex){
                                                        try{
                                                            Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                                            startActivity(unrestrictedIntent);
                                                        }
                                                        catch(ActivityNotFoundException innerEx){
                                                            Toast.makeText(UpdateMatchActivity.this, "Please install a maps application", Toast.LENGTH_LONG).show();
                                                        }
                                                    }

                                                }
                                            });
                                        }

                                        String[] array_name = getResources().getStringArray(R.array.array_name);

                                        ArrayAdapter<String> adapter_array_name = new ArrayAdapter<String>(activity,
                                                R.layout.my_spinner_style, array_name) {

                                            public View getView(int position, View convertView, ViewGroup parent) {
                                                View v = super.getView(position, convertView, parent);

                                                return v;
                                            }

                                            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                                                View v =super.getDropDownView(position, convertView, parent);
                                                //((TextView) v).setBackgroundResource(R.drawable.grey_border);
                                                return v;
                                            }
                                        };
                                        spinner_kickoff.setAdapter(adapter_array_name);
                                        MatchTime    = bundle.getString("MatchTime");
                             /*   MatchTime = MatchTime.replaceAll(" AM", "");
                                MatchTime = MatchTime.replaceAll(" PM", "");*/
                                        //String myString = ""+MatchTime; //the value you want the position for
                                        //int spinnerPosition = adapter.getPosition(MatchTime);

                                        spinner_kickoff.setSelection(adapter_array_name.getPosition(MatchTime));

                                        final int[] counter = {1};
                                        spinner_kickoff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                                getWeather();

                                            }
                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {
                                            }
                                        });

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                        return result;
                    }
                });
                webRequestCall.execute(url,"GET",params);

                spinner_game_type = findViewById(R.id.spinner_game_type);

                String[] array_game_type = getResources().getStringArray(R.array.array_game_type);

                ArrayAdapter<String> adapter_gmae_type = new ArrayAdapter<String>(activity,
                        R.layout.my_spinner_style, array_game_type) {

                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);

                        return v;
                    }

                    public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                        View v =super.getDropDownView(position, convertView, parent);
                        //((TextView) v).setBackgroundResource(R.drawable.grey_border);
                        return v;
                    }
                };

                spinner_game_type.setAdapter(adapter_gmae_type);
                String myString = ""+bundle.getString("gametype");; //the value you want the position for
                int spinnerPosition = adapter_gmae_type.getPosition(myString);

                spinner_game_type.setSelection(spinnerPosition);

                //ed_stadium_name.setText(""+bundle.getString("stadiumName"  ));
                stadiumName    = bundle.getString("stadiumName"  );

                tv_location.setText(""+bundle.getString("LocationName"  ));
                LocationName    = bundle.getString("LocationName"  );


                myCalendar = Calendar.getInstance();

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

                tv_date.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub

                        SimpleDateFormat formatter =
                                new SimpleDateFormat("EEEE dd MMM yyy");

                        String strDate = tv_date.getText().toString();

                        Date date_ = null;
                        try {
                            date_ = formatter.parse(strDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        myCalendar.setTime(date_);

                        DatePickerDialog datePickerDialog=new DatePickerDialog(UpdateMatchActivity.this, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH));

                        //following line to restrict future date selection
                        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                        datePickerDialog.show();
                    }
                });

                spinner_pitch_number = findViewById(R.id.spinner_pitch_number);

                PitchNo    = bundle.getString("PitchNo"  );
                System.out.println("PitchNo = "+PitchNo);


                String[] array_A_side = getResources().getStringArray(R.array.array_A_side);

                ArrayAdapter<String> adapter_A_side = new ArrayAdapter<String>(activity,
                        R.layout.my_spinner_style, array_A_side) {

                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);

                        return v;
                    }

                    public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                        View v =super.getDropDownView(position, convertView, parent);
                        //((TextView) v).setBackgroundResource(R.drawable.grey_border);
                        return v;
                    }
                };

                spinner_a_side.setAdapter(adapter_A_side);
                spinner_a_side.setSelection(adapter_A_side.getPosition( bundle.getString("aSide"   )));


                Turf    = bundle.getString("Turf"  );
                tv_truf.setText(""+Turf);

                /*
                tv_boots= findViewById(R.id.tv_boots);
                Boots   = bundle.getString("Boots"   );
                tv_boots.setText(""+Boots);
                */

                weather    = bundle.getString("weather"  );
                tv_condition.setText(""+weather);

                ed_cost = findViewById(R.id.ed_cost);
                currency     = bundle.getString("cost"   );
                ed_cost.setText(""+currency);
                ed_cost.setSelection(ed_cost.getText().length());

                ed_note = findViewById(R.id.ed_note);
                ed_note.setText(""+ bundle.getString("note"   ));

                //set the default according to value
                //Toast.makeText(activity,"userID = "+sharedPreferences.getString("userID","")+
                //" group_ID = "+group_Id+" game_ID = "+game_Id+" status = out",Toast.LENGTH_SHORT).show();

                SpringDotsIndicator springDotsIndicator = findViewById(R.id.spring_dots_indicator);
                springDotsIndicator.setVisibility(View.GONE);

                ImageView edit_icon = findViewById(R.id.edit_icon);
                edit_icon.setVisibility(View.GONE);

                /*
                ImageView message_img = findViewById(R.id.message_img);
                message_img.setVisibility(View.GONE);
                */

                Button btn_add_match   = findViewById(R.id.btn_add_match);
                btn_add_match.setVisibility(View.VISIBLE);

                btn_add_match.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (spinner_location_name.getSelectedItem().toString().equals("Select Location")){
                            Toast.makeText(UpdateMatchActivity.this,"Select Location",Toast.LENGTH_SHORT).show();
                            spinner_location_name.performClick();
                        }
                        else if (tv_date.getText().toString().equals("")){
                            Toast.makeText(UpdateMatchActivity.this,"Select Date",Toast.LENGTH_SHORT).show();
                            tv_date.performClick();
                            if(tv_date.requestFocus()) {
                                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            }
                        }
                        else if (spinner_kickoff.getSelectedItem().toString().equals("Select KO")){
                            Toast.makeText(UpdateMatchActivity.this,"Select Kickoff time",Toast.LENGTH_SHORT).show();
                            spinner_kickoff.performClick();
                        }
                        else if (spinner_game_type.getSelectedItem().toString().equals("Select Game")){
                            Toast.makeText(UpdateMatchActivity.this,"Select Game Type",Toast.LENGTH_SHORT).show();
                            spinner_game_type.performClick();
                        }
                        else if (tv_day_night.getText().toString().equals("")){
                            Toast.makeText(UpdateMatchActivity.this,"Select Day/ Night",Toast.LENGTH_SHORT).show();
                            tv_day_night.setError("Empty");
                        }
                        else if (pitch_id.equals("")){
                            Toast.makeText(UpdateMatchActivity.this,"Select Pitch",Toast.LENGTH_SHORT).show();
                            if (pitch_Name.length>0)
                                spinner_pitch_number.performClick();
                            else{
                                tv_label_pitch_number.performClick();
                                if(tv_label_pitch_number.requestFocus()) {
                                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                                }
                            }
                        }
                        else if (spinner_a_side.getSelectedItem().toString().equals("Select no of players")){
                            Toast.makeText(UpdateMatchActivity.this,"Select no of players",Toast.LENGTH_SHORT).show();
                            spinner_a_side.performClick();
                        }
                        else if (ed_cost.getText().toString().equals("")){
                            Toast.makeText(UpdateMatchActivity.this,"Enter cost",Toast.LENGTH_SHORT).show();
                            ed_cost.setError("Empty");ed_cost.performClick();
                            if(ed_cost.requestFocus()) {
                                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            }
                        }
                        else if (spinner_recurring_game.getSelectedItem().toString().equals("Select")){
                            Toast.makeText(UpdateMatchActivity.this,"Select Recursive Game",Toast.LENGTH_SHORT).show();
                            spinner_recurring_game.performClick();
                        }
                        else if (spinner_team_1.getSelectedItem().toString().equals("-")){
                            Toast.makeText(UpdateMatchActivity.this,"Select kit colour",Toast.LENGTH_SHORT).show();
                            spinner_team_1.performClick();
                        }
                        else if (spinner_team_2.getSelectedItem().toString().equals("-")){
                            Toast.makeText(UpdateMatchActivity.this,"Select kit colour",Toast.LENGTH_SHORT).show();
                            spinner_team_2.performClick();
                        }
                        else if (spinner_referee.getSelectedItem().toString().equals("Select")){
                            Toast.makeText(UpdateMatchActivity.this,"Select Referee",Toast.LENGTH_SHORT).show();
                            spinner_referee.performClick();
                        }else {
                            //api call here
                            if (image_real_path.equals("")){
                                //api call here
                                final JSONObject prams   = new JSONObject();
                                try {
                                    prams.put("locationID",""+location_id);
                                    prams.put("matchDate",tv_date.getText().toString());
                                    prams.put("MatchTime",spinner_kickoff.getSelectedItem().toString());
                                    prams.put("gId",group_Id);
                                    prams.put("DayNight",tv_day_night.getText().toString());
                                    prams.put("PitchNo",""+pitch_id);
                                    prams.put("aSide",spinner_a_side.getSelectedItem().toString());
                                    prams.put("MatchCost",ed_cost.getText().toString());
                                    prams.put("match_type",""+match_type);
                                    String recurringType="";
                                    if (match_type.equals("Normal")){
                                        recurringType = "never";
                                    }else {
                                        recurringType = ""+spinner_match.getSelectedItem().toString();
                                    }
                                    prams.put("recurringType",""+recurringType);
                                    prams.put("gameType",""+spinner_game_type.getSelectedItem().toString());
                                    prams.put("weather",tv_condition.getText().toString());
                                    prams.put("note",ed_note.getText().toString());
                                    prams.put("referee",""+referee);
                                    prams.put("kitColors",spinner_team_1.getSelectedItem().toString()+"^"+spinner_team_2.getSelectedItem().toString());
                                    prams.put("status","schedule");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //String tmp    = Base64.encodeBytes(prams.toString().getBytes());

                                //String tmp    = Base64.encodeBytes(prams.toString().getBytes());

                                //http://192.168.100.14/FirmGround/REST/createMatch?
                                // inputData={"locationID":"locationID", "gId":1,
                                // "DayNight":"Day", "PitchNo":"4", "MatchCost":"20",
                                // "currency": "GBP","matchDate":"2019-05-22",
                                // "Boots":"monologe", "aSide":"10", "weather":"17c ",
                                // "Turf":"4G", "status":"schedule"}
                                String url    =  getResources().getString(R.string.url)+"updateMatch";

                                String params = "inputData="+prams.toString()+"&userID="+sharedPreferences.getString("userID","")+
                                        "&gId="+group_Id+"&gameId="+game_Id;

                                WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                                    @Override
                                    public String TaskCompletionResult(String result) {

                                        try {

                                            JSONObject jsonObject = new JSONObject(result);
                                            if(jsonObject.getString("respCode").equals("200")) {

                                                String message= jsonObject.getString("message");

                                                //String gameId = jsonObject.getString("gameId");
                                                /*if (!image_real_path.equals("")){
                                                    send_data_add(gameId);
                                                }*/

                                                Toast.makeText(UpdateMatchActivity.this,""+message,Toast.LENGTH_SHORT).show();
                                                finish();

                                      /*
                                      Bundle bundle = new Bundle();
                                      bundle.putString("group_Id",group_Id);
                                      bundle.putString("game_Id",gameId);
                                      Intent intent = new Intent(getActivity(), ChatPageActivity.class);
                                      intent.putExtras(bundle);
                                      MainActivity.activity.startActivity(intent);
                                      */

                                            }
                                            else{
                                                String message= jsonObject.getString("message");
                                                Toast.makeText(UpdateMatchActivity.this,""+message,Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        return result;
                                    }
                                });
                                webRequestCall.execute(url, "POST", params);

                            }else {

                                final JSONObject prams   = new JSONObject();

                                try {
                                    prams.put("locationID",""+location_id);
                                    prams.put("matchDate",tv_date.getText().toString());
                                    prams.put("MatchTime",spinner_kickoff.getSelectedItem().toString());
                                    prams.put("gId",group_Id);
                                    prams.put("DayNight",tv_day_night.getText().toString());
                                    prams.put("PitchNo",""+pitch_id);
                                    prams.put("aSide",spinner_a_side.getSelectedItem().toString());
                                    prams.put("MatchCost",ed_cost.getText().toString());
                                    prams.put("match_type",""+match_type);
                                    String recurringType="";
                                    if (match_type.equals("Normal")){
                                        recurringType = "never";
                                    }else {
                                        recurringType = ""+spinner_match.getSelectedItem().toString();
                                    }
                                    prams.put("recurringType",""+recurringType);
                                    prams.put("gameType",""+spinner_game_type.getSelectedItem().toString());
                                    prams.put("weather",tv_condition.getText().toString());
                                    prams.put("note",ed_note.getText().toString());
                                    prams.put("referee",""+referee);
                                    prams.put("kitColors",spinner_team_1.getSelectedItem().toString()+"^"+spinner_team_2.getSelectedItem().toString());
                                    prams.put("status","schedule");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                send_data_add(""+prams.toString());
                            }

                        }
                    }
                });

                imageView          = findViewById(R.id.imageView);
                //img_edited_stadium = findViewById(R.id.img_edited_stadium);

                image     = bundle.getString("image");
                Picasso.with(imageView.getContext()).load(""+getResources().getString(R.string.imageBaseUrl)+image).into(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
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


            }
            else {

                TextView tv_toolbar_title= findViewById(R.id.tv_toolbar_title);
                tv_toolbar_title.setText("Create Match");

                SpringDotsIndicator springDotsIndicator = findViewById(R.id.spring_dots_indicator);
                springDotsIndicator.setVisibility(View.GONE);

                ImageView edit_icon = findViewById(R.id.edit_icon);
                edit_icon.setVisibility(View.GONE);

                ImageView message_img= findViewById(R.id.message_img);
                message_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

                final String group_Id   = bundle.getString("group_Id");
                final String memberRole = bundle.getString("memberRole");
                group_name              = bundle.getString("group_name");

                //Toast.makeText(UpdateMatchActivity.this,"grounp_id = "+group_Id,Toast.LENGTH_SHORT).show();

/*                ImageView add_match_img = findViewById(R.id.add_match_img);
                add_match_img.setVisibility(View.GONE);*/

                Button btn_reminder,btn_cancel_game,btn_add_match;

                btn_add_match   = findViewById(R.id.btn_add_match);
                btn_add_match.setVisibility(View.VISIBLE);
                btn_add_match.setText("Create Match");

                spinner_kickoff   = findViewById(R.id.spinner_kickoff);

                String[] array_name = getResources().getStringArray(R.array.array_name);

                ArrayAdapter<String> adapter_array_name = new ArrayAdapter<String>(activity,
                        R.layout.my_spinner_style, array_name) {

                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);

                        return v;
                    }

                    public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                        View v =super.getDropDownView(position, convertView, parent);
                        //((TextView) v).setBackgroundResource(R.drawable.grey_border);
                        return v;
                    }
                };


                spinner_kickoff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ed_note.setFocusableInTouchMode(false);
                        ed_note.setFocusable(false);

                        ed_note.clearFocus();

                        ed_note.setCursorVisible(false);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                spinner_kickoff.setAdapter(adapter_array_name);

                spinner_match     = findViewById(R.id.spinner_match);

                String[] match_type_array = getResources().getStringArray(R.array.match_type);

                ArrayAdapter<String> adapter_match_type = new ArrayAdapter<String>(activity,
                        R.layout.my_spinner_style, match_type_array) {

                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);

                        return v;
                    }

                    public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                        View v =super.getDropDownView(position, convertView, parent);
                        //((TextView) v).setBackgroundResource(R.drawable.grey_border);
                        return v;
                    }
                };

                spinner_match.setAdapter(adapter_match_type);


                spinner_recurring_game= findViewById(R.id.spinner_recurring_game);

                String[] recurring_game = getResources().getStringArray(R.array.recurring_game);

                ArrayAdapter<String> adapter_recurring_game = new ArrayAdapter<String>(activity,
                        R.layout.my_spinner_style, recurring_game) {

                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);

                        return v;
                    }

                    public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                        View v =super.getDropDownView(position, convertView, parent);
                        //((TextView) v).setBackgroundResource(R.drawable.grey_border);
                        return v;
                    }
                };

                spinner_recurring_game.setAdapter(adapter_recurring_game);

                spinner_recurring_game.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        if (spinner_recurring_game.getSelectedItem().toString().equals("Yes")){
                            match_type = "Recurring";
                            spinner_match.setVisibility(View.VISIBLE);
                        }else {
                            match_type = "Normal";
                            spinner_match.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                spinner_referee= findViewById(R.id.spinner_referee);

                String[] referee_ = getResources().getStringArray(R.array.recurring_game);

                ArrayAdapter<String> adapter_referee= new ArrayAdapter<String>(activity,
                        R.layout.my_spinner_style, referee_) {

                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);

                        return v;
                    }

                    public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                        View v =super.getDropDownView(position, convertView, parent);
                        //((TextView) v).setBackgroundResource(R.drawable.grey_border);
                        return v;
                    }
                };

                spinner_referee.setAdapter(adapter_referee);

                spinner_referee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (spinner_referee.getSelectedItem().toString().equals("Yes")){
                            referee="Yes";
                        }else {
                            referee="No";
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                spinner_location_name = findViewById(R.id.spinner_location_name);

                /*try {
                    Field popup = Spinner.class.getDeclaredField("mPopup");
                    popup.setAccessible(true);

                    // Get private mPopup member variable and try cast to ListPopupWindow
                    android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinner_location_name);

                    // Set popupWindow height to 500px
                    popupWindow.setHeight(200);
                }
                catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
                    // silently fail...
                }*/

                spinner_a_side    = findViewById(R.id.spinner_a_side);

                String[] array_A_side = getResources().getStringArray(R.array.array_A_side);

                ArrayAdapter<String> adapter_A_side = new ArrayAdapter<String>(activity,
                        R.layout.my_spinner_style, array_A_side) {

                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);

                        return v;
                    }

                    public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                        View v =super.getDropDownView(position, convertView, parent);
                        //((TextView) v).setBackgroundResource(R.drawable.grey_border);
                        return v;
                    }
                };

                spinner_a_side.setAdapter(adapter_A_side);

                tv_day_night = findViewById(R.id.tv_day_night);

                final int[] counter = {1};

                spinner_kickoff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        getWeather();

                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                tv_location     = findViewById(R.id.tv_location);

                final ProgressDialog progressDialog;

                progressDialog = new ProgressDialog(UpdateMatchActivity.this);
                progressDialog.setMessage("Loading Data...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                final String url = getResources().getString(R.string.url)+"getGameLocations";
                final String params = "";
                final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                    @Override
                    public String TaskCompletionResult(String result) throws JSONException {

                        final JSONObject jsonObject = new JSONObject(result);

                        if(jsonObject.getString("respCode").equals("200")) {

                            results             = jsonObject.getJSONArray("results");
                            boots               = jsonObject.getJSONArray("boots");
                            addotionalFacilities= jsonObject.getJSONArray("addotionalFacilities");
                            restrictions        = jsonObject.getJSONArray("restrictions");

                            location_Name   = new String [results.length()+1];
                            location_Name[0]="Select Location";

                            location_ID     = new String [results.length()+1];
                            location_ID[0]  ="0";

                            address_lane1   = new String [results.length()+1];
                            address_lane1[0]="-";

                            address_lane2   = new String [results.length()+1];
                            address_lane2[0]="";

                            additonal_fact   = new String [results.length()+1];
                            additonal_fact[0]="";

                            mrestrictions    = new String [results.length()+1];
                            mrestrictions [0]="";

                            location_lat     = new String [results.length()+1];
                            location_lat[0]  ="";

                            location_lng     = new String [results.length()+1];
                            location_lng[0]  ="";

                            for(int i = 0; i < results.length(); i++) {
                                JSONObject c = results.getJSONObject(i);

                                String locationID = c.getString("locationID");
                                String locationName = c.getString("locationName");
                                String addressLane1 = c.getString("addressLane1");
                                String addressLane2 = c.getString("addressLane2");
                                String lat          = c.getString("lat");
                                String lng          = c.getString("lng");
                                String addtionalFacilities = c.getString("addtionalFacilities");
                                String lStatus             = c.getString("lStatus");
                                String restrictions        = c.getString("restrictions");

                                //Toast.makeText(UpdateMatchActivity.this,"location_ID = "+location_ID[0],Toast.LENGTH_LONG).show();
                                location_ID   [i+1] = locationID;
                                location_Name [i+1] = locationName;
                                additonal_fact[i+1] = addtionalFacilities;
                                address_lane1 [i+1] = addressLane1;
                                address_lane2 [i+1] = addressLane2;
                                mrestrictions [i+1] = restrictions;
                                location_lat  [i+1] = lat;
                                location_lng  [i+1] = lng;
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(UpdateMatchActivity.this,R.layout.my_spinner_style, location_Name) {

                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View v = super.getView(position, convertView, parent);

                                    return v;
                                }

                                public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                                    View v =super.getDropDownView(position, convertView, parent);
                                    //((TextView) v).setBackgroundResource(R.drawable.grey_border);
                                    return v;
                                }
                            };
                            spinner_location_name.setAdapter(adapter);

                            spinner_location_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                    try {
                                        location_id = location_ID[position];

                                        lat=location_lat[position];
                                        lng=location_lng[position];

                                        if (position!=0)
                                            getWeather();

                                        pitch(Integer.parseInt(location_ID[position]));
                                        System.out.println("location_id = "+location_ID[position]);
                                        addotionalFacilities(additonal_fact[position]);
                                        restrictions(mrestrictions[position]);

                                        if (address_lane1[position].equals("-")){
                                            tv_location.setText(""+address_lane1[position]);
                                            tv_location.setOnClickListener(null);
                                            tv_location.setSingleLine(true);
                                        }else {
                                            tv_location.setText(""+address_lane1[position]+"\n"+address_lane2[position]);
                                            tv_location.setSingleLine(false);
                                            tv_location.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    //String uri = "http://maps.google.com/maps?hl=en&saddr="+lat+","+lng;/*"&daddr=51.5074,0.1278"*/
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
                                                            Toast.makeText(UpdateMatchActivity.this, "Please install a maps application", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                }
                                            });
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                            progressDialog.dismiss();
                        }
                        return result;
                    }
                });
                webRequestCall.execute(url,"GET",params);

                tv_date = findViewById(R.id.tv_date);

                spinner_pitch_number= findViewById(R.id.spinner_pitch_number);

                spinner_team_1      = findViewById(R.id.spinner_team_1);

                String[] foo_array = getResources().getStringArray(R.array.team_1);

                ArrayAdapter<String> adapter_team = new ArrayAdapter<String>(UpdateMatchActivity.this,
                        R.layout.my_spinner_style, foo_array) {

                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);

                        return v;
                    }

                    public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                        View v =super.getDropDownView(position, convertView, parent);
                        //((TextView) v).setBackgroundResource(R.drawable.grey_border);
                        // Toast.makeText(UpdateMatchActivity.this,((TextView) v).getText().toString(),Toast.LENGTH_SHORT).show();

                        if (((TextView) v).getText().equals("-")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.colorWhite));

                        } else if (((TextView) v).getText().equals("Skins")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.color_skin));

                        }else if (((TextView) v).getText().equals("Red")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.color_red));
                            ((TextView) v).setTextColor(getResources().getColor(R.color.colorWhite));

                        }else if (((TextView) v).getText().equals("Orange")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.colorOrange));

                        }else if (((TextView) v).getText().equals("Yellow")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.color_yellow));

                        }else if (((TextView) v).getText().equals("Green")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.color_green));

                        }else if (((TextView) v).getText().equals("Blue")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.squadin));

                        }else if (((TextView) v).getText().equals("Purple")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.color_purple));
                            ((TextView) v).setTextColor(getResources().getColor(R.color.colorWhite));

                        }else if (((TextView) v).getText().equals("Pink")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.color_pink));

                        }else if (((TextView) v).getText().equals("Brown")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.color_brown));

                        }else if (((TextView) v).getText().equals("Black")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.color_black));
                            ((TextView) v).setTextColor(getResources().getColor(R.color.colorWhite));

                        }else if (((TextView) v).getText().equals("White")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            ((TextView) v).setTextColor(getResources().getColor(R.color.color_black));

                        }else if (((TextView) v).getText().equals("Grey")){
                            ((TextView) v).setBackgroundColor(getResources().getColor(R.color.grey_light));
                        }

                        return v;
                    }
                };
                spinner_team_1.setAdapter(adapter_team);
                final int[] spinner_counter_1 = {0};
                spinner_team_1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //Toast.makeText(UpdateMatchActivity.this,""+spinner_team_1.getSelectedItem().toString(),Toast.LENGTH_SHORT).show();

                        if ((spinner_team_1.getSelectedItem().toString().equals("Non-Bibs"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            view.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        if ((spinner_team_1.getSelectedItem().toString().equals("-"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            view.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_1.getSelectedItem().toString().equals("Skins"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_skin));
                            view.setBackgroundColor(getResources().getColor(R.color.color_skin));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_1.getSelectedItem().toString().equals("Red"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_red));
                            view.setBackgroundColor(getResources().getColor(R.color.color_red));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.colorWhite));
                        }
                        else if ((spinner_team_1.getSelectedItem().toString().equals("Orange"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                            view.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_1.getSelectedItem().toString().equals("Yellow"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_yellow));
                            view.setBackgroundColor(getResources().getColor(R.color.color_yellow));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_1.getSelectedItem().toString().equals("Green"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_green));
                            view.setBackgroundColor(getResources().getColor(R.color.color_green));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_1.getSelectedItem().toString().equals("Blue"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.squadin));
                            view.setBackgroundColor(getResources().getColor(R.color.squadin));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_1.getSelectedItem().toString().equals("Purple"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_purple));
                            view.setBackgroundColor(getResources().getColor(R.color.color_purple));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.colorWhite));
                        }
                        else if ((spinner_team_1.getSelectedItem().toString().equals("Pink"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_pink));
                            view.setBackgroundColor(getResources().getColor(R.color.color_pink));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_1.getSelectedItem().toString().equals("Brown"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_brown));
                            view.setBackgroundColor(getResources().getColor(R.color.color_brown));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_1.getSelectedItem().toString().equals("Black"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_black));
                            view.setBackgroundColor(getResources().getColor(R.color.color_black));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.colorWhite));
                        }
                        else if ((spinner_team_1.getSelectedItem().toString().equals("White"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            view.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));

                        }else if ((spinner_team_1.getSelectedItem().toString().equals("Grey"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.grey_light));
                            view.setBackgroundColor(getResources().getColor(R.color.grey_light));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }


                        if (spinner_counter_1[0] !=0){

                            if (spinner_team_1.getSelectedItem().toString().equals(spinner_team_2.getSelectedItem().toString())){
                                Toast.makeText(UpdateMatchActivity.this,"Both team Kits color cannot be same",Toast.LENGTH_LONG).show();
                                spinner_team_1.setSelection(0);
                            }
                        }
                        spinner_counter_1[0]++;
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


                spinner_team_2      = findViewById(R.id.spinner_team_2);
                spinner_team_2.setAdapter(adapter_team);

                final int[] spinner_counter_2 = {0};
                spinner_team_2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //Toast.makeText(UpdateMatchActivity.this,""+spinner_team_2.getSelectedItem().toString(),Toast.LENGTH_SHORT).show();

                        if ((spinner_team_2.getSelectedItem().toString().equals("Non-Bibs"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            view.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        if ((spinner_team_2.getSelectedItem().toString().equals("-"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            view.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_2.getSelectedItem().toString().equals("Skins"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_skin));
                            view.setBackgroundColor(getResources().getColor(R.color.color_skin));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_2.getSelectedItem().toString().equals("Red"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_red));
                            view.setBackgroundColor(getResources().getColor(R.color.color_red));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.colorWhite));
                        }
                        else if ((spinner_team_2.getSelectedItem().toString().equals("Orange"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                            view.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_2.getSelectedItem().toString().equals("Yellow"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_yellow));
                            view.setBackgroundColor(getResources().getColor(R.color.color_yellow));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_2.getSelectedItem().toString().equals("Green"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_green));
                            view.setBackgroundColor(getResources().getColor(R.color.color_green));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_2.getSelectedItem().toString().equals("Blue"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.squadin));
                            view.setBackgroundColor(getResources().getColor(R.color.squadin));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_2.getSelectedItem().toString().equals("Purple"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_purple));
                            view.setBackgroundColor(getResources().getColor(R.color.color_purple));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.colorWhite));
                        }
                        else if ((spinner_team_2.getSelectedItem().toString().equals("Pink"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_pink));
                            view.setBackgroundColor(getResources().getColor(R.color.color_pink));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_2.getSelectedItem().toString().equals("Brown"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_brown));
                            view.setBackgroundColor(getResources().getColor(R.color.color_brown));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }
                        else if ((spinner_team_2.getSelectedItem().toString().equals("Black"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.color_black));
                            view.setBackgroundColor(getResources().getColor(R.color.color_black));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.colorWhite));
                        }
                        else if ((spinner_team_2.getSelectedItem().toString().equals("White"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            view.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));

                        }else if ((spinner_team_2.getSelectedItem().toString().equals("Grey"))){
                            //relativespinnerlayout1.setBackgroundColor(getResources().getColor(R.color.grey_light));
                            view.setBackgroundColor(getResources().getColor(R.color.grey_light));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.color_black));
                        }


                        if (spinner_counter_2[0] !=0){
                            if (spinner_team_2.getSelectedItem().toString().equals(spinner_team_1.getSelectedItem().toString())){
                                Toast.makeText(UpdateMatchActivity.this,"Both team Kits color cannot be same",Toast.LENGTH_LONG).show();
                                spinner_team_2.setSelection(0);
                            }
                        }
                        spinner_counter_2[0]++;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


                spinner_game_type   = findViewById(R.id.spinner_game_type);
                String[] array_game_type = getResources().getStringArray(R.array.array_game_type);
                ArrayAdapter<String> adapter_gmae_type = new ArrayAdapter<String>(activity,
                        R.layout.my_spinner_style, array_game_type) {

                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);

                        return v;
                    }

                    public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                        View v =super.getDropDownView(position, convertView, parent);
                        //((TextView) v).setBackgroundResource(R.drawable.grey_border);
                        return v;
                    }
                };

                spinner_game_type.setAdapter(adapter_gmae_type);

                tv_truf         = findViewById(R.id.tv_truf);
                tv_condition    = findViewById(R.id.tv_condition);
                tv_boots        = findViewById(R.id.tv_boots);
                tv_facilities   = findViewById(R.id.tv_facilities);
                tv_restriction  = findViewById(R.id.tv_restriction);
                ed_cost         = findViewById(R.id.ed_cost);
                ed_note         = findViewById(R.id.ed_note);
                tv_label_pitch_number= findViewById(R.id.tv_label_pitch_number);



                ed_note.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            Log.d("EDTA", "text was entered.");
                            ed_note.clearFocus();
                            return true;
                        }
                        return false;
                    }
                });

                ed_note.setFocusableInTouchMode(true);
                ed_note.setFocusable(true);

                myCalendar = Calendar.getInstance();

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

                tv_date.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub

                        if (tv_date.getText().toString().equals("")){
                            DatePickerDialog datePickerDialog=new DatePickerDialog(UpdateMatchActivity.this, date, myCalendar
                                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                    myCalendar.get(Calendar.DAY_OF_MONTH));

                            //following line to restrict future date selection
                            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                            datePickerDialog.show();
                        }else {

                            SimpleDateFormat formatter =
                                    new SimpleDateFormat("EEEE dd MMM yyy");
                            String strDate = tv_date.getText().toString();
                            Date date_ = null;
                            try {
                                date_ = formatter.parse(strDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            myCalendar.setTime(date_);

                            DatePickerDialog datePickerDialog=new DatePickerDialog(UpdateMatchActivity.this, date, myCalendar
                                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                    myCalendar.get(Calendar.DAY_OF_MONTH));

                            //following line to restrict future date selection
                            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                            datePickerDialog.show();
                        }
                    }
                });

                btn_add_match.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (spinner_location_name.getSelectedItem().toString().equals("Select Location")){
                            Toast.makeText(UpdateMatchActivity.this,"Select Location",Toast.LENGTH_SHORT).show();
                            spinner_location_name.performClick();
                        }
                        else if (tv_date.getText().toString().equals("")){
                            Toast.makeText(UpdateMatchActivity.this,"Select Date",Toast.LENGTH_SHORT).show();
                            tv_date.performClick();
                            if(tv_date.requestFocus()) {
                                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            }
                        }
                        else if (spinner_kickoff.getSelectedItem().toString().equals("Select KO")){
                            Toast.makeText(UpdateMatchActivity.this,"Select Kickoff time",Toast.LENGTH_SHORT).show();
                            spinner_kickoff.performClick();
                        }
                        else if (spinner_game_type.getSelectedItem().toString().equals("Select Game")){
                            Toast.makeText(UpdateMatchActivity.this,"Select Game Type",Toast.LENGTH_SHORT).show();
                            spinner_game_type.performClick();
                        }
                        else if (tv_day_night.getText().toString().equals("")){
                            tv_day_night.setError("Empty");
                        }
                        else if (pitch_id.equals("")){
                            Toast.makeText(UpdateMatchActivity.this,"Select Pitch",Toast.LENGTH_SHORT).show();
                            if (pitch_Name.length>0)
                                spinner_pitch_number.performClick();
                            else{
                                tv_label_pitch_number.performClick();
                                if(tv_label_pitch_number.requestFocus()) {
                                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                                }
                            }
                        }
                        else if (spinner_a_side.getSelectedItem().toString().equals("Select no of players")){
                            Toast.makeText(UpdateMatchActivity.this,"Select no of players",Toast.LENGTH_SHORT).show();
                            spinner_a_side.performClick();

                        }
                        else if (ed_cost.getText().toString().equals("")){
                            ed_cost.setError("Empty");ed_cost.performClick();
                            if(ed_cost.requestFocus()) {
                                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            }
                        }
                        else if (spinner_recurring_game.getSelectedItem().toString().equals("Nothing Selected")){
                            Toast.makeText(UpdateMatchActivity.this,"Select Recursive Game",Toast.LENGTH_SHORT).show();
                            spinner_recurring_game.performClick();
                        }
                        else if (spinner_team_1.getSelectedItem().toString().equals("-")){
                            Toast.makeText(UpdateMatchActivity.this,"Select kit colour",Toast.LENGTH_SHORT).show();
                            spinner_team_1.performClick();

                        }
                        else if (spinner_team_2.getSelectedItem().toString().equals("-")){
                            Toast.makeText(UpdateMatchActivity.this,"Select kit colour",Toast.LENGTH_SHORT).show();
                            spinner_team_2.performClick();

                        }
                        else if (spinner_referee.getSelectedItem().toString().equals("Selected")){
                            Toast.makeText(UpdateMatchActivity.this,"Select Referee",Toast.LENGTH_SHORT).show();
                            spinner_referee.performClick();
                        }
                        else {
                            //api call here
                            final JSONObject prams   = new JSONObject();
                            try {
                                prams.put("locationID",""+location_id);
                                prams.put("matchDate",tv_date.getText().toString());
                                prams.put("MatchTime",spinner_kickoff.getSelectedItem().toString());
                                prams.put("gId",group_Id);
                                prams.put("DayNight",tv_day_night.getText().toString());
                                prams.put("PitchNo",""+pitch_id);
                                prams.put("aSide",spinner_a_side.getSelectedItem().toString());
                                prams.put("MatchCost",ed_cost.getText().toString());
                                prams.put("match_type",""+match_type);
                                String recurringType="";
                                if (match_type.equals("Normal")){
                                    recurringType = "never";
                                }else {
                                    recurringType = ""+spinner_match.getSelectedItem().toString();
                                }
                                prams.put("recurringType",""+recurringType);
                                prams.put("gameType",""+spinner_game_type.getSelectedItem().toString());
                                prams.put("weather",tv_condition.getText().toString());
                                prams.put("substitutes","");
                                prams.put("note",ed_note.getText().toString());
                                prams.put("referee",""+referee);
                                prams.put("kitColors",spinner_team_1.getSelectedItem().toString()+"^"+spinner_team_2.getSelectedItem().toString());
                                prams.put("status","schedule");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String tmp    = Base64.encodeToString(prams.toString().getBytes(),Base64.NO_WRAP);
// http://192.168.100.14/FirmGround/REST/createMatch?
// inputData={"locationID":"locationID", "gId":1,
// "DayNight":"Day", "PitchNo":"4", "MatchCost":"20",
// "currency": "GBP","matchDate":"2019-05-22",
// "Boots":"monologe", "aSide":"10", "weather":"17c ",
// "Turf":"4G", "status":"schedule"}
                            String url    =  getResources().getString(R.string.url)+"createMatch";
                            String params = "inputData="+tmp+"&userID="+sharedPreferences.getString("userID","");

                            WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                                @Override
                                public String TaskCompletionResult(String result) {

                                    try {

                                        JSONObject jsonObject = new JSONObject(result);
                                        if(jsonObject.getString("respCode").equals("200")) {

                                            String message= jsonObject.getString("message");
                                            String gameId = jsonObject.getString("gameId");

                                            if (!image_real_path.equals("")){
                                                send_data_add_upload_game(gameId);
                                            }

                                            Toast.makeText(UpdateMatchActivity.this,""+message,Toast.LENGTH_SHORT).show();

                                            Bundle bundle = new Bundle();

                                            bundle.putString("memberRole" , memberRole);
                                            bundle.putString("game_Id"    , gameId);
                                            bundle.putString("group_Id"   , group_Id);
                                            bundle.putString("group_name" , group_name);

                                            Intent intent = new Intent(UpdateMatchActivity.this, ChatPageActivity.class);
                                            intent.putExtras(bundle);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();

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


                imageView = findViewById(R.id.imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
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

            }
        }

    }

    private String removeLastChar(String str) {

        if(str.equals("")){
            return "";
        }else
            return str.substring(0, str.length() - 1);
    }

    String[] pitch_Name, pitch_ID,allow_Boots,pitch_turf;
    public void pitch(int position) throws JSONException {

        String mposition = String.valueOf(position);
        int pitch_lenth = 0;

        if (position==0){


            spinner_pitch_number.setVisibility(View.GONE);
            tv_label_pitch_number.setVisibility(View.VISIBLE);
            tv_label_pitch_number.setText("-");
            tv_label_pitch_number.setSingleLine(true);
            tv_truf.setText("-");

            tv_boots.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            tv_boots.setHorizontallyScrolling(true);
            tv_boots.setMarqueeRepeatLimit(-1);
            tv_boots.setFocusable(true);
            tv_boots.setFocusableInTouchMode(true);
            tv_boots.setSelected(true);
            tv_boots.setTextColor(getResources().getColor(R.color.color_black));
            tv_boots.setText("-");

        }else {

            tv_truf.setText("-");

            for(int i = 0; i < results.length(); i++) {
                JSONObject c = results.getJSONObject(i);
                JSONArray pitches = c.getJSONArray("pitches");

                if (position==0){

                    pitch_Name    = new String [1];
                    pitch_Name[0] = "Select Pitch";

                    spinner_pitch_number.setVisibility(View.GONE);
                    tv_label_pitch_number.setVisibility(View.VISIBLE);
                    tv_label_pitch_number.setText(""+pitch_Name[0]);

                    pitch_ID      = new String [1];
                    pitch_ID[0]   = "";

                    allow_Boots   = new String [1];
                    allow_Boots[0]= "";

                    pitch_turf    = new String [1];
                    pitch_turf[0] = "";

                }
                else {
                    //Toast.makeText(UpdateMatchActivity.this,"pitches.length() = "+pitches.length(),Toast.LENGTH_LONG).show();

                    tv_label_pitch_number.setVisibility(View.GONE);
                    spinner_pitch_number.setVisibility(View.VISIBLE);

                    pitch_Name    = new String [pitches.length()+1];
                    pitch_Name[0] = "Select Pitch";

                    pitch_ID      = new String [pitches.length()+1];
                    pitch_ID[0]   = "";

                    allow_Boots   = new String [pitches.length()+1];
                    allow_Boots[0]= "";

                    pitch_turf    = new String [pitches.length()+1];
                    pitch_turf[0] = "";

                    String plocationID="";

                    pitch_lenth = pitches.length();

                    for(int j = 0; j < pitches.length(); j++) {
                        JSONObject d = pitches.getJSONObject(j);

                        String id          = d.getString("id");
                        plocationID        = d.getString("locationID");
                        String pitchName   = d.getString("pitchName");
                        String ptichTurf   = d.getString("ptichTurf");
                        String allowBoots  = d.getString("allowBoots");
                        String pitchStatus = d.getString("pitchStatus");

                        pitch_ID   [j+1]= id;
                        pitch_Name [j+1]= pitchName;
                        allow_Boots[j+1]= allowBoots;
                        pitch_turf [j+1]= ptichTurf;

                    }
                    //Toast.makeText(UpdateMatchActivity.this,"pitch_lenth = "+pitch_lenth,Toast.LENGTH_LONG).show();

                    if (pitch_lenth!=1){

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(UpdateMatchActivity.this,
                                R.layout.my_spinner_style, pitch_Name) {

                            public View getView(int position, View convertView, ViewGroup parent) {
                                View v = super.getView(position, convertView, parent);

                                return v;
                            }

                            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                                View v =super.getDropDownView(position, convertView, parent);
                                //((TextView) v).setBackgroundResource(R.drawable.grey_border);
                                return v;
                            }
                        };

                        if (adapter!=null)
                            spinner_pitch_number.setAdapter(adapter);
                        else
                            Toast.makeText(UpdateMatchActivity.this,"else",Toast.LENGTH_LONG).show();

                        spinner_pitch_number.setSelection(adapter.getPosition(PitchNo));

                        spinner_pitch_number.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                // Toast.makeText(MainActivity.this,"allow_Boots[position] = "+allow_Boots[position],Toast.LENGTH_LONG).show();
                                try {
                                    pitch_id = pitch_ID[position];
                                    boots(allow_Boots[position]);
                                    tv_truf.setText(""+ pitch_turf[position]);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                    else {
                        spinner_pitch_number.setVisibility(View.GONE);
                        tv_label_pitch_number.setVisibility(View.VISIBLE);
                        tv_label_pitch_number.setText(""+pitch_Name[1]);
                        tv_label_pitch_number.setSingleLine(true);

                        pitch_id=pitch_ID[1];
                        boots(allow_Boots[1]);
                        tv_truf.setText(""+ pitch_turf[1]);
                        //tv_label_pitch_number.setBackgroundColor(getActivity().getResources().getColor(R.color.color_red));
                    }


                    if (mposition.equals(plocationID)){
                        break;
                    }

                }
            }
        }

    }

    String[] boot_Name, boot_ID;
    String   indoor_check="";
    public void boots(String position) throws JSONException{

        //Toast.makeText(MainActivity.this,"lenth = "+position.replaceAll("\\D", "").length(),Toast.LENGTH_LONG).show();


        boot_Name = new String [position.replaceAll("\\D", "").length()];
        boot_ID   = new String [position.replaceAll("\\D", "").length()];

        //Toast.makeText(MainActivity.this,"position = "+position,Toast.LENGTH_LONG).show();

        int counter = 0;

        String str_boots="";

        for(int i = 0; i < boots.length(); i++) {
            JSONObject c  = boots.getJSONObject(i);
            String bID    = c.getString("bID");
            String bName  = c.getString("bName");
            String bStatus= c.getString("bStatus");

            if (position.contains(bID)){

                boot_ID[counter]  = bID;
                boot_Name[counter]= bName;
                str_boots += bName+", ";
                counter++;
                //Toast.makeText(MainActivity.this,"position = "+position+" bid = "+bID,Toast.LENGTH_LONG).show();
            }else {
                if (position.equals(""))
                    str_boots = "-,";
            }
            //Toast.makeText(MainActivity.this,"bStatus = "+bStatus,Toast.LENGTH_LONG).show();
        }


        tv_boots.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tv_boots.setHorizontallyScrolling(true);
        tv_boots.setMarqueeRepeatLimit(-1);
        tv_boots.setFocusable(true);
        tv_boots.setFocusableInTouchMode(true);
        tv_boots.setSelected(true);
        tv_boots.setTextColor(getResources().getColor(R.color.color_black));
        tv_boots.setText(removeLastChar(removeLastChar(str_boots)));
        if (tv_boots.getText().toString().equals("Indoor")){
            //Toast.makeText(getActivity(),"textView = "+textView.getText().toString(),Toast.LENGTH_LONG).show();
            indoor_check = tv_boots.getText().toString();
            tv_day_night.setText("N/A");
            tv_condition.setText("N/A");

        }else {
            //Toast.makeText(getActivity(),"textView = "+textView.getText().toString(),Toast.LENGTH_LONG).show();
            indoor_check="";
        }

    }

    String[] fact_Name, fact_ID;
    public void addotionalFacilities(String position) throws JSONException{

        //Toast.makeText(MainActivity.this,"lenth = "+position.replaceAll("\\D", "").length(),Toast.LENGTH_LONG).show();

        fact_Name = new String [position.replaceAll("\\D", "").length()];
        fact_ID   = new String [position.replaceAll("\\D", "").length()];

        //Toast.makeText(MainActivity.this,"position = "+position,Toast.LENGTH_LONG).show();
        int counter = 0;
        String facilities="";

        for(int i = 0; i < addotionalFacilities.length(); i++) {
            JSONObject c = addotionalFacilities.getJSONObject(i);

            String fID = c.getString("fID");
            String fName = c.getString("fName");
            String fStatus = c.getString("fStatus");

            if (position.contains(fID)){

                fact_ID[counter]  = fID;
                fact_Name[counter]= fName;
                facilities+=fName+", ";
                counter++;
                //Toast.makeText(MainActivity.this,"position = "+position+" bid = "+bID,Toast.LENGTH_LONG).show();
            }else {
                if (position.equals(""))
                    facilities="-,,";
            }
            //Toast.makeText(MainActivity.this,"bStatus = "+bStatus,Toast.LENGTH_LONG).show();
        }

        tv_facilities.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tv_facilities.setHorizontallyScrolling(true);
        tv_facilities.setMarqueeRepeatLimit(-1);
        tv_facilities.setFocusable(true);
        tv_facilities.setFocusableInTouchMode(true);
        tv_facilities.setSelected(true);
        tv_facilities.setTextColor(getResources().getColor(R.color.color_black));
        tv_facilities.setText(removeLastChar(removeLastChar(facilities)));

    }

    String[] rest_Name, rest_ID;
    public void restrictions(String position) throws JSONException{

        //Toast.makeText(MainActivity.this,"lenth = "+position.replaceAll("\\D", "").length(),Toast.LENGTH_LONG).show();

        rest_Name = new String [position.replaceAll("\\D", "").length()];
        rest_ID   = new String [position.replaceAll("\\D", "").length()];

        //Toast.makeText(MainActivity.this,"position = "+position,Toast.LENGTH_LONG).show();
        int counter = 0;
        String rest="";

        for(int i = 0; i < restrictions.length(); i++) {
            JSONObject c = restrictions.getJSONObject(i);

            String id = c.getString("id");
            String restriction = c.getString("restriction");
            String resStatus = c.getString("resStatus");

            if (position.contains(id)){

                rest_ID[counter]  = id;
                rest_Name[counter]= restriction;
                rest+=restriction+", ";
                counter++;
                //Toast.makeText(MainActivity.this,"position = "+position+" bid = "+bID,Toast.LENGTH_LONG).show();
            }else {
                if (position.equals(""))
                    rest="-,,";
            }
            //Toast.makeText(MainActivity.this,"bStatus = "+bStatus,Toast.LENGTH_LONG).show();
        }

        tv_restriction.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tv_restriction.setHorizontallyScrolling(true);
        tv_restriction.setMarqueeRepeatLimit(-1);
        tv_restriction.setFocusable(true);
        tv_restriction.setFocusableInTouchMode(true);
        tv_restriction.setSelected(true);
        tv_restriction.setTextColor(getResources().getColor(R.color.color_black));
        tv_restriction.setText(removeLastChar(removeLastChar(rest)));
    }

    String sunriseTime="",sunsetTime="";
    private void updateLabel() {
        String myFormat = "EEEE dd MMM yyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        tv_date.setText(sdf.format(myCalendar.getTime()));
        tv_date.setError(null);

        getWeather();
    }

    int datecheck=0;
    public void getWeather(){

        if (datecheck==0){

        }else {

            if (location_id.equals("") || location_id.equals("0")){
                Toast.makeText(UpdateMatchActivity.this,"Please select Location",Toast.LENGTH_LONG).show();
                spinner_location_name.performClick();

            } else if (tv_date.getText().toString().equals("")){
                Toast.makeText(UpdateMatchActivity.this,"Select Date",Toast.LENGTH_SHORT).show();
                tv_date.setError("Empty");

            }else if (spinner_kickoff.getSelectedItem().toString().equals("Select KO")){
                Toast.makeText(UpdateMatchActivity.this,"Please select Kickoff time",Toast.LENGTH_LONG).show();
            }else {

                final String url = "http://qa.monshiapp.com/FirmGround/REST/dayGameWeather";
                final String params = "locationID="+location_id+"&dateTime="+tv_date.getText().toString()+"&time="+spinner_kickoff.getSelectedItem().toString();
                final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public String TaskCompletionResult(String result) throws JSONException {

                        JSONObject jsonObject = new JSONObject(result);

                        if(jsonObject.getString("respCode").equals("200")) {

                            if (indoor_check.equals("Indoor")){
                                tv_condition.setText("N/A");
                                tv_day_night.setText("N/A");
                            }else {
                                tv_condition.setText(jsonObject.getString("temperatureC")+"C, "+jsonObject.getString("results"));
                                tv_day_night.setText(jsonObject.getString("daynight"));
                            }

                            JSONObject feedData = jsonObject.getJSONObject("feedData");

                            //Toast.makeText(getActivity(),"feedData = "+feedData,Toast.LENGTH_LONG).show();

                            JSONObject daily = feedData.getJSONObject("daily");
                            //Toast.makeText(getActivity(),"data = "+daily,Toast.LENGTH_LONG).show();

                            JSONArray data = daily.getJSONArray("data");
                            JSONObject jsonObject_1 =data.getJSONObject(0);

                            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

                            sunriseTime = formatter.format(new Date(Long.parseLong(jsonObject_1.getString("sunriseTime"))*1000));
                            sunsetTime  = formatter.format(new Date(Long.parseLong(jsonObject_1.getString( "sunsetTime"))*1000));

                            System.out.println("sunriseTime = "+sunriseTime);
                            System.out.println("sunsetTime = "+sunsetTime);

                   /* Toast.makeText(getActivity(),"sunriseTime = "+sunriseTime,Toast.LENGTH_LONG).show();
                    Toast.makeText(getActivity(),"sunsetTime = " +sunsetTime,Toast.LENGTH_LONG).show();*/
                        }else {
                        }
                        return result;
                    }
                });
                webRequestCall.execute(url,"POST",params);

            }

        }
        datecheck++;
    }

    public boolean checkPermissionForReadExtertalStorage() {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            int result = UpdateMatchActivity.this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = UpdateMatchActivity.this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
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
            ActivityCompat.requestPermissions((UpdateMatchActivity.this), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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
                realPath = RealPathUtil_match.getRealPathFromURI_BelowAPI11(UpdateMatchActivity.this, data.getData());

                // SDK >= 11 && SDK < 19
            else if (Build.VERSION.SDK_INT < 19)
                realPath = RealPathUtil_match.getRealPathFromURI_API11to18(UpdateMatchActivity.this, data.getData());

                // SDK > 19 (Android 4.4)
            else
                realPath = RealPathUtil_match.getRealPathFromURI_API19(UpdateMatchActivity.this, data.getData());


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


        Log.d("HMKCODE", "Build.VERSION.SDK_INT:" + sdk);
        Log.d("HMKCODE", "URI Path:" + uriPath);
        Log.d("HMKCODE", "Real Path: " + realPath);

        image_real_path = file.getAbsolutePath();

        compressImage(image_real_path);

        //send_data_add(""+sharedPreferences.getString("userID",""),"profileImage");
    }

    private void send_data_add(final String params_) {

        //
        new AsyncTask<Void, Integer, Boolean>() {
            ProgressDialog progressDialog;



            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(UpdateMatchActivity.this);
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
                    jsonObject=jsonParser_update_match.uploadImage(image_real_path,""+params_,""+sharedPreferences.getString("userID",""),
                            ""+group_Id,""+game_Id,UpdateMatchActivity.this);

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

                        Toast.makeText(UpdateMatchActivity.this,"Profile Saved!",Toast.LENGTH_SHORT).show();

                        getFragmentManager().popBackStack();
                        finish();
                    }
                    else{

                        Toast.makeText(UpdateMatchActivity.this, "Failed To Save Data, Try Again!", Toast.LENGTH_LONG).show();
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
    private void send_data_add_upload_game(final String game_id) {

        new AsyncTask<Void, Integer, Boolean>() {
            ProgressDialog progressDialog;



            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(UpdateMatchActivity.this);
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
                    jsonObject=jsonParser_upload_match.uploadImage(image_real_path,""+game_id,UpdateMatchActivity.this);

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

                        Toast.makeText(UpdateMatchActivity.this,"Profile Saved!",Toast.LENGTH_SHORT).show();

                        getFragmentManager().popBackStack();
                    }
                    else{

                        Toast.makeText(UpdateMatchActivity.this, "Failed To Save Data, Try Again!", Toast.LENGTH_LONG).show();


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

            imageView.setImageBitmap(scaledBitmap);
            //img_group_icon.setImageBitmap(scaledBitmap);


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

class RealPathUtil_match {

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
