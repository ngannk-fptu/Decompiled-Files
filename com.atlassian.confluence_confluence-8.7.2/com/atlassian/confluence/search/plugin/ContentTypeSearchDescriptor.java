/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.plugin;

import com.atlassian.confluence.search.v2.SearchQuery;

public interface ContentTypeSearchDescriptor {
    public String getIdentifier();

    public String getI18NKey();

    public boolean isIncludedInDefaultSearch();

    public SearchQuery getQuery();
}

