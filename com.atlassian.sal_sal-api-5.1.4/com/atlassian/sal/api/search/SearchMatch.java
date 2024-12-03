/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.search;

import com.atlassian.sal.api.search.ResourceType;

public interface SearchMatch {
    public String getUrl();

    public String getTitle();

    public String getExcerpt();

    public ResourceType getResourceType();
}

