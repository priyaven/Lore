package com.loreaudio.lore;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.TextView;

public class StoryDescription extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_story_description);

        //get story description from intent
        Story curStory = (Story) getIntent().getSerializableExtra("CurStory");
        TextView description = (TextView)findViewById(R.id.storyDescription);
        description.setText(curStory.getDescription());

        //displays and adjusts popup size to screen size of phone
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width * 0.8), (int)(height * 0.5));
    }
}
