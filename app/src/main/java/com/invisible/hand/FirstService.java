package com.invisible.hand;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.invisible.hand.AppNot.CHANNEL_ID;

public class FirstService extends Service {

    private DatabaseReference databaseReference;

    public static double victimLongitude;
    public static double victimLatitude;

    public static final int ID=377;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Active Request");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Intent intent = new Intent(FirstService.this, LocateVictimActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(FirstService.this,
                        0, intent, PendingIntent.FLAG_UPDATE_CURRENT);



                for (DataSnapshot dt : dataSnapshot.getChildren()) {
                    SendAlertToDatabase sendAlertToDatabase = dt.getValue(SendAlertToDatabase.class);
                    createNotification(sendAlertToDatabase, pendingIntent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return START_STICKY;
    }

    public void createNotification(SendAlertToDatabase sendAlertToDatabase, PendingIntent pendingIntent) {

        victimLatitude = sendAlertToDatabase.getLatitude();
        victimLongitude = sendAlertToDatabase.getLongitude();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Help Alert")
                .setContentText(sendAlertToDatabase.getHelpType())
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .build();


        startForeground(ID, notification);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
