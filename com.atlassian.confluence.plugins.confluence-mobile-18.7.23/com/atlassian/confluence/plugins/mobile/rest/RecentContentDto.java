/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.rest.dto.StreamItem
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize$Inclusion
 */
package com.atlassian.confluence.plugins.mobile.rest;

import com.atlassian.confluence.plugins.rest.dto.StreamItem;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public class RecentContentDto {
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
    private int nextPageOffset = 0;
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
    private long token = 0L;
    @JsonProperty
    private List<StreamItem> streamItems;

    public int getNextPageOffset() {
        return this.nextPageOffset;
    }

    public void setNextPageOffset(int nextPageOffset) {
        this.nextPageOffset = nextPageOffset;
    }

    public long getToken() {
        return this.token;
    }

    public void setToken(long token) {
        this.token = token;
    }

    public List<StreamItem> getStreamItems() {
        return this.streamItems;
    }

    public void setStreamItems(List<StreamItem> streamItems) {
        this.streamItems = streamItems;
    }
}

