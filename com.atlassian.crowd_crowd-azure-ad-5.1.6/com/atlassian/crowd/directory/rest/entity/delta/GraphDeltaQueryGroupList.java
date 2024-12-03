/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.rest.entity.delta;

import com.atlassian.crowd.directory.rest.entity.delta.GraphDeltaQueryGroup;
import com.atlassian.crowd.directory.rest.entity.delta.PageableDeltaQueryGraphList;
import java.util.List;

public class GraphDeltaQueryGroupList
extends PageableDeltaQueryGraphList<GraphDeltaQueryGroup> {
    private GraphDeltaQueryGroupList() {
    }

    public GraphDeltaQueryGroupList(String nextLink, List<GraphDeltaQueryGroup> entries) {
        super(nextLink, entries);
    }

    public GraphDeltaQueryGroupList(String nextLink, List<GraphDeltaQueryGroup> entries, String deltaLink) {
        super(nextLink, entries, deltaLink);
    }
}

