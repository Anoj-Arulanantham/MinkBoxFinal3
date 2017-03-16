package com.example.anojarulanantham.minkboxfinal;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_three, container, false);
        System.out.println("HELLO");

        getActivity().registerReceiver(this.batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        name = ((EditText) v.findViewById(R.id.nameentry));
        phoneNumber = ((EditText) v.findViewById(R.id.numberentry));
        message = ((EditText) v.findViewById(R.id.messageentry));
        lowBatterySwitch = (SwitchCompat) v.findViewById(R.id.mySwitch);

        System.out.println(phoneNumber);
        Button saveData = (Button) v.findViewById(R.id.saveData);
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                lowBatterySwitch.setChecked(true);

                MyClientTask myClientTask = new MyClientTask("192.168.43.193", 12345);
                myClientTask.execute("sending image");
            }
        });

        return v;
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
