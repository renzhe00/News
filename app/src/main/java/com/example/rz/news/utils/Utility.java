package com.example.rz.news.utils;

import com.example.rz.news.gson.NewsList;
import com.google.gson.Gson;

public class Utility {

    public static NewsList parseJsonWithGson(final String requestTest) {
        Gson gson = new Gson();
        return gson.fromJson(requestTest, NewsList.class);
    }
}
