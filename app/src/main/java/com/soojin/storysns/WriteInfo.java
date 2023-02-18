package com.soojin.storysns;

public class WriteInfo {
    private String title;
    private String content;
    private String publisher;



    public WriteInfo(String title, String content, String publisher) {
        this.title = title;
        this.content = content;
        this.publisher=publisher;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }


}