/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SearchWithToken;
import java.util.Iterator;
import java.util.List;

public interface SearchResults
extends Iterable<SearchResult> {
    public int getUnfilteredResultsCount();

    public int size();

    @Override
    public Iterator<SearchResult> iterator();

    public List<SearchResult> getAll();

    public List<String> getSearchWords();

    default public String getSearchQuery() {
        return "";
    }

    public SearchWithToken getNextPageSearch();

    public boolean isLastPage();

    public List<String> getSearchAfter();
}

