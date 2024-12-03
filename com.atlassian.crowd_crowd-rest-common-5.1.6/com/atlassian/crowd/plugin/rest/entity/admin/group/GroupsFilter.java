/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.admin.group;

import org.codehaus.jackson.annotate.JsonProperty;

public class GroupsFilter {
    @JsonProperty(value="search")
    private final String search;
    @JsonProperty(value="directoryId")
    private final Long directoryId;

    public GroupsFilter() {
        this.search = null;
        this.directoryId = null;
    }

    public GroupsFilter(String search, Long directoryId) {
        this.search = search;
        this.directoryId = directoryId;
    }

    public String getSearch() {
        return this.search;
    }

    public Long getDirectoryId() {
        return this.directoryId;
    }
}

