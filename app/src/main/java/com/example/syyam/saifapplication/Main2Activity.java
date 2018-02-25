package com.example.syyam.saifapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class Main2Activity extends AppCompatActivity {

     EditText Medicine;
    AlarmManager alarmManager;
    TimePicker timePicker;
    Context context;
    PendingIntent pendingIntent;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.context = this;
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        timePicker = (TimePicker) findViewById(R.id.time_picker);

        Medicine=(EditText) findViewById(R.id.medicine);




    }



    public void startAlarm(View v) {
        final String mName=Medicine.getText().toString().trim();
        if(!TextUtils.isEmpty(mName)) {
            final Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());

            int hour = timePicker.getCurrentHour();
            int minute = timePicker.getCurrentMinute();

            String hour_string = String.valueOf(hour);
            String minute_string = String.valueOf(minute);

            if (hour > 12) {
                hour_string = String.valueOf(hour - 12);
            }
            if (minute < 10) {
                minute_string = "0" + String.valueOf(minute);
            }
            Intent i = new Intent(this, Alarm_Reciever.class);
            i.putExtra("string", "alarm on");
            i.putExtra("string2",mName);
            int f;
            for( f=0;f<=100;++f)
            {
                pendingIntent = pendingIntent.getBroadcast(this.context, f, i, pendingIntent.FLAG_UPDATE_CURRENT);
                //alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmManager.INTERVAL_DAY, pendingIntent);
                Toast.makeText(this, "Alarm Set To " + hour_string + " :" + minute_string, Toast.LENGTH_SHORT).show();
                Intent l=new Intent(Main2Activity.this,MainActivity.class);
                startActivity(l);
                finish();
            }

        }
        else
        {
            Toast.makeText(Main2Activity.this,"Please enter medicine name", Toast.LENGTH_LONG).show();
        }
    }
    public void End_Alarm(View v)
    {
        Intent i = new Intent(this,Alarm_Reciever.class);
        i.putExtra("string","alarm off");
        alarmManager.cancel(pendingIntent);
        sendBroadcast(i);
        Toast.makeText(this, "Alarm Turned Off", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Are you sure you want to exit the app?");
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //moveTaskToBack(true);
                                Process.killProcess(Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}