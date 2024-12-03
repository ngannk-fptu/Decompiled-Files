/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.roadmap.models;

public class LaneColor {
    private String lane;
    private String bar;
    private String text;

    public LaneColor() {
    }

    public LaneColor(String lane, String bar, String text) {
        this.lane = lane;
        this.bar = bar;
        this.text = text;
    }

    public String getLane() {
        return this.lane;
    }

    public void setLane(String lane) {
        this.lane = lane;
    }

    public String getBar() {
        return this.bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

