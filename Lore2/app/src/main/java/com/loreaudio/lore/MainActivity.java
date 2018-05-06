package com.loreaudio.lore;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    ListView eachstory;
    ArrayList<Story> storylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);
        eachstory = (ListView) findViewById(R.id.eachstory);
        eachstory.setClickable(true);
        //eachstory.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,
        //getResources().getStringArray(R.array.stories)));

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(getResources().openRawResource(R.raw.stories));
            NodeList storyNodes = doc.getElementsByTagName("story");
            storylist = Story.storyObjects(storyNodes);
            StoryAdapter storyAdapter = new StoryAdapter(MainActivity.this, storylist);
            eachstory.setAdapter(storyAdapter);
            eachstory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Get the selected item text from ListView
                    playStory(position);
                }
            });
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

    }

   // public void playNextChapter(View view) {
    public void playStory(int position){

        Intent intent = new Intent(this, PlayerView.class);
        Story curStory = storylist.get(position);
        if(curStory == null){
            return;
        }
        intent.putExtra("CurStory", curStory);
        intent.putExtra("CurPosition", curStory.getFirstChapterId());
        intent.putExtra("PrevPosition", 0);
        startActivity(intent);
    }

}
