package com.loreaudio.lore;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
        Story story = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.story_layout, parent, false);
        }
        // Lookup view for data population
        TextView title = (TextView) convertView.findViewById(R.id.titleText);
        TextView author = (TextView) convertView.findViewById(R.id.Author);
        // Populate the data into the template view using the data object
        title.setText(story.getTitle());
        author.setText(story.getAuthor());
        // Return the completed view to render on screen
        return convertView;
    }
}
