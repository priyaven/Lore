package com.loreaudio.lore;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.widget.MediaController.MediaPlayerControl;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayChapterActivity extends AppCompatActivity implements MediaPlayerControl {

    private MusicController controller;
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;
    private ArrayList<String> songList;

    Story curStory;
    int curPosition = 1;
    int prevPosition = 0;
    Chapter curChapter;
    Chapter prevChapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_chapter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        curStory = (Story) getIntent().getSerializableExtra("CurStory");
        curPosition = (int) getIntent().getIntExtra("CurPosition", 1);
        prevPosition = (int) getIntent().getIntExtra("PrevPosition", 0);

        fix_chapter();


        //TextView storyTitle = (TextView) findViewById(R.id.textview) ;
        //storyTitle.setText(curStory.getTitle());

        TextView chapterTitle = (TextView) findViewById(R.id.textView);
        chapterTitle.setText(curChapter.getTitle());

        setController();

        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("audio-done"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            if(!curChapter.isEnd()) {
                int audiotype = intent.getIntExtra("audiotype", 999);
                if (audiotype == 0) {
                    musicSrv.playSong(1);
                } else if (audiotype == 1) {
                    yesNoListener();
                }
            }
        }
    };

    private void fix_chapter(){
        prevChapter = curChapter;
        curChapter = curStory.chapters.get(new Integer(curPosition));
        songList = new ArrayList<String>();
        songList.add(curChapter.mp3File);
        songList.add(curChapter.mp3QuestionFile);

        if (curChapter.isEnd()){
            Button fwd = (Button) findViewById(R.id.button);
            fwd.setEnabled(false);
            Button pause = (Button) findViewById(R.id.button2);
            pause.setEnabled(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
        //musicSrv.playSong();
    }

    private void setController(){
        //set the controller up
        controller = new MusicController(this);
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        controller.setMediaPlayer(this);
        //controller.setAnchorView(findViewById(R.id.song_list));
        controller.setEnabled(true);
    }

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    //play next
    private void playNext(){
        musicSrv.playNext();
        controller.show(0);
    }

    //play previous
    private void playPrev(){
        musicSrv.playPrev();
        controller.show(0);
    }

    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void seekTo(int pos) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }


    public void songpicked(View view){
        musicSrv.playSong(0);
    }

    public void goToQuestion(View view) {
        musicSrv.playSong(1);

    }

    private void yesNoListener(){
        Intent intent = new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

        try {
            startActivityForResult(intent, 1);
        } catch (ActivityNotFoundException a) {
            Toast t = Toast.makeText(getApplicationContext(),
                    "Opps! Your device doesn't support Speech to Text",
                    Toast.LENGTH_SHORT);
            t.show();
        }

    }
    public void listenToYesNo(View view) {
        yesNoListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    String yesNoText = text.get(0);

                    if (yesNoText.equalsIgnoreCase("Yes")) {
                        prevPosition = curPosition;
                        curPosition = curChapter.getOnYes();
                        fix_chapter();

                    } else {
                        prevPosition = curPosition;
                        curPosition = curChapter.getOnNo();
                        fix_chapter();
                    }
                    musicSrv.setList(songList);
                    musicSrv.playSong(0);
                }
                break;
            }

        }
    }
}
