package com.kaliya.lbr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private ListView listView;
    public static ArrayList<String> ArrayofTask = new ArrayList<String>();
    private List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBHandler db = new DBHandler(this, null, null, 1);
        //List<Reminder> reminders = db.getAllReminders();

        /*for (Reminder cn : reminders) {
            String log = "Id: "+cn.get_id()+" , Task Name: " + cn.get_taskname() +
                    " , Location: " + cn.get_location()+" , Date: " + cn.get_date()
                    +" , Time: "+cn.get_time();
            // Writing Contacts to log
            Log.d("Task: ", log);

        }*/
        list=db.getAllReminders();
        listView = (ListView)findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, ArrayofTask);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });

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
