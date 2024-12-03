/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.query;

import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.AllQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class BooleanQueryFactory {
    private final Set<SearchQuery> must;
    private final Set<SearchQuery> should;
    private final Set<SearchQuery> mustNot;

    public BooleanQueryFactory() {
        this.must = new HashSet<SearchQuery>();
        this.should = new HashSet<SearchQuery>();
        this.mustNot = new HashSet<SearchQuery>();
    }

    public BooleanQueryFactory(Set<SearchQuery> must, Set<SearchQuery> should, Set<SearchQuery> mustNot) {
        this.must = must;
        this.should = should;
        this.mustNot = mustNot;
    }

    public BooleanQuery toBooleanQuery() {
        if (this.must.isEmpty() && this.should.isEmpty() && !this.mustNot.isEmpty()) {
            HashSet<AllQuery> all = new HashSet<AllQuery>();
            all.add(AllQuery.getInstance());
            return new BooleanQuery(all, null, this.mustNot);
        }
        return new BooleanQuery(this.must, this.should, this.mustNot);
    }

    public void addMust(SearchQuery query) {
        this.must.add(query);
    }

    public void addMust(Collection<? extends SearchQuery> queries) {
        this.must.addAll(queries);
    }

    public void addMust(BooleanQueryFactory factory) {
        if (factory != null && !factory.isEmpty()) {
            this.must.add(factory.toBooleanQuery());
        }
    }

    public void addShould(SearchQuery query) {
        this.should.add(query);
    }

    public void addShould(Collection<? extends SearchQuery> queries) {
        this.should.addAll(queries);
    }

    public void addShould(BooleanQueryFactory factory) {
        if (factory != null && !factory.isEmpty()) {
            this.should.add(factory.toBooleanQuery());
        }
    }

    public void addMustNot(SearchQuery query) {
        this.mustNot.add(query);
    }

    public void addMustNot(Collection<? extends SearchQuery> queries) {
        this.mustNot.addAll(queries);
    }

    public void addMustNot(BooleanQueryFactory factory) {
        if (factory != null && !factory.isEmpty()) {
            this.mustNot.add(factory.toBooleanQuery());
        }
    }

    Set<SearchQuery> getMust() {
        return Collections.unmodifiableSet(this.must);
    }

    Set<SearchQuery> getShould() {
        return Collections.unmodifiableSet(this.should);
    }

    Set<SearchQuery> getMustNot() {
        return Collections.unmodifiableSet(this.mustNot);
    }

    public void merge(BooleanQueryFactory result) {
        this.addMust(result.getMust());
        this.addShould(result.getShould());
        this.addMustNot(result.getMustNot());
    }

    public boolean isEmpty() {
        return this.must.isEmpty() && this.should.isEmpty() && this.mustNot.isEmpty();
    }
}

