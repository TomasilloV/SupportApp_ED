package com.example.mysupportapp

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        // SI QUIERO PROCESAR DATOS DE LA NOTIFICACION
        //if (message.getData().size() > 0) {
        //    Log.d(TAG, "Message data payload: " + message.getData());
        //}
        //if (message.getNotification() != null) {
        //    Log.d(TAG, "Message Notification Body: " + message.getNotification().getBody());
        //}

        super.onMessageReceived(message)
    }


    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}
