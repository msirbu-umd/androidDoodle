package com.example.mms.doodleexample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;
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

public class MainActivity extends AppCompatActivity implements OnClickListener {


    private DoodleView drawView;
    private ImageButton currPaint, drawBtn, opacityBtn, eraseBtn, colorwheelBtn, newBtn, saveBtn;
    private float smallBrush, mediumBrush, largeBrush;
    int color = 0xffffff00;

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
                    //save drawing


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

                        String fname = UUID.randomUUID().toString() + ".png";
                        File file = new File (mediaDir, fname);
                        if (file.exists ()){
                            Toast savedToast = Toast.makeText(getApplicationContext(),
                                    "blah", Toast.LENGTH_SHORT);
                            savedToast.show();

                            file.delete ();
                        }

                        try {
                            FileOutputStream out = new FileOutputStream(file); //from here it goes to catch block
                            drawView.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 90, out);
                            out.flush();
                            out.close();
                            String[] paths = {file.toString()};
                            String[] mimeTypes = {"/image/png"};
                            MediaScannerConnection.scanFile(MainActivity.this, paths, mimeTypes, null);

                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }





                    /*
                    String imgSaved = MediaStore.Images.Media.insertImage(
                            MainActivity.this.getContentResolver(), drawView.getDrawingCache(),
                            UUID.randomUUID().toString() + ".png", "drawing");

                    if(imgSaved != null) {
                        Toast savedToast = Toast.makeText(getApplicationContext(),
                                "blah", Toast.LENGTH_SHORT);
                        savedToast.show();
                    }else{
                        Toast savedToast = Toast.makeText(getApplicationContext(),
                                "blah2", Toast.LENGTH_SHORT);
                        savedToast.show();
                    }
                    */

                    drawView.destroyDrawingCache();


                    /*
                    drawView.setDrawingCacheEnabled(true);
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
                    drawView.destroyDrawingCache();*/
                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
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


}
