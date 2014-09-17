package com.rangiworks.android.widget.radiallibrary.sample.sample;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by sajjad on 8/27/14.
 */
public class FancyRadialLayoutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fancy_radial_layout);
        ViewGroup vg = (ViewGroup)findViewById(R.id.radial_layout);
        vg.setLayoutTransition(new LayoutTransition());
        ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.ic_launcher);
        vg.addView(iv);

    }
}
