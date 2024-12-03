/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.contentnames.SearchResult
 *  com.atlassian.confluence.search.v2.SearchResult
 */
package com.atlassian.confluence.plugins.rest.entities.builders;

import com.atlassian.confluence.plugins.rest.entities.SearchResultEntity;
import com.atlassian.confluence.search.v2.SearchResult;

public interface SearchEntityBuilder {
    public SearchResultEntity build(SearchResult var1);

    public SearchResultEntity build(com.atlassian.confluence.search.contentnames.SearchResult var1);
}

