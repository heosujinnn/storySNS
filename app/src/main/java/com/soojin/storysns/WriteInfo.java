package com.soojin.storysns;

import java.util.ArrayList;
import java.util.Date;

public class WriteInfo {
    private String title;
    private ArrayList<String> content;
    private String publisher;
    private Date createdAt; //셍성일

    public WriteInfo(String title, ArrayList<String>  content, String publisher, Date createdAt) {
        this.title = title;
        this.content = content;
        this.publisher=publisher;
        this.createdAt=createdAt;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String>  getContent() {
        return content;
    }

    public void setContent(ArrayList<String>  content) {
        this.content = content;
    }
    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Date getCreatedAt() {return createdAt;}

    public void setCreatedAt(Date createdAt) {this.createdAt = createdAt;}

}