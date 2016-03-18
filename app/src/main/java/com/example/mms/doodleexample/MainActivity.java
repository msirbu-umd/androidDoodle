package com.example.mms.doodleexample;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import yuku.ambilwarna.AmbilWarnaDialog;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;


public class MainActivity extends AppCompatActivity implements OnClickListener, SensorEventListener {


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private DoodleView drawView;
    private ImageButton currPaint, drawBtn, opacityBtn, eraseBtn, colorwheelBtn, newBtn, saveBtn;
    private float smallBrush, mediumBrush, largeBrush;
    int color = 0xffffff00;

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;

    @Override
    public void onClick(View view){//respond to clicks
        if(view.getId()==R.id.opacity_btn){

            final Dialog seekDialog = new AppCompatDialog(this);
            seekDialog.setTitle("Opacity level:");
            seekDialog.setContentView(R.layout.opacity_chooser);

            final TextView seekTxt = (TextView)seekDialog.findViewById(R.id.opq_txt);
            final SeekBar seekOpq = (SeekBar)seekDialog.findViewById(R.id.opacity_seek);

            seekOpq.setMax(100);

            int currLevel = drawView.getPaintAlpha();
            seekTxt.setText(currLevel+"%");
            seekOpq.setProgress(currLevel);

            seekOpq.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(progress < 5){
                        progress = 5;
                    }
                    seekTxt.setText(Integer.toString(progress) + "%");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            Button opqBtn = (Button)seekDialog.findViewById(R.id.opq_ok);

            opqBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(seekOpq.getProgress()  > 5) {
                        drawView.setPaintAlpha(seekOpq.getProgress());
                    }else{
                        drawView.setPaintAlpha(5);
                    }
                    seekDialog.dismiss();
                }
            });

            seekDialog.show();


            /*
            final Dialog brushDialog = new AppCompatDialog(this);
            brushDialog.setContentView(R.layout.brush_chooser);
            brushDialog.setTitle("BRUSH SIZE:");
            brushDialog.show();


            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(smallBrush);
                    drawView.setLastBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(mediumBrush);
                    drawView.setLastBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(largeBrush);
                    drawView.setLastBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });

            /*
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Alert message to be shown");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();*/
        }else if(view.getId()==R.id.draw_btn){
            final Dialog brushDialog = new AppCompatDialog(this);
            brushDialog.setTitle("Brush Width:");
            brushDialog.setContentView(R.layout.brush_chooser);

            final TextView seekTxt = (TextView)brushDialog.findViewById(R.id.brush_txt);
            final SeekBar seekBrush = (SeekBar)brushDialog.findViewById(R.id.brush_seek);

            seekBrush.setMax(100);
            int currBrush = drawView.getBrushSize();
            seekTxt.setText(currBrush + "px");
            seekBrush.setProgress(currBrush);



            seekBrush.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(progress < 5){
                        progress = 5;
                    }
                    seekTxt.setText(Integer.toString(progress) + "px");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });


            Button brushBtn = (Button)brushDialog.findViewById(R.id.brush_ok);

            brushBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(seekBrush.getProgress() > 5) {
                        drawView.setBrushSize(seekBrush.getProgress());
                    }else{
                        drawView.setBrushSize(5);
                    }
                    drawView.setErase(false);
                    opacityBtn.setEnabled(true);
                    opacityBtn.setAlpha(1f);
                    colorwheelBtn.setEnabled(true);
                    colorwheelBtn.setAlpha(1f);

                    brushDialog.dismiss();
                }
            });


            brushDialog.show();

        }else if(view.getId()==R.id.erase_btn){
            final Dialog brushDialog = new AppCompatDialog(this);
            brushDialog.setTitle("Erase Width:");
            brushDialog.setContentView(R.layout.erase_chooser);

            final TextView seekTxt = (TextView)brushDialog.findViewById(R.id.erase_txt);
            final SeekBar seekBrush = (SeekBar)brushDialog.findViewById(R.id.erase_seek);

            seekBrush.setMax(100);
            int currBrush = drawView.getEraseSize();
            seekTxt.setText(currBrush + "px");
            seekBrush.setProgress(currBrush);



            seekBrush.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(progress < 5){
                        progress = 5;
                    }
                    seekTxt.setText(Integer.toString(progress) + "px");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });


            Button brushBtn = (Button)brushDialog.findViewById(R.id.erase_ok);

            brushBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(seekBrush.getProgress() > 5) {
                        drawView.setEraseSize(seekBrush.getProgress());
                    }else{
                        drawView.setEraseSize(5);
                    }
                    drawView.setErase(true);
                    opacityBtn.setEnabled(false);
                    opacityBtn.setAlpha(0.5f);
                    colorwheelBtn.setEnabled(false);
                    colorwheelBtn.setAlpha(.5f);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();
        }else if(view.getId()==R.id.colorwheel_btn){

            AmbilWarnaDialog dialog = new AmbilWarnaDialog(MainActivity.this, drawView.getColor(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();
                    drawView.setColorInt(color);
                }

                @Override
                public void onCancel(AmbilWarnaDialog dialog) {
                    Toast.makeText(getApplicationContext(), "cancel", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();
        }else if(view.getId() == R.id.new_btn){
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New drawing");
            newDialog.setMessage("Start new drawing (you will lose the current drawing)?");

            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            newDialog.show();
        }else if(view.getId()==R.id.save_btn){
            //save drawing
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to device Gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("testing", "I GOT IT!");

                    checkStoragePermissions();

                    /*drawView.setDrawingCacheEnabled(true);
                    Bitmap bm = drawView.getDrawingCache();

                    Log.d("testing", bm.toString());

                    File fPath = Environment.getExternalStorageDirectory();
                    File f = null;
                    f = new File(fPath, "drawPic1.png");

                    Log.d("testing", f.getAbsolutePath());



                        try {
                            FileOutputStream strm = new FileOutputStream(f);
                            bm.compress(Bitmap.CompressFormat.PNG, 80, strm);
                            strm.close();
                            Log.d("testing", "YAS!");
                            MediaStore.Images.Media.insertImage(getContentResolver(), drawView.getDrawingCache(),
                                    UUID.randomUUID().toString() + ".png", "drawing");
                            Log.d("testing", "OKAY LETS GO!");

                            drawView.destroyDrawingCache();
                        } catch (IOException e) {
                            Log.d("testing", "crap");
                            e.printStackTrace();
                        }
                    }*/


                    /*
                    File sdcard = Environment.getExternalStorageDirectory();
                    File mediaDir;
                    if (sdcard != null) {
                        mediaDir = new File(sdcard, "DCIM/Camera");
                        if (!mediaDir.exists()) {
                            mediaDir.mkdirs();
                            Toast savedToast = Toast.makeText(getApplicationContext(),
                                    "HAHAH", Toast.LENGTH_SHORT);
                            savedToast.show();
                        }

                        drawView.setDrawingCacheEnabled(true);

                        verifyStoragePermissions(MainActivity.this);

                        String imgSaved = MediaStore.Images.Media.insertImage(
                                getContentResolver(), drawView.getDrawingCache(),
                                UUID.randomUUID().toString()+".png", "drawing");

                        if(imgSaved!=null){
                            Toast savedToast = Toast.makeText(getApplicationContext(),
                                    "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                            savedToast.show();
                        }
                        else{
                            Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                    "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                            unsavedToast.show();
                        }




                        drawView.destroyDrawingCache();

                    }*/
                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }
        /*else if(view.getId()==R.id.new_btn){
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New drawing");
            newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                   // drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();
        }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //verifyStoragePermissions(this);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);


        drawView = (DoodleView)findViewById(R.id.drawing);
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.paint_pressed, null));

        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        //drawView.setBrushSize(mediumBrush);
        drawView.setBrushSize(50);
        drawView.setEraseSize(50);

        drawBtn = (ImageButton) findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);


        opacityBtn = (ImageButton)findViewById(R.id.opacity_btn);
        opacityBtn.setOnClickListener(this);

        eraseBtn = (ImageButton)findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);

        colorwheelBtn = (ImageButton)findViewById(R.id.colorwheel_btn);
        colorwheelBtn.setOnClickListener(this);

        newBtn = (ImageButton)findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);

        saveBtn = (ImageButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);

    }

    public void savePicture(){
        drawView.setDrawingCacheEnabled(true);
        MediaStore.Images.Media.insertImage(getContentResolver(), drawView.getDrawingCache(),
                    UUID.randomUUID().toString() + ".png", "drawing");
        Log.d("testing", "OKAY LETS GO!");
        drawView.destroyDrawingCache();
    }

    public void checkStoragePermissions(){
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        Log.d("testing", "SO FAR SO GOOD");
        if (permission != PackageManager.PERMISSION_GRANTED) {

            Log.d("testing", "I'm in here!!!");

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE
            );
        }else{
            savePicture();
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {

                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d("testing", "YAY WE ARE HERE");
                    Log.d("testing", permissions.toString());
                    Log.d("testing", grantResults.toString());
                    savePicture();
                }
                return;
            }
            /*case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }*/

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /*
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            // For API 23+ you need to request the read/write permissions even if they are already in your manifest.
            // See: http://developer.android.com/training/permissions/requesting.html
            Log.d("testing", "I'm in here!!!");

            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }*/

    public void paintClicked(View view){
        //use chosen color
        if(view!=currPaint){
            //update color
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            imgView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.paint_pressed, null));
            currPaint.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.paint, null));
            currPaint=(ImageButton)view;
        }
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
                    saveDialog.setTitle("ERASE EVERYTHIBNG YOU LOVE?!");
                    saveDialog.setMessage("DO IT !!!?");
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
