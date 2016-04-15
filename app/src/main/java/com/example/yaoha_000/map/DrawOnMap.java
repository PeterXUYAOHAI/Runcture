package com.example.yaoha_000.map;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by yaoha_000 on 11/6/2015.
 */
public class DrawOnMap extends Activity{

        private String path;
        private double lat;
        private double lng;
        private int zoom;
        private double returnWidth;
        private double returnHeight;
        private DrawingView v;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            Intent intent = getIntent();

            path = intent.getStringExtra("bitmapPath");
            lat = intent.getDoubleExtra("lat", 0);
            lng = intent.getDoubleExtra("lng", 0);
            zoom = intent.getIntExtra("zoom", 0);
            returnWidth = intent.getIntExtra("returnWidth",0)*2;
            returnHeight =  intent.getIntExtra("returnHeight",0)*2;

            setContentView(R.layout.draw_on_map);
            v = (DrawingView)findViewById(R.id.drawView);
        }

    public void onClick(View view){
        ArrayList<String> r = rountConvert(v.getCoord());
        double distance = calculateDistance(r);
        Intent intent = new Intent(this, Navigate.class);
       saveToInternalSorage(loadBitmapFromView(this, v));
        intent.putStringArrayListExtra("coord", r);
        intent.putExtra("distance",distance);
        startActivity(intent);
    }

    private double calculateDistance(ArrayList<String> r){
        double totalDis = 0;
        for (int i=0; i<(r.size()-2);i++){
            String[] first = r.get(i).split(",");
            String[] second = r.get(i+1).split(",");
            totalDis +=distance(Double.parseDouble(first[0]),
                    Double.parseDouble(first[1]),
                    Double.parseDouble(second[0]),
                    Double.parseDouble(second[1]), "K");
        }
        return totalDis;
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }

        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public ArrayList<String> rountConvert(ArrayList<String> pixels){
        GetConner getConner = new GetConner();
        double[] SW = getConner.getSWCorners(lat, lng, zoom, returnWidth, returnHeight);
        double[] NE = getConner.getNECorners(lat, lng, zoom, returnWidth, returnHeight);
        double[] NW = {SW[0], NE[1]};
        double latPerPixel = (NE[0]-SW[0])/returnWidth;
        double lngPerPixel = (SW[1]-NE[1])/returnHeight;

        ArrayList<String> route = new ArrayList<>();
        for (String e: pixels){
            String[] coordS = e.split(",");
            String result = (Double.parseDouble(coordS[0])*latPerPixel+NW[0])+","+(Double.parseDouble(coordS[1])*lngPerPixel+NW[1]);
            route.add(result);
        }
        return route;
    }

    public void changeColor(View view){
        ColorDrawable buttonColor = (ColorDrawable) view.getBackground();
        int colorId = buttonColor.getColor();
        v.changColor(colorId);
    }

    public void changeThickness(View view){
         Button b = (Button)view;
        int thickness = Integer.parseInt(b.getText().toString().split(" ")[0]);
        v.changThickness(thickness);
    }

    public void reset(View view) throws IOException {
        v.reset();
    }
    public Bitmap loadBitmapFromView(Context context, View v) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        v.measure(View.MeasureSpec.makeMeasureSpec(dm.widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(dm.heightPixels, View.MeasureSpec.EXACTLY));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        Bitmap returnedBitmap = Bitmap.createBitmap(v.getMeasuredWidth(),
                v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(returnedBitmap);
        v.draw(c);

        return returnedBitmap;
    }

    private String saveToInternalSorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("savedDrawDir", Context.MODE_PRIVATE);
        int fileNum = (int)Math.random()*100;
        File myPath = new File(directory,"storedDrawing"+fileNum+".jpg");
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(myPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }


    public String getPath(){
        return path;
    }

    protected class GetConner
    {
        private int MERCATOR_RANGE = 256;
        private double pointOriginX = MERCATOR_RANGE/2;
        private double pointOriginY = MERCATOR_RANGE/2;
        private double pixelsPerLonDegree_=MERCATOR_RANGE/360.0;
        private double pixelsPerLonRadian_=MERCATOR_RANGE/(2*Math.PI);

        public double[]  getSWCorners ( double centerLat, double centerLng, int zoom,
        double mapWidth, double mapHeight){
            double scale = Math.pow(2, zoom);
            double[] centerPx = fromLatLngToPoint(centerLat, centerLng);
            double[] SWPoint = {(centerPx[0] - (mapWidth / 2) / scale), (centerPx[1] + (mapHeight / 2) / scale)};
            double[] SWLatLon = fromPointToLatLng(SWPoint[0], SWPoint[1]);
            return SWLatLon;
        }

        public double[]  getNECorners ( double centerLat, double centerLng, int zoom,
            double mapWidth, double mapHeight){
            double scale = Math.pow(2, zoom);
            double[] centerPx = fromLatLngToPoint(centerLat, centerLng);
            double[] NEPoint = {(centerPx[0] + (mapWidth / 2) / scale), (centerPx[1] - (mapHeight / 2) / scale)};
            double[] NELatLon = fromPointToLatLng(NEPoint[0], NEPoint[1]);
            return NELatLon;
        }


        public double  degreesToRadians(double deg) {
            return deg * (Math.PI / 180);
        }

        public double  radiansToDegrees(double rad) {
            return rad / (Math.PI / 180);
        }

        public double[]  fromLatLngToPoint(double lat, double lng) {
            double pointX = 0;
            double pointY = 0;
            double originX = this.pointOriginX;
            double originY = this.pointOriginY;
            pointX = originX + lng * pixelsPerLonDegree_;
            double sinY = bound(Math.sin(degreesToRadians(lat)), -0.9999, 0.9999);
            pointY = originY + 0.5 * Math.log((1 + sinY) / (1 - sinY)) * -pixelsPerLonRadian_;
            double[] result = {pointX, pointY};
            return result;
        }

        public double[] fromPointToLatLng(double x, double y) {
            double originX = pointOriginX;
            double originY = pointOriginY;
            double lng = (x - originX) / pixelsPerLonDegree_;
            double latRadians = (y - originY) / -pixelsPerLonRadian_;
            double lat = radiansToDegrees(2 * Math.atan(Math.exp(latRadians)) - Math.PI / 2);
            double[] result = {lat, lng};
            return result;
        }

        public double bound(double value, double opt_min, double opt_max) {
            if (opt_min != 0) value = Math.max(value, opt_min);
            if (opt_max != 0) value = Math.min(value, opt_max);
            return value;
    }
};
}










