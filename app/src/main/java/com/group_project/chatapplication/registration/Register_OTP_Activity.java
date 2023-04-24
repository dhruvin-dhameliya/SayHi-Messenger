package com.group_project.chatapplication.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group_project.chatapplication.R;

import java.util.concurrent.TimeUnit;

public class Register_OTP_Activity extends AppCompatActivity {

    TextView display_phone;
    TextInputEditText txt_reg_otp;
    MaterialButton btn_reg_otp_verify;
    String verificationId, code, previous_phone_number, onlineStatus = "", typing = "";
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String user_name = "", user_about = "Hey there! I am using Say Hi.", user_profile_img = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_otpactivity);

        previous_phone_number = getIntent().getExtras().get("lastPhoneNumber").toString();
        code = getIntent().getExtras().get("countryCode").toString();

        display_phone = findViewById(R.id.display_phone);
        display_phone.setText(code + " " + previous_phone_number + ".");

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait for creating account...");
        progressDialog.setCanceledOnTouchOutside(false);

        txt_reg_otp = findViewById(R.id.txt_reg_otp);
        btn_reg_otp_verify = findViewById(R.id.btn_reg_otp_verify);

        senderVerificationCode(previous_phone_number);

        btn_reg_otp_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp_txt = txt_reg_otp.getText().toString();
                if (TextUtils.isEmpty(otp_txt)) {
                    txt_reg_otp.setError("Enter 6 digit OTP.");
                } else if (otp_txt.length() != 6) {
                    txt_reg_otp.setError("OTP should be only 6 digits.");
                } else {
                    verifyCode(otp_txt);
                    progressDialog.show();
                }
            }
        });
    }

    private void senderVerificationCode(String phoneNo) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(code + phoneNo)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            final String code = credential.getSmsCode();
            if (code != null) {
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(getApplicationContext(), "Verification Invalid!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(s, token);
            verificationId = s;
            Toast.makeText(getApplicationContext(), "OTP send.", Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String Code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, Code);
        signinByCredential(credential);
    }

    private void signinByCredential(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    DatabaseReference reference = database.getReference().child("Users Details").child(code + previous_phone_number);
                    User_Model model_user = new User_Model(auth.getUid(), user_name, code + previous_phone_number, user_about, user_profile_img, onlineStatus, typing);
                    reference.setValue(model_user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(Register_OTP_Activity.this, Profile_Info_Activity.class));
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Something went wrong!!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}