/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.search.v2.score;

import com.atlassian.confluence.internal.search.v2.score.ScoreFunctionFactoryInternal;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.FunctionScoreQuery;
import com.atlassian.confluence.search.v2.score.FunctionScoreQueryFactory;

public class DefaultFunctionScoreQueryFactory
implements FunctionScoreQueryFactory {
    private final ScoreFunctionFactoryInternal scoreFunctionFactoryInternal;

    public DefaultFunctionScoreQueryFactory(ScoreFunctionFactoryInternal scoreFunctionFactoryInternal) {
        this.scoreFunctionFactoryInternal = scoreFunctionFactoryInternal;
    }

    @Override
    public SearchQuery applyFunctionScoring(SearchQuery query) {
        query = new FunctionScoreQuery(query, this.scoreFunctionFactoryInternal.createContentTypeScoreFunction_v2(), FunctionScoreQuery.BoostMode.MULTIPLY);
        query = new FunctionScoreQuery(query, this.scoreFunctionFactoryInternal.createRecencyOfModificationScoreFunction(), FunctionScoreQuery.BoostMode.MULTIPLY);
        return query;
    }
}

