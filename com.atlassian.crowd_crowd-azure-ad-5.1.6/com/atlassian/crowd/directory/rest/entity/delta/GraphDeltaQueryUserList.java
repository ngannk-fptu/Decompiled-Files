/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.rest.entity.delta;

import com.atlassian.crowd.directory.rest.entity.delta.GraphDeltaQueryUser;
import com.atlassian.crowd.directory.rest.entity.delta.PageableDeltaQueryGraphList;
import java.util.List;

public class GraphDeltaQueryUserList
extends PageableDeltaQueryGraphList<GraphDeltaQueryUser> {
    private GraphDeltaQueryUserList() {
    }

    public GraphDeltaQueryUserList(String nextLink, List<GraphDeltaQueryUser> entries) {
        super(nextLink, entries);
    }
}

