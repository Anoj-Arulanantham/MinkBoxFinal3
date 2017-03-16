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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;

import android.widget.EditText;
import android.widget.ImageView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by anojarulanantham on 2017-03-08.
 */

public class ThreeFragment extends Fragment {

    private String[] prefs = {"", "", ""};
    private String[] values;
    Boolean storedSwitch;
    private Bitmap bitmap;
    private EditText name;
    private EditText phoneNumber;
    private EditText message;
    private SwitchCompat lowBatterySwitch;

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

        name = ((EditText) v.findViewById(R.id.nameentry));
        phoneNumber = ((EditText) v.findViewById(R.id.numberentry));
        message = ((EditText) v.findViewById(R.id.messageentry));
        lowBatterySwitch = (SwitchCompat) v.findViewById(R.id.mySwitch);

        // Button b = (Button) v.findViewById(R.id.savetext);
        // b.setOnClickListener(mButtonClickListener);

        name.setText(values[0]);
        phoneNumber.setText(values[1]);
        message.setText(values[2]);
        lowBatterySwitch.setChecked(storedSwitch);

        getActivity().registerReceiver(this.batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        System.out.println(phoneNumber);
        Button saveData = (Button) v.findViewById(R.id.saveData);
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lowBatterySwitch.setChecked(true);
                writepref();
                int loops = 0;
                bitmap = Bitmap.createBitmap(176, 264, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                Typeface font = Typeface.createFromAsset(getContext().getAssets(), "os-regular.ttf");
                Paint paint = new Paint();
                paint.setColor(Color.WHITE);
                paint.setTypeface(font);
                paint.setAntiAlias(true);
                paint.setTextSize(12.5f);
                canvas.drawText("Name:",10,30,paint);
                canvas.drawText(name.getText().toString(),53,30,paint);
                canvas.drawText("Phone:",10,60,paint);
                canvas.drawText(phoneNumber.getText().toString(),55,60,paint);
                canvas.drawText("Message:",10,90,paint);
                for(int i = 0; i < message.getText().toString().length(); i = i + 24){
                    if(i + 24 < message.getText().toString().length()) {
                        canvas.drawText(message.getText().toString().substring(i, i + 24), 12, (105 + (loops * 15)), paint);
                    }else{
                        canvas.drawText(message.getText().toString().substring(i, message.getText().toString().length()), (12), (105 + (loops * 15)), paint);
                    }
                    loops = loops + 1;
                }
                //canvas.drawText(message.getText().toString(),13,105,paint);
                

                MyClientTask myClientTask = new MyClientTask("192.168.43.193", 12345);
                myClientTask.execute("sending image");
            }
        });

        return v;
    }

    // private View.OnClickListener mButtonClickListener = new View.OnClickListener() {
    //     public void onClick(View v) {
    //         writepref();
    //     }
    // };

    public void writepref() {
        Context context = getActivity();
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.namepref), name.getText().toString());
        editor.putString(getString(R.string.phonepref), phoneNumber.getText().toString());
        editor.putString(getString(R.string.messagepref), message.getText().toString());
        editor.putBoolean(getString(R.string.notificationpref), lowBatterySwitch.isChecked());
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

            if (level < 10 && !lowBatterySwitch.isChecked()) {
                NotificationCompat.Builder mBuilder =
                        (NotificationCompat.Builder) new NotificationCompat.Builder(getActivity())
                                .setSmallIcon(R.drawable.matt)
                                .setContentTitle("mInk Box")
                                .setContentText("Your battery level is low!")
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
        }
    };

    public class MyClientTask extends AsyncTask<String, Void, Void> {

        String dstAddress;
        int dstPort;
        String response;

        MyClientTask(String addr, int port) {
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                System.out.println("mInkBox");
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                //Bitmap resizedImage = Bitmap.createScaledBitmap(bitmap, 264, 176, true);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                //resizedImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                System.out.println(encoded);

                Socket socket = new Socket("192.168.43.193", 12345);
                OutputStream outputStream = socket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(encoded);

                socket.close();

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }
}
