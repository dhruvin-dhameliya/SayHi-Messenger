package com.group_project.chatapplication.registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.group_project.chatapplication.MainActivity;
import com.group_project.chatapplication.R;
import com.hbb20.CountryCodePicker;

public class Registration_Activity extends AppCompatActivity {

    TextInputEditText txt_reg_phone_number;
    CountryCodePicker regSelectCountryCode;
    FloatingActionButton btn_reg_next_otp;
    String new_phone_number, country_code;
    FirebaseUser user;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            startActivity(new Intent(Registration_Activity.this, MainActivity.class));
            finish();
        }

        txt_reg_phone_number = findViewById(R.id.txt_reg_phone_number);
        regSelectCountryCode = findViewById(R.id.regSelectCountryCode);
        btn_reg_next_otp = findViewById(R.id.btn_reg_next_otp);
        country_code = regSelectCountryCode.getSelectedCountryCodeWithPlus();

        btn_reg_next_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Creanewaccount();
            }
        });
    }

    private void Creanewaccount() {
        new_phone_number = txt_reg_phone_number.getText().toString().trim();
        if (TextUtils.isEmpty(new_phone_number)) {
            txt_reg_phone_number.setError("Please enter your phone number.");
        } else if (new_phone_number.length() != 10) {
            txt_reg_phone_number.setError("Phone number should be 10 digits.");
        } else {
            String phoneNo = txt_reg_phone_number.getText().toString().trim();
            Intent moveOtpScreen = new Intent(Registration_Activity.this, Register_OTP_Activity.class);
            moveOtpScreen.putExtra("lastPhoneNumber", phoneNo);
            moveOtpScreen.putExtra("countryCode", country_code);
            startActivity(moveOtpScreen);
        }
    }
}