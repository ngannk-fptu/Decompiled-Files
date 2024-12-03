/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.Member;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.map.QueryResultSizeExceededException;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.query.Query;
import com.hazelcast.map.impl.query.QueryEngine;
import com.hazelcast.map.impl.query.QueryResultSizeLimiter;
import com.hazelcast.map.impl.query.Result;
import com.hazelcast.map.impl.query.ResultProcessorRegistry;
import com.hazelcast.map.impl.query.Target;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.QueryException;
import com.hazelcast.query.TruePredicate;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.util.BitSetUtils;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.IterationType;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

public class QueryEngineImpl
implements QueryEngine {
    private final MapServiceContext mapServiceContext;
    private final NodeEngine nodeEngine;
    private final ILogger logger;
    private final QueryResultSizeLimiter queryResultSizeLimiter;
    private final IPartitionService partitionService;
    private final OperationService operationService;
    private final ClusterService clusterService;
    private final ResultProcessorRegistry resultProcessorRegistry;

    public QueryEngineImpl(MapServiceContext mapServiceContext) {
        this.mapServiceContext = mapServiceContext;
        this.nodeEngine = mapServiceContext.getNodeEngine();
        this.partitionService = this.nodeEngine.getPartitionService();
        this.logger = this.nodeEngine.getLogger(this.getClass());
        this.queryResultSizeLimiter = new QueryResultSizeLimiter(mapServiceContext, this.logger);
        this.operationService = this.nodeEngine.getOperationService();
        this.clusterService = this.nodeEngine.getClusterService();
        this.resultProcessorRegistry = mapServiceContext.getResultProcessorRegistry();
    }

    public Result execute(Query query, Target target) {
        Query adjustedQuery = this.adjustQuery(query);
        switch (target.mode()) {
            case ALL_NODES: {
                return this.runOnAllPartitions(adjustedQuery);
            }
            case LOCAL_NODE: {
                return this.runOnLocalPartitions(adjustedQuery);
            }
            case PARTITION_OWNER: {
                return this.runOnGivenPartition(adjustedQuery, target);
            }
        }
        throw new IllegalArgumentException("Illegal target " + query);
    }

    private Query adjustQuery(Query query) {
        IterationType retrievalIterationType = this.getRetrievalIterationType(query.getPredicate(), query.getIterationType());
        Query adjustedQuery = Query.of(query).iterationType(retrievalIterationType).build();
        if (adjustedQuery.getPredicate() instanceof PagingPredicate) {
            ((PagingPredicate)adjustedQuery.getPredicate()).setIterationType(query.getIterationType());
        } else if (adjustedQuery.getPredicate() == TruePredicate.INSTANCE) {
            this.queryResultSizeLimiter.precheckMaxResultLimitOnLocalPartitions(adjustedQuery.getMapName());
        }
        return adjustedQuery;
    }

    private Result runOnLocalPartitions(Query query) {
        BitSet mutablePartitionIds = this.getLocalPartitionIds();
        Result result = this.doRunOnQueryThreads(query, mutablePartitionIds, Target.LOCAL_NODE);
        if (this.isResultFromAnyPartitionMissing(mutablePartitionIds)) {
            this.doRunOnPartitionThreads(query, mutablePartitionIds, result);
        }
        this.assertAllPartitionsQueried(mutablePartitionIds);
        return result;
    }

    private Result runOnAllPartitions(Query query) {
        BitSet mutablePartitionIds = this.getAllPartitionIds();
        Result result = this.doRunOnQueryThreads(query, mutablePartitionIds, Target.ALL_NODES);
        if (this.isResultFromAnyPartitionMissing(mutablePartitionIds)) {
            this.doRunOnPartitionThreads(query, mutablePartitionIds, result);
        }
        this.assertAllPartitionsQueried(mutablePartitionIds);
        return result;
    }

    private Result runOnGivenPartition(Query query, Target target) {
        try {
            return this.dispatchPartitionScanQueryOnOwnerMemberOnPartitionThread(query, target.partitionId()).get();
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    private Result doRunOnQueryThreads(Query query, BitSet partitionIds, Target target) {
        Result result = this.populateResult(query, partitionIds);
        List<Future<Result>> futures = this.dispatchOnQueryThreads(query, target);
        this.addResultsOfPredicate(futures, result, partitionIds, false);
        return result;
    }

    private List<Future<Result>> dispatchOnQueryThreads(Query query, Target target) {
        try {
            return this.dispatchFullQueryOnQueryThread(query, target);
        }
        catch (Throwable t) {
            if (!(t instanceof HazelcastException)) {
                throw ExceptionUtil.rethrow(t);
            }
            if (t.getCause() instanceof QueryResultSizeExceededException) {
                throw ExceptionUtil.rethrow(t);
            }
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Query invocation failed on member ", t);
            }
            return Collections.emptyList();
        }
    }

    private Result populateResult(Query query, BitSet partitionIds) {
        return this.resultProcessorRegistry.get(query.getResultType()).populateResult(query, this.queryResultSizeLimiter.getNodeResultLimit(partitionIds.cardinality()));
    }

    private void doRunOnPartitionThreads(Query query, BitSet partitionIds, Result result) {
        try {
            List<Future<Result>> futures = this.dispatchPartitionScanQueryOnOwnerMemberOnPartitionThread(query, partitionIds);
            this.addResultsOfPredicate(futures, result, partitionIds, true);
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    private void addResultsOfPredicate(List<Future<Result>> futures, Result result, BitSet finishedPartitionIds, boolean rethrowAll) {
        for (Future<Result> future : futures) {
            Collection<Integer> queriedPartitionIds;
            Result queryResult = null;
            try {
                queryResult = future.get();
            }
            catch (Throwable t) {
                if (t.getCause() instanceof QueryResultSizeExceededException || rethrowAll) {
                    throw ExceptionUtil.rethrow(t);
                }
                this.logger.fine("Could not get query results", t);
            }
            if (queryResult == null || (queriedPartitionIds = queryResult.getPartitionIds()) == null || !BitSetUtils.hasAllBitsSet(finishedPartitionIds, queriedPartitionIds)) continue;
            BitSetUtils.unsetBits(finishedPartitionIds, queriedPartitionIds);
            result.combine(queryResult);
        }
    }

    private void assertAllPartitionsQueried(BitSet mutablePartitionIds) {
        if (this.isResultFromAnyPartitionMissing(mutablePartitionIds)) {
            throw new QueryException("Query aborted. Could not execute query for all partitions. Missed " + mutablePartitionIds.cardinality() + " partitions");
        }
    }

    private IterationType getRetrievalIterationType(Predicate predicate, IterationType iterationType) {
        IterationType retrievalIterationType = iterationType;
        if (predicate instanceof PagingPredicate) {
            PagingPredicate pagingPredicate = (PagingPredicate)predicate;
            retrievalIterationType = pagingPredicate.getComparator() != null ? IterationType.ENTRY : (iterationType == IterationType.VALUE ? IterationType.ENTRY : iterationType);
        }
        return retrievalIterationType;
    }

    private BitSet getLocalPartitionIds() {
        int partitionCount = this.partitionService.getPartitionCount();
        BitSet partitionIds = new BitSet(partitionCount);
        BitSetUtils.setBits(partitionIds, this.partitionService.getMemberPartitions(this.nodeEngine.getThisAddress()));
        return partitionIds;
    }

    private BitSet getAllPartitionIds() {
        int partitionCount = this.partitionService.getPartitionCount();
        BitSet partitionIds = new BitSet(partitionCount);
        partitionIds.set(0, partitionCount, true);
        return partitionIds;
    }

    private boolean isResultFromAnyPartitionMissing(BitSet finishedPartitionIds) {
        return !finishedPartitionIds.isEmpty();
    }

    protected QueryResultSizeLimiter getQueryResultSizeLimiter() {
        return this.queryResultSizeLimiter;
    }

    protected List<Future<Result>> dispatchFullQueryOnQueryThread(Query query, Target target) {
        switch (target.mode()) {
            case ALL_NODES: {
                return this.dispatchFullQueryOnAllMembersOnQueryThread(query);
            }
            case LOCAL_NODE: {
                return this.dispatchFullQueryOnLocalMemberOnQueryThread(query);
            }
        }
        throw new IllegalArgumentException("Illegal target " + query);
    }

    private List<Future<Result>> dispatchFullQueryOnLocalMemberOnQueryThread(Query query) {
        MapOperation operation = this.mapServiceContext.getMapOperationProvider(query.getMapName()).createQueryOperation(query);
        InternalCompletableFuture result = this.operationService.invokeOnTarget("hz:impl:mapService", operation, this.nodeEngine.getThisAddress());
        return Collections.singletonList(result);
    }

    private List<Future<Result>> dispatchFullQueryOnAllMembersOnQueryThread(Query query) {
        Collection<Member> members = this.clusterService.getMembers(MemberSelectors.DATA_MEMBER_SELECTOR);
        ArrayList<Future<Result>> futures = new ArrayList<Future<Result>>(members.size());
        for (Member member : members) {
            Operation operation = this.createQueryOperation(query);
            InternalCompletableFuture future = this.operationService.invokeOnTarget("hz:impl:mapService", operation, member.getAddress());
            futures.add(future);
        }
        return futures;
    }

    private Operation createQueryOperation(Query query) {
        return this.mapServiceContext.getMapOperationProvider(query.getMapName()).createQueryOperation(query);
    }

    protected List<Future<Result>> dispatchPartitionScanQueryOnOwnerMemberOnPartitionThread(Query query, BitSet partitionIds) {
        if (QueryEngineImpl.shouldSkipPartitionsQuery(partitionIds)) {
            return Collections.emptyList();
        }
        ArrayList<Future<Result>> futures = new ArrayList<Future<Result>>(partitionIds.size());
        for (int partitionId = 0; partitionId < partitionIds.length(); ++partitionId) {
            if (!partitionIds.get(partitionId)) continue;
            futures.add(this.dispatchPartitionScanQueryOnOwnerMemberOnPartitionThread(query, partitionId));
        }
        return futures;
    }

    protected Future<Result> dispatchPartitionScanQueryOnOwnerMemberOnPartitionThread(Query query, int partitionId) {
        Operation op = this.createQueryPartitionOperation(query);
        op.setPartitionId(partitionId);
        try {
            return this.operationService.invokeOnPartition("hz:impl:mapService", op, partitionId);
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    private Operation createQueryPartitionOperation(Query query) {
        return this.mapServiceContext.getMapOperationProvider(query.getMapName()).createQueryPartitionOperation(query);
    }

    private static boolean shouldSkipPartitionsQuery(BitSet partitionIds) {
        return partitionIds == null || partitionIds.isEmpty();
    }
}

