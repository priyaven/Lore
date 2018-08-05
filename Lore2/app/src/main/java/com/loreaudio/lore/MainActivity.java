package com.loreaudio.lore;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.loreaudio.lore.DownloadStories.*;

public class MainActivity extends AppCompatActivity {
    public static final int PERMISSION_REQUEST_CODE = 1;

    ListView eachstory;
    ArrayList<Story> storylist;
    NavigationView navView;
    DrawerLayout drawer;

    final String storiesXml = "https://s3-us-west-1.amazonaws.com/loreaudio/stories.xml";
    final String localXml = "Lore Audio/stories.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eachstory = (ListView) findViewById(R.id.eachstory);
        eachstory.setClickable(true);

        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);

        navView = (NavigationView)findViewById(R.id.nav_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawer.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });
        if(!checkPermission(this)) {
            requestPermission(this);
        } else {
            createStoriesXml();
        }

    }

    public boolean checkPermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else {
            return false;
        }
    }

    public void requestPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        //ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private void createStoriesXml(){
        try {
            File xmlFile = new File(Environment.getExternalStorageDirectory()+ File.separator + localXml);
            if(!xmlFile.exists()) {
                // TODO check if file is too old, or malformed.
                DownloadStories dl = new DownloadStories();
                // Blocking download
                // TODO behavior if download fails.
                dl.downloadWait(storiesXml, localXml, MainActivity.this);
            }
            loadStoriesXml(xmlFile);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void loadStoriesXml(File xmlFile){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            //Document doc = builder.parse(getResources().openRawResource(R.raw.stories));
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
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                    createStoriesXml();
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
