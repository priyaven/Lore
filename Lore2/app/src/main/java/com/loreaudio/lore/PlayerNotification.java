package com.loreaudio.lore;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.widget.RemoteViews;

/**
 * Created by priya on 9/1/2018.
 */

// https://stackoverflow.com/questions/12526228/how-to-put-media-controller-button-on-notification-bar

public class PlayerNotification extends Notification {
    private Context ctx;
    private NotificationManager mNotificationManager;

    @SuppressLint("NewApi")
    public PlayerNotification(Context ctx){
        super();
        this.ctx=ctx;
        String ns = Context.NOTIFICATION_SERVICE;
        mNotificationManager = (NotificationManager) ctx.getSystemService(ns);
        CharSequence tickerText = "Shortcuts";
        long when = System.currentTimeMillis();
        Notification.Builder builder = new Notification.Builder(ctx);
        @SuppressWarnings("deprecation")
        Notification notification=builder.getNotification();
        notification.when=when;
        notification.tickerText=tickerText;
        notification.icon=R.drawable.user_icon;

        RemoteViews contentView=new RemoteViews(ctx.getPackageName(), R.layout.activity_playernotification);

        //set the button listeners
        setListeners(contentView);

        notification.contentView = contentView;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        CharSequence contentTitle = "From Shortcuts";
        mNotificationManager.notify(548853, notification);
    }

    public void setListeners(RemoteViews view){

    }
}
