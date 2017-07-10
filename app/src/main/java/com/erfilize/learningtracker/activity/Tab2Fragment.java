package com.erfilize.learningtracker.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.erfilize.learningtracker.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A ListView-Setter based on <a href="http://www.worldbestlearningcenter.com/tips/Android-listview-json-array.htm"> the second part shown here.</a>
 */

public class Tab2Fragment extends Fragment {

    private String TAG = "Tab2Fragment";

    SharedPrefEditor mySPE;
    JSONArray jsonArray;
    JSONArray testArray;
    XApiStatements myXAPI;

    private ListAdapter adapter;
    private ListView listView;
    private ArrayList<JSONObject> data;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment,container,false);

        mySPE = new SharedPrefEditor(view.getContext());
        myXAPI = new XApiStatements(view.getContext());


        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        if(preferences.contains("xApiStatementsArray")){
            String xapi = mySPE.getString("xApiStatementsArray").replaceAll("\\\\","");
            try{
                JSONArray newJSONArray = new JSONArray(xapi);
                myXAPI.setArray(newJSONArray);
                Log.i("String to JSONArray",newJSONArray.toString());
            }catch(JSONException e){
                e.printStackTrace();
            }
        }else{
            myXAPI.setArray(new JSONArray());
        }

        getJSONArray();

        data = getDataFromJSONArray(jsonArray);

        listView = (ListView) view.findViewById(R.id.xAPIListView);

        adapter = new ListAdapter(view.getContext(),R.layout.custom_xapi_row,1, data);

        listView.setAdapter(adapter);




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){

                testArray = myXAPI.getArray();

                try{
                    String learning = data.get(position).getJSONObject("learning").getJSONObject("display").getString("en-US");
                    //
                    String object = data.get(position).getJSONObject("object").getJSONObject("definition").getJSONObject("name").getString(getContext().getString(R.string.xAPI_language_tag));
                    //
                    Log.i("TAB2",learning);

                    if(learning.equals("Yes")){

                        for (int i = 0; i < data.size(); i++){
                            if (data.get(i).getJSONObject("object").getJSONObject("definition").getJSONObject("name").getString(getContext().getString(R.string.xAPI_language_tag)).equals(object)){
                                data.get(i).getJSONObject("learning").getJSONObject("display").put("en-US","No");
                                data.get(i).getJSONObject("learning").getJSONObject("display").put("de-DE","Nein");
                            }
                        }
                        //data.get(position).getJSONObject("learning").getJSONObject("display").put("en-US","No");
                        //data.get(position).getJSONObject("learning").getJSONObject("display").put("de-DE","Nein");
                        Log.i(TAG,"Learning changed to No");
                    }else {
                        for (int i = 0; i < data.size(); i++) {
                            if (data.get(i).getJSONObject("object").getJSONObject("definition").getJSONObject("name").getString(getContext().getString(R.string.xAPI_language_tag)).equals(object)) {
                                data.get(i).getJSONObject("learning").getJSONObject("display").put("en-US","Yes");
                                data.get(i).getJSONObject("learning").getJSONObject("display").put("de-DE","Ja");
                            }
                            //data.get(position).getJSONObject("learning").getJSONObject("display").put("en-US","Yes");
                            //data.get(position).getJSONObject("learning").getJSONObject("display").put("de-DE","Ja");
                            Log.i(TAG, "Learning changed to Yes");
                        }
                    }
                    adapter.notifyDataSetChanged();
                    JSONArray newArray = new JSONArray(data);
                    myXAPI.setArray(newArray);
                    mySPE.putString("xApiStatementsArray",newArray.toString());


                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        return view;
    }


    private void getJSONArray(){

        String xapi = mySPE.getString("xApiStatementsArray").replaceAll("\\\\","");

        try{
            jsonArray = new JSONArray(xapi);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private ArrayList<JSONObject> getDataFromJSONArray(JSONArray array){
        ArrayList<JSONObject> data = new ArrayList<>();
        try{
            for (int i = 0; i <array.length();i++){
                data.add(array.getJSONObject(i));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return data;
    }
}


