package com.firmground.evs.firmground.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firmground.evs.firmground.R;

import static com.firmground.evs.firmground.activity.ForgetPassWordActivity.CODE;

public class ConfirmPinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_pin);
        TextView tv_label_code = findViewById(R.id.tv_label_code);
        tv_label_code.setText(""+CODE);

        Button email_sign_in_button = findViewById(R.id.email_sign_in_button);
        email_sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText ed_pin = findViewById(R.id.ed_pin);
                if (ed_pin.getText().toString().equals(""+CODE)){

                    Toast.makeText(getApplicationContext(),"PIN match succfully",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ConfirmPinActivity.this,ChangePasswordActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
