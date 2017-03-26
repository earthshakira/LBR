package com.kaliya.lbr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import static com.kaliya.lbr.MyGoogleApiClientService.googleApiClient;

public class MainActivity extends AppCompatActivity{

    private ListView listView;
    public static ArrayList<String> ArrayofTask = new ArrayList<String>();
    private List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("helo", "onCreate: ");
        DBHandler db = new DBHandler(this, null, null, 2);
        //List<Reminder> reminders = db.getAllReminders();

        /*for (Reminder cn : reminders) {
            String log = "Id: "+cn.get_id()+" , Task Name: " + cn.get_taskname() +
                    " , Location: " + cn.get_location()+" , Date: " + cn.get_date()
                    +" , Time: "+cn.get_time();
            // Writing Contacts to log
            Log.d("Task: ", log);

        }*/
        //ArrayofTask = new ArrayList<String>();
        list=db.getAllReminders();
        listView = (ListView)findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, ArrayofTask);
        listView.setAdapter(adapter);

//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(),((TextView) view).getText(), Toast.LENGTH_SHORT).show();
//            }
//        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DBHandler db = new DBHandler(getBaseContext(), null, null, 2);
                String x = (ArrayofTask.get(position)).split("\n")[0];
                Reminder deleter = db.getReminderByName(x);
                ArrayList<String> geofenceIds = new ArrayList<>();
                geofenceIds.add(deleter.get_id()+"");
                LocationServices.GeofencingApi.removeGeofences(googleApiClient,geofenceIds);
                db.deleteReminder(deleter);
                db.getAllReminders();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
                        android.R.layout.simple_list_item_1, ArrayofTask);
                listView.setAdapter(adapter);
                Toast.makeText(getApplicationContext(),"deleted task "+deleter.get_date()+" ->"+deleter.get_time(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        startService(new Intent(this, MyGoogleApiClientService.class));
        /*Vector<String> values = new Vector<String>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.activity_listview,values);*/

    }

    private void printDatabase() {
    }

    public void addTask(View view){
        Intent intent = new Intent(this, addRecord.class);
        startActivity(intent);
    }


}
