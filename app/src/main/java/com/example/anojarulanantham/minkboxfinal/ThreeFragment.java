package com.example.anojarulanantham.minkboxfinal;



import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * Created by anojarulanantham on 2017-03-08.
 */

public class ThreeFragment extends Fragment {

    private EditText nameent;
    private EditText phoneent;
    private EditText messageent;
    public SwitchCompat toggle;
    private String[] prefs = {"", "", ""};
    private String[] values;
    Boolean storedSwitch;

    public ThreeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        values = readpref();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_three, container, false);
        nameent = (EditText) v.findViewById(R.id.nameentry);
        phoneent = (EditText) v.findViewById(R.id.numberentry);
        messageent = (EditText) v.findViewById(R.id.messageentry);
        toggle = (SwitchCompat) v.findViewById(R.id.mySwitch);

        Button b = (Button) v.findViewById(R.id.savetext);
        b.setOnClickListener(mButtonClickListener);

        nameent.setText(values[0]);
        phoneent.setText(values[1]);
        messageent.setText(values[2]);
        toggle.setChecked(storedSwitch);

        getActivity().registerReceiver(this.batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        return v;
    }

    private View.OnClickListener mButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            writepref();
        }
    };

    public void writepref() {
        Context context = getActivity();
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.namepref), nameent.getText().toString());
        editor.putString(getString(R.string.phonepref), phoneent.getText().toString());
        editor.putString(getString(R.string.messagepref), messageent.getText().toString());
        editor.putBoolean(getString(R.string.notificationpref), toggle.isChecked());
        editor.commit();
    }

    public String[] readpref() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String defaultName = getResources().getString(R.string.namepref);
        String defaultPhone = getResources().getString(R.string.phonepref);
        String defaultMessage = getResources().getString(R.string.messagepref);
        Boolean defaultSwitch = false;

        String storedName = sharedPref.getString(getString(R.string.namepref), defaultName);
        String storedPhone = sharedPref.getString(getString(R.string.phonepref), defaultPhone);
        String storedMessage = sharedPref.getString(getString(R.string.messagepref), defaultMessage);
        storedSwitch = sharedPref.getBoolean(getString(R.string.notificationpref), defaultSwitch);
        prefs[0] = storedName;
        prefs[1] = storedPhone;
        prefs[2] = storedMessage;
        return prefs;
    }

    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level / (float)scale;
            System.out.println(level);

            NotificationCompat.Builder mBuilder =
                    (NotificationCompat.Builder) new NotificationCompat.Builder(getActivity())
                            .setSmallIcon(R.drawable.matt)
                            .setContentTitle("My notification")
                            .setContentText("Hello World!")
                            .setPriority(Notification.PRIORITY_MAX);

            Intent resultIntent = new Intent(getActivity(), MainActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());

            stackBuilder.addParentStack(MainActivity.class);

            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            
            mNotificationManager.notify(0, mBuilder.build());
        }
    };
}
