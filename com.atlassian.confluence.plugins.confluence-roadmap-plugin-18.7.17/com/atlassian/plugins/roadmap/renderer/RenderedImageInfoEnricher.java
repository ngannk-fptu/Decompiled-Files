/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.roadmap.renderer;

import com.atlassian.plugins.roadmap.models.Bar;
import com.atlassian.plugins.roadmap.models.Lane;
import com.atlassian.plugins.roadmap.models.LaneColor;
import com.atlassian.plugins.roadmap.models.Marker;
import com.atlassian.plugins.roadmap.models.TimelinePlanner;
import com.atlassian.plugins.roadmap.renderer.beans.TimelinePosition;

public interface RenderedImageInfoEnricher {
    public void enrichBar(int var1, int var2, int var3, int var4, Bar var5);

    public void enrichBarTitle(int var1, int var2, int var3, int var4, Bar var5, LaneColor var6);

    public void enrichLane(int var1, int var2, int var3, int var4, Lane var5);

    public void enrichColumn(int var1, int var2, int var3, int var4, TimelinePosition var5);

    public void enrichMarker(int var1, int var2, int var3, int var4, Marker var5);

    public void enrichContainer(TimelinePlanner var1, int var2);

    public void enrichColumnText();
}

