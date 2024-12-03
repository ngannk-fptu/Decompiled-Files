/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.score;

import com.atlassian.confluence.search.v2.SearchQuery;

public interface FunctionScoreQueryFactory {
    public SearchQuery applyFunctionScoring(SearchQuery var1);
}

