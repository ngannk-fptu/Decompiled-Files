/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.v2search.query.NotQuery
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.DateRangeQuery
 *  com.atlassian.querylang.antlrgen.AqlParser$EqOpContext
 *  com.atlassian.querylang.antlrgen.AqlParser$RangeOpContext
 *  com.atlassian.querylang.antlrgen.AqlParser$SetOpContext
 *  com.atlassian.querylang.antlrgen.AqlParser$TextOpContext
 *  com.google.common.base.Predicate
 */
package com.atlassian.confluence.plugins.contentproperty.search.query;

import com.atlassian.confluence.plugins.cql.spi.v2search.query.NotQuery;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.DateRangeQuery;
import com.atlassian.querylang.antlrgen.AqlParser;
import com.google.common.base.Predicate;

class V2FieldHandlerHelper {
    V2FieldHandlerHelper() {
    }

    private static <T> SearchQuery negateIfNecessary(SearchQuery query, T opCtx, Predicate<T> shouldNegate) {
        if (shouldNegate.apply(opCtx)) {
            return V2FieldHandlerHelper.negate(query);
        }
        return query;
    }

    private static SearchQuery negate(SearchQuery query) {
        return new NotQuery(query);
    }

    public static V2SearchQueryWrapper wrapV2Search(SearchQuery query, AqlParser.EqOpContext opCxt) {
        return new V2SearchQueryWrapper(V2FieldHandlerHelper.negateIfNecessary(query, opCxt, eqOpContext -> eqOpContext.OP_NOT_EQUALS() != null));
    }

    public static V2SearchQueryWrapper wrapV2Search(SearchQuery query, AqlParser.SetOpContext opCxt) {
        return new V2SearchQueryWrapper(V2FieldHandlerHelper.negateIfNecessary(query, opCxt, opCtx -> opCtx.not() != null));
    }

    public static V2SearchQueryWrapper wrapV2Search(SearchQuery query, AqlParser.TextOpContext opCxt) {
        return new V2SearchQueryWrapper(V2FieldHandlerHelper.negateIfNecessary(query, opCxt, eqOpContext -> eqOpContext.OP_NOT_LIKE() != null));
    }

    public static V2SearchQueryWrapper wrapV2Search(DateRangeQuery query, AqlParser.RangeOpContext opCtx) {
        return new V2SearchQueryWrapper(V2FieldHandlerHelper.negateIfNecessary((SearchQuery)query, opCtx, eqOpContext -> eqOpContext.OP_NOT_EQUALS() != null));
    }
}

