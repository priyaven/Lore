package com.loreaudio.lore;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Created by priya on 9/9/2017.
 */

public class Story {
    int id;
    String title;
    String author;
    String imgfile;
    HashMap<Integer, Chapter> chapters;

    public static ArrayList<Story> storyObjects(NodeList storyNodes) {
        ArrayList<Story> storyArrayList = new ArrayList<Story>();
        for (int i = 0; i < storyNodes.getLength(); i++) {
            Element element = (Element) storyNodes.item(i);
            NodeList title = element.getElementsByTagName("storyTitle");
            Element txt = (Element) title.item(0);
            String storyTitle = txt.getTextContent();
            NodeList authorNode = element.getElementsByTagName("author");
            Element authortxt = (Element) authorNode.item(0);
            String author = authortxt.getTextContent();
            int storyId = new Integer(element.getAttribute("id"));

            Story newstory = new Story(storyId,  storyTitle, author);

            NodeList chapterNodes = element.getElementsByTagName("chapter");
            ArrayList<Chapter> chapterList = Chapter.chapterObjects(chapterNodes);
            for(Chapter chapter:chapterList){
                newstory.addChapter(chapter);
            }
            storyArrayList.add(newstory);
        }

        return storyArrayList;
    }

    public Story(int id, String title, String author, HashMap<Integer, Chapter> chapters) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.chapters = chapters;
        this.imgfile = "@drawable/s1"; // + String.valueOf(this.id);
    }

    public Story(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
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
        return chapters;
    }

    public void addChapter(Chapter chapter) {
        chapters.put(new Integer(chapter.getId()), chapter);
    }

    public String getAuthor() {
        return author;
    }

    public String getImgfile() { return this.imgfile; }

    public void setAuthor(String author) {
        this.author = author;
    }

}
