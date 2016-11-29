package j.ex.com.firebasenotificationtestapp;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d("TAG", "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d("TAG", "Message data payload: " + remoteMessage.getData());
            Intent it = new Intent(MainActivity.ACTION);
            NotificationDbHelper mdbHelper = NotificationDbHelper.getInstance(getApplicationContext());
            String infoMessage = remoteMessage.getData().get(NotificationTypes.INFO_MESSAGE);
            if(infoMessage != null && !infoMessage.isEmpty()){
                mdbHelper.insert(infoMessage, NotificationTypes.INFO_MESSAGE);
            }
            String warningMessage = remoteMessage.getData().get(NotificationTypes.WARNING_MESSAGE);
            if(warningMessage != null && !warningMessage.isEmpty()) {
                mdbHelper.insert(warningMessage,NotificationTypes.WARNING_MESSAGE);
            }
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(it);
        }

    }

}
