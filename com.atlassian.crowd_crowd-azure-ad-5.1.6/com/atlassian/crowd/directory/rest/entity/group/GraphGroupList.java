/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.crowd.directory.rest.entity.group;

import com.atlassian.crowd.directory.rest.entity.PageableGraphList;
import com.atlassian.crowd.directory.rest.entity.group.GraphGroup;
import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class GraphGroupList
extends PageableGraphList<GraphGroup> {
    private GraphGroupList() {
    }

    public GraphGroupList(String nextLink, List<GraphGroup> entries) {
        super(nextLink, entries);
    }
}

