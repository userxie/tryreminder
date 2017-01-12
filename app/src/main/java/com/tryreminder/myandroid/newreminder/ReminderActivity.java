package com.tryreminder.myandroid.newreminder;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ReminderActivity extends AppCompatActivity {

    private  ListView mListView;
    private ReminderDbAdapter mDbAdapter;
    private ReminderSimpleCursorAdapter mCursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);
        mListView =(ListView)findViewById(R.id.reminder_list_view);
        mDbAdapter =new ReminderDbAdapter(this);
        mDbAdapter.open();
        Cursor cursor =mDbAdapter.fetchAllReminders();
        String [] from =new String[]{ReminderDbAdapter.COL_CONTENT};
        int [] to =new int[]{R.id.row_text};
        mCursorAdapter =new ReminderSimpleCursorAdapter(this,R.layout.reminders_row,cursor,from,to,0);
        mListView.setAdapter(mCursorAdapter);
//        ArrayAdapter<String> arrayAdapter =new ArrayAdapter<String>(
//                this,
//                R.layout.reminders_row,
//                R.id.row_text,
//                new String[]{"first record","second record","third record"});
//        mListView.setAdapter(arrayAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                //create new Reminder
                Log.d(getLocalClassName(),"Create new Reminder");
                return true;
            case R.id.action_exit:
                finish();
                return true;
            default:
                return false;
        }
    }

}
