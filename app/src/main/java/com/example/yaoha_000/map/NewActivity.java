package com.example.yaoha_000.map;

import android.os.Bundle;
import android.app.Activity;

public class NewActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.new_activity);
    }
}