package com.example.syyam.saifapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;


public class RingtonePlayingService extends Service {

    MediaPlayer mediaPlayer;
    boolean is_Running;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        //fetch hte extra string values

        String get_string_extra = intent.getExtras().getString("extra");
        String medicine_extra = intent.getExtras().getString("name");


        System.out.print(get_string_extra);
        //Setting Notifications
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        //set intent that goes to main activity
        Intent mainActivity = new Intent(this.getApplicationContext(),Main2Activity.class);
        PendingIntent pendingIntents = PendingIntent.getActivity(this,0,mainActivity,0);
        //Make the notification parameters
        Notification notification = new Notification.Builder(this)
                .setContentTitle("An Alarm Is Going Off!")
                .setContentText("Time to take "+ medicine_extra)
                .setContentIntent(pendingIntents)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.front9)
                .build();

        //set up the notification call command
        notificationManager.notify(0,notification);

        assert get_string_extra!=null;
        switch (get_string_extra)
        {
            case "alarm on":
                startId = 1;
                break;
            case "alarm off":
                startId = 0;
                break;
            default:
                startId = 0;
                break;
        }



        //if there is no music playing and the user presses 'alarm on'
        //music should start playing
        if (!this.is_Running && startId == 1){
            System.out.println("There is no music and you want to turn on that music.");
            mediaPlayer = MediaPlayer.create(this,R.raw.alarmtune);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();

            this.is_Running = true;
            //this.startId = 0;
        }
        // if there is music playing and user presses 'alarm off' the music should stop
        else if (this.is_Running && startId == 0){
            System.out.println("There is  music and you want to turn off that music.");
            mediaPlayer.stop();
            mediaPlayer.reset();

            this.is_Running = false;
            //this.startId = 0
        }
        //random buttons to bug proof the app
        // if the user presses alarm off and already no music is there then do nothing
        else if (!this.is_Running && startId == 0){
            System.out.println("There is  no music and you want to turn off that music.");

            this.is_Running = false;
            //startId = 0;
        }
        //if the user presses alarm on and already the music is playing do nothing.
        else if(this.is_Running && startId == 1){
            System.out.println("There is  music and you want to turn on that music.");
            this.is_Running = true;
            //startId = 1;
        }
        else{
            System.out.println("Somehow you reached This");
        }

        System.out.println("in on startcommand");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        System.out.println("On Destroy Called");
    }
}