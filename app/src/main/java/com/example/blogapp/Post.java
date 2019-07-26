package com.example.blogapp;

public class Post {
    private String uid;
    private String title;
    private String desc;
    private String imageUrl;
    private String username;
    public Post()
    {
        uid=null;
        title=null;
        desc=null;
        imageUrl=null;
        username=null;
    }
    public Post(String uid,String title,String desc,String imageUrl,String username)
    {

        this.uid=uid;
        this.title=title;
        this.desc=desc;
        this.imageUrl=imageUrl;
        this.username=username;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUsername() {
        return username;
    }
}
