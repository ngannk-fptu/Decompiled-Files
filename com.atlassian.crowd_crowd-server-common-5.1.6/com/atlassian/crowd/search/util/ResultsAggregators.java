/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.model.NameComparator
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.atlassian.crowd.search.util;

import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.model.NameComparator;
import com.atlassian.crowd.search.util.ResultsAggregator;
import com.atlassian.crowd.search.util.ResultsAggregatorImpl;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.apache.commons.lang3.tuple.Pair;

public class ResultsAggregators {
    public static <T, K extends Comparable<? super K>> ResultsAggregator<T> with(Function<? super T, ? extends K> maker, Query<? extends T> query) {
        return new ResultsAggregatorImpl<T, K>(maker, query);
    }

    public static <T, K extends Comparable<? super K>> ResultsAggregator<T> with(Function<? super T, ? extends K> maker, int startIndex, int maxResults) {
        return new ResultsAggregatorImpl<T, K>(maker, startIndex, maxResults);
    }

    public static <T, K extends Comparable<? super K>> ResultsAggregator<T> with(Function<? super T, ? extends K> maker, int startIndex, int maxResults, boolean merge) {
        if (merge) {
            return new ResultsAggregatorImpl<T, K>(maker, startIndex, maxResults);
        }
        AtomicInteger counter = new AtomicInteger();
        return new ResultsAggregatorImpl<Object, Pair>(e -> Pair.of(maker.apply(e), (Object)counter.incrementAndGet()), startIndex, maxResults);
    }

    public static <T extends User> ResultsAggregator<T> forUsers(int startIndex, int maxResults) {
        return ResultsAggregators.with(user -> IdentifierUtils.toLowerCase((String)user.getName()), startIndex, maxResults);
    }

    public static <T> ResultsAggregator<T> with(Query<T> query) {
        return ResultsAggregators.with(query, true);
    }

    public static <T> ResultsAggregator<T> with(Query<T> query, boolean merge) {
        return ResultsAggregators.with(NameComparator.normaliserOf((Class)query.getReturnType()), query.getStartIndex(), query.getMaxResults(), merge);
    }

    public static <T> List<T> constrainResults(Query<T> query, Collection<T> values) {
        ResultsAggregator<T> results = ResultsAggregators.with(query);
        results.addAll(values);
        return results.constrainResults();
    }
}

