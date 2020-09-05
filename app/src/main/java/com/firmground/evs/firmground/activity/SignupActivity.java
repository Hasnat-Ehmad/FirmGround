package com.firmground.evs.firmground.activity;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.firmground.evs.firmground.R;

import com.firmground.evs.firmground.webservice.TaskDelegate;
import com.firmground.evs.firmground.webservice.WebRequestCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SignupActivity extends AppCompatActivity {

    Button registerButton;
    Calendar myCalendar;

    EditText ed_date_of_birth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ImageView back_img = findViewById(R.id.back_img);
        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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



        final EditText ed_first_name        = findViewById(R.id.ed_first_name);
        final EditText ed_last_name         = findViewById(R.id.ed_last_name );
        ed_date_of_birth     = findViewById(R.id.ed_date_of_birth );
        ed_date_of_birth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog datePickerDialog=new DatePickerDialog(SignupActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                //following line to restrict future date selection
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        final EditText ed_email             = findViewById(R.id.ed_email     );
        final EditText ed_contact           = findViewById(R.id.ed_contact   );
        final EditText ed_password          = findViewById(R.id.ed_password  );
        final EditText ed_user_support      = findViewById(R.id.ed_user_support);
        final EditText ed_pref_play_position= findViewById(R.id.ed_pref_play_position);
        final EditText ed_emerg_contact     = findViewById(R.id.ed_emerg_contact    );
        final EditText ed_emerg_contact_num = findViewById(R.id.ed_emerg_contact_num);

        registerButton = (Button)findViewById(R.id.btn_send);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ed_first_name.getText().toString().equals("")){
                    ed_first_name.setError("Empty");

                }else if(ed_last_name.getText().toString().equals("")){
                         ed_last_name.setError("Empty");

                }else if(ed_date_of_birth.getText().toString().equals("")){
                         ed_date_of_birth.setError("Empty");

                } else if(ed_last_name.getText().toString().equals("")){
                          ed_last_name.setError("Empty");

                }else if(ed_email.getText().toString().equals("")){
                         ed_email.setError("Empty");

                }else if(ed_contact.getText().toString().equals("")){
                         ed_contact.setError("Empty");

                }else if(ed_password.getText().toString().equals("") ){
                         ed_password.setError("Empty");

                }else if(ed_password.getText().toString().length() < 6){
                         ed_password.setError("Password length short");

                }else if(ed_user_support.getText().toString().equals("")){
                         ed_user_support.setError("Empty");

                }else if(ed_pref_play_position.getText().toString().equals("")){
                         ed_pref_play_position.setError("Empty");

                }else if(ed_emerg_contact.getText().toString().equals("")){
                         ed_emerg_contact.setError("Empty");

                }else if(ed_emerg_contact_num.getText().toString().equals("")) {
                         ed_emerg_contact_num.setError("Empty");

                }else {
                      /*  final ProgressDialog pd = new ProgressDialog(SignupActivity.this);
                    pd.setMessage("Loading...");
                    pd.show();*/

                    // Toast.makeText(SignupActivity.this,"All checks pass",Toast.LENGTH_SHORT).show();

                    final JSONObject jsonobj   = new JSONObject();
                    try {
                        jsonobj.put("fname", ""+ed_first_name.getText().toString());
                        jsonobj.put("lname", ""+ed_last_name.getText().toString());
                        jsonobj.put("birth_date",""+ed_date_of_birth.getText().toString());
                        jsonobj.put("email", ""+ed_email.getText().toString());
                        jsonobj.put("contact", ""+ed_contact.getText().toString());
                        jsonobj.put("password", ""+ed_password.getText().toString());

                        jsonobj.put("team_user_supports", ""+ed_user_support.getText().toString());
                        jsonobj.put("play_position", ""+ed_pref_play_position.getText().toString());
                        jsonobj.put("emerg_contact", ""+ed_emerg_contact.getText().toString());

                        jsonobj.put("emerg_contact_num", ""+ed_emerg_contact_num.getText().toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Toast.makeText(SignupActivity.this,"params = "+jsonobj.toString(),Toast.LENGTH_SHORT).show();

//http://192.168.100.14/FirmGround/REST/signUp?
// inputData={"logIn":2, "groupName":"City Spiders", "users":[{"name":"Abc2","phonenumber":"122323407","id":1},{"name":"Abc3","phonenumber":"123456701","id":3}]}

                    String tmp    =  Base64.encodeToString(jsonobj.toString().getBytes(),Base64.NO_WRAP);
                    String url    =  getResources().getString(R.string.url)+"signUp?inputData="+tmp;

                    String params = "";

                    //Toast.makeText(SignupActivity.this,"url = "+url,Toast.LENGTH_SHORT).show();

                    WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                        @Override
                        public String TaskCompletionResult(String result) {

                            try {

                                JSONObject jsonObject = new JSONObject(result);
                                if(jsonObject.getString("respCode").equals("200")) {

                                    Toast.makeText(SignupActivity.this,""+jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                                    finish();
                                    //pd.dismiss();
                                }
                                else{
                                    //pd.dismiss();
                                    Toast.makeText(SignupActivity.this,""+Html.fromHtml(jsonObject.getString("message_object")),Toast.LENGTH_LONG).show();

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

    private void updateLabel() {
        String myFormat = "dd MMM yyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        ed_date_of_birth.setText(sdf.format(myCalendar.getTime()));
    }

}
