/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.crowd.directory.rest.entity;

import com.atlassian.crowd.directory.rest.entity.PageableGraphList;
import com.atlassian.crowd.directory.rest.entity.membership.DirectoryObject;
import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class GraphDirectoryObjectList
extends PageableGraphList<DirectoryObject> {
    private GraphDirectoryObjectList() {
    }

    public GraphDirectoryObjectList(String nextLink, List<DirectoryObject> entries) {
        super(nextLink, entries);
    }
}

