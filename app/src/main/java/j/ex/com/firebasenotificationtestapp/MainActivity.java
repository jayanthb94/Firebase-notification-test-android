package j.ex.com.firebasenotificationtestapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView.ViewHolder hv;
    private RecyclerView.LayoutManager warninglm, infolm;
    private RecyclerView.Adapter warningAdapter, infoAdapter;
    private ArrayList<String> warningList;
    private ArrayList<String> infoList;
    private LocalBroadcastManager manager;
    public static final String ACTION = "Notification.ACTION.STRING";
    public static final int WARNING = 1;
    public static final int INFO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        warningList = new ArrayList<String>();
        infoList = new ArrayList<String>();

        manager = LocalBroadcastManager.getInstance(this);

        RecyclerView wrv = (RecyclerView) findViewById(R.id.warning_recycler_view);
        RecyclerView irv = (RecyclerView) findViewById(R.id.info_recycler_view);

        warninglm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        infolm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        wrv.setLayoutManager(warninglm);
        irv.setLayoutManager(infolm);


        warningAdapter = new NotificationAdapter(warningList, WARNING);
        infoAdapter = new NotificationAdapter(infoList, INFO);

        wrv.setAdapter(warningAdapter);
        irv.setAdapter(infoAdapter);

        boolean readDone = false;
        if (getIntent().getExtras() != null) {
            if(getIntent().getExtras().get(NotificationTypes.INFO_MESSAGE) != null) {
                String infoMessage = getIntent().getExtras().get(NotificationTypes.INFO_MESSAGE).toString();
                readDone = true;
                new DatabaseWriteTask(getApplicationContext(), MainActivity.INFO, infoMessage).execute();
            }
            if (getIntent().getExtras().get(NotificationTypes.WARNING_MESSAGE) != null) {
                String warningMessage = getIntent().getExtras().get(NotificationTypes.WARNING_MESSAGE).toString();
                readDone = true;
                new DatabaseWriteTask(getApplicationContext(), MainActivity.WARNING, warningMessage).execute();
            }
        }
        if(!readDone) {
            new DataBaseReadTask(getApplicationContext(), MainActivity.INFO).execute();
            new DataBaseReadTask(getApplicationContext(), MainActivity.WARNING).execute();
        }
    }

    @Override
    protected void onPause() {
        manager.unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter mif = new IntentFilter(ACTION);
        manager.registerReceiver(mReceiver, mif);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new DataBaseReadTask(getApplicationContext(), MainActivity.INFO).execute();
            new DataBaseReadTask(getApplicationContext(), MainActivity.WARNING).execute();
        }
    };

    private class DatabaseWriteTask extends AsyncTask<Void, Void, Void>{
        private String message;
        private Context c;
        private int choice;

        public DatabaseWriteTask(Context c, int choice, String message) {
            this.choice = choice;
            this.c = c;
            this.message = message;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            NotificationDbHelper mdbHelper = NotificationDbHelper.getInstance(c);
            if(choice == MainActivity.INFO){
                mdbHelper.insert(message, NotificationTypes.INFO_MESSAGE);
            }
            else {
                mdbHelper.insert(message,NotificationTypes.WARNING_MESSAGE);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new DataBaseReadTask(c,choice).execute();
        }
    }

    private class DataBaseReadTask extends AsyncTask<Void, Void, ArrayList<String>>{
        private Context c;
        private int Choice;
        DataBaseReadTask(Context c, int Choice){
            this.c = c;
            this.Choice = Choice;
        }

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            NotificationDbHelper ndbHelper = NotificationDbHelper.getInstance(c);
            Cursor cu = null;
            if(Choice == MainActivity.INFO)
                cu = ndbHelper.queryInfoMessages();
            else
                cu = ndbHelper.queryWarningMessages();
            ArrayList<String> ar = new ArrayList<String>();
            while(cu.moveToNext()){
                String message = cu.getString(cu.getColumnIndex(NotificationContract.NotificationEntry.COLUMN_NAME_VALUE));
                ar.add(message);
            }
            cu.close();
            return ar;
        }

        @Override
        protected void onPostExecute(ArrayList<String> arrayList) {
            super.onPostExecute(arrayList);
            if(Choice == MainActivity.INFO){
                infoList.clear();
                infoList.addAll(arrayList);
                infoAdapter.notifyDataSetChanged();
            } else {
                warningList.clear();
                warningList.addAll(arrayList);
                warningAdapter.notifyDataSetChanged();
            }
        }
    }
}
