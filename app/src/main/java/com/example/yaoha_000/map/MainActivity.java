package com.example.yaoha_000.map;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class MainActivity extends Activity {
    Button button1,button2,button3, button4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = (Button) findViewById(R.id.NewButton);

        button1.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(MainActivity.this,
                        MapsActivity.class);
                startActivity(myIntent);
            }
        });

        button2 = (Button) findViewById(R.id.PastButton);

        button2.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Intent i;
                i = new Intent(MainActivity.this,GalleryActivity.class);
                startActivity(i);
            }
        });

        button3 = (Button) findViewById(R.id.HelpButton);

        button3.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("About Us");
                dialog.setMessage("Copyright@2016 by"+"\n" + "WANG Haoying 53545289"+"\n"+"XU Yaohai 53546028"+"\n"+"CHEN Haoyuan 53063079");
                dialog.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener(){
                            public void onClick(
                                    DialogInterface dialoginterface, int i){
                            }
                        });
                dialog.show();
            }
        });

        button4 = (Button) findViewById(R.id.Help);
        button4.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Intent i = new Intent(MainActivity.this,SwitchViewActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
