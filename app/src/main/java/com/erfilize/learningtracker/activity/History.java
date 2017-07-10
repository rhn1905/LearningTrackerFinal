package com.erfilize.learningtracker.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import com.erfilize.learningtracker.R;


/**
 *
 */

public class History extends AppCompatActivity{

    DataBaseHelper myDB;
    private SimpleCursorAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_history_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDB = new DataBaseHelper(this);

        populateListView();

    }


    /**
     * populates the Custom History Listview with a SimpleCursorAdapter
     */
    private void populateListView(){

        Cursor data = myDB.getListContents();
        if(data.getCount() == 0){
            Toast.makeText(History.this,"DB was empty",Toast.LENGTH_LONG).show();
        }

        adapter = new SimpleCursorAdapter(this,
                R.layout.custom_history_row,
                myDB.getAllEntries(),
                new String[] { "urltitle", "urladdress","timestamp"/*,"timespent"*/ },
                new int[] { R.id.urlTitle, R.id.urlAddress, R.id.timestamp/*, R.id.timeSpent*/});

        ListView customListView = (ListView) findViewById(R.id.listView);
        customListView.setAdapter(adapter);

        customListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Cursor cursor = (Cursor) adapter.getItem(position);
                String urladdress = cursor.getString(2);
                //Toast.makeText(History.this,urladdress,Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("RESULT_STRING",urladdress);
                setResult(Activity.RESULT_OK,resultIntent);
                finish();
            }
        });

    }

}

