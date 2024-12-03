/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.roadmap.renderer.beans;

public class TimelinePositionTitle {
    private String month;
    private String year;

    public TimelinePositionTitle() {
    }

    public TimelinePositionTitle(String month, String year) {
        this.month = month;
        this.year = year;
    }

    public String getMonth() {
        return this.month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return this.year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}

