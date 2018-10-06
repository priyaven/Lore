package com.loreaudio.lore;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RemoteViews;

/**
 * Created by priya on 9/1/2018.
 */

// https://stackoverflow.com/questions/12526228/how-to-put-media-controller-button-on-notification-bar

public class PlayerNotification extends Notification {
    private Context ctx;
    private NotificationManager mNotificationManager;
    private static final String MyOnClick = "Button OnClick";

    public int notifId = 548853;

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
        notification.icon=R.drawable.logo1;
        notification.deleteIntent = createOnDismissedIntent(ctx, notifId);

        RemoteViews contentView=new RemoteViews(ctx.getPackageName(), R.layout.activity_playernotification);

        //set the button listeners
        Log.i("PlayerNotification", "Setting listeners");
        setListeners(contentView, ctx);

        notification.contentView = contentView;
        //notification.flags |= Notification.FLAG_ONGOING_EVENT;
        CharSequence contentTitle = "From Shortcuts";
        mNotificationManager.notify(notifId, notification);
    }

    public void setListeners(RemoteViews view, Context ctx){
        Intent togglePlay = new Intent(ctx,PlayerNotificationHelperActivity.class);
        togglePlay.putExtra("DO", "togglePlay");
        PendingIntent pTogglePlay = PendingIntent.getActivity(ctx, 1, togglePlay, 0);
        view.setOnClickPendingIntent(R.id.playpause_notif, pTogglePlay);

        Intent rewind = new Intent(ctx,PlayerNotificationHelperActivity.class);
        rewind.putExtra("DO", "rewind");
        PendingIntent pRewind = PendingIntent.getActivity(ctx, 2, rewind, 0);
        view.setOnClickPendingIntent(R.id.rewind_notif, pRewind);

        Intent playStory = new Intent(ctx,PlayerNotificationHelperActivity.class);
        playStory.putExtra("DO", "playStory");
        PendingIntent pPlayStory = PendingIntent.getActivity(ctx, 3, playStory, 0);
        view.setOnClickPendingIntent(R.id.playstory_notif, pPlayStory);

        Intent playQuestion = new Intent(ctx,PlayerNotificationHelperActivity.class);
        playQuestion.putExtra("DO", "playQuestion");
        PendingIntent pPlayQuestion = PendingIntent.getActivity(ctx, 4, playQuestion, 0);
        view.setOnClickPendingIntent(R.id.playquestion_notif, pPlayQuestion);

        Intent ffw = new Intent(ctx,PlayerNotificationHelperActivity.class);
        ffw.putExtra("DO", "ffw");
        PendingIntent pffw = PendingIntent.getActivity(ctx, 5, ffw, 0);
        view.setOnClickPendingIntent(R.id.forward_notif, pffw);

    }

    private PendingIntent createOnDismissedIntent(Context context, int notificationId) {
        Intent intent = new Intent(context, NotificationDismissedReceiver.class);
        intent.putExtra("notificationId", notificationId);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context.getApplicationContext(),
                        notificationId, intent, 0);
        return pendingIntent;
    }

}
