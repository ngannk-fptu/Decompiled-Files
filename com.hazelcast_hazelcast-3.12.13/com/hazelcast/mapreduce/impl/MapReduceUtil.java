/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl;

import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.core.Member;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.mapreduce.JobPartitionState;
import com.hazelcast.mapreduce.PartitionIdAware;
import com.hazelcast.mapreduce.RemoteMapReduceException;
import com.hazelcast.mapreduce.impl.MapReduceService;
import com.hazelcast.mapreduce.impl.operation.NotifyRemoteExceptionOperation;
import com.hazelcast.mapreduce.impl.task.JobPartitionStateImpl;
import com.hazelcast.mapreduce.impl.task.JobProcessInformationImpl;
import com.hazelcast.mapreduce.impl.task.JobSupervisor;
import com.hazelcast.mapreduce.impl.task.JobTaskConfiguration;
import com.hazelcast.mapreduce.impl.task.MemberAssigningJobProcessInformationImpl;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.InvocationBuilder;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.util.Clock;
import com.hazelcast.util.EmptyStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeoutException;

public final class MapReduceUtil {
    private static final String EXECUTOR_NAME_PREFIX = "mapreduce::hz::";
    private static final String SERVICE_NAME = "hz:impl:mapReduceService";
    private static final float DEFAULT_MAP_GROWTH_FACTOR = 0.75f;
    private static final int RETRY_PARTITION_TABLE_MILLIS = 100;
    private static final long PARTITION_READY_TIMEOUT = 10000L;

    private MapReduceUtil() {
    }

    public static JobProcessInformationImpl createJobProcessInformation(JobTaskConfiguration configuration, JobSupervisor supervisor) {
        NodeEngine nodeEngine = configuration.getNodeEngine();
        if (configuration.getKeyValueSource() instanceof PartitionIdAware) {
            int partitionCount = nodeEngine.getPartitionService().getPartitionCount();
            return new JobProcessInformationImpl(partitionCount, supervisor);
        }
        int partitionCount = nodeEngine.getClusterService().getSize(MemberSelectors.DATA_MEMBER_SELECTOR);
        return new MemberAssigningJobProcessInformationImpl(partitionCount, supervisor);
    }

    public static void notifyRemoteException(JobSupervisor supervisor, Throwable throwable) {
        MapReduceService mapReduceService = supervisor.getMapReduceService();
        NodeEngine nodeEngine = mapReduceService.getNodeEngine();
        try {
            Address jobOwner = supervisor.getJobOwner();
            if (supervisor.isOwnerNode()) {
                supervisor.notifyRemoteException(jobOwner, throwable);
            } else {
                String name = supervisor.getConfiguration().getName();
                String jobId = supervisor.getConfiguration().getJobId();
                NotifyRemoteExceptionOperation operation = new NotifyRemoteExceptionOperation(name, jobId, throwable);
                OperationService os = nodeEngine.getOperationService();
                os.send(operation, jobOwner);
            }
        }
        catch (Exception e) {
            ILogger logger = nodeEngine.getLogger(MapReduceUtil.class);
            logger.warning("Could not notify remote map-reduce owner", e);
        }
    }

    public static JobPartitionState.State stateChange(Address owner, int partitionId, JobPartitionState.State currentState, JobProcessInformationImpl processInformation, JobTaskConfiguration configuration) {
        JobPartitionState[] partitionStates = processInformation.getPartitionStates();
        JobPartitionState partitionState = partitionStates[partitionId];
        JobPartitionState.State finalState = null;
        if (partitionState != null) {
            if (!owner.equals(partitionState.getOwner())) {
                return null;
            }
            if (partitionState.getState() != currentState) {
                return null;
            }
            if (currentState == JobPartitionState.State.MAPPING) {
                finalState = MapReduceUtil.stateChangeMapping(partitionId, partitionState, processInformation, owner, configuration);
            } else if (currentState == JobPartitionState.State.REDUCING) {
                finalState = MapReduceUtil.stateChangeReducing(partitionId, partitionState, processInformation, owner);
            }
        }
        if (currentState == JobPartitionState.State.WAITING && MapReduceUtil.compareAndSwapPartitionState(partitionId, partitionState, processInformation, owner, JobPartitionState.State.MAPPING)) {
            finalState = JobPartitionState.State.MAPPING;
        }
        return finalState;
    }

    private static JobPartitionState.State stateChangeReducing(int partitionId, JobPartitionState oldPartitionState, JobProcessInformationImpl processInformation, Address owner) {
        if (MapReduceUtil.compareAndSwapPartitionState(partitionId, oldPartitionState, processInformation, owner, JobPartitionState.State.PROCESSED)) {
            return JobPartitionState.State.PROCESSED;
        }
        return null;
    }

    private static JobPartitionState.State stateChangeMapping(int partitionId, JobPartitionState oldPartitionState, JobProcessInformationImpl processInformation, Address owner, JobTaskConfiguration configuration) {
        JobPartitionState.State newState = JobPartitionState.State.PROCESSED;
        if (configuration.getReducerFactory() != null) {
            newState = JobPartitionState.State.REDUCING;
        }
        if (MapReduceUtil.compareAndSwapPartitionState(partitionId, oldPartitionState, processInformation, owner, newState)) {
            return newState;
        }
        return null;
    }

    private static boolean compareAndSwapPartitionState(int partitionId, JobPartitionState oldPartitionState, JobProcessInformationImpl processInformation, Address owner, JobPartitionState.State newState) {
        JobPartitionStateImpl newPartitionState = new JobPartitionStateImpl(owner, newState);
        return processInformation.updatePartitionState(partitionId, oldPartitionState, newPartitionState);
    }

    public static <V> List<V> executeOperation(Collection<Member> members, OperationFactory operationFactory, MapReduceService mapReduceService, NodeEngine nodeEngine) {
        OperationService operationService = nodeEngine.getOperationService();
        ArrayList futures = new ArrayList();
        ArrayList<Object> results = new ArrayList<Object>();
        ArrayList<Exception> exceptions = new ArrayList<Exception>(members.size());
        for (Member member : members) {
            try {
                Operation operation = operationFactory.createOperation();
                if (nodeEngine.getThisAddress().equals(member.getAddress())) {
                    operation.setNodeEngine(nodeEngine);
                    operation.setCallerUuid(nodeEngine.getLocalMember().getUuid());
                    operation.setService(mapReduceService);
                    operation.run();
                    Object response = operation.getResponse();
                    if (response == null) continue;
                    results.add(response);
                    continue;
                }
                InvocationBuilder ib = operationService.createInvocationBuilder(SERVICE_NAME, operation, member.getAddress());
                InternalCompletableFuture future = ib.invoke();
                futures.add(future);
            }
            catch (Exception e) {
                exceptions.add(e);
            }
        }
        for (InternalCompletableFuture internalCompletableFuture : futures) {
            try {
                Object response = internalCompletableFuture.join();
                if (response == null) continue;
                results.add(response);
            }
            catch (Exception e) {
                exceptions.add(e);
            }
        }
        if (exceptions.size() > 0) {
            throw new RemoteMapReduceException("Exception on mapreduce operation", exceptions);
        }
        return results;
    }

    public static <V> V executeOperation(Operation operation, Address address, MapReduceService mapReduceService, NodeEngine nodeEngine) {
        ClusterService cs = nodeEngine.getClusterService();
        OperationService os = nodeEngine.getOperationService();
        boolean returnsResponse = operation.returnsResponse();
        try {
            if (cs.getThisAddress().equals(address)) {
                operation.setNodeEngine(nodeEngine);
                operation.setCallerUuid(nodeEngine.getLocalMember().getUuid());
                operation.setService(mapReduceService);
                operation.run();
                if (returnsResponse) {
                    return (V)operation.getResponse();
                }
            } else {
                if (returnsResponse) {
                    InvocationBuilder ib = os.createInvocationBuilder(SERVICE_NAME, operation, address);
                    return ib.invoke().get();
                }
                os.send(operation, address);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static String buildExecutorName(String name) {
        return EXECUTOR_NAME_PREFIX + name;
    }

    public static int mapSize(int sourceSize) {
        return sourceSize == 0 ? 0 : (int)((float)sourceSize / 0.75f) + 1;
    }

    public static void enforcePartitionTableWarmup(MapReduceService mapReduceService) throws TimeoutException {
        IPartitionService partitionService = mapReduceService.getNodeEngine().getPartitionService();
        int partitionCount = partitionService.getPartitionCount();
        long startTime = Clock.currentTimeMillis();
        for (int p = 0; p < partitionCount; ++p) {
            while (partitionService.getPartitionOwner(p) == null) {
                try {
                    Thread.sleep(100L);
                }
                catch (Exception ignore) {
                    EmptyStatement.ignore(ignore);
                }
                if (Clock.currentTimeMillis() - startTime <= 10000L) continue;
                throw new TimeoutException("Partition get ready timeout reached!");
            }
        }
    }
}

