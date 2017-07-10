package com.erfilize.learningtracker.activity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.erfilize.learningtracker.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A ListAdapter based on <a href="http://www.worldbestlearningcenter.com/tips/Android-listview-json-array.htm"> the ListAdapter shown here.</a>
 * This Adapter is used to fill Tab2Fragment with xAPI-Statements
 */

public class ListAdapter extends ArrayAdapter<JSONObject> {

    Context context;
    int ressource;
    ArrayList<JSONObject> list;


    public ListAdapter(Context context, int ressource, int id, ArrayList<JSONObject> list){
        super (context, ressource, id, list);
        this.context = context;
        this.ressource = ressource;
        this.list = list;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //View itemView = inflater.inflate(vg, parent, false);
        View itemView = inflater.inflate(ressource,parent,false);

        //TextView actor =(TextView)itemView.findViewById(R.id.textViewActor);
        TextView verb=(TextView)itemView.findViewById(R.id.textViewVerb);
        TextView object=(TextView)itemView.findViewById(R.id.textViewObject);
        TextView learning=(TextView)itemView.findViewById(R.id.textViewLearning);
        TextView duration=(TextView)itemView.findViewById(R.id.textViewDuration);
        TextView timestamp = (TextView)itemView.findViewById(R.id.textViewTimestamp);



        try {
            Log.i("LISTADAPTERHAS", Boolean.toString(list.get(position).has("result")));

            // Convert UTF timestamp to localtime
            String utcDate = list.get(position).getString("timestamp");
            SimpleDateFormat sDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SS");
            sDF.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date utcTime = sDF.parse(utcDate);

            TimeZone localTZ = TimeZone.getDefault();
            SimpleDateFormat sDF2 = new SimpleDateFormat("dd-MM-yyyy' 'HH:mm:SS");
            sDF2.setTimeZone(localTZ);

            String convertedTime = sDF2.format(utcTime);


            if(list.get(position).has("result")){
                //actor.setText("Actor: " + list.get(position).getJSONObject("actor").getString("name"));
                verb.setText("Verb: "+list.get(position).getJSONObject("verb").getJSONObject("display").getString(getContext().getString(R.string.xAPI_language_tag)));
                object.setText("Object: "+list.get(position).getJSONObject("object").getJSONObject("definition").getJSONObject("name").getString(getContext().getString(R.string.xAPI_language_tag)));
                learning.setText(getContext().getString(R.string.xapi_learning)+": "+list.get(position).getJSONObject("learning").getJSONObject("display").getString(getContext().getString(R.string.xAPI_language_tag)));
                //timestamp.setText("At: "+list.get(position).getString("timestamp"));
                timestamp.setText(getContext().getString(R.string.xapi_timespent)+": "+convertedTime);
                duration.setText(getContext().getString(R.string.xapi_duration)+": "+list.get(position).getJSONObject("result").getString("duration"));
            }else {
                //actor.setText("Actor: " + list.get(position).getJSONObject("actor").getString("name"));
                verb.setText("Verb: " + list.get(position).getJSONObject("verb").getJSONObject("display").getString(getContext().getString(R.string.xAPI_language_tag)));
                object.setText("Object: " + list.get(position).getJSONObject("object").getJSONObject("definition").getJSONObject("name").getString(getContext().getString(R.string.xAPI_language_tag)));
                learning.setText(getContext().getString(R.string.xapi_learning)+": "+list.get(position).getJSONObject("learning").getJSONObject("display").getString(getContext().getString(R.string.xAPI_language_tag)));
                //timestamp.setText("At: " + list.get(position).getString("timestamp"));
                timestamp.setText(getContext().getString(R.string.xapi_timespent)+": "+convertedTime);
                //duration.setText("Duration: "+list.get(position).getJSONObject("result").getString("duration"));
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return itemView;

    }

}
