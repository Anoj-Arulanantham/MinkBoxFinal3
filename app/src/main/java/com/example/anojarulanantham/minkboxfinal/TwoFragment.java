package com.example.anojarulanantham.minkboxfinal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;

/**
 * Created by anojarulanantham on 2017-03-08.
 */

public class TwoFragment extends Fragment implements View.OnClickListener{

    private DrawingView drawView;
    private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn, sendBtn;
    private float smallBrush, mediumBrush, largeBrush;
    private Bitmap doodle;

    public TwoFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_two, container, false);

        drawView = (DrawingView) v.findViewById(R.id.drawing);
        LinearLayout paintLayout = (LinearLayout) v.findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
        drawBtn = (ImageButton) v.findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);

        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        drawView.setBrushSize(mediumBrush);

        eraseBtn = (ImageButton) v.findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);

        newBtn = (ImageButton) v.findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);

        saveBtn = (ImageButton) v.findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);

        sendBtn = (ImageButton) v.findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(this);

        ImageButton brownButton = (ImageButton) v.findViewById(R.id.brown);
        ImageButton redButton = (ImageButton) v.findViewById(R.id.red);
        ImageButton orangeButton = (ImageButton) v.findViewById(R.id.orange);
        ImageButton yellowButton = (ImageButton) v.findViewById(R.id.yellow);
        ImageButton greenButton = (ImageButton) v.findViewById(R.id.green);
        ImageButton skyBlueButton = (ImageButton) v.findViewById(R.id.skyblue);
        ImageButton blueButton = (ImageButton) v.findViewById(R.id.blue);
        ImageButton purpleButton = (ImageButton) v.findViewById(R.id.purple);
        ImageButton pinkButton = (ImageButton) v.findViewById(R.id.pink);
        ImageButton whiteButton = (ImageButton) v.findViewById(R.id.white);
        ImageButton greyButton = (ImageButton) v.findViewById(R.id.grey);
        ImageButton blackButton = (ImageButton) v.findViewById(R.id.black);

        brownButton.setOnClickListener(paintClicked);
        redButton.setOnClickListener(paintClicked);
        orangeButton.setOnClickListener(paintClicked);
        yellowButton.setOnClickListener(paintClicked);
        greenButton.setOnClickListener(paintClicked);
        skyBlueButton.setOnClickListener(paintClicked);
        blueButton.setOnClickListener(paintClicked);
        purpleButton.setOnClickListener(paintClicked);
        pinkButton.setOnClickListener(paintClicked);
        whiteButton.setOnClickListener(paintClicked);
        greyButton.setOnClickListener(paintClicked);
        blackButton.setOnClickListener(paintClicked);

        return v;
    }

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    private View.OnClickListener paintClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view!=currPaint){
                ImageButton imgView = (ImageButton)view;
                String color = view.getTag().toString();
                drawView.setColor(color);
                imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
                currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
                currPaint=(ImageButton)view;
                drawView.setErase(false);
                drawView.setBrushSize(drawView.getLastBrushSize());
            }
        }
    };


    @Override
    public void onClick(View view){
        if(view.getId()==R.id.draw_btn){

            final Dialog brushDialog = new Dialog(getActivity());
            brushDialog.setTitle("Brush size:");
            brushDialog.setContentView(R.layout.brush_chooser);

            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(smallBrush);
                    drawView.setLastBrushSize(smallBrush);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });

            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(mediumBrush);
                    drawView.setLastBrushSize(mediumBrush);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(largeBrush);
                    drawView.setLastBrushSize(largeBrush);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();
        }else if(view.getId()==R.id.erase_btn){
            final Dialog brushDialog = new Dialog(getActivity());
            brushDialog.setTitle("Eraser size:");
            brushDialog.setContentView(R.layout.brush_chooser);

            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();
        }else if(view.getId()==R.id.new_btn){
            AlertDialog.Builder newDialog = new AlertDialog.Builder(getActivity());
            newDialog.setTitle("New drawing");
            newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();
        }else if(view.getId()==R.id.save_btn){
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(getActivity());
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to device Gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    drawView.setDrawingCacheEnabled(true);
                    String imgSaved = MediaStore.Images.Media.insertImage(
                            getActivity().getContentResolver(), drawView.getDrawingCache(),
                            UUID.randomUUID().toString()+".png", "drawing");

                    if(imgSaved!=null){
                        Toast savedToast = Toast.makeText(getActivity().getApplicationContext(),
                                "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                        savedToast.show();
                    }
                    else{
                        Toast unsavedToast = Toast.makeText(getActivity().getApplicationContext(),
                                "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                        unsavedToast.show();
                    }

                    drawView.destroyDrawingCache();
                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }else if(view.getId()==R.id.send_btn) {
            AlertDialog.Builder newDialog = new AlertDialog.Builder(getActivity());
            newDialog.setTitle("Send Image");
            newDialog.setMessage("Do you want to send this image to your mInk Box?");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    doodle = getBitmapFromView(drawView);
                    MyClientTask myClientTask = new MyClientTask("192.168.43.193", 12345);
                    myClientTask.execute("sending image");
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();
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
                Bitmap resizedImage = Bitmap.createScaledBitmap(doodle, 264, 176, true);
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
