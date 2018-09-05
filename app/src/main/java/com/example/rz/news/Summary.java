package com.example.rz.news;

public class Summary {

    private String title;
    private String description;
    private String time;
    private String imageUrl;
    private String url;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUrl() {
        return url;
    }

    public Summary(String title, String description, String time, String imageUrl, String url) {
        this.title = title;
        this.description = description;
        this.time = time;
        this.imageUrl = imageUrl;
        this.url = url;
    }
}
