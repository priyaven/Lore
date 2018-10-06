package com.loreaudio.lore;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by priya on 9/16/2017.
 */

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener{

    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<String[]> songs;
    private int curChapterid;
    private int boundChapterid;
    private boolean boundFromRestart;
    //current position
    private int songPosn;

    private int lastPlayedSong = 999;

    public boolean playing = false;

    private final IBinder musicBind = new MusicBinder();

    public void onCreate(){
        //create the service
        super.onCreate();
        //initialize position
        songPosn=0;
        //create player
        player = new MediaPlayer();
        initMusicPlayer();

        //playSong(0);
    }

    public void initMusicPlayer(){
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        //player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i("OnCompletion Lore", "Lastplayedsong:" + Integer.toString(lastPlayedSong));
                audioDone(lastPlayedSong);
            }
        });

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        this.boundChapterid = intent.getIntExtra("CurChapter", 0);
        this.boundFromRestart = intent.getBooleanExtra("fromRestart", false);
        Log.i("MusicService:onBind", "Binding chapter " + Integer.toString(this.boundChapterid));
        return musicBind;

    }

    @Override
    public boolean onUnbind(Intent intent){
        super.onUnbind(intent);
        int intentboundid = intent.getIntExtra("CurChapter", 0);
        Log.i("MusicService:onUnbind", "Curchapter:" + Integer.toString(intentboundid) + " boundchapter:" + Integer.toString(this.boundChapterid));
        if((intentboundid == this.boundChapterid) && (intentboundid !=0)) { // check to make sure unbind happens only when that chapter's activity instance calls unbind.
            player.stop();
            player.release();
        }
        return true; // True means you want onrebind called. was originally false.
    }

    public void playSong(int i){
        //play a song
        player.reset();
        String trackUri = songs.get(i)[0];
        try{
            player.setDataSource(trackUri);
        }
        catch(Exception e){
            //e.printStackTrace();
            Log.i("MusicService:playSong", "Exception in setting datasource " + trackUri );
            String trackUriStream = songs.get(i)[1];
            try {
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDataSource(trackUriStream);
            } catch (IOException e1) {
                e1.printStackTrace();
                Log.i("MusicService:playSong", "exception in setting streaming URL " + trackUriStream);

            }
        }
        player.setOnPreparedListener(this);
        player.prepareAsync();
        lastPlayedSong = i;
    }

    public void setList(ArrayList<String[]> theSongs, int chid){
        songs=theSongs;
        if(boundFromRestart || (lastPlayedSong == 0)){
            playSong(1);
        } else {
            playSong(0);
        }
        curChapterid = chid;
       /* if(lastPlayedSong > 0) {//if(lastPlayedSong == 999) {
            playSong(0);
        } else {
            playSong(1);
        }*/
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        if(player !=null) {
            try {
                return player.isPlaying();
            } catch(IllegalStateException il) {
                Log.i("MusicService.isPng", "Illegal State Exception caught");
                return false;

            }
        }
        return false;
    }

    public void pausePlayer(){
        player.pause();
    }

    public void stopPlayer() { player.stop();}

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }

    public void playPrev() {

    }

    public void playNext() {

    }

    public void rewind() {

        int current_position = getPosn();
        int duration = getDur();
        int rewind_duration = 5000;
        if (duration > 300000){
            rewind_duration = 15000;
        }
        int new_position = current_position - rewind_duration;
        if (new_position < 0){
            new_position = 0;
        }
        seek(new_position);

    }

    public void ffw() {

        int current_position = getPosn();
        int duration = getDur();
        int ffw_duration = 5000;
        if (duration > 300000){
            ffw_duration = 15000;
        }
        int new_position = current_position + ffw_duration;
        if (new_position > duration){
            new_position = duration;
        }
        seek(new_position);

    }


    private void audioDone(int i) {
        Intent intent = new Intent("audio-done");
        // add data
        intent.putExtra("audiotype", i);
        intent.putExtra("chid", curChapterid);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public int getPlaylistLength() {
        return songs.size();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
    }
}
