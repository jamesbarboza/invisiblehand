package com.invisible.hand;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.invisible.hand.HomeActivity.latitude;
import static com.invisible.hand.HomeActivity.longitude;
import static com.invisible.hand.HomeActivity.mobileNumber;
import static com.invisible.hand.HomeActivity.userName;

public class DialogBox extends Application {
    private Context context;
    private String[] itemArray;
    private String itemSelected;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    public DialogBox(Context context) {
        this.context = context;
    }

    public void generateDialogBox() {
        Log.d("Dialog", "generateDialogBox: in dialog");
        itemArray = new String[]{"Cross Road", "Need Medical help", "Stuck in a unknown area", "Enter Manually"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("What help you need?");
        final EditText manualEditText = new EditText(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        manualEditText.setLayoutParams(params);
        builder.setView(manualEditText);
        manualEditText.setVisibility(View.INVISIBLE);
        builder.setSingleChoiceItems(itemArray, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemSelected = itemArray[which];
                Log.d("Dialog", "onClick: item selected " + itemSelected);
                if (itemSelected.equals("Enter Manually")) {
                    Log.d("Dialog", "onClick: in if");
                    manualEditText.setVisibility(View.VISIBLE);
                } else {
                    manualEditText.setVisibility(View.INVISIBLE);
                }
            }
        });


        builder.setPositiveButton("Sent Alert", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }

        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manualEditText.getText().toString().length() < 4) {
                    manualEditText.setError("please describe");
                } else {

                    if(itemSelected.equals("Enter Manually")){
                        itemSelected=manualEditText.getText().toString();
                    }
                    mAuth = FirebaseAuth.getInstance();
                    SendAlertToDatabase sendAlertToDatabase = new SendAlertToDatabase(userName, mobileNumber,
                            itemSelected, longitude, latitude);
                    databaseReference = FirebaseDatabase.getInstance().getReference("Active Request");
                    databaseReference.child(mAuth.getUid()).setValue(sendAlertToDatabase);
                    dialog.dismiss();
                    // send to firebase
                }

            }
        });
    }
}
