package com.loreaudio.lore;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by priya on 9/15/2018.
 */

public class PlayerNotificationHelperActivity extends Activity {
    private PlayerNotificationHelperActivity ctx;
    private MusicService musicSrv;
    private boolean musicBound;

    private String curAction;

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("onServiceConnected", "Connecting from Player Notification");
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            if (musicSrv == null) {
                Log.i("onServiceConnected", "Musicservice is null in player notification");
            } else {
                Log.i("onServiceConnected", "Musicservice is NOT null in player notification");
                doCurAction();
            }

            //pass list
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("NotificationHelper", "Launched!");
        Intent playIntent = new Intent(this, MusicService.class);
        boolean x = bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        if (x) {
            Log.i("NotificationHelperCreat", "Service Connected");
        } else {
            Log.i("NotificationHelperCreat", "Service Disconnected");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        ctx = this;
        String action = (String) getIntent().getExtras().get("DO");
        Log.i("NotificationHelperStart", action);
        this.curAction = action;
        if (!action.equals("reboot"))
            finish();
    }

    private void doCurAction() {
        if (musicSrv == null) {
            Log.i("Notif DoCurAction", "Music Service is null. Returning...");
            return;
        }
        if (curAction.equals("togglePlay")) {
            Log.i("Notif DoCurAction", "Action toggleplay!");
            if (musicSrv.isPng()) {
                Log.i("Notif DoCurAction", "Music Service is playing");
                musicSrv.pausePlayer();
            } else {
                musicSrv.go();
            }

        } else if (curAction.equals("rewind")) {
            Log.i("Notif DoCurAction", "Action rewind!");
            musicSrv.rewind();
        } else if (curAction.equals("playStory")) {
            Log.i("Notif DoCurAction", "Action Play story!");
            musicSrv.playSong(0);
        } else if (curAction.equals("playQuestion")) {
            Log.i("Notif DoCurAction", "Action play question!");
            if (musicSrv.getPlaylistLength() > 1) {
                musicSrv.playSong(1);
            }
        } else if (curAction.equals("ffw")) {
            Log.i("Notif DoCurAction", "Action ffw!");
            musicSrv.ffw();
        }

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unbindService(musicConnection);
    }
}

