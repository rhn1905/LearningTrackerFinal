package com.erfilize.learningtracker.activity;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.erfilize.learningtracker.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * The Tab1Fragment class visualizes the time spent on all learning relevant URLs in a Pie Chart.
 * Visualizer powered by <a href=https://github.com/PhilJay/MPAndroidChart>MPAndroidChart by Philipp Jahoda.</a>
 */

public class Tab1Fragment extends Fragment {
    private static final String TAG = "Tab1Fragment";



    public static final int[] ChartColors = {
            Color.rgb(85, 96, 128), Color.rgb(240, 120, 90), Color.rgb(240, 196, 25),
            Color.rgb(113, 194, 133), Color.rgb(136, 73, 143), Color.rgb(255, 0, 0),
            Color.rgb(33, 145, 251), Color.rgb(158, 228, 147), Color.rgb(65, 34, 52)
    };

    public static Tab1Fragment newInstance(){
        return new Tab1Fragment();
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment, container, false);

        DataBaseHelper myDB = new DataBaseHelper(view.getContext());

        ArrayList<Float> urlTime = new ArrayList<>();
        ArrayList<String> urlHost = new ArrayList<>();

        Cursor urlData = myDB.getGraphData();

        if (urlData.getCount() == 0) {
            Toast.makeText(view.getContext(), "No data available", Toast.LENGTH_LONG).show();
        } else {
            while (urlData.moveToNext()) {
                urlHost.add(urlData.getString(1)); //0
                urlTime.add(urlData.getFloat(2)/60); //1
                Log.i(TAG,"urlData[0]: "+urlData.getInt(1));
                Log.i(TAG,"urlData[1]: "+urlData.getString(0));
            }
        }
        // setup pie chart
        List<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < urlTime.size(); i++) {
            pieEntries.add(new PieEntry(urlTime.get(i), urlHost.get(i)));
        }
        PieDataSet dataSet = new PieDataSet(pieEntries, "Webpages");
        dataSet.setColors(ChartColors);
        dataSet.setValueTextSize(12);
        dataSet.setValueTextColor(Color.rgb(255,255,255));
        dataSet.setDrawValues(false);

        PieData data = new PieData(dataSet);

        // Get chart
        PieChart chart = (PieChart) view.findViewById(R.id.pieChart);
        //chart = (PieChart) view.findViewById(R.id.pieChart);
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
        chart.setRotationEnabled(true);
        chart.setData(data);
        chart.setTransparentCircleAlpha(0);
        chart.setHoleRadius(0);
        chart.animateY(1000);
        chart.notifyDataSetChanged();
        chart.invalidate();


        //setup list

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(view.getContext(),
                                        R.layout.custom_frag1_tablerow,
                                        myDB.getGraphData(),
                                        new String[] {"urlhost", "SUM(timespent)"},
                                        new int[] { R.id.frag1DomainTextview, R.id.frag1TimeTextview});

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder(){

            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (columnIndex == 2){
                    int totalSeconds = cursor.getInt(columnIndex);

                    // thanks to Geobits at https://stackoverflow.com/a/6118983/3084356

                    int hours = totalSeconds / 3600;
                    int minutes = (totalSeconds % 3600) / 60;
                    int seconds = totalSeconds % 60;

                    String timeString = String.format("%02dh:%02dm:%02ds", hours, minutes, seconds);
                    TextView textView = (TextView) view;
                    textView.setText(timeString);
                    return true;
                }
                return false;
            }
        });

        ListView customListView = (ListView) view.findViewById(R.id.pieChartListView);
        customListView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}

