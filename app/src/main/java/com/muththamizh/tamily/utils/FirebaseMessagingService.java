package com.muththamizh.tamily.utils;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.muththamizh.tamily.ui.activities.Splash;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    NotificationUtils notificationUtils;
    private static final String TAG = FirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d("onMessage",remoteMessage.getNotification().getBody());
        if (remoteMessage == null)
            return;

        if (remoteMessage.getNotification() != null){
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("title",remoteMessage.getNotification().getTitle());
                jsonObject.put("message",remoteMessage.getNotification().getBody());
                handleDataMessage(jsonObject);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if (remoteMessage.getData().size() > 0) {
            try {
                JSONObject json = new JSONObject(remoteMessage.getData());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.d("Exception",e.getMessage());
                e.printStackTrace();
            }
        }

    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());
        try {
            String title = json.getString("title");
            String message = json.getString("message");
            Intent resultIntent = new Intent(getApplicationContext(), Splash.class);
            showNotificationMessage(getApplicationContext(), title, message, resultIntent);
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private void showNotificationMessage(Context context, String title, String message, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, intent);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        //Toast.makeText(this, "token:"+token, Toast.LENGTH_SHORT).show();
    }
}