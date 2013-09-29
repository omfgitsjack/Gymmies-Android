package com.example.gymmies;

import android.content.Context;
import android.content.Intent;


/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class CommonUtilities {

    /**
     * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
     */
    public static final String SERVER_URL = "http://www.thejackyiu.com/utsc_gymmies_server";

    /**
     * URL for localhost
     */
     public static final String LOCALHOST_URL = "142.1.24.232"; 
    
    /**
    * URL for the messaging server
    */
    public static String MESSAGINGSERVER_URL = "http://"+LOCALHOST_URL+":9090";
    
    public static String LOCALHOSTSCRIPTS_URL = "http://"+LOCALHOST_URL+":8080/openfire";
    
    
    /**
     * Google API project id registered to use GCM.
     */
    public static final String SENDER_ID = "1084462279661";

    /**
     * CURRENTLY LOGGED IN USER, their uid
     */
    private static String CURRENT_USERID = "";
    
    // GET and SET methods for CURRENT_USER
    public static void setCURRENT_USERID(String uid){
    	CURRENT_USERID = uid; 
    }
    public static String getCURRENT_USERID(){
    	return CURRENT_USERID;
    }
    
    public static String username;
    public static String firstname;
    public static String lastname;
    public static String gender;
    
    
    /**
     * Tag used on log messages.
     */
    public static final String TAG = "UTSCGymmies";

    /**
     * Intent used to display a message in the screen.
     */
    public static final String DISPLAY_MESSAGE_ACTION =
            "com.example.gymmies.DISPLAY_MESSAGE";

    /**
     * Intent's extra that contains the message to be displayed.
     */
    public static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        
        context.sendBroadcast(intent);
    }
}