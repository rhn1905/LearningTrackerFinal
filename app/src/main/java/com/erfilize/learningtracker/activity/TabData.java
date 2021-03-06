package com.erfilize.learningtracker.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.erfilize.learningtracker.R;

/**
 * The TabData class is used to setup the Tab Fragments and utilizes the SectionsPageAdapter class.
 * Based on the <a href=https://www.youtube.com/watch?v=bNpWGI_hGGg&user=UCoNZZLhPuuRteu02rh7bzsw>Android Tab Tutorial by Mitch Tabian on YouTube</a>
 */

public class TabData extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_visualizer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Log.d(TAG, "onCreate: Starting.");

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout  = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1Fragment(),getResources().getString(R.string.tab_charts));
        adapter.addFragment(new Tab2Fragment(),getResources().getString(R.string.tab_xapi));

        viewPager.setAdapter(adapter);
    }
}
