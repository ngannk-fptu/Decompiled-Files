/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 */
package com.atlassian.crowd.search.util;

import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.crowd.search.util.ResultsAggregator;
import com.atlassian.crowd.search.util.ResultsAggregators;
import java.util.List;
import java.util.Optional;

public class QuerySplitter {
    public static <T, E extends Exception> List<T> batchConditionsIfNeeded(EntityQuery<T> query, EntitySearcher<T, E> searcher, int maxRestrictionsPerQuery) throws E {
        Optional split = query.splitOrRestrictionIfNeeded(maxRestrictionsPerQuery);
        return split.isPresent() ? QuerySplitter.runInBatches(query, (List)split.get(), searcher) : searcher.search(query);
    }

    public static <T, E extends Exception> List<T> batchNamesToMatchIfNeeded(MembershipQuery<T> query, MembershipSearcher<T, E> searcher, int maxBatchSize) throws E {
        return query.getEntityNamesToMatch().size() > maxBatchSize ? QuerySplitter.runInBatches(query, query.splitEntityNamesToMatch(maxBatchSize), searcher) : searcher.search(query);
    }

    private static <T, Q extends Query<T>, E extends Exception> List<T> runInBatches(Q original, List<Q> split, Searcher<T, Q, E> searcher) throws E {
        ResultsAggregator<T> aggregator = ResultsAggregators.with(original);
        for (Query singleQuery : split) {
            aggregator.addAll(searcher.search(singleQuery));
        }
        return aggregator.constrainResults();
    }

    public static interface Searcher<T, Q extends Query<T>, E extends Exception> {
        public List<T> search(Q var1) throws E;
    }

    public static interface MembershipSearcher<T, E extends Exception>
    extends Searcher<T, MembershipQuery<T>, E> {
    }

    public static interface EntitySearcher<T, E extends Exception>
    extends Searcher<T, EntityQuery<T>, E> {
    }
}

