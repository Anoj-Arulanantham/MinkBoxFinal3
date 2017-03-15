package com.example.anojarulanantham.minkboxfinal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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

import static android.app.Activity.RESULT_OK;

/**
 * Created by anojarulanantham on 2017-03-08.
 */

public class OneFragment extends Fragment {
    Bitmap bitmap = null;
    ImageView imageView = null;
    ImageView cameraView = null;

    private static final int CAMERA_PIC_REQUEST = 1337;

    public OneFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_one, container, false);

        Drawable myDrawable = getResources().getDrawable(R.drawable.harder);
        bitmap = ((BitmapDrawable) myDrawable).getBitmap();

        ImageView getImage = (ImageView) v.findViewById(R.id.gallery);
        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Hello");
                Intent intent = new Intent();
                // Intent will only show images
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Where do you want to select a picture from?"), 1);
            }
        });

        Button pushImage = (Button) v.findViewById(R.id.pushImage);
        pushImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("pushing image");
                MyClientTask myClientTask = new MyClientTask("192.168.43.193", 12345);
                myClientTask.execute("yo");
            }
        });

        cameraView = (ImageView) v.findViewById(R.id.camera);
        cameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("about to launch camera");
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("inside onActivtyResult");
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("after super call");

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                imageView = (ImageView) getActivity().findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == CAMERA_PIC_REQUEST) {
                if(resultCode == 0) {
                    return;
                }
                bitmap = (Bitmap) data.getExtras().get("data");
                imageView = (ImageView) getActivity().findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
        }
    }

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
                Bitmap resizedImage = Bitmap.createScaledBitmap(bitmap, 264, 176, true);
                resizedImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
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
