package com.invisible.hand;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;

    private Button callHelpButton;
    private Button locateVictimButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth=FirebaseAuth.getInstance();

        callHelpButton=findViewById(R.id.callHelpButton);
        locateVictimButton=findViewById(R.id.locateVictimButton);
        locateVictimButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                locateVictim();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.signOutMenuButton: {
                signOut();
                break;
            }
        }
        return true;
    }
    public void signOut() {
        Log.d("home", "signOut: outsss");
        mAuth.signOut();
        if (LoginActivity.mGoogleSignInClient == null) {
            Log.d("Home Activity", "signOut: in here");
            goToLoginActivity();
        } else {
            LoginActivity.mGoogleSignInClient.signOut();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                goToLoginActivity();
            }
            Log.d("user", "signOut: complete " + currentUser);
        }

    }

    private void goToLoginActivity(){
        Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    public void callForHelp(View view){
        Toast.makeText(this, R.string.looking_for_help, Toast.LENGTH_SHORT).show();
    }

    public void locateVictim(){
        Intent intent = new Intent(this, LocateVictimActivity.class);
        startActivity(intent);
        Log.d("ERRROR", "this studio is shit!");
    }


}
