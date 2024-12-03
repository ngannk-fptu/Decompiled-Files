/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.roadmap.models;

import com.atlassian.plugins.roadmap.models.RoadmapPageLink;
import java.util.Date;

public class Bar {
    private String id;
    private String title;
    private String description;
    private Date startDate;
    private double duration;
    private int rowIndex;
    private RoadmapPageLink pageLink;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public double getDuration() {
        return this.duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public int getRowIndex() {
        return this.rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public RoadmapPageLink getPageLink() {
        return this.pageLink;
    }

    public void setPageLink(RoadmapPageLink pageLink) {
        this.pageLink = pageLink;
    }
}

