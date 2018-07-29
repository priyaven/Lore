package com.loreaudio.lore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by priya on 9/9/2017.
 */

public class StoryAdapter extends ArrayAdapter<Story> {
    public StoryAdapter(@NonNull Context context, @NonNull ArrayList<Story> stories) {
        super(context, 0, stories);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Story story = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.content_story_layout, parent, false);
        }

        // Lookup view for data population
        TextView title = (TextView) convertView.findViewById(R.id.titleText);
        TextView author = (TextView) convertView.findViewById(R.id.Author);
        ImageButton imgButton = (ImageButton) convertView.findViewById(R.id.storyImage);
        // Populate the data into the template view using the data object
        title.setText(story.getTitle().trim());
        author.setText(story.getAuthor().trim());

        Button downloadButton = (Button) convertView.findViewById(R.id.download);
        downloadButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                story.downloadStory(getContext());
                //tv.setText(months[rand.nextInt(12)]);
                //tv.setTextColor(Color.rgb(rand.nextInt(255)+1, rand.nextInt(255)+1, rand.nextInt(255)+1));
            }
        });

        TextView storyid_hidden = (TextView) convertView.findViewById(R.id.hiddenStoryId);
        storyid_hidden.setText(Integer.toString(position));

        //int imageResource = getContext().getResources().getIdentifier(story.getImgfile(), null, getContext().getPackageName());

        //Drawable resimg = getContext().getResources().getDrawable(imageResource);
        //imgButton.setImageDrawable(resimg);
        Bitmap bmp = BitmapFactory.decodeFile(story.getImgfile(getContext()));
        imgButton.setImageBitmap(bmp);

        // Return the completed view to render on screen
        return convertView;
    }


}
