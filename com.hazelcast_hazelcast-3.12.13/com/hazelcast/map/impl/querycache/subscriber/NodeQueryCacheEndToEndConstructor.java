/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.core.Member;
import com.hazelcast.map.impl.query.QueryResult;
import com.hazelcast.map.impl.query.QueryResultRow;
import com.hazelcast.map.impl.querycache.InvokerWrapper;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import com.hazelcast.map.impl.querycache.subscriber.AbstractQueryCacheEndToEndConstructor;
import com.hazelcast.map.impl.querycache.subscriber.InternalQueryCache;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheRequest;
import com.hazelcast.map.impl.querycache.subscriber.operation.MadePublishableOperation;
import com.hazelcast.map.impl.querycache.subscriber.operation.PublisherCreateOperation;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.util.CollectionUtil;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.FutureUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class NodeQueryCacheEndToEndConstructor
extends AbstractQueryCacheEndToEndConstructor {
    public NodeQueryCacheEndToEndConstructor(QueryCacheRequest request) {
        super(request);
    }

    @Override
    public void createPublisherAccumulator(AccumulatorInfo info) {
        Collection<QueryResult> results = this.createPublishersAndGetQueryResults(info);
        if (!CollectionUtil.isEmpty(results)) {
            NodeQueryCacheEndToEndConstructor.prepopulate(this.queryCache, results);
        }
        boolean populate = info.isPopulate();
        if (this.logger.isFinestEnabled()) {
            this.logger.finest(String.format("Pre population is %s", populate ? "enabled" : "disabled"));
        }
        if (populate) {
            this.madePublishable(info.getMapName(), info.getCacheId());
        }
    }

    private Collection<QueryResult> createPublishersAndGetQueryResults(AccumulatorInfo info) {
        InvokerWrapper invokerWrapper = this.context.getInvokerWrapper();
        Collection<Member> members = this.context.getMemberList();
        ArrayList futures = new ArrayList(members.size());
        for (Member member : members) {
            Address address = member.getAddress();
            Future future = invokerWrapper.invokeOnTarget(new PublisherCreateOperation(info), address);
            futures.add(future);
        }
        return FutureUtil.returnWithDeadline(futures, 5L, TimeUnit.MINUTES);
    }

    private void madePublishable(String mapName, String cacheId) {
        InvokerWrapper invokerWrapper = this.context.getInvokerWrapper();
        Collection<Member> memberList = this.context.getMemberList();
        ArrayList<Future> futures = new ArrayList<Future>(memberList.size());
        for (Member member : memberList) {
            MadePublishableOperation operation = new MadePublishableOperation(mapName, cacheId);
            Future future = invokerWrapper.invokeOnTarget(operation, member.getAddress());
            futures.add(future);
        }
        FutureUtil.waitWithDeadline(futures, 5L, TimeUnit.MINUTES);
    }

    private static void prepopulate(InternalQueryCache queryCache, Collection<QueryResult> resultSets) {
        for (QueryResult queryResult : resultSets) {
            try {
                if (queryResult == null || queryResult.isEmpty()) continue;
                if (queryCache.reachedMaxCapacity()) break;
                for (QueryResultRow row : queryResult) {
                    Data keyData = row.getKey();
                    Data valueData = row.getValue();
                    queryCache.prepopulate(keyData, valueData);
                }
            }
            catch (Throwable t) {
                throw ExceptionUtil.rethrow(t);
            }
        }
    }
}

