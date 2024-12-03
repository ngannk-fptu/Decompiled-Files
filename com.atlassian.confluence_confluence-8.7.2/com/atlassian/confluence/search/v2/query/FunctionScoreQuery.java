/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.ClauseType;
import com.atlassian.confluence.search.v2.SearchExpander;
import com.atlassian.confluence.search.v2.SearchPrimitive;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SubClause;
import com.atlassian.confluence.search.v2.score.ScoreFunction;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@SearchPrimitive
public class FunctionScoreQuery
implements SearchQuery {
    public static final String KEY = "functionScore";
    private final SearchQuery wrappedQuery;
    private final BoostMode boostMode;
    private final ScoreFunction function;

    public FunctionScoreQuery(SearchQuery wrappedQuery, ScoreFunction function, BoostMode boostMode) {
        this.wrappedQuery = wrappedQuery;
        this.function = function;
        this.boostMode = boostMode;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return Collections.emptyList();
    }

    public BoostMode getBoostMode() {
        return this.boostMode;
    }

    public ScoreFunction getFunction() {
        return this.function;
    }

    public SearchQuery getWrappedQuery() {
        return this.wrappedQuery;
    }

    @Override
    public SearchQuery expand() {
        return new FunctionScoreQuery(SearchExpander.expandAll(this.wrappedQuery), this.function, this.boostMode){

            @Override
            public SearchQuery expand() {
                return this;
            }
        };
    }

    @Override
    public Stream<SubClause<SearchQuery>> getSubClauses() {
        return Stream.of(new SubClause<SearchQuery>(this.getWrappedQuery(), ClauseType.MUST));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FunctionScoreQuery)) {
            return false;
        }
        FunctionScoreQuery that = (FunctionScoreQuery)o;
        return this.getWrappedQuery().equals(that.getWrappedQuery()) && this.getBoostMode() == that.getBoostMode() && this.getFunction().equals(that.getFunction());
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.getWrappedQuery(), this.getBoostMode(), this.getFunction()});
    }

    public static enum BoostMode {
        MULTIPLY{

            @Override
            public double apply(double queryScore, double functionScore) {
                return queryScore * functionScore;
            }

            public String toString() {
                return "product";
            }
        }
        ,
        SUM{

            @Override
            public double apply(double queryScore, double functionScore) {
                return queryScore + functionScore;
            }

            public String toString() {
                return "sum";
            }
        }
        ,
        MIN{

            @Override
            public double apply(double queryScore, double functionScore) {
                return Math.min(queryScore, functionScore);
            }

            public String toString() {
                return "min";
            }
        }
        ,
        MAX{

            @Override
            public double apply(double queryScore, double functionScore) {
                return Math.max(queryScore, functionScore);
            }

            public String toString() {
                return "max";
            }
        }
        ,
        REPLACE{

            @Override
            public double apply(double queryScore, double functionScore) {
                return functionScore;
            }

            public String toString() {
                return "replace";
            }
        };


        public abstract double apply(double var1, double var3);
    }
}

