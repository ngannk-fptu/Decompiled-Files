/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.roadmap.models;

import com.atlassian.plugins.roadmap.models.Bar;
import com.atlassian.plugins.roadmap.models.LaneColor;
import java.util.List;

public class Lane {
    private String title;
    private LaneColor color;
    private List<Bar> bars;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LaneColor getColor() {
        return this.color;
    }

    public void setColor(LaneColor colour) {
        this.color = colour;
    }

    public List<Bar> getBars() {
        return this.bars;
    }

    public void setBars(List<Bar> bars) {
        this.bars = bars;
    }
}

