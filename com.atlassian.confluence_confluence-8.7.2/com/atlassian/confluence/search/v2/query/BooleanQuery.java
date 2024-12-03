/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.BooleanQueryBuilder;
import com.atlassian.confluence.search.v2.ClauseType;
import com.atlassian.confluence.search.v2.SearchExpander;
import com.atlassian.confluence.search.v2.SearchPrimitive;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SubClause;
import com.atlassian.confluence.search.v2.query.AllQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.commons.lang3.builder.ToStringBuilder;

@SearchPrimitive
public class BooleanQuery
implements SearchQuery {
    public static final String KEY = "boolean";
    private final boolean disableCoord;
    private final Set<SearchQuery> andQueries;
    private final Set<SearchQuery> orQueries;
    private final Set<SearchQuery> notQueries;
    private final Set<SearchQuery> filters;
    private final float boost;

    @Override
    public String getKey() {
        return KEY;
    }

    public static SearchQuery composeAndQuery(Set<? extends SearchQuery> subqueries) {
        if (subqueries.isEmpty()) {
            return AllQuery.getInstance();
        }
        if (subqueries.size() == 1) {
            return subqueries.iterator().next();
        }
        return new BooleanQuery(subqueries, null, null);
    }

    public static SearchQuery andQuery(SearchQuery ... queries) {
        return BooleanQuery.composeAndQuery(new HashSet<SearchQuery>(Arrays.asList(queries)));
    }

    public static SearchQuery composeOrQuery(Set<? extends SearchQuery> subqueries) {
        if (subqueries.isEmpty()) {
            return AllQuery.getInstance();
        }
        if (subqueries.size() == 1) {
            return subqueries.iterator().next();
        }
        return new BooleanQuery(null, subqueries, null);
    }

    public static SearchQuery orQuery(SearchQuery ... queries) {
        return BooleanQuery.composeOrQuery(new HashSet<SearchQuery>(Arrays.asList(queries)));
    }

    public BooleanQuery(Collection<? extends SearchQuery> must, Collection<? extends SearchQuery> should, Collection<? extends SearchQuery> mustNot) {
        this(must, should, mustNot, 1.0f);
    }

    public BooleanQuery(Collection<? extends SearchQuery> must, Collection<? extends SearchQuery> should, Collection<? extends SearchQuery> mustNot, float boost) {
        this(must, should, mustNot, Collections.emptySet(), boost);
    }

    public BooleanQuery(Collection<? extends SearchQuery> must, Collection<? extends SearchQuery> should, Collection<? extends SearchQuery> mustNot, Collection<? extends SearchQuery> filters) {
        this(must, should, mustNot, filters, 1.0f);
    }

    public BooleanQuery(Collection<? extends SearchQuery> must, Collection<? extends SearchQuery> should, Collection<? extends SearchQuery> mustNot, Collection<? extends SearchQuery> filters, float boost) {
        this(must, should, mustNot, filters, boost, false);
    }

    public BooleanQuery(Collection<? extends SearchQuery> must, Collection<? extends SearchQuery> should, Collection<? extends SearchQuery> mustNot, Collection<? extends SearchQuery> filters, float boost, boolean disableCoord) {
        this.checkValidQueries(must);
        this.checkValidQueries(should);
        this.checkValidQueries(mustNot);
        this.checkValidQueries(filters);
        this.andQueries = must == null ? Collections.emptySet() : Collections.unmodifiableSet(new LinkedHashSet<SearchQuery>(must));
        this.orQueries = should == null ? Collections.emptySet() : Collections.unmodifiableSet(new LinkedHashSet<SearchQuery>(should));
        this.notQueries = mustNot == null ? Collections.emptySet() : Collections.unmodifiableSet(new LinkedHashSet<SearchQuery>(mustNot));
        this.filters = filters == null ? Collections.emptySet() : Collections.unmodifiableSet(new LinkedHashSet<SearchQuery>(filters));
        this.boost = boost;
        this.disableCoord = disableCoord;
    }

    private void checkValidQueries(Collection<? extends SearchQuery> queries) {
        if (queries != null && queries.contains(null)) {
            throw new IllegalArgumentException("A null query was provided");
        }
    }

    public Set<SearchQuery> getMustQueries() {
        return this.andQueries;
    }

    public Set<SearchQuery> getShouldQueries() {
        return this.orQueries;
    }

    public Set<SearchQuery> getMustNotQueries() {
        return this.notQueries;
    }

    public Set<SearchQuery> getFilters() {
        return this.filters;
    }

    @Override
    public SearchQuery expand() {
        List<SearchQuery> mustClauses = SearchExpander.expandAll(this.getMustQueries());
        List<SearchQuery> shouldClauses = SearchExpander.expandAll(this.getShouldQueries());
        List<SearchQuery> mustNotClauses = SearchExpander.expandAll(this.getMustNotQueries());
        List<SearchQuery> filterClauses = SearchExpander.expandAll(this.getFilters());
        return new BooleanQuery(mustClauses, shouldClauses, mustNotClauses, filterClauses, this.boost, this.disableCoord){

            @Override
            public SearchQuery expand() {
                return this;
            }
        };
    }

    @Override
    public Stream<SubClause<SearchQuery>> getSubClauses() {
        return Stream.of(this.getMustQueries().stream().map(x -> new SubClause<SearchQuery>((SearchQuery)x, ClauseType.MUST)), this.getShouldQueries().stream().map(x -> new SubClause<SearchQuery>((SearchQuery)x, ClauseType.SHOULD)), this.getMustNotQueries().stream().map(x -> new SubClause<SearchQuery>((SearchQuery)x, ClauseType.MUST_NOT)), this.getFilters().stream().map(x -> new SubClause<SearchQuery>((SearchQuery)x, ClauseType.FILTER))).flatMap(Function.identity());
    }

    @Override
    public List getParameters() {
        ArrayList<SearchQuery> queries = new ArrayList<SearchQuery>(this.andQueries.size() + this.orQueries.size() + this.notQueries.size());
        queries.addAll(this.andQueries);
        queries.addAll(this.orQueries);
        queries.addAll(this.notQueries);
        queries.addAll(this.filters);
        return queries;
    }

    @Override
    public float getBoost() {
        return this.boost;
    }

    public boolean isCoordDisabled() {
        return this.disableCoord;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BooleanQuery)) {
            return false;
        }
        BooleanQuery that = (BooleanQuery)o;
        return Float.compare(that.getBoost(), this.getBoost()) == 0 && this.andQueries.equals(that.andQueries) && this.orQueries.equals(that.orQueries) && this.notQueries.equals(that.notQueries) && this.filters.equals(that.filters) && this.disableCoord == that.disableCoord;
    }

    public int hashCode() {
        return Objects.hash(this.andQueries, this.orQueries, this.notQueries, this.filters, Float.valueOf(this.getBoost()), this.disableCoord);
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("andQueries", this.andQueries).append("orQueries", this.orQueries).append("notQueries", this.notQueries).append("filters", this.filters).toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
    extends BooleanQueryBuilder<SearchQuery> {
        private final Set<SearchQuery> filters = new HashSet<SearchQuery>();
        private boolean disableCoord = false;

        public BooleanQueryBuilder<SearchQuery> addFilter(SearchQuery filter) {
            this.filters.add(filter);
            return this;
        }

        public BooleanQueryBuilder<SearchQuery> addFilters(Set<SearchQuery> filters) {
            this.filters.addAll(filters);
            return this;
        }

        public BooleanQueryBuilder<SearchQuery> disableCoord(boolean disableCoord) {
            this.disableCoord = disableCoord;
            return this;
        }

        @Override
        public SearchQuery build() {
            if (this.must.size() == 1 && this.should.isEmpty() && this.mustNot.isEmpty() && this.filters.isEmpty() && this.boost == 1.0f) {
                return (SearchQuery)this.must.iterator().next();
            }
            if (this.must.isEmpty() && this.should.size() == 1 && this.mustNot.isEmpty() && this.filters.isEmpty() && this.boost == 1.0f) {
                return (SearchQuery)this.should.iterator().next();
            }
            return new BooleanQuery(this.must, this.should, this.mustNot, this.filters, this.boost, this.disableCoord);
        }

        public boolean isEmpty() {
            return this.must.isEmpty() && this.should.isEmpty() && this.mustNot.isEmpty() && this.filters.isEmpty();
        }
    }
}

