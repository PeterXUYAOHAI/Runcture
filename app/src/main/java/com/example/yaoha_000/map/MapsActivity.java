package com.example.yaoha_000.map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


import javax.net.ssl.HttpsURLConnection;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String inputLocation = null;
    private Geocoder geocoder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);
//        ContextWrapper cw = new ContextWrapper(getApplicationContext());
//        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
//        TextView t = (TextView)findViewById(R.id.textView);
//        t.setText(directory.toString());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng hongkong = new LatLng(22.25, 114.1667);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(hongkong));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(11));
    }


    public void clickRelocated(View view) throws IOException {
        alertD();

    }

    public void changCameraByInput() throws IOException {
        if(inputLocation!=null){

            if(geocoder.getFromLocationName(inputLocation, 1).size()==0)
                inputInvalidAlert();
            else {
                Address address = geocoder.getFromLocationName(inputLocation, 1).get(0);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(address.getLatitude(), address.getLongitude())));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(11));
            }
        }
        else{
            inputInvalidAlert();
        }

    }

    public void inputInvalidAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("The address you input is invalid!");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }
    public void alertD(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input the location");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                inputLocation = input.getText().toString();
                try {
                    changCameraByInput();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void clickZoom(View view) throws IOException {
        View mapFragment = findViewById(R.id.map);
        int zoomNum = Math.round(mMap.getCameraPosition().zoom);
        float height = mapFragment.getHeight();
        float width = mapFragment.getWidth();
        double lat = mMap.getCameraPosition().target.latitude;
        double lng = mMap.getCameraPosition().target.longitude;
        int returnWidth = 640;
        int returnHeight = Math.round((640 / width) * height);

        String urlString = "https://maps.google.com/maps/api/staticmap?center=" + lat + "," + lng + "&zoom=" + (zoomNum + 1) + "&size=" + returnWidth + "x" + returnHeight + "&sensor=false&scale=2";

        Bitmap bmp = getStaticMap(urlString);

        String path = saveToInternalSorage(bmp);

        Intent intent = new Intent(this, DrawOnMap.class);
        intent.putExtra("bitmapPath", path);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        intent.putExtra("zoom", (zoomNum + 1));
        intent.putExtra("returnWidth", returnWidth);
        intent.putExtra("returnHeight", returnHeight);

        startActivity(intent);

    }

    private String saveToInternalSorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory,"storedMap.jpg");
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

    private Bitmap getStaticMap(String urlString) throws IOException {
        Bitmap bmp =  null;
        URL url = new URL(urlString);

        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        InputStream in = conn.getInputStream();

        try {
            in = new BufferedInputStream(conn.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        bmp = BitmapFactory.decodeStream(in);
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmp;
    }

}
