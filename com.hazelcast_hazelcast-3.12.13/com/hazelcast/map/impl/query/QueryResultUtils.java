/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.map.impl.query.QueryResult;
import com.hazelcast.map.impl.query.QueryResultCollection;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.PartitionPredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.IterationType;
import com.hazelcast.util.SortingUtil;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public final class QueryResultUtils {
    private QueryResultUtils() {
    }

    public static Set transformToSet(SerializationService ss, QueryResult queryResult, Predicate predicate, IterationType iterationType, boolean unique, boolean binary) {
        Predicate unwrappedPredicate = QueryResultUtils.unwrapPartitionPredicate(predicate);
        if (unwrappedPredicate instanceof PagingPredicate) {
            QueryResultCollection result = new QueryResultCollection(ss, IterationType.ENTRY, binary, unique, queryResult);
            return SortingUtil.getSortedQueryResultSet(new ArrayList<Map.Entry>(result), (PagingPredicate)unwrappedPredicate, iterationType);
        }
        return new QueryResultCollection(ss, iterationType, binary, unique, queryResult);
    }

    private static Predicate unwrapPartitionPredicate(Predicate predicate) {
        return predicate instanceof PartitionPredicate ? ((PartitionPredicate)predicate).getTarget() : predicate;
    }
}

