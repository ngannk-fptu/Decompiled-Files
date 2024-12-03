/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.crowd.directory.rest.entity.user;

import com.atlassian.crowd.directory.rest.entity.PageableGraphList;
import com.atlassian.crowd.directory.rest.entity.user.GraphUser;
import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class GraphUsersList
extends PageableGraphList<GraphUser> {
    private GraphUsersList() {
    }

    public GraphUsersList(String nextLink, List<GraphUser> entries) {
        super(nextLink, entries);
    }
}

