package com.loreaudio.lore;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.Serializable;
import java.util.ArrayList;

import com.loreaudio.lore.DownloadStories;

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
    String downloadFolder;

    String storyPath;
    String chapterPath;
    String chapterQpath;

    String awsS3path = "https://s3-us-west-1.amazonaws.com/loreaudio/";

    String localChapterPath;
    String localChapterQPath;

    public Chapter(int id, String title, int storyId) {
        this.id = id;
        this.storyId = storyId;
        this.title = title;
        this.isEnd = false;
        this.onYes = 0;
        this.onNo = 0;
        this.storyPath = "story_" + String.valueOf(storyId);
        this.chapterPath = "chapter" + String.valueOf(this.id) + ".mp3";
        this.chapterQpath =  "chapter" + String.valueOf(this.id) +"question" + ".mp3";
        //this.mp3File = "http://loreaudio.com/story_" + String.valueOf(storyId) + "/chapter" + String.valueOf(this.id);
        this.mp3File = awsS3path + storyPath + "/" + chapterPath; //"https://s3-us-west-1.amazonaws.com/loreaudio/story_" + String.valueOf(storyId) + "/chapter" + String.valueOf(this.id);
        this.mp3QuestionFile = awsS3path + storyPath + "/" + chapterQpath; //this.mp3File + "question";
        //this.mp3File += ".mp3";
        //this.mp3QuestionFile += ".mp3";

        this.downloadFolder = "Lore Audio/" + storyPath;
        this.localChapterPath = this.downloadFolder + "/" + this.chapterPath;
        this.localChapterQPath = this.downloadFolder + "/" + this.chapterQpath;

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
        if(this.isEnd){
            this.chapterQpath = null;
            this.localChapterQPath = null;
            this.mp3QuestionFile = null;
        }
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

    public boolean downloadChapter(){
        try{
            DownloadStories dl = new DownloadStories();
            dl.downloadStory(this.mp3File, this.localChapterPath);
            if(!isEnd) {
                dl.downloadStory(this.mp3QuestionFile, this.localChapterQPath);
            }

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            return false;
        }
        return true;
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

