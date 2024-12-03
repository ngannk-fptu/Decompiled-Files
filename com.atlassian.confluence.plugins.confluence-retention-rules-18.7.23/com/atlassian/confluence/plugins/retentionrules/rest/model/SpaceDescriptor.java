/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.retention.SpaceRetentionPolicy
 *  com.atlassian.confluence.search.v2.SearchResult
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.retentionrules.rest.model;

import com.atlassian.confluence.api.model.retention.SpaceRetentionPolicy;
import com.atlassian.confluence.search.v2.SearchResult;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonProperty;

public class SpaceDescriptor
implements Serializable {
    private final Map<String, Object> space = new HashMap<String, Object>();
    private final String url;

    public SpaceDescriptor(String key, String name, String type, String url) {
        this.space.put("key", key);
        this.space.put("name", name);
        this.space.put("type", type);
        this.url = url;
    }

    public SpaceDescriptor(SearchResult searchResult) {
        this.space.put("key", searchResult.getSpaceKey());
        this.space.put("name", searchResult.getDisplayTitle());
        this.space.put("type", searchResult.getType());
        this.url = searchResult.getUrlPath();
    }

    public void appendSpaceRetentionPolicy(SpaceRetentionPolicy spaceRetentionPolicy) {
        if (spaceRetentionPolicy != null) {
            this.space.put("retentionPolicy", spaceRetentionPolicy);
        }
    }

    @JsonProperty
    public Map<String, Object> getSpace() {
        return this.space;
    }

    @JsonProperty
    public String getUrl() {
        return this.url;
    }
}

