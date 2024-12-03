/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.plugins.roadmap.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.plugins.roadmap.models.Lane;
import com.atlassian.plugins.roadmap.models.TimelinePlanner;
import com.atlassian.plugins.roadmap.renderer.helper.TimeLineHelper;

@EventName(value="confluence.roadmap.consume")
public class RoadmapAnalyticObject {
    private int lanes;
    private int bars;
    private int markers;
    private int months;

    public RoadmapAnalyticObject(TimelinePlanner roadmap) {
        this.lanes = roadmap.getLanes().size();
        int barCount = 0;
        for (Lane lane : roadmap.getLanes()) {
            barCount += lane.getBars().size();
        }
        this.bars = barCount;
        this.markers = roadmap.getMarkers().size();
        this.months = TimeLineHelper.getNumberOfColumnInTimeline(roadmap.getTimeline());
    }

    public int getLanes() {
        return this.lanes;
    }

    public int getBars() {
        return this.bars;
    }

    public int getMarkers() {
        return this.markers;
    }

    public int getMonths() {
        return this.months;
    }
}

