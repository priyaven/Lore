package com.loreaudio.lore;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.DOMImplementationList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by priya on 9/9/2017.
 */

public class Story implements Serializable{
    final String awsS3path = "https://s3-us-west-1.amazonaws.com/loreaudio/";
    final String internalStorage = Environment.getExternalStorageDirectory()+ File.separator ;

    String storyPath;
    String imagePath;

    String imgUrl;
    String localImage;

    int id;
    String title;
    String author;
    String imgfile;
    String description;
    int firstChapterId = Integer.MAX_VALUE;

    HashMap<Integer, Chapter> chapters;
    ProgressBar mProgressBar;

    public static ArrayList<Story> storyObjects(NodeList storyNodes) {
        ArrayList<Story> storyArrayList = new ArrayList<Story>();
        for (int i = 0; i < storyNodes.getLength(); i++) {
            Element element = (Element) storyNodes.item(i);
            NodeList title = element.getElementsByTagName("storyTitle");
            Element txt = (Element) title.item(0);
            String storyTitle = txt.getTextContent().trim();
            NodeList authorNode = element.getElementsByTagName("author");
            Element authortxt = (Element) authorNode.item(0);
            String author = authortxt.getTextContent().trim();
            int storyId = new Integer(element.getAttribute("id"));

            //getting description from xml
            NodeList descriptionNode = element.getElementsByTagName("description");
            Element txtdescription = (Element)descriptionNode.item(0);
            String description = txtdescription.getTextContent().trim();

            Story newstory = new Story(storyId,  storyTitle, author, description);

            NodeList chapterNodes = element.getElementsByTagName("chapter");
            ArrayList<Chapter> chapterList = Chapter.chapterObjects(chapterNodes, storyId);
            for(Chapter chapter:chapterList){
                newstory.addChapter(chapter);
            }
            storyArrayList.add(newstory);
        }

        return storyArrayList;
    }

    public Story(int id, String title, String author, HashMap<Integer, Chapter> chapters, String description, int firstChapterId) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.chapters = chapters;
        this.description = description;
        this.imgfile = "@drawable/s" + String.valueOf(this.id);
        this.firstChapterId = firstChapterId;
        this.storyPath = "story_" + String.valueOf(this.id);
        this.imagePath = storyPath + File.separator + "cover.jpg";
        this.imgUrl = this.awsS3path + this.imagePath;
        this.localImage = "Lore Audio" + File.separator + this.imagePath;
        this.mProgressBar = null;
    }

    public Story(int id, String title, String author, String description) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.imgfile =  "@drawable/s" + String.valueOf(this.id);
        this.chapters = new HashMap<Integer, Chapter>();
        this.firstChapterId = Integer.MAX_VALUE;
        this.storyPath = "story_" + String.valueOf(this.id);
        this.imagePath = storyPath + File.separator + "cover.jpg";
        this.imgUrl = this.awsS3path + this.imagePath;
        this.localImage = "Lore Audio" + File.separator + this.imagePath;
        this.mProgressBar = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HashMap<Integer, Chapter> getChapters() {
        return this.chapters;
    }

    public void addChapter(Chapter chapter) {
        int chapter_id = chapter.getId();
        this.chapters.put(new Integer(chapter_id), chapter);
        if(chapter_id < this.firstChapterId){
            this.firstChapterId = chapter_id;
        }
    }

    public boolean downloadStory(Context ctx, ProgressBar bar) {
        Iterator<Map.Entry<Integer, Chapter>> itr = chapters.entrySet().iterator();
        while(itr.hasNext()){
            Map.Entry<Integer, Chapter> entry = itr.next();
            setProgressBar(bar);
            Log.d("PROGRESS", String.valueOf(bar.getId()));
            boolean success = entry.getValue().downloadChapter(ctx, bar);
            if(!success){
                return false;
            }
        }
        return true;
    }

    public void deleteStory() {
        Iterator<Map.Entry<Integer, Chapter>> itr = chapters.entrySet().iterator();
        while(itr.hasNext()){
            Map.Entry<Integer, Chapter> entry = itr.next();
            entry.getValue().deleteChapter();
        }
    }

    public String getAuthor() {
        return author;
    }

    public String getImgfile(Context ctx) {
        downloadCover(ctx, getProgressBar());
        return internalStorage + this.localImage; }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    public int getFirstChapterId() { return this.firstChapterId; }

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }
    public void setProgressBar(ProgressBar progressBar) {
        Log.d("TESTING PROGRESS BAR", "setProgressBar " + getTitle() + " to " + progressBar);
        mProgressBar = progressBar;
    }

    public boolean downloadCover(Context ctx, ProgressBar bar) {
        File localImgFile = new File(internalStorage + localImage);
        if(!localImgFile.exists()){
            DownloadStories dl = new DownloadStories();
            dl.downloadStory(this.imgUrl, this.localImage, ctx, bar);
            //TODO add try catch.
        }
        return true;
    }
}
