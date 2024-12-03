/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.search.ResourceType
 *  com.atlassian.sal.api.search.SearchMatch
 */
package com.atlassian.sal.core.search;

import com.atlassian.sal.api.search.ResourceType;
import com.atlassian.sal.api.search.SearchMatch;

public class BasicSearchMatch
implements SearchMatch {
    private String url;
    private String title;
    private String excerpt;
    private ResourceType resourceType;

    public BasicSearchMatch(String url, String title, String excerpt, ResourceType resourceType) {
        this.url = url;
        this.title = title;
        this.excerpt = excerpt;
        this.resourceType = resourceType;
    }

    public String getUrl() {
        return this.url;
    }

    public String getTitle() {
        return this.title;
    }

    public String getExcerpt() {
        return this.excerpt;
    }

    public ResourceType getResourceType() {
        return this.resourceType;
    }
}

