/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.aggregation.Aggregator;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.Member;
import com.hazelcast.instance.Node;
import com.hazelcast.map.QueryResultSizeExceededException;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.query.Query;
import com.hazelcast.map.impl.query.Result;
import com.hazelcast.nio.Connection;
import com.hazelcast.projection.Projection;
import com.hazelcast.query.PartitionPredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.QueryException;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.util.BitSetUtils;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.IterationType;
import java.security.Permission;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class AbstractMapQueryMessageTask<P, QueryResult extends Result, AccumulatedResults, ReducedResult>
extends AbstractCallableMessageTask<P> {
    protected AbstractMapQueryMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    public final String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(this.getDistributedObjectName(), "read");
    }

    protected abstract Predicate getPredicate();

    protected abstract Aggregator<?, ?> getAggregator();

    protected abstract Projection<?, ?> getProjection();

    protected abstract void extractAndAppendResult(Collection<AccumulatedResults> var1, QueryResult var2);

    protected abstract ReducedResult reduce(Collection<AccumulatedResults> var1);

    protected abstract IterationType getIterationType();

    @Override
    protected final Object call() throws Exception {
        LinkedList result = new LinkedList();
        try {
            Predicate predicate = this.getPredicate();
            if (predicate instanceof PartitionPredicate) {
                int partitionId = this.getPartitionId();
                QueryResult queryResult = this.invokeOnPartition((PartitionPredicate)predicate, partitionId);
                this.extractAndAppendResult(result, queryResult);
                return this.reduce(result);
            }
            int partitionCount = this.clientEngine.getPartitionService().getPartitionCount();
            BitSet finishedPartitions = this.invokeOnMembers(result, predicate, partitionCount);
            this.invokeOnMissingPartitions(result, predicate, finishedPartitions, partitionCount);
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
        return this.reduce(result);
    }

    private QueryResult invokeOnPartition(PartitionPredicate predicate, int partitionId) {
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        MapService mapService = (MapService)this.nodeEngine.getService(this.getServiceName());
        MapServiceContext mapServiceContext = mapService.getMapServiceContext();
        Query query = this.buildQuery(predicate);
        MapOperation queryPartitionOperation = this.createQueryPartitionOperation(query, mapServiceContext);
        queryPartitionOperation.setPartitionId(partitionId);
        try {
            return (QueryResult)((Result)operationService.invokeOnPartition("hz:impl:mapService", queryPartitionOperation, partitionId).get());
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    private BitSet invokeOnMembers(Collection<AccumulatedResults> result, Predicate predicate, int partitionCount) throws InterruptedException, ExecutionException {
        Collection<Member> members = this.clientEngine.getClusterService().getMembers(MemberSelectors.DATA_MEMBER_SELECTOR);
        List<Future> futures = this.createInvocations(members, predicate);
        return this.collectResults(result, futures, partitionCount);
    }

    private void invokeOnMissingPartitions(Collection<AccumulatedResults> result, Predicate predicate, BitSet finishedPartitions, int partitionCount) throws InterruptedException, ExecutionException {
        if (this.hasMissingPartitions(finishedPartitions, partitionCount)) {
            List<Integer> missingList = this.findMissingPartitions(finishedPartitions, partitionCount);
            ArrayList<Future> missingFutures = new ArrayList<Future>(missingList.size());
            this.createInvocationsForMissingPartitions(missingList, missingFutures, predicate);
            this.collectResultsFromMissingPartitions(finishedPartitions, result, missingFutures);
        }
        this.assertAllPartitionsQueried(finishedPartitions, partitionCount);
    }

    private List<Future> createInvocations(Collection<Member> members, Predicate predicate) {
        ArrayList<Future> futures = new ArrayList<Future>(members.size());
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        Query query = this.buildQuery(predicate);
        MapService mapService = (MapService)this.nodeEngine.getService(this.getServiceName());
        MapServiceContext mapServiceContext = mapService.getMapServiceContext();
        for (Member member : members) {
            try {
                InternalCompletableFuture future = operationService.createInvocationBuilder("hz:impl:mapService", (Operation)this.createQueryOperation(query, mapServiceContext), member.getAddress()).invoke();
                futures.add(future);
            }
            catch (Throwable t) {
                if (!(t instanceof HazelcastException)) {
                    throw ExceptionUtil.rethrow(t);
                }
                if (t.getCause() instanceof QueryResultSizeExceededException) {
                    throw ExceptionUtil.rethrow(t);
                }
                if (!this.logger.isFineEnabled()) continue;
                this.logger.fine("Query invocation failed on member " + member, t);
            }
        }
        return futures;
    }

    private Query buildQuery(Predicate predicate) {
        Query.QueryBuilder builder = Query.of().mapName(this.getDistributedObjectName()).predicate(predicate instanceof PartitionPredicate ? ((PartitionPredicate)predicate).getTarget() : predicate).iterationType(this.getIterationType());
        if (this.getAggregator() != null) {
            builder = builder.aggregator(this.getAggregator());
        }
        if (this.getProjection() != null) {
            builder = builder.projection(this.getProjection());
        }
        return builder.build();
    }

    private BitSet collectResults(Collection<AccumulatedResults> result, List<Future> futures, int partitionCount) throws InterruptedException, ExecutionException {
        BitSet finishedPartitions = new BitSet(partitionCount);
        for (Future future : futures) {
            try {
                Collection<Integer> partitionIds;
                Result queryResult = (Result)future.get();
                if (queryResult == null || (partitionIds = queryResult.getPartitionIds()) == null || BitSetUtils.hasAtLeastOneBitSet(finishedPartitions, partitionIds)) continue;
                BitSetUtils.setBits(finishedPartitions, partitionIds);
                this.extractAndAppendResult(result, queryResult);
            }
            catch (Throwable t) {
                if (t.getCause() instanceof QueryResultSizeExceededException) {
                    throw ExceptionUtil.rethrow(t);
                }
                if (!this.logger.isFineEnabled()) continue;
                this.logger.fine("Query on member failed with exception", t);
            }
        }
        return finishedPartitions;
    }

    private boolean hasMissingPartitions(BitSet finishedPartitions, int partitionCount) {
        return finishedPartitions.nextClearBit(0) < partitionCount;
    }

    private List<Integer> findMissingPartitions(BitSet finishedPartitions, int partitionCount) {
        ArrayList<Integer> missingList = new ArrayList<Integer>();
        for (int i = 0; i < partitionCount; ++i) {
            if (finishedPartitions.get(i)) continue;
            missingList.add(i);
        }
        return missingList;
    }

    private void createInvocationsForMissingPartitions(List<Integer> missingPartitionsList, List<Future> futures, Predicate predicate) {
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        MapService mapService = (MapService)this.nodeEngine.getService(this.getServiceName());
        MapServiceContext mapServiceContext = mapService.getMapServiceContext();
        Query query = this.buildQuery(predicate);
        for (Integer partitionId : missingPartitionsList) {
            MapOperation queryPartitionOperation = this.createQueryPartitionOperation(query, mapServiceContext);
            queryPartitionOperation.setPartitionId(partitionId);
            try {
                InternalCompletableFuture future = operationService.invokeOnPartition("hz:impl:mapService", queryPartitionOperation, partitionId);
                futures.add(future);
            }
            catch (Throwable t) {
                throw ExceptionUtil.rethrow(t);
            }
        }
    }

    private void collectResultsFromMissingPartitions(BitSet finishedPartitions, Collection<AccumulatedResults> result, List<Future> futures) throws InterruptedException, ExecutionException {
        for (Future future : futures) {
            Result queryResult = (Result)future.get();
            if (queryResult.getPartitionIds() == null || queryResult.getPartitionIds().size() <= 0 || BitSetUtils.hasAtLeastOneBitSet(finishedPartitions, queryResult.getPartitionIds())) continue;
            this.extractAndAppendResult(result, queryResult);
            BitSetUtils.setBits(finishedPartitions, queryResult.getPartitionIds());
        }
    }

    private MapOperation createQueryOperation(Query query, MapServiceContext mapServiceContext) {
        return mapServiceContext.getMapOperationProvider(query.getMapName()).createQueryOperation(query);
    }

    private MapOperation createQueryPartitionOperation(Query query, MapServiceContext mapServiceContext) {
        return mapServiceContext.getMapOperationProvider(query.getMapName()).createQueryPartitionOperation(query);
    }

    private void assertAllPartitionsQueried(BitSet finishedPartitions, int partitionCount) {
        if (this.hasMissingPartitions(finishedPartitions, partitionCount)) {
            int missedPartitionsCount = 0;
            for (int i = 0; i < partitionCount; ++i) {
                if (finishedPartitions.get(i)) continue;
                ++missedPartitionsCount;
            }
            throw new QueryException("Query aborted. Could not execute query for all partitions. Missed " + missedPartitionsCount + " partitions");
        }
    }
}

