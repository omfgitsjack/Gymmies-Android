package com.example.gymmies;

import java.util.ArrayList;
import java.util.Iterator;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.ServiceDiscoveryManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class messagesetup {

	
	public static Connection connection;
	public static ChatManager chatmanager;
	public static MessageListener messagelistener;
	public static Context ctx;
	static NotificationManager notificationManager;
	static int notifid;
	static PacketListener pListener;
	static String[] contactMenu;
	static ArrayList<String> contactNameList = new ArrayList<String>();
	static boolean receivedofflinemsg;
	/**
	 * Class: MessageTask Purpose: Initialize the chatting services, connect to
	 * openfire server, setup chatmanager and chatlistener
	 * 
	 * @author jack
	 * 
	 */
	static class init extends AsyncTask<Context, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Context... params) {
			try {
				notifid = 0;
				ctx = params[0];
				// Configure Connection
				ConnectionConfiguration config = new ConnectionConfiguration(
						CommonUtilities.LOCALHOST_URL, 5222);
				config.setCompressionEnabled(true);
				config.setSASLAuthenticationEnabled(false);
				messagesetup.connection = new XMPPConnection(config);
				// Connect to the server
				messagesetup.connection.connect();
				// Setup offline msg manager
				
				// Log into the server
				// Param[0] contains the name of the 'friend'
				messagesetup.connection.login(CommonUtilities.username, CommonUtilities.username);
				System.out.println("I won");
				chatmanager = messagesetup.connection.getChatManager();
				//messagelistener = new MyMessageListener();
				chatmanager.addChatListener(new ChatManagerListenerImpl());
				System.out.println("Passed4");
				// Initiate the NotificationManager
				notificationManager = (NotificationManager) params[0].getSystemService(Context.NOTIFICATION_SERVICE);
				// variable to check if you actually received offline messages.
				receivedofflinemsg = false;
				// Get offline messages
				System.out.println("offlinemessages");
				PacketListener pListener = new PacketListener() {
					@Override
					public void processPacket(Packet packet) { 
						
						// TODO Auto-generated method stub
		                Message message = (Message) packet;
		                if (message.getBody() != null) {
		                    String fromName = StringUtils.parseBareAddress(message
		                            .getFrom()).split("@localhost")[0];
		                    Log.i("XMPPClient", "Got text [" + message.getBody()
		                            + "] from [" + fromName + "]");
		                    // Store the name of the person into array.
		                    boolean containsname = false;
		                    Iterator<String> j = contactNameList.listIterator();
		                    while (j.hasNext()){
		                    	if (j.next().equals(fromName)){
		                    		containsname = true;
		                    		break;
		                    	}
		                    }
		                    if (containsname == false){
		                    	messagesetup.receivedofflinemsg = true;
		                    	System.out.println("Added"+fromName);
		                    	contactNameList.add(fromName);
		                    }
		                    
		                }
					}
		        };
				PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
		        messagesetup.connection.addPacketListener(pListener, filter);
		        // After adding the packetlistener, sleep for 10 seconds, so that after the offline messages arrive, we can remove the packetlistener.
		        Thread.sleep(10000);
		        messagesetup.connection.removePacketListener(pListener);
		        // Iterate through our array and send a notification regarding WHO messaged the person while he/she was offline
		        if (receivedofflinemsg==true){
		        	 int nummsg = 0;
				        // Setup the style
				        NotificationCompat.InboxStyle prevNotifStyle = new NotificationCompat.InboxStyle()
						.setBigContentTitle("Offline Messages");
				        
				        
				        NotificationCompat.Builder notif = new NotificationCompat.Builder(ctx)
						.setContentIntent(PendingIntent.getActivity( 
					    	    ctx,
					    	    0,
					    	    new Intent(ctx, Friends.class),
					    	    PendingIntent.FLAG_CANCEL_CURRENT
					    	))
						.setSmallIcon(R.drawable.message)
						.setContentTitle("Offline Messages")
						.setContentText("Expand to view from whom")
						.setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), 
		                                    R.drawable.big_message));
				        
				        Iterator<String> j = contactNameList.listIterator();
		                while (j.hasNext()){
		                	prevNotifStyle.addLine(nummsg+": "+j.next()); 
		                	notif.setNumber(++nummsg);
		                }
		                // Build notification and send it.
		                notif.setStyle(prevNotifStyle); 
		                Notification notif2 = notif.build();
						notificationManager.notify(notifid++, notif2);
		        }
		       
                
		        System.out.println("DONE");
			} catch (Exception e) {
				e.printStackTrace();
			} 
			return null;
		}

		@Override
		protected void onPostExecute(String msg) {
			
		} 
	}
	
	/**
	 * Class: ChatManagerListenerImpl Purpose: Acts as a chat listener that will
	 * listen to any incoming chat requests.
	 */
	static class ChatManagerListenerImpl implements ChatManagerListener {
		public void chatCreated(final Chat chat, boolean createdLocally) {
			if (!createdLocally) {
				chat.addMessageListener(new MessageListener() {
					final int notifID = notifid++;
					final String from = chat.getParticipant().split("@localhost")[0];
					// Initialize Message Listener for the first time.
					int nummsg = 0;
					// Setup InboxStyle 
					 NotificationCompat.InboxStyle prevNotifStyle = new NotificationCompat.InboxStyle()
					.setBigContentTitle("Message: "+from);
					 // Setup Notification
					 NotificationCompat.Builder prevNotif = new NotificationCompat.Builder(ctx)
					.setContentIntent(PendingIntent.getActivity( 
				    	    ctx,
				    	    notifID,
				    	    new Intent(ctx, FriendMessage.class).putExtra("friendname", from).putExtra("notifID",notifID),
				    	    PendingIntent.FLAG_CANCEL_CURRENT
				    	))
					.setSmallIcon(R.drawable.message)
					.setContentTitle("Message: "+from)
					.setContentText("expand")
					.setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), 
	                                    R.drawable.big_message));
					 
					@Override
					public void processMessage(Chat chat, Message message) {
						String body = message.getBody(); 
						prevNotifStyle.addLine(from+": "+body); 
						prevNotif.setStyle(prevNotifStyle); 
						prevNotif.setNumber(++nummsg);
						Notification notif = prevNotif.build();
						notif.flags |= Notification.FLAG_AUTO_CANCEL;	
						notificationManager.notify(notifID, notif);
					}	
				});
			}
		}
	}
	
	

	
	static class MyMessageListener implements MessageListener {
		public void processMessage(Chat chat, Message message) {
			String from = message.getFrom();
            String body = message.getBody();
            System.out.println(String.format("Received message '%1$s' from %2$s", body, from));
        }
	}

	
	/**
	 * Class: logout Purpose: Log the user out of the messaging (Display
	 * themselves as offline)
	 * 
	 * @author jack
	 * 
	 */
	static class logout extends AsyncTask<String, String, String> {
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected String doInBackground(String... msg) {
			try {
				messagesetup.connection.disconnect();
			} catch (Exception a) {
				a.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String msg) {
		}
	}
	
	public void sendMessage(String message, String buddyJID) throws XMPPException {
        System.out.println(String.format("Sending mesage '%1$s' to user %2$s", message, buddyJID));
        Chat chat = chatmanager.createChat(buddyJID, messagelistener);
        chat.sendMessage(message);
	}

	
}
