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

import static com.firmground.evs.firmground.activity.ForgetPassWordActivity.E_MAIL;

public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Button email_sign_in_button = findViewById(R.id.email_sign_in_button);
        email_sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ed_new_password = findViewById(R.id.ed_new_password);
                EditText ed_confirm_password = findViewById(R.id.ed_confirm_password);

                if (ed_confirm_password.getText().toString().equals(""+ed_new_password.getText().toString())){

                    final String url = getResources().getString(R.string.url) + "changePassword?userInput="+E_MAIL+
                                                                                    "&password="+ed_confirm_password.getText().toString();
                    final String params = "";
                    final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                        @Override
                        public String TaskCompletionResult(String result) throws JSONException {

                            JSONObject jsonObject = new JSONObject(result);

                            if(jsonObject.getString("respCode").equals("200")) {
                                Toast.makeText(getApplicationContext(),""+jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ChangePasswordActivity.this,LoginActivity.class);
                                startActivity(intent);
                            }
                            return result;
                        }
                    });
                    webRequestCall.execute(url,"GET",params);

                }else {
                    Toast.makeText(getApplicationContext(),"Password do not match",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
