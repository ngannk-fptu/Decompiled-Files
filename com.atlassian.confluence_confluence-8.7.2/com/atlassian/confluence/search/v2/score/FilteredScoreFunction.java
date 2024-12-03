/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.score;

import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.score.ComposableScoreFunction;

public class FilteredScoreFunction
implements ComposableScoreFunction {
    private final SearchQuery filter;
    private final ComposableScoreFunction delegate;

    public FilteredScoreFunction(SearchQuery filter, ComposableScoreFunction delegate) {
        this.filter = filter;
        this.delegate = delegate;
    }

    public SearchQuery getFilter() {
        return this.filter;
    }

    public ComposableScoreFunction getDelegate() {
        return this.delegate;
    }
}

