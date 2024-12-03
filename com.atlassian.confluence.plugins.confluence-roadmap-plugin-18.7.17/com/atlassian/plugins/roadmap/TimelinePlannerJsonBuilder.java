/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 */
package com.atlassian.plugins.roadmap;

import com.atlassian.plugins.roadmap.beans.Roadmap;
import com.atlassian.plugins.roadmap.models.TimelinePlanner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class TimelinePlannerJsonBuilder {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static Gson gson;

    private static Gson getGson() {
        if (gson == null) {
            GsonBuilder roadmapGsonBuilder = new GsonBuilder();
            roadmapGsonBuilder.setDateFormat(DATE_FORMAT);
            gson = roadmapGsonBuilder.create();
        }
        return gson;
    }

    public static TimelinePlanner fromJson(String json) {
        try {
            String decodedJson = URLDecoder.decode(json, "UTF-8");
            return (TimelinePlanner)TimelinePlannerJsonBuilder.getGson().fromJson(decodedJson, TimelinePlanner.class);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Roadmap fromJsonRoadmap(String json) {
        try {
            String decodedJson = URLDecoder.decode(json, "UTF-8");
            return (Roadmap)TimelinePlannerJsonBuilder.getGson().fromJson(decodedJson, Roadmap.class);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJson(TimelinePlanner timelinePlanner) {
        String json = TimelinePlannerJsonBuilder.getGson().toJson((Object)timelinePlanner);
        try {
            return URLEncoder.encode(json, "UTF-8").replace("+", "%20");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJson(Object o) {
        return TimelinePlannerJsonBuilder.getGson().toJson(o);
    }
}

