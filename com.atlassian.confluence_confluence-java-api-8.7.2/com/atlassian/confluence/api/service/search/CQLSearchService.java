/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.api.service.search;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.search.SearchContext;
import com.atlassian.confluence.api.model.search.SearchOptions;
import com.atlassian.confluence.api.model.search.SearchPageResponse;
import com.atlassian.confluence.api.model.search.SearchResult;

@ExperimentalApi
public interface CQLSearchService {
    public PageResponse<Content> searchContent(String var1, SearchContext var2, PageRequest var3, Expansion ... var4);

    public PageResponse<Content> searchContent(String var1, PageRequest var2, Expansion ... var3);

    public PageResponse<Content> searchContent(String var1, Expansion ... var2);

    public int countContent(String var1);

    public int countContent(String var1, SearchContext var2);

    @Internal
    public SearchPageResponse<SearchResult> search(String var1, SearchOptions var2, PageRequest var3, Expansion ... var4);
}

