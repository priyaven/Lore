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

import java.util.ArrayList;

/**
 * Created by priya on 9/16/2017.
 */

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener{

    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<String> songs;
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
                audioDone(lastPlayedSong);
            }
        });

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;

    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    public void playSong(int i){
        //play a song
        player.reset();

        try{
            //player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            String trackUri = songs.get(i);
            player.setDataSource(trackUri);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        player.setOnPreparedListener(this);
        player.prepareAsync();
        lastPlayedSong = i;
    }

    public void setList(ArrayList<String> theSongs){
        songs=theSongs;
        if(lastPlayedSong == 999) {
            playSong(0);
        }
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
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

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
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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
