package com.loreaudio.lore;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by priya on 9/9/2017.
 */

public class Chapter implements Serializable {
    int id;
    int storyId;
    boolean isEnd;
    String title;
    String mp3File;
    String mp3QuestionFile;

    public Chapter(int id, String title, int storyId) {
        this.id = id;
        this.storyId = storyId;
        this.title = title;
        this.isEnd = false;
        this.onYes = 0;
        this.onNo = 0;
        //this.mp3File = "http://loreaudio.com/story_" + String.valueOf(storyId) + "/chapter" + String.valueOf(this.id);
        this.mp3File = "https://s3-us-west-1.amazonaws.com/loreaudio/story_" + String.valueOf(storyId) + "/chapter" + String.valueOf(this.id);
        this.mp3QuestionFile = this.mp3File + "question";
        this.mp3File += ".mp3";
        this.mp3QuestionFile += ".mp3";

    }

    int onYes;
    int onNo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public int getOnYes() {
        return onYes;
    }

    public void setOnYes(int onYes) {
        this.onYes = onYes;
    }

    public int getOnNo() {
        return onNo;
    }

    public void setOnNo(int onNo) {
        this.onNo = onNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static ArrayList<Chapter> chapterObjects(NodeList chapterNodes, int storyId) {
        ArrayList<Chapter> chapters = new ArrayList<Chapter>();
        for (int i = 0; i < chapterNodes.getLength(); i++) {
            Element element = (Element) chapterNodes.item(i);
            NodeList titleNodes = element.getElementsByTagName("chapterTitle");
            Element titleNode = (Element) titleNodes.item(0);
            String title = titleNode.getTextContent().trim();
            int chapterId = Integer.valueOf(element.getAttribute("id"));
            Chapter newChapter = new Chapter(chapterId, title, storyId);
            NodeList isEnd = element.getElementsByTagName("isEnd");
            if (isEnd.getLength() > 0) {
                newChapter.setEnd(true);
            } else {
                NodeList onYesList = element.getElementsByTagName("onYes");
                Element onYesEl = (Element) onYesList.item(0);
                newChapter.setOnYes(Integer.valueOf(onYesEl.getTextContent().trim()));
                NodeList onNoList = element.getElementsByTagName("onNo");
                Element onNoEl = (Element) onNoList.item(0);
                newChapter.setOnNo(Integer.valueOf(onNoEl.getTextContent().trim()));
            }
            chapters.add(newChapter);
        }
        return chapters;
    }
}

