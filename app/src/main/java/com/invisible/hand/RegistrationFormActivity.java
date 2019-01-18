package com.invisible.hand;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class RegistrationFormActivity extends AppCompatActivity {

    private EditText userNameEditText;
    private EditText userEmailEditText;
    private EditText userMobileNumberEditText;
    private EditText otpEditText;
    private TextView editMobileNumberTextView;
    private Button submitButton;
    private Button resendButton;

    private FirebaseAuth mAuth;

    private String userName;
    private String userEmailId;
    private String userMobileNumber;
    private String verificationCode;


    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_form);

        mAuth=FirebaseAuth.getInstance();

        userName=getIntent().getStringExtra("name");
        userEmailId=getIntent().getStringExtra("email");

        userEmailEditText=findViewById(R.id.userEmailEditText);
        userMobileNumberEditText=findViewById(R.id.userMobileNumberEditText);
        userNameEditText=findViewById(R.id.userNameEditText);
        otpEditText=findViewById(R.id.otpEditText);
        editMobileNumberTextView=findViewById(R.id.editMobileNumberTextView);
        submitButton=findViewById(R.id.submitButton);
        resendButton=findViewById(R.id.resendButton);
        setDataInViews();

        userMobileNumberEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(userMobileNumberEditText.isFocusable()){
                    hideOtpViews();
                }
                return false;
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(submitButton.getText().toString().equals("Submit")){
                    // send data to firebase
                    verifyPhoneNumberWithCode(verificationCode,getOtp());
                    sendDataToDatabase();
                }
                else {
                    if (getPhoneNumber().length()<10) {
                        userMobileNumberEditText.setError("Invalid Number");

                    }
                    else{
                        otpEditText.setVisibility(View.VISIBLE);
                        editMobileNumberTextView.setVisibility(View.VISIBLE);
                        submitButton.setText(R.string.submit_text);
                        resendButton.setVisibility(View.VISIBLE);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.addRule(RelativeLayout.BELOW,R.id.otpEditText);
                        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        params.setMargins(0,0,16,0);
                        submitButton.setLayoutParams(params);
                        startPhoneNumberVerification(getPhoneNumber());
                        Log.d("RegistrationForrm", "onClick: "+verificationCode);
                        Log.d("RegistrationForm", "onClick: "+getPhoneNumber());
                    }

                }
            }
        });



        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d("RegistrationForm", "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w("RegistrationForm", "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    userMobileNumberEditText.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(RegistrationFormActivity.this, "Quota exceed",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d("RegistrationForm", "onCodeSent:" + verificationId);
                verificationCode = verificationId;
                mResendToken = token;
            }
        };

        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendVerificationCode(getPhoneNumber(),mResendToken);
            }
        });

    }

    private void sendDataToDatabase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserDataBase");
        UploadUserData uploadUserData = new UploadUserData(userName,userEmailId,userMobileNumber);
        databaseReference.child(mAuth.getUid()).setValue(uploadUserData);

    }

    private void hideOtpViews() {
        editMobileNumberTextView.setVisibility(View.INVISIBLE);
        otpEditText.setVisibility(View.INVISIBLE);
        submitButton.setText(R.string.verify_mobile_number);
        resendButton.setVisibility(View.INVISIBLE);
    }

    private void setDataInViews() {
        userEmailEditText.setText(userEmailId);
        userNameEditText.setText(userName);
    }

    public void signOut() {
        Log.d("RegistrationForm", "signOut: outsss");
        mAuth.signOut();
        if (LoginActivity.mGoogleSignInClient == null) {
            Log.d("RegistrationForm", "signOut: in here");
            goToLoginActivity();
        } else {
            LoginActivity.mGoogleSignInClient.signOut();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                goToLoginActivity();
            }
            Log.d("RegistrationForm", "signOut: complete " + currentUser);
        }

    }

    private void goToLoginActivity() {
        Intent intent = new Intent(RegistrationFormActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        signOut();
    }

    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("RegistrationForm", "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            sendDataToDatabase();
                            goToHomeActivity();
                        } else {
                            Log.w("RegistrationForm", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                otpEditText.setError("Invalid code.");
                            }
                        }
                    }
                });
    }

    private void goToHomeActivity() {
        Intent intent=new Intent(RegistrationFormActivity.this,HomeActivity.class);
        startActivity(intent);
    }


    public void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }


    public void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }


    public void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);
    }

    public String getPhoneNumber() {
        userMobileNumber= "+91" +userMobileNumberEditText.getText().toString();
        Log.d("as", "getPhoneNumber: "+userMobileNumber);
        return userMobileNumber;
    }
    public String getOtp() {
        return otpEditText.getText().toString();
    }

}
