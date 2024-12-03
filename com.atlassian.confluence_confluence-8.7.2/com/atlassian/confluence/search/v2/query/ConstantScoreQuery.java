/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.ClauseType;
import com.atlassian.confluence.search.v2.SearchExpander;
import com.atlassian.confluence.search.v2.SearchPrimitive;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SubClause;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@SearchPrimitive
public class ConstantScoreQuery
implements SearchQuery {
    public static final String KEY = "constantScore";
    private final SearchQuery wrappedQuery;
    private final float boost;

    public ConstantScoreQuery(SearchQuery wrappedQuery) {
        this(wrappedQuery, 1.0f);
    }

    public ConstantScoreQuery(SearchQuery query, float boost) {
        this.wrappedQuery = query;
        this.boost = boost;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List<?> getParameters() {
        return Arrays.asList(this.wrappedQuery, Float.valueOf(this.boost));
    }

    @Override
    public SearchQuery expand() {
        return new ConstantScoreQuery(SearchExpander.expandAll(this.wrappedQuery), this.getBoost()){

            @Override
            public SearchQuery expand() {
                return this;
            }
        };
    }

    @Override
    public Stream<SubClause<SearchQuery>> getSubClauses() {
        return Stream.of(new SubClause<SearchQuery>(this.wrappedQuery, ClauseType.MUST));
    }

    public SearchQuery getWrappedQuery() {
        return this.wrappedQuery;
    }

    @Override
    public float getBoost() {
        return this.boost;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConstantScoreQuery)) {
            return false;
        }
        ConstantScoreQuery that = (ConstantScoreQuery)o;
        return Float.compare(this.boost, that.boost) == 0 && Objects.equals(this.wrappedQuery, that.wrappedQuery);
    }

    public int hashCode() {
        return Objects.hash(this.wrappedQuery, Float.valueOf(this.boost));
    }
}

