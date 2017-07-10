package com.erfilize.learningtracker.activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "history.db";
    private static final String TABLE_NAME = "history_data";
    private static final String KEY_ROWID = "_id";
    //private static final String KEY_UUID = "uuid";
    private static final String KEY_URLADDRESS = "urladdress"; //URL
    private static final String KEY_URLTITLE = "urltitle"; //URL Title
    private static final String KEY_URLHOST = "urlhost";
    private static final String KEY_LEARNING = "learning";
    private static final String KEY_TIMESPENT = "timespent";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final int DATABASE_VERSION = 1;

    private static String databasePath;
    //private static final String uuid = Installation.GetUUID();

    public DataBaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        databasePath = context.getDatabasePath(DataBaseHelper.DATABASE_NAME).toString();

    }

    public String getDatabasePath(){
        return databasePath;
    }

    public String getTableName(){
        return TABLE_NAME;
    }


    @Override
    public void onCreate(SQLiteDatabase db){
        String createTable = "CREATE TABLE " + TABLE_NAME +
                "(" + KEY_ROWID +" INTEGER PRIMARY KEY AUTOINCREMENT, " /*+ KEY_UUID +" TEXT, "*/
                + KEY_URLADDRESS +" TEXT, "+ KEY_URLTITLE +" TEXT, "+KEY_URLHOST+" TEXT, "+ KEY_LEARNING +" INTEGER, "
                + KEY_TIMESPENT+ " INTEGER,"+ KEY_TIMESTAMP +" DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')))";
        db.execSQL(createTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * This function inserts the passed parameters into the database.
     * @param urlAddress
     * @param urlTitle
     * @param learning
     * @return True if the insert query was successful
     */
    public boolean addData(String urlAddress, String urlTitle, String urlHost, int learning){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put(KEY_UUID, uuid);
        contentValues.put(KEY_URLADDRESS, urlAddress);
        contentValues.put(KEY_URLTITLE, urlTitle);
        contentValues.put(KEY_URLHOST, urlHost);
        contentValues.put(KEY_LEARNING, learning);

        try{
            long result = db.insert(TABLE_NAME, null, contentValues);
            if (result == -1){
                return false;
            }else{
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    /**
     * This function will update the column "learning" of the current URL to the value of
     * the param learning, which is either 0 or 1 as SQLite does not support Boolean as a datatype.
     *
     * @param learning An Integer which is either 0 or 1 depending on the Switch state in MainActivity
     * @return True if the update query was successful.
     */
    public boolean updateLearning(String urlAddress,int learning){
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_NAME + " SET " + KEY_LEARNING + " = " + learning + " WHERE " + KEY_URLADDRESS + " = '" + urlAddress+"'";
        db.execSQL(updateQuery);
        db.close();
        return true;
    }


    /**
     * This function will update the column "learning" of the last inserted row to the value of
     * the param learning, which is either 0 or 1 as SQLite does not support Boolean as a datatype.
     *
     * @param learning An Integer which is either 0 or 1 depending on the Switch state in MainActivity
     * @return True if the update query was successful.
     */
    public boolean updateLatestLearning(int learning){
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_NAME + " SET "  + KEY_LEARNING + " = " + learning +
                " WHERE "+ KEY_ROWID +" = (SELECT MAX("+KEY_ROWID+") FROM "+ TABLE_NAME+")";
        db.rawQuery(updateQuery,null);
        db.close();
        return true;
    }

    /**
     * This Function updates the column "timespent" to the value of the param pageTime for the
     * second last inserted ID.
     * This function is called whenever the WebView has finished loading a new page and the timer for
     * the last page has been stopped.
     *
     * @param pageTime
     * @return True if the update query was successful
     */
    public boolean updatePageTime(Long pageTime){
        SQLiteDatabase db = this.getWritableDatabase();

        String updateQuery = "UPDATE "+ TABLE_NAME+ " SET "+ KEY_TIMESPENT +" = " + pageTime +
                " WHERE "+ KEY_ROWID +" = (SELECT "+ KEY_ROWID +" FROM (SELECT * FROM "+ TABLE_NAME+
                " ORDER BY "+ KEY_ROWID +"  DESC LIMIT 2)ORDER BY "+ KEY_ROWID +" LIMIT 1)";
        db.execSQL(updateQuery);
        db.close();


        return true;
    }

    /**
     * Similar to updatePageTime(Long pageTime). This Function will update the last inserted ID.
     * @param pageTime
     * @return True if the update query was successful
     */
    public boolean updateLastPageTime(Long pageTime){
        SQLiteDatabase db = this.getWritableDatabase();

        /*String updateQuery = "UPDATE " + TABLE_NAME+ " SET " + KEY_TIMESPENT + " = " + pageTime +
                " WHERE "+ KEY_ROWID +" = (SELECT last_insert_rowid())";
        */
        String updateQuery = "UPDATE " + TABLE_NAME + " SET " + KEY_TIMESPENT + " = " + pageTime +
                " WHERE "+ KEY_ROWID +" = (SELECT MAX("+KEY_ROWID+") FROM "+ TABLE_NAME+")";
        db.execSQL(updateQuery);
        db.close();
        return true;
    }

    public Cursor test(){
        SQLiteDatabase db = this.getWritableDatabase();

        String testQuery = "SELECT "+ KEY_TIMESPENT +" FROM "+TABLE_NAME+" WHERE "+ KEY_ROWID +
                " = (SELECT MAX("+KEY_ROWID+") FROM "+ TABLE_NAME+")";

        Cursor data = db.rawQuery(testQuery,null);
        return data;
    }


    /**
     * This Function binds all the IDs, UUIDs, URLAddresses,URLTitles,Learning-Status,Timestamps and
     * the time spent on these Websites to a cursor.
     * @return The cursor
     */
    public Cursor getAllEntries()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.query(TABLE_NAME,             // table
                        new String[] {
                                KEY_ROWID,
                                //KEY_UUID,
                                KEY_URLADDRESS,
                                KEY_URLTITLE,
                                KEY_LEARNING,
                                KEY_TIMESTAMP,
                                KEY_TIMESPENT}, // columns
                        null,                   // selection
                        null,                   // selectionArgs
                        null,                   // groupBy
                        null,                   // having
                        KEY_ROWID +" DESC"      // orderBy
                        );

    }

    public Cursor getGraphData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT "+KEY_ROWID+", "+ KEY_URLHOST +", SUM("+KEY_TIMESPENT+")"+" FROM "+ TABLE_NAME
                +" WHERE "+KEY_LEARNING+" = 1 GROUP BY "+KEY_URLHOST+" ORDER BY SUM("+KEY_TIMESPENT+") DESC",null);

        return data;
    }


    /*
     * does nothing right now. Fix Query if you want to do something.
     *
     */
    public Cursor getLastRow(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT "+ KEY_TIMESPENT +" FROM "+ TABLE_NAME+" SELECT MAX(_id) FROM "+ TABLE_NAME, null);
        return data;
    }

    public Cursor getListContents(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME,null);

        return data;
    }


}
