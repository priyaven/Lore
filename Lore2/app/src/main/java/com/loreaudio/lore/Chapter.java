package com.loreaudio.lore;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * Created by priya on 9/9/2017.
 */

public class Chapter {
    int id;
    boolean isEnd;
    String title;

    public Chapter(int id, String title) {
        this.id = id;
        this.title = title;
        this.isEnd = false;
        this.onYes = 0;
        this.onNo = 0;
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

    public static ArrayList<Chapter> chapterObjects(NodeList chapterNodes) {
        ArrayList<Chapter> chapters = new ArrayList<Chapter>();
        for (int i = 0; i < chapterNodes.getLength(); i++) {
            Element element = (Element) chapterNodes.item(i);
            NodeList titleNodes = element.getElementsByTagName("chapterTitle");
            Element titleNode = (Element) titleNodes.item(0);
            String title = titleNode.getTextContent().trim();
            int chapterId = new Integer(element.getAttribute("id"));
            Chapter newChapter = new Chapter(chapterId, title);
            NodeList isEnd = element.getElementsByTagName("isEnd");
            if (isEnd.getLength() > 0) {
                newChapter.setEnd(true);
            } else {
                NodeList onYesList = element.getElementsByTagName("onYes");
                Element onYesEl = (Element) onYesList.item(0);
                newChapter.setOnYes(new Integer(onYesEl.getTextContent().trim()));
                NodeList onNoList = element.getElementsByTagName("onNo");
                Element onNoEl = (Element) onNoList.item(0);
                newChapter.setOnNo(new Integer(onNoEl.getTextContent().trim()));
            }
            chapters.add(newChapter);
        }
        return chapters;
    }
}

