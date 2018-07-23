package com.loreaudio.lore;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.widget.MediaController.MediaPlayerControl;
import android.widget.Toast;

import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

import org.w3c.dom.Text;

import static java.lang.Math.ceil;

public class PlayerView extends AppCompatActivity implements MediaPlayerControl {

    Story curStory;
    int curPosition = 1;
    int prevPosition = 0;
    Chapter curChapter;
    Chapter prevChapter;
    private MusicController controller;
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;
    private ArrayList<String> songList;
    private boolean mDragging;
    private SeekBar mProgress;
    private Handler mHandler;
    private ImageButton playPauseButton;

    boolean pausedBecauseYesNo = false;
    boolean activityPaused = false;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            int chid = intent.getIntExtra("chid", 0);
            if(chid != curChapter.id){
                return;
            }
            if (!curChapter.isEnd()) {
                Log.i("BroadcastReceiver", "id:" + curChapter.getId() + " isend:" + curChapter.isEnd());
                if(prevChapter != null) {
                    Log.i("BroadcastReceiver", "previd:" + prevChapter.getId() + " isend:" + prevChapter.isEnd());
                }
                int audiotype = intent.getIntExtra("audiotype", 999);
                if (audiotype == 0) {
                    musicSrv.playSong(1);
                } else if (audiotype == 1) {
                    yesNoListener();
                }
            }
        }
    };
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("onServiceConnected", "Adding songlist" + songList.get(0));
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(songList, curChapter.id);
            musicBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }


    };
    private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {

        public void onStartTrackingTouch(SeekBar bar) {

            mDragging = true;

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

    /*@Override
    public void onBackPressed() {
        Intent backToMain = new Intent(this, MainActivity.class);
        startActivity(backToMain);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_view);

        curStory = (Story) getIntent().getSerializableExtra("CurStory");
        curPosition = (int) getIntent().getIntExtra("CurPosition", 1);
        prevPosition = (int) getIntent().getIntExtra("PrevPosition", 0);

        Log.i("OnCreate:", Integer.toString(curPosition) + " " + Integer.toString(prevPosition));

        //ImageView imgview = (ImageView) findViewById(R.id.coverImage);
        //int imgsrc = getResources().getIdentifier(curStory.getImgfile(), null, getPackageName());
        //Drawable resimg = getResources().getDrawable(imgsrc);
        //imgview.setBackground(resimg);

        fix_chapter();
        boolean isRoot = true;
        if(curPosition > 1){
            isRoot = false;
        }

        createNode(isRoot, curChapter.getTitle(),curChapter.isEnd());

        TextView storyTitle = (TextView) findViewById(R.id.bookTitle);
        storyTitle.setText(curStory.getTitle());

        TextView authorName = (TextView) findViewById(R.id.authorName);
        authorName.setText(curStory.getAuthor());

        setController();

        mProgress = (SeekBar) findViewById(R.id.seekBar);
        mProgress.setOnSeekBarChangeListener(mSeekListener);
        mProgress.setMax(100);

        playPauseButton = (ImageButton) findViewById(R.id.playpause);


        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("audio-done"));

        //Make sure you update Seekbar on UI thread
        final TextView startTime = (TextView)findViewById(R.id.start);
        final TextView endTime = (TextView)findViewById(R.id.end);

        mHandler = new Handler();
        PlayerView.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if ((musicSrv != null) && (!activityPaused)) {
                    Log.i("Playerview uithread", "Current chapter:" + Integer.toString(curChapter.id));
                    if (musicSrv.isPng()) {
                        int position = getCurrentPosition();
                        int duration = getDuration();
                        if (mProgress != null) {
                            if (duration > 0) {
                                // use long to avoid overflow
                                long pos = 100L * position / duration;
                                mProgress.setProgress((int) pos);
                                startTime.setText(milliSecondsToTimer(position));
                                endTime.setText(milliSecondsToTimer(duration));
                            }
                        }
                        if (playPauseButton != null){
                            playPauseButton.setBackgroundResource(R.drawable.pause);
                        }
                    }
                    else if (playPauseButton != null) {
                        playPauseButton.setBackgroundResource(R.drawable.play);
                    }
                }
                mHandler.postDelayed(this, 1000);
            }
        });

    }

    private void createNode(boolean isRoot, String title, boolean isEnd){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        int lytweight = 52;
        float heightfactor = 52/100;
        Context context = this;
        float marginy = 250*heightfactor;
        int marginx = 125;

        int margin = 10;
        int buttonscale = 100;

        int buttonsize = 0;
        int ringsize = 0;

        int numButtons = 3;
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels/2;
        int height2 = (int) ceil(height*heightfactor);
        int width = displayMetrics.widthPixels;

        Drawable chapterColor = getResources().getDrawable(R.drawable.circle);
        Drawable prevChapter = getResources().getDrawable(R.drawable.circle);
        Drawable yesColor = getResources().getDrawable(R.drawable.circle);
        Drawable noColor = getResources().getDrawable(R.drawable.circle);

        int preChColor = getResources().getColor(R.color.prevChColor);
        int yColor = getResources().getColor(R.color.yesColor);
        int nColor = getResources().getColor(R.color.noColor);

        if (height < width)
            buttonsize = (int)(height)/4;
        else
            buttonsize = (width)/4;
        ringsize = 3*buttonsize;

        @SuppressLint("WrongViewCast")
        final RelativeLayout lyt = (RelativeLayout) findViewById(R.id.circlegraph);
        lyt.removeAllViews();
        Button b = new Button(this);
        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
                buttonsize + buttonscale,
                buttonsize + buttonscale);

        lp1.addRule(RelativeLayout.CENTER_IN_PARENT);
        b.setLayoutParams(lp1);
        b.setText(title);
        b.setBackgroundDrawable(chapterColor);
        lyt.addView(b);

        if(!isEnd) {

            final ImageView imgv = new ImageView(this);
            GradientDrawable ringa = new GradientDrawable();
            ringa.setShape(GradientDrawable.OVAL);
            ringa.setColor(Color.TRANSPARENT);
            ringa.setStroke(2, Color.GRAY);
            imgv.setBackground(ringa);

            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(ringsize, ringsize);

            lp2.addRule(RelativeLayout.CENTER_IN_PARENT);
            imgv.setLayoutParams(lp2);
            lyt.addView(imgv);

            final Button b2 = new Button(this);
            final Button b3 = new Button(this);
            yesColor.setColorFilter(new PorterDuffColorFilter(yColor, PorterDuff.Mode.SRC));
            b2.setBackgroundDrawable(yesColor);
            //size of button
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(buttonsize, buttonsize);

            b2.setLayoutParams(params);
            b2.setX(width / 2 + ringsize / 2 - buttonsize / 2);
            b2.setY(height / 2 - marginy);
            lyt.addView(b2);

            b2.setText("Yes");
            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onYes();
                }
            });

            noColor.setColorFilter(new PorterDuffColorFilter(nColor, PorterDuff.Mode.SRC));
            b3.setBackgroundDrawable(noColor);

            //size of button
            RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(buttonsize, buttonsize);

            b3.setLayoutParams(params3);
            b3.setX(width / 2 - buttonsize / 2 - ringsize / 2);
            b3.setY(height / 2 - marginy);
            lyt.addView(b3);


            b3.setText("No");
            b3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onNo();
                }
            });
        }

        if(!isRoot) {
            final Button bpar = new Button(this);
            prevChapter.setColorFilter(new PorterDuffColorFilter(preChColor, PorterDuff.Mode.SRC));
            bpar.setBackgroundDrawable(prevChapter);

            //size of button
            RelativeLayout.LayoutParams paramspar = new RelativeLayout.LayoutParams(buttonsize * 2, buttonsize * 2);

            bpar.setLayoutParams(paramspar);
            bpar.setX(0 - buttonsize);
            bpar.setY(0 - buttonsize);
            lyt.addView(bpar);


            bpar.setText(this.prevChapter.getTitle());

            ImageView line = new ImageView(this);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            line.setImageBitmap(bitmap);

            // Line
            Paint paint = new Paint();
            paint.setColor(Color.GRAY);
            paint.setStrokeWidth(10);
            float startx = bpar.getX() + buttonsize*2 - buttonsize/3;
            float starty = bpar.getY()+ buttonsize*2 - buttonsize/3;
            int endx = width/2;
            int endy = (int)(height/2) - buttonsize/2 - margin;
            canvas.drawLine(startx, starty, endx, endy, paint);
            lyt.addView(line);

        }
    }

    //from https://stackoverflow.com/questions/31421779/androidhow-to-show-time-on-my-music-player
    private  String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    private void fix_chapter() {
        if(prevPosition != 0) {
            prevChapter = curStory.chapters.get(new Integer(prevPosition));
        }
        curChapter = curStory.chapters.get(new Integer(curPosition));
        songList = new ArrayList<String>();
        File mp3file = new File(Environment.getExternalStorageDirectory()+ File.separator +curChapter.localChapterPath);
        File mp3Qfile = new File(Environment.getExternalStorageDirectory()+ File.separator + curChapter.localChapterQPath);
        if((!mp3file.exists()) || (!mp3Qfile.exists())){
            // TODO add try-catch.
            boolean success = curChapter.downloadChapter();
            // TODO if this is false, do stuff.
        }
        //songList.add(curChapter.mp3File);
        //songList.add(curChapter.mp3QuestionFile);
        songList.add(mp3file.getAbsolutePath());
        if(!curChapter.isEnd()) {
            songList.add(mp3Qfile.getAbsolutePath());
        }

        TextView chapterTitle = (TextView) findViewById(R.id.chapterTitle);
        chapterTitle.setText(curChapter.getTitle());

        //ImageButton prev = (ImageButton) findViewById(R.id.prev_ch);
        //prev.setEnabled(true);

        if (curChapter.isEnd()) {
            //ImageButton skip = (ImageButton) findViewById(R.id.skip);
            //skip.setEnabled(false);
            //Button pause = (Button) findViewById(R.id.playpause);
            //pause.setEnabled(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            playIntent.putExtra("CurChapter", this.curChapter.id);
            playIntent.putExtra("fromResume", false);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        activityPaused = true;
        Log.i("On Pause", "Pausing... pausedbecauseyesno:" + Boolean.toString(pausedBecauseYesNo) + " curchapter:" + Integer.toString(curChapter.id));
        if(!pausedBecauseYesNo){
            controller.setEnabled(false);
            unbindService(musicConnection);
            stopService(playIntent);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        activityPaused = false;
        if (playIntent == null) {
            Log.i("on Resume Chapter:" + Integer.toString(curChapter.id), "Intent is null" );
            playIntent = new Intent(this, MusicService.class);
        } else {
            Log.i("on Resume Chapter:" + Integer.toString(curChapter.id), "Intent is NOT null");
        }
        playIntent.putExtra("fromResume", true);
        bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        controller.setEnabled(true);
        startService(playIntent);

    }

    /* seek bar stuff from
    https://github.com/brightec/ExampleMediaController/blob/master/src/uk/co/brightec/example/mediacontroller/VideoControllerView.java

     */

    private void setController() {
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

    private int setProgress() {
        //if (mDragging) {
        //    return 0;
        //}
        if (!musicSrv.playing) {
            return 0;
        }
        int position = getCurrentPosition();
        int duration = getDuration();
        if (mProgress != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 100L * position / duration;
                mProgress.setProgress((int) pos);
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

    public void updatePausePlay() {
        if((musicSrv == null) || (playPauseButton == null)) {
            return;
        }

        if (musicSrv.isPng()) {
            playPauseButton.setBackgroundResource(R.drawable.pause);
        } else {
            playPauseButton.setBackgroundResource(R.drawable.play);
        }
    }


    //play next
    private void playNext() {
        musicSrv.playNext();
        controller.show(0);
    }

    //play previous
    private void playPrev() {
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


    public void songpicked(View view) {
        musicSrv.playSong(0);
    }

    public void goToQuestion(View view) {
        musicSrv.playSong(1);

    }

    public void togglePlay(View view){
        if(musicSrv == null){
            return;
        }
        if(musicSrv.isPng()){
            musicSrv.pausePlayer();
        } else {
            musicSrv.go();
        }
        updatePausePlay();
    }

    public void fastforward(View view) {
        if (musicSrv == null) {
            return;
        }
        musicSrv.ffw();
    }

    public void rewind(View view) {
        if (musicSrv == null) {
            return;
        }
        musicSrv.rewind();
    }

    public void toggleBookmark(View view){
        ImageButton bookmark = (ImageButton) findViewById(R.id.bookmark_button);
        bookmark.setActivated(!bookmark.isActivated());
        /* TODO
         Add modifying chapter bookmarks and writing to server when the time comes.
         */
    }

    public void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(PlayerView.this, view);
        popup.setOnMenuItemClickListener(menuItemClickListener);
        popup.inflate(R.menu.playermenu);
        popup.show();
    }

    private OnMenuItemClickListener menuItemClickListener = new OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            //Toast.makeText(this, "Selected Item: " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
            switch (menuItem.getItemId()) {
                case R.id.desc:
                    //pass intent to description class to display popup description
                    Intent i = new Intent(PlayerView.this, StoryDescription.class);
                    i.putExtra("CurStory", curStory);
                    startActivity(i);
                    return true;
                case R.id.playback_speed:
                    return true;
                case R.id.set_timer:
                    return true;
                    //for all other cases etc
                case R.id.clip:
                    return true;
                case R.id.shareprog:
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                    String title = curStory.getTitle();
                    String bodyText = "Listening to the audio book \"" + title + "\" at http://loreaudio.com/";

                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, bodyText);
                    startActivity(Intent.createChooser(shareIntent, "Share audio book!"));
                    return true;
                default:
                    return false;
            }
        }
    };



    private void yesNoListener() {
        pausedBecauseYesNo = true;
        Log.i("YesNoListener", Integer.toString(curChapter.id));
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
        Log.i("YesNoListener", "Done with it");
    }

    public void listenToYesNo(View view) {
        yesNoListener();
    }

    private void onYes(){
        /*
        prevPosition = curPosition;
        curPosition = curChapter.getOnYes();
        fix_chapter();
        boolean isRoot = false;
        if (curPosition <= 1){
            isRoot = true;
        }
        createNode(isRoot, curChapter.getTitle(), curChapter.isEnd());
        musicSrv.setList(songList);
        musicSrv.playSong(0);*/
        playStory(curChapter.getOnYes());
    }

    private void onNo(){
        /*
        prevPosition = curPosition;
        curPosition = curChapter.getOnNo();
        fix_chapter();
        boolean isRoot = false;
        if (curPosition <= 1){
            isRoot = true;
        }
        createNode(isRoot, curChapter.getTitle(), curChapter.isEnd());
        musicSrv.setList(songList);
        musicSrv.playSong(0);*/
        playStory(curChapter.getOnNo());
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
                        /*prevPosition = curPosition;
                        curPosition = curChapter.getOnYes();
                        fix_chapter();*/
                        onYes();

                    } else {
                        /*prevPosition = curPosition;
                        curPosition = curChapter.getOnNo();
                        fix_chapter();*/
                        onNo();
                    }
                    //musicSrv.setList(songList);
                    //musicSrv.playSong(0);
                }
                break;
            }

        }
    }

    // public void playNextChapter(View view) {
    public void playStory(int newPosition) {
        //songList = new ArrayList<String>();
        //musicSrv.setList(songList);
        //controller.setEnabled(false);
        Log.i("PlayView:playstory", "Starting new Player activity with new position " + Integer.toString(newPosition));
        Intent intent = new Intent(this, PlayerView.class);
        intent.putExtra("CurStory", this.curStory);
        intent.putExtra("CurPosition", newPosition);
        intent.putExtra("PrevPosition", this.curPosition);
        Log.i("playstory", "unsetting pausedbecauseyesno");
        pausedBecauseYesNo = false;
        startActivity(intent);
    }
}

