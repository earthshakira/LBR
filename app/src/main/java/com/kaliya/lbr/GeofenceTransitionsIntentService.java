
package com.kaliya.lbr;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;


public class GeofenceTransitionsIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, impor\   tant only for debugging.
     */
    String TAG = "GeofenceService";
    DBHandler db;
    public static final String TRANSITION_INTENT_SERVICE = "ReceiveTransitionsIntentService";
    public GeofenceTransitionsIntentService() {
        super(TRANSITION_INTENT_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        Log.d(TAG, "onHandleIntent: event triggered");
        if(event.hasError()){
            //TODO: Handle Error
        } else {
            int transition = event.getGeofenceTransition();
            List<Geofence> geofences = event.getTriggeringGeofences();
            for(int i=0;i<geofences.size();i++) {
                Geofence geofence = geofences.get(i);
                String requestId = geofence.getRequestId();
                db = new DBHandler(getBaseContext(), null, null, 2);
                Reminder now = db.getReminder(Integer.parseInt(requestId));
                db.close();
                if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                    Log.d(TAG, "onHandleIntent: Entering " + requestId);
                    notifier(now.get_taskname(), "Entering " + now.get_place(), Integer.parseInt(requestId));
                } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                    Log.d(TAG, "onHandleIntent: Exiting  " + requestId);
                    notifier(now.get_taskname(), "Exiting " + now.get_place(), Integer.parseInt(requestId));
                }
            }
        }
    }


    public void notifier(String title,String text,int id) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                        .setContentTitle(title)
                        .setContentText(text);
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(id, mBuilder.build());
    }
}
