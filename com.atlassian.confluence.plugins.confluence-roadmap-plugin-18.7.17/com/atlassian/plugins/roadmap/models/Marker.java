/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.roadmap.models;

import java.util.Date;

public class Marker {
    private String title;
    private Date markerDate;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getMarkerDate() {
        return this.markerDate;
    }

    public void setMarkerDate(Date markerDate) {
        this.markerDate = markerDate;
    }
}

