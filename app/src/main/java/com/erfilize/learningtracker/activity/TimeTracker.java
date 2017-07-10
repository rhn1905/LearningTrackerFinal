package com.erfilize.learningtracker.activity;


import android.util.Log;

import java.util.concurrent.TimeUnit;


public class TimeTracker {

    private long tStartPage;
    private long tEnd;
    private long tPause;
    private long tResumed;
    private long tTotalPause = 0;

    private long tResult;


    /**
     * saves the starting time of the learning session
     */



    public void setPageStartTime(){
        tStartPage = System.nanoTime();
    }


    /**
     * Main function.
     * @return time spent on page in seconds
     */

    public void getTimeSpentOnPage() {
        Log.w("tTotalPause", Long.toString(tTotalPause));
        tEnd = System.nanoTime();
        tEnd = tEnd - tTotalPause;
        tTotalPause = 0;

        tResult = TimeUnit.SECONDS.convert(tEnd - tStartPage, TimeUnit.NANOSECONDS);

        Log.w("tResultInDouble", Long.toString(tResult)+" Seconds");

    }

    public void  resetTotalPauseTime(){
        tTotalPause = 0;
    }

    /**
     * @return the Result of the calculated time which has been spent on the last website
     */
    public long getResult(){
        return tResult;
    }

    /**
     * Saves the time of the current pause
     */
    public void pauseTime() {
        tPause = System.nanoTime();
        Log.w("tMESSAGE: ","Time has been stopped");
    }

    /**
     * Adds the length of the last pause to the total pause time
     */
    public void resumeCounter(){
        tResumed = System.nanoTime();
        tTotalPause += tResumed - tPause;
        Log.w("tMESSAGE","Counter resumes");
        Log.w("tCURRENT TOTALPAUSE",Double.toString((double)tTotalPause/1000000000.0));
    }

}
