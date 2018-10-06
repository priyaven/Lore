package com.loreaudio.lore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by priya on 9/15/2018.
 * https://stackoverflow.com/questions/14671453/catch-on-swipe-to-dismiss-event
 */

public class NotificationDismissedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getExtras().getInt("notificationId");
      /* Your code to handle the event here */
    }
}