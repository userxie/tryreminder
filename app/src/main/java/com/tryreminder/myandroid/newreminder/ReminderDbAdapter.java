package com.tryreminder.myandroid.newreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.StringBuilderPrinter;

/**
 * Created by Muzhou on 1/11/2017.
 */
public class ReminderDbAdapter {
    //*********************
    //数据库的代理类
    //*********************
    //column names
    public static final String COL_ID ="_id";
    public static final String COL_CONTENT ="content";
    public static final String COL_IMPORTANT ="important";
    //corresponding indices
    public static final int INDEX_ID =0;
    public static final int INDEX_CONTENT =INDEX_ID+1;
    public static final int INDEX_IMPORTANT =INDEX_ID+2;
    //used for logging
    private static final String TAG ="RemindersDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASSE_NAME ="dba_remdrs";
    private static final String TABLE_NAME ="tbl_remdrs";
    private static final int DATABASE_VERSION =1;
    private final Context mCtx;

    private static final String DATABASE_CRETE =
            "CREATE TABLE if not exists "+TABLE_NAME+"("+COL_ID
            +"INTEGER PRIMARY KEY autoincrement, "+
                    COL_CONTENT+"TEXT, "+
                    COL_IMPORTANT+"INTEGER);";

    public ReminderDbAdapter(Context ctx){
        this.mCtx =ctx;
    }
    //open database
    public void open() throws SQLiteException{
        mDbHelper=new DatabaseHelper(mCtx);
        mDb =mDbHelper.getWritableDatabase();
    }
    //close
    public void close(){
        if(mDbHelper!=null){
            mDbHelper.close();
        }
    }
    //CREATE database
    public void createReminder(String name,boolean important){
        ContentValues values =new ContentValues();
        values.put(COL_CONTENT,name);
        values.put(COL_IMPORTANT,important?1:0);
        mDb.insert(TABLE_NAME,null,values);
    }

    public long createReminder (Reminder reminder){
        ContentValues values =new ContentValues();
        values.put(COL_CONTENT,reminder.getContent());
        values.put(COL_IMPORTANT,reminder.getImportant());
        return mDb.insert(TABLE_NAME,null,values);
    }
    //READ
    public Reminder fetchReminderById(int id){
        Cursor cursor =mDb.query(TABLE_NAME,new String[]{COL_ID,
        COL_CONTENT,COL_IMPORTANT},COL_ID+"=?",new String[]{String.valueOf(id)},null,null,null,null);
        if(cursor!= null) cursor.moveToFirst();
        return new Reminder(
                cursor.getInt(INDEX_ID),
                cursor.getString(INDEX_CONTENT),
                cursor.getInt(INDEX_IMPORTANT)
        );
    }
    public Cursor fetchAllReminders(){
        Cursor cursor =mDb.query(TABLE_NAME,new String[]{COL_ID,
        COL_CONTENT,COL_IMPORTANT},null,null,null,null,null);
        if(cursor!=null){
            cursor.moveToFirst();
        }
        return  cursor;
    }
    //UPDATE
    public void updateReminder(Reminder reminder){
        ContentValues values =new ContentValues();
        values.put(COL_CONTENT,reminder.getContent());
        values.put(COL_IMPORTANT,reminder.getImportant());
        mDb.update(TABLE_NAME,values,COL_ID+"=?",new String[]{String.valueOf(reminder.getId())});

    }

    //DELETE
    public void deleteReminderById(int nId){
        mDb.delete(TABLE_NAME,COL_ID+"=?",new String[]{String.valueOf(nId)});

    }
    public  void deleteAllReminders(){
        mDb.delete(TABLE_NAME,null,null);
    }
    private static class DatabaseHelper extends SQLiteOpenHelper{
        DatabaseHelper(Context context){
            super(context,DATABASSE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG,DATABASE_CRETE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int OldVersion, int newVersion) {
            Log.w(TAG,"Upgrading database from version "+OldVersion+"to"+newVersion+
                    ",which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
            onCreate(db);
        }
    }
}
