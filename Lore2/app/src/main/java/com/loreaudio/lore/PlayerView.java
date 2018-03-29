/*package com.loreaudio.lore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PlayerView extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_view);
    }
}
*/
package com.loreaudio.lore;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.MediaController.MediaPlayerControl;
import android.widget.Toast;

import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.util.ArrayList;
import java.util.HashMap;
import android.util.Log;

public class PlayerView extends AppCompatActivity implements MediaPlayerControl {

    private MusicController controller;
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;
    private ArrayList<String> songList;

    private boolean mDragging;

    Story curStory;
    int curPosition = 1;
    int prevPosition = 0;
    Chapter curChapter;
    Chapter prevChapter;


    private SeekBar mProgress;
    private Handler mHandler ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_view);

        curStory = (Story) getIntent().getSerializableExtra("CurStory");
        curPosition = (int) getIntent().getIntExtra("CurPosition", 1);
        prevPosition = (int) getIntent().getIntExtra("PrevPosition", 0);

        ImageView imgview = (ImageView) findViewById(R.id.coverImage);
        int imgsrc = getResources().getIdentifier(curStory.getImgfile(), null, getPackageName());
        Drawable resimg = getResources().getDrawable(imgsrc);
        imgview.setBackground(resimg);
        fix_chapter();


        TextView storyTitle = (TextView) findViewById(R.id.bookTitle) ;
        storyTitle.setText(curStory.getTitle());

        TextView authorName = (TextView) findViewById(R.id.authorName);
        authorName.setText(curStory.getAuthor());

        setController();

        mProgress = (SeekBar) findViewById(R.id.seekBar);
        mProgress.setOnSeekBarChangeListener(mSeekListener);
        mProgress.setMax(100);


        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("audio-done"));

        //Make sure you update Seekbar on UI thread
        mHandler = new Handler();
        PlayerView.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if ((musicSrv!= null) && (musicSrv.isPng())) {
                        int position = getCurrentPosition();
                        int duration = getDuration();
                        if (mProgress != null) {
                            if (duration > 0) {
                                // use long to avoid overflow
                                long pos = 100L * position / duration;
                                mProgress.setProgress((int) pos);

                            }
                        }
                    }
                mHandler.postDelayed(this, 100);
            }
        });

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

        TextView chapterTitle = (TextView) findViewById(R.id.chapterTitle);
        chapterTitle.setText(curChapter.getTitle());

        ImageButton prev = (ImageButton) findViewById(R.id.prev_ch);
        prev.setEnabled(true);

        if (curChapter.isEnd()){
            ImageButton skip = (ImageButton) findViewById(R.id.skip);
            skip.setEnabled(false);
            //Button pause = (Button) findViewById(R.id.playpause);
            //pause.setEnabled(false);
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
        controller.setAnchorView(findViewById(R.id.seekBar));
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

    /* seek bar stuff from https://github.com/brightec/ExampleMediaController/blob/master/src/uk/co/brightec/example/mediacontroller/VideoControllerView.java

     */

    private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {

        public void onStartTrackingTouch(SeekBar bar) {

            mDragging = true;

            // By removing these pending progress messages we make sure
            // that a) we won't update the progress while the user adjusts
            // the seekbar and b) once the user is done dragging the thumb
            // we will post one of these messages to the queue again and
            // this ensures that there will be exactly one message queued up.
        }

        public void onProgressChanged(SeekBar bar, int pos, boolean fromuser) {

            if (!fromuser) {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }

            long duration = getDuration();
            long newposition = (duration * pos) / 100L;
            seekTo((int) newposition);
            /*
            if (mCurrentTime != null)
                mCurrentTime.setText(stringForTime( (int) newposition));
                */
        }

        public void onStopTrackingTouch(SeekBar bar) {
            mDragging = false;
            setProgress();
            //updatePausePlay();
            //show(sDefaultTimeout);

            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
            //mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
    };

    private int setProgress() {
        //if (mDragging) {
        //    return 0;
        //}
        if (!musicSrv.playing){
            return 0;
        }
        int position = getCurrentPosition();
        int duration = getDuration();
        if (mProgress != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 100L * position / duration;
                mProgress.setProgress( (int) pos);
            }
            //int percent = mPlayer.getBufferPercentage();
            //mProgress.setSecondaryProgress(percent * 10);
        }
        /*
        if (mEndTime != null)
            mEndTime.setText(stringForTime(duration));
        if (mCurrentTime != null)
            mCurrentTime.setText(stringForTime(position));
        */

        return position;
    }


    /* end seek bar stuff */


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
        musicSrv.go();

    }

    @Override
    public void pause() {
        musicSrv.pausePlayer();

    }

    @Override
    public int getDuration() {
        return musicSrv.getDur();
    }

    @Override
    public int getCurrentPosition() {
        return musicSrv.getPosn();
    }

    @Override
    public void seekTo(int pos) {
        musicSrv.seek(pos);

    }

    @Override
    public boolean isPlaying() {
        return musicSrv.isPng();
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
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
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
                    "Oops! Your device doesn't support Speech to Text",
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

