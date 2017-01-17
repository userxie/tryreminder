package com.tryreminder.myandroid.newreminder;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Date;
import java.util.List;

import static com.tryreminder.myandroid.newreminder.R.color.blue;

public class ReminderActivity extends AppCompatActivity {

    private ListView mListView;
    private ReminderDbAdapter mDbAdapter;
    private ReminderSimpleCursorAdapter mCursorAdapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mClient;

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setIcon(R.drawable.ic_action_name);
        }
        mListView = (ListView) findViewById(R.id.reminder_list_view);
        mListView.setDivider(null);
        mDbAdapter = new ReminderDbAdapter(this);
        mDbAdapter.open();
        if (savedInstanceState == null) {
            mDbAdapter.deleteAllReminders();
            insertSomeReminders();
        }
        Cursor cursor = mDbAdapter.fetchAllReminders();
        String[] from = new String[]{ReminderDbAdapter.COL_CONTENT};
        int[] to = new int[]{R.id.row_text};
        mCursorAdapter = new ReminderSimpleCursorAdapter(ReminderActivity.this,
                R.layout.reminders_row, cursor, from, to, 0);
        mListView.setAdapter(mCursorAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int masterListPosition, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ReminderActivity.this);
                ListView modeListView = new ListView(ReminderActivity.this);
                String[] modes = new String[]{"编辑", "删除","时间"};
                ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(ReminderActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, modes);
                modeListView.setAdapter(modeAdapter);
                builder.setView(modeListView);
                final Dialog dialog = builder.create();
                dialog.show();
                modeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int nId =getIdFromPosition(masterListPosition);
                        final Reminder reminder =mDbAdapter.fetchReminderById(nId);
                        if(position==0){
                            Log.d("choice","choice edit");

                            fireCustomDialog(reminder);
                            //Toast.makeText(ReminderActivity.this,"edit"+masterListPosition,
                            // Toast.LENGTH_LONG).show();
                        }else if(position==1){
                            mDbAdapter.deleteReminderById(getIdFromPosition(masterListPosition));
                            mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                            //Toast.makeText(ReminderActivity.this,"delete"+masterListPosition,
                            // Toast.LENGTH_SHORT).show();
                        }else{
                            final Date today =new Date();
                            TimePickerDialog.OnTimeSetListener listener =new
                                    TimePickerDialog.OnTimeSetListener(){

                                        @Override
                                        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                            Date alarm =new Date(today.getYear(),today.getMonth(),today.getDate(),hour,minute);
                                            scheduleReminder(alarm.getTime(),reminder.getContent());
                                        }
                                    };
                            new TimePickerDialog(ReminderActivity.this,null,
                                    today.getHours(),today.getMinutes(),false).show();
                        }
                        dialog.dismiss();
                    }
                });

            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){

            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                                                      boolean checked) {
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater =mode.getMenuInflater();
                    inflater.inflate(R.menu.cam_menu,menu);
                    return true;
                }


                @Override
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.menu_item_delete_reminder:
                            for(int nC=mCursorAdapter.getCount()-1;nC>=0;nC--){
                                if(mListView.isItemChecked(nC)){
                                    mDbAdapter.deleteReminderById(getIdFromPosition(nC));
                                }
                            }
                            actionMode.finish();
                            mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                            return true;
                        case R.id.menu_item_add_reminder:
                            fireCustomDialog(null);
                            actionMode.finish();
                            mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                            return true;
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode actionMode) {
                }
            });
        }
    }

    private void scheduleReminder(long time, String content) {
        AlarmManager alarmManager =(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent alrmIntent =new Intent(this,ReminderAlarmReceiver.class);
        alrmIntent.putExtra(ReminderAlarmReceiver.REMINDER_TEXT,content);
        PendingIntent broadcast =PendingIntent.getBroadcast(this,0,alrmIntent,0);
        alarmManager.set(AlarmManager.RTC_WAKEUP,time,broadcast);
    }

    private int getIdFromPosition(int nC) {
        return (int)mCursorAdapter.getItemId(nC);
    }

    private void insertSomeReminders() {
        mDbAdapter.createReminder("Buy Learn Android Studio", true);
        mDbAdapter.createReminder("Send Dad birthday gift", false);
        mDbAdapter.createReminder("Dinner at the Gage on Friday", false);
        mDbAdapter.createReminder("String squash racket", false);
        mDbAdapter.createReminder("Shovel and salt walkways", false);
        mDbAdapter.createReminder("Prepare Advanced Android syllabus", true);
        mDbAdapter.createReminder("Buy new office chair", false);
        mDbAdapter.createReminder("Call Auto-body shop for quote", false);
        mDbAdapter.createReminder("Renew membership to club", false);
        mDbAdapter.createReminder("Buy new Galaxy Android phone", true);
        mDbAdapter.createReminder("Sell old Android phone - auction", false);
        mDbAdapter.createReminder("Buy new paddles for kayaks", false);
        mDbAdapter.createReminder("Call accountant about tax returns", false);
        mDbAdapter.createReminder("Buy 300,000 shares of Google", false);
        mDbAdapter.createReminder("Call the Dalai Lama back", true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reminder, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void fireCustomDialog (final Reminder reminder){
        final Dialog dialog =new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);
        TextView titleview = (TextView)dialog.findViewById(R.id.custom_title);
        final EditText editText = (EditText) dialog.findViewById(R.id.custom_edit_reminder);
        Button commitButton= (Button) dialog.findViewById(R.id.custom_button_commit);
        final CheckBox checkBox = (CheckBox) dialog.findViewById(R.id.custom_check_box);
        checkBox.setChecked(true);//important Reminders
        LinearLayout rootLayout = (LinearLayout) dialog.findViewById(R.id.custom_root_layout);
        final boolean isEditOperation =(reminder !=null);
        if(isEditOperation){
            titleview.setText("Edit Reminder");
            checkBox.setChecked(reminder.getImportant()==1);
            editText.setText(reminder.getContent());
            rootLayout.setBackgroundColor(getResources().getColor(R.color.blue));
        }
        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reminderText =editText.getText().toString();
                if(isEditOperation){
                    Reminder reminderEdited =new Reminder(reminder.getId(),reminderText,
                            checkBox.isChecked()?1:0);
                    mDbAdapter.updateReminder(reminderEdited);
                }else{
                    mDbAdapter.createReminder(reminderText,checkBox.isChecked());
                }
                mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                dialog.dismiss();
            }
        });
        Button buttoncancle = (Button) dialog.findViewById(R.id.custom_button_cancel);
        buttoncancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                //create new Reminder
                fireCustomDialog(null);
                Log.d(getLocalClassName(), "Create new Reminder");
                return true;
            case R.id.action_exit:
                finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Reminder Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.tryreminder.myandroid.newreminder/http/host/path")
        );
        AppIndex.AppIndexApi.start(mClient, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Reminder Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.tryreminder.myandroid.newreminder/http/host/path")
        );
        AppIndex.AppIndexApi.end(mClient, viewAction);
        mClient.disconnect();
    }
}
