package com.erfilize.learningtracker.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * The XApiStatements class creates valid xAPI-Statements.
 */

public class XApiStatements extends AppCompatActivity {

    private JSONObject actor;
    private JSONObject verb;
    private JSONObject object;
    private JSONObject extensions;
    private JSONObject result;

    private JSONObject xApiStatement;
    //private JSONArray xApiStatementsArray = new JSONArray();
    private JSONArray xApiStatementsArray;

    protected final String verbCompleted = "completed";
    protected final String verbExperienced = "experienced";
    protected final String verbExited = "exited";
    protected final String verbInteracted = "interacted";
    protected final String verbInitialized = "initialized";
    protected final String verbResumed = "resumed";
    protected final String verbSuspended = "suspended";
    protected final String verbTerminated = "terminated";

    protected final String activityMedia = "http://adlnet.gov/expapi/activities/media";

    private String languageTag="";

    private Boolean exited = false;



    public XApiStatements(Context context){

    }

    /**
     * sets the language tag for the Object
     * @param tag language tag
     */
    public void setLanguageTag(String tag){
        languageTag = tag;
    }

    /**
     * Sets the Actor for the xAPI-Statement
     * @param name Name of the Actor
     * @param id ID of the Actor
     */
    public void setActor(String name, String id){
        actor = new JSONObject();

        try{
            actor.put("name",name);
            actor.put("uuid",id);
        }catch(Exception e){
            Log.d("setActor", e.getMessage());
        }
    }


    /**
     * Sets the Verb of the xAPI-Statement
     * @param newVerb xAPI-Verb
     */
    public void setVerb(String newVerb){
        verb = new JSONObject();
        String id = "";
        String deDE ="";
        JSONObject display = new JSONObject();

        switch (newVerb) {
            case "completed":
                id = "http://adlnet.gov/expapi/verbs/completed";
                deDE="schloss ab";
                exited = false;
                break;
            case "experienced":
                id ="http://adlnet.gov/expapi/verbs/experienced";
                deDE="erfuhr";
                exited = false;
                break;
            case "exited":
                id ="http://adlnet.gov/expapi/verbs/exited";
                deDE="verließ";
                exited = true;
                break;
            case "interacted":
                id = "http://adlnet.gov/expapi/verbs/interacted";
                deDE="interagierte";
                exited = false;
                break;
            case "initialized":
                id = "http://adlnet.gov/expapi/verbs/initialized";
                deDE="initialisierte";
                exited = false;
                break;
            case "launched":
                id = "http://adlnet.gov/expapi/verbs/launched";
                deDE="startete";
                exited = false;
                break;
            case "resumed":
                id = "http://adlnet.gov/expapi/verbs/resumed";
                deDE="setzte fort";
                exited = false;
                break;
            case "suspended":
                id = "http://adlnet.gov/expapi/verbs/suspended";
                deDE = "suspendierte";
                exited = false;
                break;
            case "terminated":
                id ="http://adlnet.gov/expapi/verbs/terminated";
                deDE="beendete";
                exited = false;
                break;
            default:
                Log.w("setVerb", "Verb is unknown");
                exited = false;
                break;
        }

        try{
            display.put("en-US",newVerb);
            display.put("de-DE",deDE);
            verb.put("id",id);
            verb.put("display",display);
        }catch(Exception e){
            Log.d("setVerb",e.getMessage());
        }
    }

    /**
     * Sets the Object of the xAPI-Statement
     * @param id ID of the Object
     * @param objectName Name of the Object
     * @param activityType Activitytype of the Object
     */
    public void setObject(String id, String objectName, String activityType){
        object = new JSONObject();
        JSONObject definition = new JSONObject();
        JSONObject name = new JSONObject();
        try{
            //name.put("en-US",objectName);
            name.put(languageTag,objectName);
            definition.put("type",activityType);
            definition.put("name",name);
            object.put("id",id);
            object.put("definition",definition);
        }catch(Exception e){
            Log.d("setObject",e.getMessage());
        }
    }

    /**
     * Sets the (self-defined) Learning property of the xAPI-Statement
     * @param value Either 0 oder 1 depending on Switch-State
     */
    public void setLearning(int value){
        extensions = new JSONObject();
        JSONObject learning = new JSONObject();
        JSONObject display = new JSONObject();

        try{
            if(value==0){
                display.put("en-US", "No");
                display.put("de-DE", "Nein");
            }else{
                display.put("en-US", "Yes");
                display.put("de-DE", "Ja");
            }

            learning.put("display",display);
            extensions.put("learning",learning);
            object.put("extensions",extensions);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * sets the duration of the xAPI-Statement
     * @param duration the time
     */
    public void setxAPIResult(int duration){
        result = new JSONObject();
        String seconds = Integer.toString(duration);
        String time = "PT"+seconds+"S";
        try{
            result.put("duration",time);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Creates the complete xapi statement
     */
    public void createStatement(){
        xApiStatement = new JSONObject();
        // Local Time
        //SimpleDateFormat sDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",Locale.getDefault());
        //String date = sDF.format(new Date());

        // UTC Time
        SimpleDateFormat utc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        utc.setTimeZone(TimeZone.getTimeZone("UTC"));
        String date = utc.format(new Date());

        try{
            if(exited) {
                xApiStatement.put("actor", actor);
                xApiStatement.put("verb", verb);
                xApiStatement.put("object", object);
                xApiStatement.put("result", result);
                //xApiStatement.put("extensions", extensions);
                xApiStatement.put("timestamp", date);
                xApiStatementsArray.put(xApiStatement);
                result = new JSONObject();
            }else{
                xApiStatement.put("actor", actor);
                xApiStatement.put("verb", verb);
                xApiStatement.put("object", object);
                //xApiStatement.put("extensions", extensions);
                xApiStatement.put("timestamp", date);
                xApiStatementsArray.put(xApiStatement);
            }
        }catch(Exception e){
            Log.d("createStatement", e.getMessage());
        }

    }

    /**
     * Returns the latest created xAPI-Statement
     * @return xAPIStatement
     */
    public JSONObject getStatement(){
        return xApiStatement;
    }

    /**
     * returns the xAPI-Array in a String
     * @return xAPI-Array as String
     */
    public String getArrayInString() {
        return xApiStatementsArray.toString();
    }

    /**
     * sets xApiStatementsArray to the passed array
     * @param array a xAPI-Statement array
     */

    public void setArray(JSONArray array){
        xApiStatementsArray = array;
    }

    /**
     * returns the set Object
     */
    public JSONObject getObject(){
        return object;
    }

    /**
     * returns the set xAPI-Array
     */
    public JSONArray getArray(){
        return xApiStatementsArray;
    }
}
