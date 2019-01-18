package com.invisible.hand;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private Button gmailLoginButton;

    private FirebaseAuth mAuth;
    public static GoogleSignInClient mGoogleSignInClient;


    private static int RC_SIGN_IN = 100;

    Context context;
    private BackgroundCheck backgroundCheck;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        gmailLoginButton = findViewById(R.id.gmailLoginButton);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        gmailLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d("user", "onStart: " + currentUser + mAuth.getUid());
        if (currentUser != null) {
            //checkIfUserExist(currentUser);
            goToHomeActivity();
        }

        context = this;
        backgroundCheck = new BackgroundCheck(this.getContext());
        serviceIntent = new Intent(this.getContext(), backgroundCheck.getClass());
    }

    private boolean isServiceRunning(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName())){
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    private void goToHomeActivity() {
        Intent intent =new Intent(LoginActivity.this,HomeActivity.class);
    }

    public void signIn() {
        Log.d("aa", "signIn: ");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                Log.d("LoginActivity", "google signIn successful...starting firebase");
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account, data);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

                Log.w("LoginActivity", "Google sign in failed", e);
                // ...
            }
        }
    }

    public void firebaseAuthWithGoogle(final GoogleSignInAccount acct, final Intent data) {
        Log.d("LoginActivity", "firebaseAuthWithGoogle:" + acct.getId());
        Log.d("LoginActivity", "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LoginActivity", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "LogIn Success", Toast.LENGTH_SHORT).show();
                            goToRegistration(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LoginActivity", "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(LoginActivity.this, "Failed Login", Toast.LENGTH_SHORT).show();
                            ;
                        }
                    }

                    });
    }

    private void goToRegistration(FirebaseUser user) {
        Log.d("LoginAcivity", "goToRegistration: In here");
        Intent intent = new Intent(LoginActivity.this,RegistrationFormActivity.class);
        intent.putExtra("name",user.getDisplayName());
        intent.putExtra("email",user.getEmail());
        Log.d("LoginActivity", "goToRegistration: starting activity");
        startActivity(intent);
    }

    public Context getContext(){
        return context;
    }

    @Override
    protected void onDestroy(){
        stopService(serviceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();
    }

}
