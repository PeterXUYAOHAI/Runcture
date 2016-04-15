package com.example.yaoha_000.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by yaoha_000 on 11/8/2015.
 */
class DrawingView extends View {
    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private int paintColor = 0xFF660000;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private Bitmap bmp;
    private int imageWidth;
    private int imageHeight;
    private double scaleRate;
    Paint paint;
    private ArrayList<String> coord;
    private int w;
    private int h;



    private String path;
    String e;
    Paint textPaint;

    public DrawingView(Context context, AttributeSet attrs) throws IOException {
        super(context, attrs);
        setupDrawing();
        path=((DrawOnMap)getContext()).getPath();
        bmp = loadImageFromStorage(path);
    }

    private void setupDrawing() throws IOException {
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(35);
        textPaint.setColor(Color.BLACK);
        coord = new ArrayList<>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.w =w;
        this.h=h;
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bmp, 0, 0, canvasPaint);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                coord.add(touchX/scaleRate + "," + touchY/scaleRate);
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

    private Bitmap loadImageFromStorage(String path) {
        try {
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            File f = new File(path, "storedMap.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imageWidth = displayMetrics.widthPixels;
            scaleRate = (double)imageWidth/b.getWidth();
            imageHeight = imageWidth*(int)((double)b.getHeight()/b.getWidth());
            return Bitmap.createScaledBitmap(b, imageWidth,  imageHeight, false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void reset() throws IOException {

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        setupDrawing();
        invalidate();
    }

    public void changColor(int color){
        drawPaint.setColor(color);
    }

    public void changThickness(int thickness){
        drawPaint.setStrokeWidth(thickness);
    }


    public ArrayList<String> getCoord() {
        return coord;
    }

}
