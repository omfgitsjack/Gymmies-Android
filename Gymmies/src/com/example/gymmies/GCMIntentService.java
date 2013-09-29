package com.example.gymmies;

import static com.example.gymmies.CommonUtilities.SENDER_ID;
import static com.example.gymmies.CommonUtilities.displayMessage;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.gymmies.R;
import com.example.gymmies.utilities.ServerUtilities;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

    @SuppressWarnings("hiding")
    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, getString(R.string.gcm_registered));
        GCMRegistrar.setRegisteredOnServer(context, true);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            ServerUtilities.unregister(context, registrationId);
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.i(TAG, "Ignoring unregister callback");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        if (intent.getExtras().getString("uid").equals(CommonUtilities.getCURRENT_USERID())){
        	String messagetype = intent.getExtras().getString("messagetype");
        	if (messagetype.equals("sendinvitation")){
        		String message = intent.getExtras().getString("message");
        		String gid = intent.getExtras().getString("gid");
        		Intent i = new Intent(context, Group.class);
        		i.putExtra("isinvited", true);
        		i.putExtra("gid", gid);
        		System.out.println("msg:"+intent.getExtras().get("message"));
        		// Send a notification with the message.
        		generateNotification(context, message, i);
        	} else if (messagetype.equals("friendrequest")){
        		String message = intent.getExtras().getString("message");
        		Intent i = new Intent(context, Friendprofile.class);
        		i.putExtra("fid", intent.getExtras().getString("senderid"));
        		i.putExtra("friendname", intent.getExtras().getString("sendername"));
        		// Send a notification with the message.
        		generateNotification(context, message, i);
        	}
        }
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        // notifies user
      //  generateNotification(context, message);
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message, Intent notificationIntent) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);
        //Intent notificationIntent = new Intent(context, Login.class);
        // set intent so it does not start a new activity
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        System.out.println("Generate Notification gid is: "+notificationIntent.getExtras().get("gid"));
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

}
