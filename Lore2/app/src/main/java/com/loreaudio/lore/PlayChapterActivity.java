package com.loreaudio.lore;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import android.widget.MediaController.MediaPlayerControl;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayChapterActivity extends AppCompatActivity implements MediaPlayerControl {

    private MusicController controller;
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;
    private ArrayList<String> songList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_chapter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Story curStory = (Story) getIntent().getSerializableExtra("CurStory");
        int curPosition = (int) getIntent().getIntExtra("CurPosition", 1);
        int prevPosition = (int) getIntent().getIntExtra("PrevPosition", 0);

        Chapter curChapter = curStory.chapters.get(new Integer(curPosition));
        Chapter prevChapter = null;
        if (prevPosition > 0){
            prevChapter = curStory.chapters.get(new Integer(prevPosition));
        }

        //TextView storyTitle = (TextView) findViewById(R.id.textview) ;
        //storyTitle.setText(curStory.getTitle());

        TextView chapterTitle = (TextView) findViewById(R.id.textView);
        chapterTitle.setText(curChapter.getTitle());

        songList = new ArrayList<String>();
        songList.add(curChapter.mp3File);
        songList.add(curChapter.mp3QuestionFile);

        setController();


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
        musicSrv.playSong();
    }

}
