package com.erfilize.learningtracker.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toast;

import com.erfilize.learningtracker.R;

import org.json.JSONArray;
import org.json.JSONException;


/**
 * The MainActivity contains the browser in form of a WebView and methods to track the learning data.
 */
public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    private boolean firstStart = true;
    private boolean firstTimerCalculated = false;
    private String website;
    private WebView myWebView;
    private EditText editText; // editText = the address bar / search bar
    private ProgressBar progressBar;
    private FrameLayout frameLayout;
    private Switch trackingSwitch;

    private String webUrl = "";
    private String webTitle = "";
    private String webHost = "";

    private String formerURL = "";
    private String formerTitle = "";

    private int learning;

    DataBaseHelper myDB;
    TimeTracker myTT;
    XApiStatements myXAPI;
    SharedPrefEditor mySPE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



        myXAPI = new XApiStatements(this);
        myDB = new DataBaseHelper(this);
        mySPE = new SharedPrefEditor(this);
        myTT = new TimeTracker();

        new SimpleEula(this).show();

        /*
        ============ XAPI PREPARATION ============
        */

        //Check if xApiStatementsArray exists in SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
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

        /*
        * Check if a UUID exists. If not, a UUID will be created. The UUID is the ID for the actor
        *  in the xAPI Statements.
        * */
        Installation.id(this);

        // set the actor for the xApi statements
        myXAPI.setActor("Student",Installation.GetUUID());

        // set language tag for object
        myXAPI.setLanguageTag(getResources().getString(R.string.xAPI_language_tag));


        /*
        ============ TOOLBAR PREPARATION ============
        */
        // set toolbar to custom toolbar
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // add Progressbar to toolbar
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);


        /*
        ============ MAIN ACTION HAPPENS HERE ============
        */
        trackingSwitch = (Switch) findViewById(R.id.trackingSwitch);
        myWebView = (WebView) findViewById(R.id.activity_main_webview);
        // enable JavaScript for pages that use JavaScript
        myWebView.getSettings().setJavaScriptEnabled(true);
        // enable pinch to zoom
        myWebView.getSettings().setBuiltInZoomControls(true);
        //disable zoom overlay control
        myWebView.getSettings().setDisplayZoomControls(false);
        // load homepage
        myWebView.loadUrl(getBaseContext().getString(R.string.homepage));
        progressBar.setProgress(0);


        myWebView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                if(firstStart){

                    myTT.setPageStartTime();
                    myTT.resetTotalPauseTime();

                    editText.setText("");

                    webUrl = view.getUrl();
                    webTitle = view.getTitle();
                    webHost = Uri.parse(webUrl).getHost();

                    formerURL = view.getUrl();
                    formerTitle = view.getTitle();

                    learning = (trackingSwitch.isChecked()) ? 1 : 0; // true = 1, false = 0


                    //create xAPI-Statement with experienced as verb
                    myXAPI.setVerb(myXAPI.verbExperienced);
                    myXAPI.setObject(webUrl,webTitle,myXAPI.activityMedia);
                    myXAPI.setLearning(learning);
                    myXAPI.createStatement();
                    mySPE.putString("xApiStatementsArray",myXAPI.getArrayInString());

                    AddData(webUrl, webTitle, webHost,learning);

                    firstStart = false;

                }else{
                    myTT.getTimeSpentOnPage();
                    if(firstTimerCalculated){
                        myTT.setPageStartTime();
                    }
                    firstTimerCalculated = true;


                    // create XAPI Statement with exited as verb
                    myXAPI.setVerb(myXAPI.verbExited);
                    myXAPI.setObject(formerURL, formerTitle, myXAPI.activityMedia);
                    myXAPI.setLearning(learning);
                    myXAPI.setxAPIResult((int)myTT.getResult());
                    myXAPI.createStatement();
                    Log.i("xAPI exited: ",myXAPI.getObject().toString());
                    mySPE.putString("xApiStatementsArray",myXAPI.getArrayInString());



                    // change editText to show url like an addressbar
                    editText.setText(view.getUrl());


                    webUrl = view.getUrl();
                    webTitle = view.getTitle();
                    webHost = Uri.parse(webUrl).getHost();


                    learning = (trackingSwitch.isChecked()) ? 1 : 0; // true = 1, false = 0

                    //create xAPI-Statement with experienced as verb
                    myXAPI.setVerb(myXAPI.verbExperienced);
                    myXAPI.setObject(webUrl,webTitle,myXAPI.activityMedia);
                    myXAPI.setLearning(learning);
                    myXAPI.createStatement();
                    Log.i("xAPI experienced: ",myXAPI.getObject().toString());
                    mySPE.putString("xApiStatementsArray",myXAPI.getArrayInString());

                    // add history data to database
                    AddData(webUrl, webTitle, webHost, learning);

                    // add time spent on previous URL to associated row in database
                    UpdatePageTime(myTT.getResult());

                    // save current website data for next exited xAPI Statement
                    formerURL = view.getUrl();
                    formerTitle = view.getTitle();

                    //Toast.makeText(MainActivity.this,myTT.getResult()+" Seconds spent on former page", Toast.LENGTH_SHORT).show();

                }
            }
        });

        // animation of the progressbar
        myWebView.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress){
                frameLayout.setVisibility(View.VISIBLE);
                progressBar.setProgress(progress);
                if (progress == 100){
                    frameLayout.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, progress);
            }

        });


        // ActionListener for editText that fires up a Google search upon send
        editText = (EditText) findViewById(R.id.editText);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if(actionId == EditorInfo.IME_ACTION_SEND) {
                    // Keyboard does hide on emulator but not on devices for whatever reason.
                    // Use InputMethodManager.hideSoftInputFromWindow() to hide manually
                    InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    website = editText.getText().toString();
                    if(website.toLowerCase().contains("https://") || website.toLowerCase().contains("http://")){
                        myWebView.loadUrl(website);
                        myWebView.requestFocus();
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    }else if(website.toLowerCase().contains("www.")){
                        myWebView.loadUrl("https://"+website);
                        myWebView.requestFocus();
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    }else if(Patterns.WEB_URL.matcher(website).matches()) {
                        myWebView.loadUrl("https://www." + website);
                        myWebView.requestFocus();
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    }else {
                        myWebView.loadUrl(getBaseContext().getString(R.string.google_search) + website);
                        myWebView.requestFocus();
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    }
                }
                return true;
            }
        });


        trackingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                learning = isChecked ? 1 : 0; // true = 1, false = 0
                UpdateLearning(learning);
                if (learning == 1){
                    Toast.makeText(MainActivity.this, R.string.learning_note,Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, R.string.learning_note2,Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    protected void onRestart(){
        super.onRestart();
    }

    protected void onResume(){
        // falls irgendwas nicht passt wieder auskommentieren
        //myTT.resumeCounter();

        if(firstStart==false){
            Log.i("MAINACTIVITY:", "onResume");
            myTT.resumeCounter();

            try{
                String xapi = mySPE.getString("xApiStatementsArray").replaceAll("\\\\","");
                JSONArray newJSONArray = new JSONArray(xapi);
                myXAPI.setArray(newJSONArray);
                Log.i("ONCLICK - onPAUSE","new Array set");
            }catch(JSONException e){
                e.printStackTrace();
            }

            myXAPI.setVerb(myXAPI.verbResumed);
            myXAPI.setObject(webUrl,webTitle,myXAPI.activityMedia);
            myXAPI.setLearning(learning);
            myXAPI.createStatement();
            mySPE.putString("xApiStatementsArray",myXAPI.getArrayInString());
            Log.i("ONCLICK - onResume",myXAPI.getArray().toString());
        }
        super.onResume();
    }

    protected void onPause() {
        //create xAPI-Statement
        if(firstStart==false){
            myTT.pauseTime();
            myXAPI.setVerb(myXAPI.verbSuspended);
            myXAPI.setObject(webUrl,webTitle,myXAPI.activityMedia);
            myXAPI.setLearning(learning);
            myXAPI.createStatement();
            mySPE.putString("xApiStatementsArray",myXAPI.getArrayInString());
        }
        super.onPause();

    }

    protected void onStop(){
        Log.i("MAINACTIVITY:", "onStop");
        myTT.getTimeSpentOnPage();
        UpdateLastPageTime(myTT.getResult());

        super.onStop();
    }

    protected void onDestroy(){
        Log.i("MAINACTIVITY:", "onDestroy");
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0){
            if(resultCode == RESULT_OK){
                String string = data.getStringExtra("RESULT_STRING");
                myWebView.loadUrl(string);
            }
        super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Inflates the options menu into the action bar
     * @param menu The menu which is inflated
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /**
     * This hook is called whenever an item in the options menu is selected.
     * @param item The selected MenuItem
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_forward:
                if(myWebView.canGoForward()) {
                    myWebView.goForward();
                }
                return true;
            case R.id.action_refresh:
                myWebView.loadUrl(myWebView.getUrl());
                return true;
            case R.id.action_history:
                Intent intentHistory = new Intent(MainActivity.this, History.class);
                startActivityForResult(intentHistory, 0);
                return true;
            case R.id.action_about:

                Intent intentAbout = new Intent(MainActivity.this, About.class);
                startActivity(intentAbout);
                return true;

            case R.id.action_visualize:
                Intent intentVisualize = new Intent(MainActivity.this, TabData.class);
                startActivity(intentVisualize);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Called when the activity has detected the user's press of the back key.
     * If the WebView can go back, it will return to the previous page.
     * Otherwise its super is called and the default implementation finishes the current activity.
     */
    @Override
    public void onBackPressed() {
        if (myWebView.canGoBack()) {
            myWebView.goBack();
        }

        super.onBackPressed();

    }


    /**
     * Called when data needs to be added to the database.
     * @param newURL The URL of the website
     * @param newTitle The title of the website
     * @param learning Either 0 or 1 depending on the case of the Switch. The website is learning relevant if learning is 1.
     */
    public void AddData(String newURL, String newTitle, String newHost, int learning){
        boolean insertData = myDB.addData(newURL, newTitle, newHost, learning);

        if(insertData){
            //Toast.makeText(MainActivity.this,"URL successfully entered in DB",Toast.LENGTH_SHORT).show();
            Log.i(TAG,"URL successfully entered in DB");
        }else{
            //Toast.makeText(MainActivity.this,"Could not enter URL in DB",Toast.LENGTH_SHORT).show();
            Log.i(TAG,"Could not enter URL in DB");
        }
    }

    /**
     * Updates the time spent on the last website.
     * @param pageTime The time which is kept track of.
     */
    public void UpdatePageTime(long pageTime){
        boolean updateData = myDB.updatePageTime(pageTime);

        if (updateData){
            //Toast.makeText(MainActivity.this, "Time succesfully updated", Toast.LENGTH_SHORT).show();
            Log.i(TAG,"Time succesfully updated");
        }else{
            //Toast.makeText(MainActivity.this,"Could not update Time",Toast.LENGTH_SHORT).show();
            Log.i(TAG,"Could not update Time");
        }
    }

    /**
     * Updates the time for the last row in the database.
     * Called onStop as a last resort attempt to save the time if the app is killed.
     * @param pageTime The time which is kept track of.
     */
    public void UpdateLastPageTime(long pageTime){
        boolean updateData = myDB.updateLastPageTime(pageTime);

        if(updateData){
            //Toast.makeText(MainActivity.this, "Last page time succesfully updated", Toast.LENGTH_SHORT).show();
            Log.i(TAG,"Last page time succesfully updated");
        }else{
            //Toast.makeText(MainActivity.this, "Could not update last page", Toast.LENGTH_SHORT).show();
            Log.i(TAG,"Could not update last page");
        }
    }

    /**
     * Called when a website needs to be set as learning relevant or not relevant.
     * The method will update each entry of the URL.
     * @param learning Either 0 or 1 depending on the case of the Switch. The website is learning relevant if learning is 1.
     */
    public void UpdateLearning(int learning){
        boolean updateData = myDB.updateLearning(webUrl,learning);
        if(updateData){
            //Toast.makeText(MainActivity.this,"Learning for this URL has been set to "+learning,Toast.LENGTH_LONG).show();
            Log.i(TAG,"Learning for this URL has been set to "+learning);
        }else{
            //Toast.makeText(MainActivity.this,"Could not update Learning",Toast.LENGTH_LONG).show();
            Log.i(TAG,"Could not update Learning");
        }
    }
}
