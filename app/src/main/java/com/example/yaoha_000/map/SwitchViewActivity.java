package com.example.yaoha_000.map;

import com.example.yaoha_000.map.MyScrollLayout;
import com.example.yaoha_000.map.OnViewChangeListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
public class SwitchViewActivity extends Activity implements OnViewChangeListener, OnClickListener{


    private MyScrollLayout mScrollLayout;
    private ImageView[] mImageViews;
    private int mViewCount;
    private int mCurSel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view);
        Button button = (Button) findViewById(R.id.menuButton);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(SwitchViewActivity.this,
                        MainActivity.class);
                startActivity(myIntent);
            }
        });
        init();
        Log.v("@@@@@@", "this is in  SwitchViewDemoActivity onClick()");
    }

    private void init()
    {
        mScrollLayout = (MyScrollLayout) findViewById(R.id.ScrollLayout);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.llayout);
        mViewCount = mScrollLayout.getChildCount();
        mImageViews = new ImageView[mViewCount];
        for(int i = 0; i < mViewCount; i++)    	{
            mImageViews[i] = (ImageView) linearLayout.getChildAt(i);
            mImageViews[i].setEnabled(true);
            mImageViews[i].setOnClickListener(this);
            mImageViews[i].setTag(i);
        }
        mCurSel = 0;
        mImageViews[mCurSel].setEnabled(false);
        mScrollLayout.SetOnViewChangeListener(this);
        Log.v("@@@@@@", "this is in  SwitchViewDemoActivity init()");
    }

    private void setCurPoint(int index)
    {
        if (index < 0 || index > mViewCount - 1 || mCurSel == index)    	{
            return ;
        }
        mImageViews[mCurSel].setEnabled(true);
        mImageViews[index].setEnabled(false);
        mCurSel = index;
    }

    @Override
    public void OnViewChange(int view) {

        setCurPoint(view);
    }

    @Override
    public void onClick(View v) {
        int pos = (Integer)(v.getTag());
        setCurPoint(pos);
        mScrollLayout.snapToScreen(pos);
    }
}