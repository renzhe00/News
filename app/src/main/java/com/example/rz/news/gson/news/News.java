package com.example.rz.news.gson.news;

import com.google.gson.annotations.SerializedName;

public class News {

    /**
     * time : 2015-07-17
     * title : 那个抱走王明涵的，你上微信吗？看完这个你会心软吗？
     * description : 中国传统文化
     * picUrl : http://zxpic.gtimg.com/infonew/0/wechat_pics_-667708.jpg/640
     * url : http://mp.weixin.qq.com/s?__biz=MzA3OTg2NjEwNg==&amp;idx=5&amp;mid=209313388&amp;sn=7e30bd2851d22f69580e202c31fc7ecf
     */
    @SerializedName("ctime")
    private String time;
    private String title;
    private String description;
    private String picUrl;
    private String url;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
