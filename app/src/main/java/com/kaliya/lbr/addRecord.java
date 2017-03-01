        package com.kaliya.lbr;

        import android.app.DatePickerDialog;
        import android.app.TimePickerDialog;
        import android.content.Intent;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.view.View;
        import android.widget.DatePicker;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.TimePicker;

        import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
        import com.google.android.gms.common.GooglePlayServicesRepairableException;
        import com.google.android.gms.location.LocationRequest;
        import com.google.android.gms.location.places.Place;
        import com.google.android.gms.location.places.ui.PlacePicker;

        import java.util.Calendar;


public class addRecord extends AppCompatActivity implements
        View.OnClickListener {

    EditText txtDate, txtTime, getPlace, taskName, fetchLocation;
    private int mYear, mMonth, mDay, mHour, mMinute;
    int PlACE_PICKER_REQUEST = 1;
    Double latitude, longitude;

    DBHandler db;
    /*GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    TextView mLatitudeText = null;
    TextView mLongitudeText = null;*/
    LocationRequest mLocationRequest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        taskName = (EditText) findViewById(R.id.taskName);
        txtDate = (EditText) findViewById(R.id.date);
        txtTime = (EditText) findViewById(R.id.time);
        getPlace = (EditText) findViewById(R.id.fetchLocation);

        txtDate.setOnClickListener(this);
        txtTime.setOnClickListener(this);

        db = new DBHandler(this, null, null, 2);

        //launching the map acivity
        getPlace.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                Intent intent;
                try {
                    intent = builder.build(addRecord.this);
                    startActivityForResult(intent, PlACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });


    }



    public void saveTask(View view) {
        Reminder reminder = new Reminder(taskName.getText().toString(), latitude+","+longitude, txtDate.getText().toString(), txtTime.getText().toString(),getPlace.getText().toString());
        db.addReminder(reminder);
        stopService(new Intent(this,MyGoogleApiClientService.class));
        startService(new Intent(this,MyGoogleApiClientService.class));
        Intent intent = new Intent(this, MainActivity.class);
        /*Bundle bundle = new Bundle();
        bundle.putString("taskName", String.valueOf(taskName));
        bundle.putString("Location", String.valueOf(fetchLocation));
        intent.putExtras(bundle);*/
        startActivity(intent);

    }

    public void clearTextFields(View view) {
        // Set all the text views to blank
        TextView tempName = (TextView) this.findViewById(R.id.taskName);
        TextView tempLocation = (TextView) this
                .findViewById(R.id.fetchLocation);
        TextView tempDate = (TextView) this.findViewById(R.id.date);
        TextView tempTime = (TextView) this.findViewById(R.id.time);
        tempName.setText("");
        tempLocation.setText("");
        tempDate.setText("");
        tempTime.setText("");
    }

    public void home(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //Start the place picker if conditions are satisfied
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PlACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
                String address = (String) (place.getAddress());
                getPlace.setText(address);
            }
        }
    }


    //Get date and Time
    @Override
    public void onClick(View v) {

        if (v == txtDate) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == txtTime) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            txtTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }

    }



}
