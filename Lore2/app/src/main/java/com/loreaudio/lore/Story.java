package com.loreaudio.lore;

import android.widget.TextView;

import org.w3c.dom.DOMImplementationList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Created by priya on 9/9/2017.
 */

public class Story implements Serializable{
    int id;
    String title;
    String author;
    String imgfile;
    String description;
    HashMap<Integer, Chapter> chapters;

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
            ArrayList<Chapter> chapterList = Chapter.chapterObjects(chapterNodes);
            for(Chapter chapter:chapterList){
                newstory.addChapter(chapter);
            }
            storyArrayList.add(newstory);
        }

        return storyArrayList;
    }

    public Story(int id, String title, String author, HashMap<Integer, Chapter> chapters, String description) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.chapters = chapters;
        this.description = description;
        this.imgfile = "@drawable/s" + String.valueOf(this.id);
    }

    public Story(int id, String title, String author, String description) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.imgfile = "@drawable/s" + String.valueOf(this.id);
        this.chapters = new HashMap<Integer, Chapter>();
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
        this.chapters.put(new Integer(chapter.getId()), chapter);
    }

    public String getAuthor() {
        return author;
    }

    public String getImgfile() { return this.imgfile; }

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
}
