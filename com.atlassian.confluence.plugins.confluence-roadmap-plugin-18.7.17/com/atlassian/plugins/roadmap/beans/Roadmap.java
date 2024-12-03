/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.roadmap.beans;

import com.atlassian.plugins.roadmap.beans.RoadmapColumn;
import com.atlassian.plugins.roadmap.beans.RoadmapMarker;
import com.atlassian.plugins.roadmap.beans.RoadmapTheme;
import java.util.List;

public class Roadmap {
    public String title;
    public List<RoadmapColumn> columns;
    public List<RoadmapMarker> markers;
    public List<RoadmapTheme> themes;
}

