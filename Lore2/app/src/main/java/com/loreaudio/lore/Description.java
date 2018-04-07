package com.loreaudio.lore;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

public class Description extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.description);

        //get story description from intent
        Story curStory = (Story) getIntent().getSerializableExtra("CurStory");
        TextView storyDescription = (TextView) findViewById(R.id.storyDescription);

        //set and get description methods in Story.java
        storyDescription.setText(curStory.getDescription());
        //displays and adjusts popup size to screen size of phone
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width * 0.8), (int)(height * 0.5));

    }
}
