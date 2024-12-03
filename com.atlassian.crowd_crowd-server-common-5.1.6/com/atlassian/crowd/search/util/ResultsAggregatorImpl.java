/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 */
package com.atlassian.crowd.search.util;

import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.util.ResultsAggregator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class ResultsAggregatorImpl<T, K extends Comparable<? super K>>
implements ResultsAggregator<T> {
    private final int startIndex;
    private final int maxResults;
    private final int totalResults;
    private final Function<? super T, ? extends K> keymaker;
    private final Map<K, T> contents;

    ResultsAggregatorImpl(Function<? super T, ? extends K> keymaker, Query<? extends T> query) {
        this(keymaker, query.getStartIndex(), query.getMaxResults());
    }

    ResultsAggregatorImpl(Function<? super T, ? extends K> keymaker, int startIndex, int maxResults) {
        this.startIndex = startIndex;
        this.maxResults = maxResults;
        this.totalResults = EntityQuery.addToMaxResults((int)maxResults, (int)startIndex);
        this.keymaker = keymaker;
        this.contents = new HashMap<K, T>();
    }

    @Override
    public void add(T t) {
        Comparable k = (Comparable)this.keymaker.apply(t);
        this.contents.putIfAbsent(k, t);
    }

    @Override
    public void addAll(Iterable<? extends T> results) {
        for (T t : results) {
            this.add(t);
        }
    }

    @Override
    public List<T> constrainResults() {
        return this.constrainResults(t -> true);
    }

    @Override
    public List<T> constrainResults(Predicate<? super T> criteria) {
        return this.contents.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).filter(criteria).skip(this.startIndex).limit(EntityQuery.allResultsToLongMax((int)this.maxResults)).collect(Collectors.toList());
    }

    @Override
    public int size() {
        return this.contents.size();
    }

    @Override
    public int getRequiredResultCount() {
        return this.totalResults;
    }
}

