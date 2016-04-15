package com.example.yaoha_000.map;
/**
 * Created by Haoyuan Chen on 2015/11/4.
 */

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class Navigate extends AppCompatActivity implements SensorEventListener, LocationListener {

    private ArrayList<Coordinate> aryOfCoord = new ArrayList<Coordinate>();

    private SensorManager mySensorManager;
    private Sensor aSensor;
    private Sensor mSensor;
    private LocationManager myLocationManager;
    private Location location;

    float[] accelerometerValues = new float[3];
    float[] magneticFieldValues = new float[3];

    private String locationProvider;

    Coordinate currentPosition, nextPostion;
    float currentDegree=0f;

    long startTime = 0;
    long endTime = 0;
    Coordinate startLocation;
    double distance;
    int theFirstPoint = 0;

    TextView orientation;
    TextView disToNext;
    TextView percentageOfTotal;
    ImageView arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_navigate);

        //get intent from Peter's code
        Intent i = getIntent();
        distance = (double)i.getDoubleExtra("distance", 0);
        ArrayList<String> track = (ArrayList<String>)i.getStringArrayListExtra("coord");

        for(int m=0; m<track.size(); m++){
            String[] a =track.get(m).split(",");
            aryOfCoord.add(new Coordinate(Double.parseDouble(a[1]), Double.parseDouble(a[0])));
        }
        startLocation = aryOfCoord.get(0);


        //Start my code
        arrow = (ImageView)findViewById(R.id.arrow);
        orientation = (TextView)findViewById(R.id.orientation);
        disToNext = (TextView)findViewById(R.id.disToNext);
        percentageOfTotal = (TextView)findViewById(R.id.percentageOfTotal);

        //set up sensor
        setUpSensor();
        setUpSensorListener();

        //set up GPS and get the current location
        setUpLocation();
        prepareCurrentLocation();

        //set up next position
        prepareNextLocation();

                double dis = DrawOnMap.distance(nextPostion.getLat(),nextPostion.getLon(),currentPosition.getLat(),currentPosition.getLon(),"K");
                dis = dis * 1000f;

                if(theFirstPoint > 0) {
                    disToNext.setText(  "Distance to next checkpoint is "  + (int)dis + " meters.");
                    percentageOfTotal.setText("Percentage of completion is " +
                            ((int)
                                    ((DrawOnMap.distance(startLocation.getLat(), startLocation.getLon(), currentPosition.getLat(), currentPosition.getLon(), "K") / distance) * 100f))
                                    / 100f
                            + "%");
                }else {
                    percentageOfTotal.setText("Please go to the starting point.");
                    disToNext.setText("Please go to the starting point.");
                };
    }

    @Override
    protected void onStart() {
        super.onStart();
            //update coordinates and degree on z-axis
            calculateOrientation();
            }



    @Override
    protected void onResume() {
        super.onResume();
        try {myLocationManager.requestLocationUpdates(locationProvider, 0, 0, this);}catch (SecurityException e){e.printStackTrace();}
        try{
            location = myLocationManager.getLastKnownLocation(locationProvider);
            currentPosition = new Coordinate(location.getLongitude(), location.getLatitude());
        }catch (SecurityException e){e.printStackTrace();}
        mySensorManager.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mySensorManager.unregisterListener(this);
        if(myLocationManager!=null){
            try{myLocationManager.removeUpdates(this);}catch(SecurityException e){e.printStackTrace();}
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /***********************************************************************************/
    //Listener
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            magneticFieldValues = sensorEvent.values;
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            accelerometerValues = sensorEvent.values;


        calculateOrientation();
    }


    @Override
    public void onLocationChanged(Location location){
        try {myLocationManager.requestLocationUpdates(locationProvider, 0, 0, this);}catch (SecurityException e){e.printStackTrace();}
        currentPosition = new Coordinate(location.getLongitude(), location.getLatitude());
        /*currentPos.setText( currentPosition.toString() + locationProvider );*/
        double dis = DrawOnMap.distance(nextPostion.getLat(),nextPostion.getLon(),currentPosition.getLat(),currentPosition.getLon(),"K");
        if(theFirstPoint > 0) {
            disToNext.setText(  "Distance to next checkpoint is "  + (int)dis + " meters.");
            percentageOfTotal.setText("Percentage of completion is " +
                    ((int)
                            ((DrawOnMap.distance(startLocation.getLat(), startLocation.getLon(), currentPosition.getLat(), currentPosition.getLon(), "K") / distance) * 100f))
                            / 100f
                    + "%");
        }else {
            percentageOfTotal.setText("Please go to the starting point.");
            disToNext.setText("Please go to the starting point.");
        };
        verifyLocation();
        calculateOrientation();
    }


    /***********************************************************************************/
    //auxiliary function defined by me
    private void setUpSensor(){
        mySensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        aSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }

    private void setUpSensorListener(){
        mySensorManager.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void setUpLocation(){
        myLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(myLocationManager == null){
            return;
        }
        List<String> providers = myLocationManager.getProviders(true);
        if(providers.contains(LocationManager.NETWORK_PROVIDER)){
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }else if(providers.contains(LocationManager.GPS_PROVIDER)){
            locationProvider = LocationManager.GPS_PROVIDER;
        }else{

            return ;
        }
        try {myLocationManager.requestLocationUpdates(locationProvider, 0, 0, this);}catch (SecurityException e){e.printStackTrace();}
    }

    private void prepareCurrentLocation(){
        try{
            location = myLocationManager.getLastKnownLocation(locationProvider);
            currentPosition = new Coordinate(location.getLongitude(), location.getLatitude());
        }catch (SecurityException e){e.printStackTrace();}
    }

    private void prepareNextLocation() {
        nextPostion = new Coordinate(aryOfCoord.get(0).getLon(),aryOfCoord.get(0).getLat());
    }

    public boolean comparePos (Coordinate posA, Coordinate posB)
    {
        boolean equal = false;
        if (((int)(posA.getLat()*1000f))/1000f == ((int)(posB.getLat()*1000f))/1000f
                && ((int)(posA.getLon()*1000f))/1000f == ((int)(posB.getLon()*1000f))/1000f
                ){
            equal = true;
        }
        return equal;
    }

    private void verifyLocation(){
        if(this.comparePos(currentPosition, nextPostion)) {
            if(theFirstPoint == 0)
            {
                startTime = System.currentTimeMillis();
            }
            theFirstPoint = theFirstPoint + 1;
            aryOfCoord.remove(0);
            if (aryOfCoord.isEmpty()==true)
            {
                endTime = System.currentTimeMillis();
                String duration = formatTime(endTime - startTime);
                //pass to the last page
                Intent i = new Intent(this,Finish.class);
                i.putExtra("duration", duration);
                i.putExtra("distance", distance);
                startActivity(i);
            }
            if (aryOfCoord.isEmpty()==false) {
                nextPostion.setCoord(aryOfCoord.get(0).getLon(), aryOfCoord.get(0).getLat());
            }
        }
    }


    private float calculateOrientation() {

        float[] values = new float[3];
        float[] R = new float[9];
        float angle;
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(R, values);
        angle = (float) Math.toDegrees(values[0]);
        if(angle > -10f && angle < 10f)
            orientation.setText("Your Current Direction is: North");
        else if (angle > -100f && angle < -80f)
            orientation.setText("Your Current Direction is: West");
        else if (angle > 80f && angle < 100f)
            orientation.setText("Your Current Direction is: East");
        else if (angle > 170f || angle < -180f)
            orientation.setText("Your Current Direction is: South");
        else if (angle > -80f && angle < -10f)
            orientation.setText("Your Current Direction is: Northwest");
        else if (angle > -170f && angle < 100f)
            orientation.setText("Your Current Direction is: Southest");
        else if (angle < 80f && angle > 10f)
            orientation.setText("Your Current Direction is: Northeast");
        else if (angle > 100f && angle < 170f)
            orientation.setText("Your Current Direction is: Southeast");
        computeDegree(currentPosition, nextPostion, angle);
        return values[0];
    }

   private boolean computeDegree(Coordinate c, Coordinate n, float d){
        float degree = 0f;
        double tan = (n.getLat()- c.getLat())/(n.getLon()-c.getLon());
        float degreeToNext = (float)Math.toDegrees(Math.atan(tan));
        if((n.getLat()- c.getLat())<0 && (n.getLon()-c.getLon())>0){
            degree = (float)(d + degreeToNext - 90f);
        }
        else if((n.getLat()- c.getLat())>0 && (n.getLon()-c.getLon())<0){
            degree = (float)(d + degreeToNext + 90f);
        }
        else if((n.getLat()- c.getLat())>0 && (n.getLon()-c.getLon())>0) {
            degree = (float) (d + degreeToNext - 90f);
        }
        else if((n.getLat()- c.getLat())<0 && (n.getLon()-c.getLon())<0) {
            degree = (float)(d + degreeToNext + 90f);
        }

        rotateArrow(arrow, degree);
        return true;
    }

    private boolean rotateArrow(ImageView arrow, float degree){
        boolean flag = true;
        RotateAnimation ra = new RotateAnimation(currentDegree, -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        ra.setDuration(200);
        ra.setFillAfter(true);
        arrow.startAnimation(ra);
        currentDegree = -degree;
        return  flag;
    }
    /***********************************************************************************/
    //Method for timing

    protected String formatTime(long t){
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        return sdf.format(t);
    }


    /***********************************************************************************/
    //Other override functions
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    @Override
    public void onStatusChanged(String provider, int status, Bundle arg2) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onProviderDisabled(String provider) {}

    /***********************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
