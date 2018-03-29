package com.loreaudio.lore;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

    }

    public void playNextChapter(View view) {
        Intent intent = new Intent(this, PlayerView.class);
        Story firstStory = storylist.get(0);
        intent.putExtra("CurStory", firstStory);
        intent.putExtra("CurPosition", 1);
        intent.putExtra("PrevPosition", 0);
        startActivity(intent);
    }


}
