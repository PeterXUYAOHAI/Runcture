package com.example.yaoha_000.map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by Haoyuan Chen on 2015/11/16.
 */
public class Finish extends AppCompatActivity {

    String dur;
    double dist;
    TextView time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finish);
        Intent i = getIntent();
        time = (TextView)findViewById(R.id.duration);
        dur = i.getStringExtra("duration");
        dist = i.getDoubleExtra("distance", 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        time.setText(
                    "Total Time Spent: " + dur + "\n" +
                    "Total Distance: " + Integer.toString((int)dist) + "km");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_navigate, menu);
        return true;
    }

    /***********************************************************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
