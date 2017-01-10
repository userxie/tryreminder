package com.tryreminder.myandroid.newreminder;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ReminderActivity extends AppCompatActivity {

    private  ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);
        mListView =(ListView)findViewById(R.id.reminder_list_view);
        ArrayAdapter<String> arrayAdapter =new ArrayAdapter<String>(
                this,
                R.layout.reminders_row,
                R.id.row_text,
                new String[]{"first record","second record","third record"});
        mListView.setAdapter(arrayAdapter);

    }


}
