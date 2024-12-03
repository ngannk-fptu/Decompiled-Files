/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.search.api;

import com.atlassian.confluence.plugins.search.api.model.SearchExplanation;
import com.atlassian.confluence.plugins.search.api.model.SearchQueryParameters;
import com.atlassian.confluence.plugins.search.api.model.SearchResults;
import java.util.List;

public interface Searcher {
    public SearchResults search(SearchQueryParameters var1, boolean var2);

    public List<SearchExplanation> explain(SearchQueryParameters var1, long[] var2);
}

