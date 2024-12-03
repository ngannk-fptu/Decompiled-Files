/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.roadmap.renderer.enricher;

import com.atlassian.plugins.roadmap.models.Bar;
import com.atlassian.plugins.roadmap.models.Lane;
import com.atlassian.plugins.roadmap.models.LaneColor;
import com.atlassian.plugins.roadmap.models.Marker;
import com.atlassian.plugins.roadmap.models.TimelinePlanner;
import com.atlassian.plugins.roadmap.renderer.RenderedImageInfoEnricher;
import com.atlassian.plugins.roadmap.renderer.beans.TimelinePosition;

public class NoopInfoEnricher
implements RenderedImageInfoEnricher {
    @Override
    public void enrichBar(int x, int y, int width, int height, Bar task) {
    }

    @Override
    public void enrichBarTitle(int x, int y, int width, int height, Bar task, LaneColor laneColor) {
    }

    @Override
    public void enrichLane(int x, int y, int width, int height, Lane theme) {
    }

    @Override
    public void enrichColumn(int x, int y, int width, int height, TimelinePosition column) {
    }

    @Override
    public void enrichMarker(int x1, int y1, int x2, int y2, Marker marker) {
    }

    @Override
    public void enrichContainer(TimelinePlanner Roadmap2, int width) {
    }

    @Override
    public void enrichColumnText() {
    }
}

