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

/**
 * The majority of the code was inspired from the following tutorials:
 * http://code.tutsplus.com/tutorials/android-sdk-create-a-drawing-app-interface-creation--mobile-19021
 * http://code.tutsplus.com/tutorials/android-sdk-create-a-drawing-app-touch-interaction--mobile-19202
 * http://code.tutsplus.com/tutorials/android-sdk-create-a-drawing-app-essential-functionality--mobile-19328

 * The ability to incorporate the colorpicker github library came from:
 * https://stackoverflow.com/questions/21798694/add-github-library-as-dependency-to-android-studio-project
 *
 * Learning how to properly log came from:
 * https://stackoverflow.com/questions/15425975/creating-a-simple-output-to-logcat
 * https://developer.android.com/intl/zh-tw/reference/android/util/Log.html
 */
public class MainActivity extends AppCompatActivity implements OnClickListener{


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private DoodleView drawView;
    private ImageButton drawBtn, opacityBtn, eraseBtn, colorwheelBtn, newBtn, saveBtn;


    //This code for this method was based on this:
    // http://code.tutsplus.com/tutorials/android-sdk-create-a-drawing-app-essential-functionality--mobile-19328
    @Override
    public void onClick(View view){//respond to clicks


        //Opacity button is clicked

        // Help for theses sections (especially seekbars) came from:
        // http://javatechig.com/android/android-seekbar-example
        // http://www.thaicreate.com/mobile/android-seekbar-alertdialog-popup.html
        // http://code.tutsplus.com/tutorials/android-sdk-create-a-drawing-app-essential-functionality--mobile-19328
        // https://stackoverflow.com/questions/8807569/how-to-display-some-value-on-seekbar-as-default
        if(view.getId()==R.id.opacity_btn){

            // The opacity functionality came from
            // http://code.tutsplus.com/tutorials/android-sdk-drawing-with-opacity--mobile-19682

            //Dialog help/assistance came from
            //https://androidresearch.wordpress.com/2012/04/16/creating-and-displaying-a-custom-dialog-in-android/
            //http://www.cs.dartmouth.edu/~campbell/cs65/lecture13/lecture13.html
            //https://developer.android.com/intl/zh-tw/guide/topics/ui/dialogs.html
            //https://developer.android.com/intl/zh-tw/reference/android/support/v7/app/AppCompatDialog.html
            //https://stackoverflow.com/questions/32405042/android-dialogfragment-title-not-showing
            final Dialog seekDialog = new AppCompatDialog(this);
            seekDialog.setTitle("Opacity level:");
            seekDialog.setContentView(R.layout.opacity_chooser);

            final TextView seekTxt = (TextView)seekDialog.findViewById(R.id.opq_txt);
            final SeekBar seekOpq = (SeekBar)seekDialog.findViewById(R.id.opacity_seek);

            seekOpq.setMax(100);
            int currLevel = drawView.getPaintAlpha();
            seekTxt.setText(currLevel+"%");
            seekOpq.setProgress(currLevel);

            //Actions to take as the seekbar changes
            seekOpq.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (progress < 5) {
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

            //Once button is clicked set the opacity level appropriately.
            Button opqBtn = (Button)seekDialog.findViewById(R.id.opq_ok);

            opqBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    //Set a minimum opacity level 5%
                    if(seekOpq.getProgress()  > 5) {
                        drawView.setPaintAlpha(seekOpq.getProgress());
                    }else{
                        drawView.setPaintAlpha(5);
                    }
                    seekDialog.dismiss();
                }
            });

            seekDialog.show();

        //The button involved with brush size is clicked.
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

                //Setting minimum for progress bar came from
                // https://stackoverflow.com/questions/3490951/how-to-limit-seekbar
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
                    //When the button is clicked we turn off erasing mode
                    //(assuming it is turned on in the first place).

                    //Enabling and disabling came from
                    // https://stackoverflow.com/questions/8743120/how-to-grey-out-a-button
                    // https://stackoverflow.com/questions/4384890/how-to-disable-an-android-button
                    drawView.setErase(false);
                    opacityBtn.setEnabled(true);
                    opacityBtn.setAlpha(1f);
                    colorwheelBtn.setEnabled(true);
                    colorwheelBtn.setAlpha(1f);

                    brushDialog.dismiss();
                }
            });


            brushDialog.show();

        //The section devoted to when the erase button is clicked. Turns
        // the doodle into erase mode! Like draw mode, users can select
        // the size of the eraser as well.
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
                    //Go into eraser mode and disable the opacity and
                    //colorwheel buttons (so the user knows that they are
                    //in a different mode). To exit erase mode, they need
                    //to click on the draw button (e.g. the button that
                    //changes the brush side).
                    drawView.setErase(true);
                    opacityBtn.setEnabled(false);
                    opacityBtn.setAlpha(0.5f);
                    colorwheelBtn.setEnabled(false);
                    colorwheelBtn.setAlpha(0.5f);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();
            //The colorwheel button is clicked; this allows the user to
            //select the color of the line they draw. The dialog
            //is taken from https://github.com/yukuku/ambilwarna and this example within
            //that code:
            // https://github.com/yukuku/ambilwarna/blob/master/demo/src/main/java/yuku/ambilwarna/demo/AmbilWarnaDemoActivity.java
        }else if(view.getId()==R.id.colorwheel_btn){

            AmbilWarnaDialog dialog = new AmbilWarnaDialog(MainActivity.this, drawView.getColor(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    Toast.makeText(getApplicationContext(), "Color changed", Toast.LENGTH_SHORT).show();
                    drawView.setColorInt(color);
                }

                @Override
                public void onCancel(AmbilWarnaDialog dialog) {
                    Toast.makeText(getApplicationContext(), "Color NOT changed", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();
            //Button that clears the drawing. This section calls the startNew() function
            //in DoodleView to clear the canvas. Of course, it make sures to ask
            //users to confirm that they want to clear the canvas.
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
            //The button that saves an image to the gallery.
        }else if(view.getId()==R.id.save_btn){
            //save drawing
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to device Gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("testing", "I GOT IT!");
                    //This method handles the permission and ultimately calls
                    //savePicture()
                    checkStoragePermissions();
                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }
    }

    /**
     * This method makes sure to set onClickListeners on all
     * the relevant buttons involved in the doodle app. The onClick
     * functions are defined above.
     *
     * This method again was based on:
     * http://code.tutsplus.com/tutorials/android-sdk-create-a-drawing-app-essential-functionality--mobile-19328
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawView = (DoodleView)findViewById(R.id.drawing);
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

    /***
     * The following three functions were created through help
     * by Dr. Froehlich and the following sources:
     * https://developer.android.com/intl/zh-tw/training/permissions/requesting.html#perm-check
     * http://android-er.blogspot.com/2015/04/save-bitmap-to-storage.html
     * https://stackoverflow.com/questions/34597367/how-to-save-bitmap-image-into-my-gallery-folder-in-android
     * https://stackoverflow.com/questions/4646913/android-how-to-use-mediascannerconnection-scanfile
     * http://code.tutsplus.com/tutorials/android-sdk-create-a-drawing-app-essential-functionality--mobile-19328
     * https://stackoverflow.com/questions/22610699/getdrawingcache-always-returns-the-same-bitmap
     * http://inthecheesefactory.com/blog/things-you-need-to-know-about-android-m-permission-developer-edition/en
     * https://github.com/googlesamples/android-RuntimePermissions
     * https://stackoverflow.com/questions/23527767/open-failed-eacces-permission-denied
     * https://stackoverflow.com/questions/33030933/android-6-0-open-failed-eacces-permission-denied
     */

    /**
     * This function saves the current bitmap/image/picture
     * to the gallery. This is only called when we have
     * proper permissions to access the storage.
     */
    public void savePicture(){
        drawView.setDrawingCacheEnabled(true);
        MediaStore.Images.Media.insertImage(getContentResolver(), drawView.getDrawingCache(),
                    UUID.randomUUID().toString() + ".png", "drawing");
        Log.d("testing", "OKAY LETS GO!");
        drawView.destroyDrawingCache();
    }

    /**
     * This function makes sure to check we have
     * Storage permission to save our files. If
     * permission is already given then savePicture is called
     * automatically.
     */
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

    /**
     * Based on the choice in checkStoragePermissions, we can then
     * call savePicture() to save the image on the canvas to the gallery.
     * If permissions are not given, no savePictures are given.
     */
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("testing", "YAY WE ARE HERE");
                    Log.d("testing", permissions.toString());
                    Log.d("testing", grantResults.toString());
                    savePicture();
                }
                return;
            }
        }
    }
}
