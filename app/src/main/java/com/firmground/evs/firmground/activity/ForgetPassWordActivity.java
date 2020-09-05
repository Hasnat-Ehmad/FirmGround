package com.firmground.evs.firmground.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firmground.evs.firmground.R;
import com.firmground.evs.firmground.webservice.TaskDelegate;
import com.firmground.evs.firmground.webservice.WebRequestCall;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgetPassWordActivity extends AppCompatActivity {

    EditText ed_email;

    public static String CODE,E_MAIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass_word);

        ed_email = findViewById(R.id.ed_email);

        Button email_sign_in_button = findViewById(R.id.email_sign_in_button);
        email_sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed_email.getText().toString().equals("")){
                    ed_email.setError("Empty");
                }else {
                    ApiCall();//Api calling is here
                }

            }
        });
    }

    //call the api here
    public void ApiCall(){

        final String url = getResources().getString(R.string.url) + "forgetPassword?userInput="+ed_email.getText().toString();
        final String params = "";
        final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
            @Override
            public String TaskCompletionResult(String result) throws JSONException {

                JSONObject jsonObject = new JSONObject(result);

                if(jsonObject.getString("respCode").equals("200")) {
                    Toast.makeText(getApplicationContext(),""+jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                    CODE = jsonObject.getString("code");
                    E_MAIL = ed_email.getText().toString();
                    Intent intent = new Intent(ForgetPassWordActivity.this,ConfirmPinActivity.class);
                    startActivity(intent);
                }
                return result;
            }
        });
        webRequestCall.execute(url,"GET",params);
    }
}
