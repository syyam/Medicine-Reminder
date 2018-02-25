package com.example.syyam.saifapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by syyam on 27-Sep-16.
 */
public class Alarm_Reciever extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {


        //fetch extra string from the intents

        String get_your_string = intent.getExtras().getString("string");
        String medicine = intent.getExtras().getString("string2");
        System.out.println("What is the key ?" +get_your_string);

        Intent i = new Intent(context,RingtonePlayingService.class);
        // pass the extra string from mainactivity to RingtonePlayingService

        i.putExtra("extra",get_your_string);
        i.putExtra("name",medicine);

        context.startService(i);
    }


}
