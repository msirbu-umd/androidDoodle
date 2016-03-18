package com.example.mms.doodleexample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;

import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * Created by MMS on 3/6/2016.
 */
public class DoodleView extends View {

    private Paint _paintDoodle = new Paint();
    private Path _path = new Path();

    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF660000;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;


    private int lastColor = -1;
    private int paintAlpha = 255;
    private int brushSize = 50;
    private int eraseSize = 50;

    private boolean erase=false;

    public DoodleView(Context context) {
        super(context);
        init(null, 0);
    }

    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DoodleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);

    }

    /**
     * These three functions are used when the user selects to erase
     * the canvas. Like brush size, users will be able to set the size
     * of the eraser as well!
     */
    public void setErase(boolean isErase){
        //set erase true or false
        erase=isErase;
        if(erase){
            lastColor = getColor();
            drawPaint.setColor(Color.WHITE);
            //drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        else{
            //Use paintColor when we are not coming back from "erase mode."
            if(lastColor == -1) {
                drawPaint.setColor(paintColor);
            }else{
                drawPaint.setColor(lastColor);
                lastColor = -1;
            }
            drawPaint.setXfermode(null);
        }
    }

    public void setEraseSize(int newSize){
        eraseSize = newSize;
        drawPaint.setStrokeWidth(eraseSize);
    }

    public int getEraseSize(){
        return eraseSize;
    }

    /***
     * Functions used to set and get the alpha levels of the canvas.
     * This is used to set opacity.
     */
    public int getPaintAlpha(){
        return Math.round((float)paintAlpha/255*100);
    }

    public void setPaintAlpha(int newAlpha){
        paintAlpha=Math.round((float)newAlpha/100*255);
        drawPaint.setColor(paintColor);
        drawPaint.setAlpha(paintAlpha);
    }

    /**
     * These two functions are used to set and get the brush size
     * on the canvas
     */
    public void setBrushSize(int newSize){
        brushSize = newSize;
        drawPaint.setStrokeWidth(brushSize);
    }

    public int getBrushSize(){
        return brushSize;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        float touchX = motionEvent.getX();
        float touchY = motionEvent.getY();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    /**
     * Two methods to set the color of the drawingCanvas
     * allowing you to pass in a color as a string or an int.
     */
    public void setColor(String newColor){
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
        drawPaint.setAlpha(paintAlpha);
    }

    public void setColorInt(int newColor){
        invalidate();
        paintColor = newColor;
        drawPaint.setColor(paintColor);
        drawPaint.setAlpha(paintAlpha);
    }

    public int getColor(){
        return paintColor;
    }


    /**
     * Function used to clear the canvas and start afresh!
     */
    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

}
