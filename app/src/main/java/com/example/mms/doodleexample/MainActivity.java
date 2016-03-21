package com.example.mms.doodleexample;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
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

    private static int RESULT_LOAD_IMG = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_EXTERNAL_OPEN_STORAGE = 2;
    private DoodleView drawView;
    private ImageButton drawBtn, opacityBtn, eraseBtn, colorwheelBtn, newBtn, saveBtn, openBtn;
    String imgDecodableString;

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

                    Toast.makeText(getApplicationContext(), "Erase Mode -- Color & Opacity Disabled",
                            Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Click Brush to return to Doodle Mode",
                            Toast.LENGTH_LONG).show();


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

                    //This method saves the current bitmap to the gallery. See
                    //corresponding method for more details. NOTE: Permission are checked BEFORE a user even interacts
                    //with the app so if the user doesn't give us permission this button is disabled.
                    savePicture();
                    //checkStoragePermissions();
                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            saveDialog.show();
            //This button opens an image from the gallery. See corresponding method for
            //more details. NOTE: Permission are checked BEFORE a user even interacts
            //with the app so if the user doesn't give us permission this button is disabled.
        }else if(view.getId()==R.id.open_btn){
            loadImagefromGallery(drawView);
            //checkOpenStoragePermissions();
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

        openBtn = (ImageButton)findViewById(R.id.open_btn);
        openBtn.setOnClickListener(this);

        //Permissions are now checked as soon as the app is created. This is because
        //of a bug where the app must be restarted in order for loading an image
        //from a gallery is allowed, even after the user grants permission.
        //See: https://stackoverflow.com/questions/32699129/android-6-0-needs-restart-after-granting-user-permission-at-runtime
        //and https://stackoverflow.com/questions/33062006/cant-write-to-external-storage-unless-app-is-restarted-after-granting-permissio
        checkStoragePermissions();
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
     * proper permissions to access the storage. This method was
     */
    public void savePicture(){
        drawView.setDrawingCacheEnabled(true);
        String imgSaved = MediaStore.Images.Media.insertImage(getContentResolver(), drawView.getDrawingCache(),
                    UUID.randomUUID().toString() + ".png", "drawing");

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
    }

    /**
     * This function makes sure to check we have
     * Storage permission to save our file or open an image from the gallery. If
     * permission is granted then nothing else is done.
     */
    public void checkStoragePermissions(){
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    /**
     * Based on the choice in checkStoragePermissions, we can either allow
     * the user to save their image (or load images) into the app (because they
     * have given us permission) or we disable those buttons as they are not
     * allowed.
     */
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //This is a "hack" to restart the app in order to make sure
                    //the permissions are "on" for the read and write to external storage.
                    //This was taken because of research found here:
                    //https://stackoverflow.com/questions/32699129/android-6-0-needs-restart-after-granting-user-permission-at-runtime
                    //https://stackoverflow.com/questions/33062006/cant-write-to-external-storage-unless-app-is-restarted-after-granting-permissio
                    android.os.Process.killProcess(android.os.Process.myPid());

                }else{
                    //If permission is denied you can disable the buttons as trying
                    //to read or write to external storage will return an error!
                    openBtn.setEnabled(false);
                    saveBtn.setEnabled(false);
                    openBtn.setAlpha(0.5f);
                    saveBtn.setAlpha(0.5f);

                    Toast.makeText(getApplicationContext(), "Permission Denied\nSaving and Loading Images Disabled",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    /**
     * These two functions were created to open an image from the gallery.
     * Code for this method was based on the following sources:
     * http://programmerguru.com/android-tutorial/how-to-pick-image-from-gallery/
     * http://tjkannan.blogspot.in/2012/01/load-image-from-camera-or-gallery.html
     * https://stackoverflow.com/questions/14174104/how-to-set-a-bitmap-image-in-canvas-of-my-custom-view
     * http://viralpatel.net/blogs/pick-image-from-galary-android-app/
     */
    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                //String filePath = cursor.getString(columnIndex);
                Bitmap b = BitmapFactory.decodeFile(imgDecodableString);

                //Important info from:
                //https://stackoverflow.com/questions/26016655/draw-with-a-canvas-over-an-image-in-android-java
                //and http://sudarnimalan.blogspot.com/2011/09/android-convert-immutable-bitmap-into.html
                //https://stackoverflow.com/questions/5176441/drawable-image-on-a-canvas
                Bitmap mb = b.copy(Bitmap.Config.ARGB_8888, true);

                drawView = (DoodleView)findViewById(R.id.drawing);
                drawView.loadBitmap(mb);

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }
}
