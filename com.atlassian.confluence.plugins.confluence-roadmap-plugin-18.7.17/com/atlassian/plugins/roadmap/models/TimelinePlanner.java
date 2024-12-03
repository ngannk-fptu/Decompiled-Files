/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.roadmap.models;

import com.atlassian.plugins.roadmap.models.Lane;
import com.atlassian.plugins.roadmap.models.Marker;
import com.atlassian.plugins.roadmap.models.Timeline;
import java.util.List;

public class TimelinePlanner {
    private String title;
    private Timeline timeline;
    private List<Lane> lanes;
    private List<Marker> markers;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timeline getTimeline() {
        return this.timeline;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    public List<Lane> getLanes() {
        return this.lanes;
    }

    public void setLanes(List<Lane> lanes) {
        this.lanes = lanes;
    }

    public List<Marker> getMarkers() {
        return this.markers;
    }

    public void setMarkers(List<Marker> markers) {
        this.markers = markers;
    }
}

