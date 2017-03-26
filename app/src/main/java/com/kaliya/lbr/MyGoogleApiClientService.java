package com.kaliya.lbr;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by earthshakira on 1/3/17.
 */

public class MyGoogleApiClientService extends Service {
    public static GoogleApiClient googleApiClient = null;
    String GEOFENCE_ID = "home_shizz";

    final String TAG = "APIClient";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: service started");
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.d(TAG, "onConnected: to Google API Client");
                        //startGeofenceMonitoring();
                        DBHandler db = new DBHandler(getBaseContext(), null, null, 2);
                        initGeofences(db.fetchAllReminders());
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d(TAG, "onConnectionSuspended: to google API Client");

                    }
                }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.d(TAG, "onConnectionFailed: to API Client ->" + connectionResult.getErrorMessage());
                    }
                }).build();
        googleApiClient.connect();
        Log.d(TAG, "onStartCommand: client build called");
        return super.onStartCommand(intent, flags, startId);
    }

    private void starLocationMonitoring() {
        Log.d(TAG, "starLocationMonitoring: called");

        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Log.d(TAG, "starLocationMonitoring: " + "no permission");
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged: " + location.getLatitude() + " , " + location.getLatitude());
            }
        });
    }

    private void initGeofences(ArrayList<Reminder> locations){
        Log.d(TAG, "initGeofences: no. of fences "+locations.size());
        for( Reminder x : locations){
            String[] locStr = x.get_location().split(",");
            startGeofenceMonitoring(Double.parseDouble(locStr[0]),Double.parseDouble(locStr[1]),String.valueOf(x.get_id()),get_date_obj(x));
        }
    }

    Date get_date_obj(Reminder x){
        String date[] = x.get_date().split("-");
        String time[] = x.get_time().split(":");
        int day = Integer.parseInt(date[0]),mm=Integer.parseInt(date[1]),y=Integer.parseInt(date[2]);
        int hours = Integer.parseInt(time[0]),min=Integer.parseInt(time[1]);
        return new Date(y,mm,day,hours,min);
    }
    private void startGeofenceMonitoring(double lat,double lon,String id,Date exp) {
        Log.d(TAG, "startGeofenceMonitoring: ");
        try {
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(id)
                    .setCircularRegion( lat, lon , 30)
                    .setExpirationDuration(exp.getTime())
                    .setNotificationResponsiveness(1000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
            GeofencingRequest geofenceRequest = new GeofencingRequest.Builder()
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .addGeofence(geofence).build();
            Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
            PendingIntent pendingIntent = PendingIntent.getService(this.getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (!googleApiClient.isConnected()) {
                Log.d(TAG, "startGeofenceMonitoring: googlepiClient not connected");
            } else {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                LocationServices.GeofencingApi.addGeofences(googleApiClient, geofenceRequest, pendingIntent)
                        .setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                if(status.isSuccess()){
                                    Log.d(TAG, "onResult: Successfully added Geofence");
                                }else{
                                    Log.d(TAG, "onResult: Failed to add Geofence");
                                }
                            }
                        });
            }
        }catch (Exception e){
            Log.d(TAG, "startGeofenceMonitoring: Error"+e.getMessage());
        }
    }
    private void stopGeofenceMonitoring(String x){
        Log.d(TAG, "stopGeofenceMonitoring: ");
        ArrayList<String> geofenceIds = new ArrayList<>();
        geofenceIds.add(x);
        LocationServices.GeofencingApi.removeGeofences(googleApiClient,geofenceIds);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}