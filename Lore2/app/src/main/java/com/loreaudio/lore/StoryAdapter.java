package com.loreaudio.lore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by priya on 9/9/2017.
 */

public class StoryAdapter extends ArrayAdapter<Story> {
    ViewHolder holder = null;

    private static class ViewHolder
    {
        ProgressBar progressBar;
        Button button;
        Story story;
    }

    public StoryAdapter(@NonNull Context context, @NonNull ArrayList<Story> stories) {
        //super(context, 0, stories);
        super(context, 0, stories);

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Story story = getItem(position);
        View row = convertView;

//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.content_story_layout, parent, false);
//            holder = new ViewHolder();
//            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
//            holder.button = (Button) convertView.findViewById(R.id.download);
//
//            convertView.setTag(holder);
//        }

        if (row == null)
        {
            //LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //row = inflater.inflate(R.layout.content_story_layout, parent, false);
            row = LayoutInflater.from(getContext()).inflate(R.layout.content_story_layout, parent, false);

            holder = new ViewHolder();
            holder.progressBar = (ProgressBar) row.findViewById(R.id.progress_bar);
            holder.button = (Button)row.findViewById(R.id.download);
            holder.story = story;

            row.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)row.getTag();
            holder.story.setProgressBar(null);
            holder.story = story;
            Log.d("STORY", holder.story.getTitle());
            holder.story.setProgressBar(holder.progressBar);
        }
        // Lookup view for data population
        TextView title = (TextView) row.findViewById(R.id.titleText);
        TextView author = (TextView) row.findViewById(R.id.Author);
        ImageButton imgButton = (ImageButton) row.findViewById(R.id.storyImage);
        // Populate the data into the template view using the data object
        title.setText(story.getTitle().trim());
        author.setText(story.getAuthor().trim());

        //progress bar
        story.setProgressBar(holder.progressBar);

        //final Button downloadButton = (Button) convertView.findViewById(R.id.download);

        final Button downloadButton = holder.button;

        downloadButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                story.downloadStory(getContext(), holder.progressBar);
                Log.d("PROGRESS", String.valueOf(R.id.progress_bar));
                //DownloadStories task = new DownloadStories(story);
                downloadButton.setEnabled(false);
                downloadButton.invalidate();
                downloadButton.setVisibility(View.INVISIBLE);
                //task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                //tv.setText(months[rand.nextInt(12)]);
                //tv.setTextColor(Color.rgb(rand.nextInt(255)+1, rand.nextInt(255)+1, rand.nextInt(255)+1));
            }

        });
        Button deleteButton = (Button) row.findViewById(R.id.deleteDownload);
        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                story.deleteStory();
                //tv.setText(months[rand.nextInt(12)]);
                //tv.setTextColor(Color.rgb(rand.nextInt(255)+1, rand.nextInt(255)+1, rand.nextInt(255)+1));
            }
        });

        TextView storyid_hidden = (TextView) row.findViewById(R.id.hiddenStoryId);
        storyid_hidden.setText(Integer.toString(position));

        //int imageResource = getContext().getResources().getIdentifier(story.getImgfile(), null, getContext().getPackageName());

        //Drawable resimg = getContext().getResources().getDrawable(imageResource);
        //imgButton.setImageDrawable(resimg);
        Bitmap bmp = BitmapFactory.decodeFile(story.getImgfile(getContext()));
        imgButton.setImageBitmap(bmp);

        // Return the completed view to render on screen
        return row;
    }


}
