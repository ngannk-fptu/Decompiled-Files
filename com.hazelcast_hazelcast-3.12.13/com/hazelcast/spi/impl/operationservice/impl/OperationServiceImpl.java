/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice.impl;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.LocalMemberResetException;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.ClusterClock;
import com.hazelcast.internal.management.dto.SlowOperationDTO;
import com.hazelcast.internal.metrics.MetricsProvider;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.util.counters.Counter;
import com.hazelcast.internal.util.counters.MwCounter;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.InvocationBuilder;
import com.hazelcast.spi.LiveOperations;
import com.hazelcast.spi.LiveOperationsTracker;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.PartitionSpecificRunnable;
import com.hazelcast.spi.impl.operationexecutor.OperationExecutor;
import com.hazelcast.spi.impl.operationexecutor.impl.OperationExecutorImpl;
import com.hazelcast.spi.impl.operationexecutor.slowoperationdetector.SlowOperationDetector;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.impl.operationservice.PartitionTaskFactory;
import com.hazelcast.spi.impl.operationservice.impl.BackpressureRegulator;
import com.hazelcast.spi.impl.operationservice.impl.InboundResponseHandler;
import com.hazelcast.spi.impl.operationservice.impl.InboundResponseHandlerSupplier;
import com.hazelcast.spi.impl.operationservice.impl.Invocation;
import com.hazelcast.spi.impl.operationservice.impl.InvocationBuilderImpl;
import com.hazelcast.spi.impl.operationservice.impl.InvocationFuture;
import com.hazelcast.spi.impl.operationservice.impl.InvocationMonitor;
import com.hazelcast.spi.impl.operationservice.impl.InvocationRegistry;
import com.hazelcast.spi.impl.operationservice.impl.InvokeOnPartitions;
import com.hazelcast.spi.impl.operationservice.impl.OperationBackupHandler;
import com.hazelcast.spi.impl.operationservice.impl.OperationRunnerFactoryImpl;
import com.hazelcast.spi.impl.operationservice.impl.OutboundOperationHandler;
import com.hazelcast.spi.impl.operationservice.impl.OutboundResponseHandler;
import com.hazelcast.spi.impl.operationservice.impl.PartitionInvocation;
import com.hazelcast.spi.impl.operationservice.impl.TargetInvocation;
import com.hazelcast.spi.impl.operationutil.Operations;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.util.CollectionUtil;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.Preconditions;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class OperationServiceImpl
implements InternalOperationService,
MetricsProvider,
LiveOperationsTracker {
    private static final long TERMINATION_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(10L);
    @Probe
    final Set<Operation> asyncOperations = Collections.newSetFromMap(new ConcurrentHashMap());
    final InvocationRegistry invocationRegistry;
    final OperationExecutor operationExecutor;
    @Probe(name="operationTimeoutCount", level=ProbeLevel.MANDATORY)
    final MwCounter operationTimeoutCount = MwCounter.newMwCounter();
    @Probe(name="callTimeoutCount", level=ProbeLevel.MANDATORY)
    final MwCounter callTimeoutCount = MwCounter.newMwCounter();
    @Probe(name="retryCount", level=ProbeLevel.MANDATORY)
    final MwCounter retryCount = MwCounter.newMwCounter();
    @Probe(name="failedBackups", level=ProbeLevel.MANDATORY)
    final Counter failedBackupsCount = MwCounter.newMwCounter();
    final NodeEngineImpl nodeEngine;
    final Node node;
    final ILogger logger;
    final OperationBackupHandler backupHandler;
    final BackpressureRegulator backpressureRegulator;
    final OutboundResponseHandler outboundResponseHandler;
    final OutboundOperationHandler outboundOperationHandler;
    volatile Invocation.Context invocationContext;
    private final InvocationMonitor invocationMonitor;
    private final SlowOperationDetector slowOperationDetector;
    private final InboundResponseHandlerSupplier inboundResponseHandlerSupplier;
    private final InternalSerializationService serializationService;
    private final int invocationMaxRetryCount;
    private final long invocationRetryPauseMillis;
    private final boolean failOnIndeterminateOperationState;

    public OperationServiceImpl(NodeEngineImpl nodeEngine) {
        this.nodeEngine = nodeEngine;
        this.node = nodeEngine.getNode();
        Address thisAddress = this.node.getThisAddress();
        this.logger = this.node.getLogger(OperationService.class);
        this.serializationService = (InternalSerializationService)nodeEngine.getSerializationService();
        this.invocationMaxRetryCount = this.node.getProperties().getInteger(GroupProperty.INVOCATION_MAX_RETRY_COUNT);
        this.invocationRetryPauseMillis = this.node.getProperties().getMillis(GroupProperty.INVOCATION_RETRY_PAUSE);
        this.failOnIndeterminateOperationState = nodeEngine.getProperties().getBoolean(GroupProperty.FAIL_ON_INDETERMINATE_OPERATION_STATE);
        this.backpressureRegulator = new BackpressureRegulator(this.node.getProperties(), this.node.getLogger(BackpressureRegulator.class));
        this.outboundResponseHandler = new OutboundResponseHandler(thisAddress, this.serializationService, this.node.getLogger(OutboundResponseHandler.class));
        this.invocationRegistry = new InvocationRegistry(this.node.getLogger(OperationServiceImpl.class), this.backpressureRegulator.newCallIdSequence());
        this.invocationMonitor = new InvocationMonitor(nodeEngine, thisAddress, this.node.getProperties(), this.invocationRegistry, this.node.getLogger(InvocationMonitor.class), this.serializationService, nodeEngine.getServiceManager());
        this.outboundOperationHandler = new OutboundOperationHandler(this.node, this.serializationService);
        this.backupHandler = new OperationBackupHandler(this, this.outboundOperationHandler);
        String hzName = nodeEngine.getHazelcastInstance().getName();
        ClassLoader configClassLoader = this.node.getConfigClassLoader();
        this.inboundResponseHandlerSupplier = new InboundResponseHandlerSupplier(configClassLoader, this.invocationRegistry, hzName, nodeEngine);
        this.operationExecutor = new OperationExecutorImpl(this.node.getProperties(), this.node.loggingService, thisAddress, new OperationRunnerFactoryImpl(this), this.node.getNodeExtension(), hzName, configClassLoader);
        this.slowOperationDetector = new SlowOperationDetector(this.node.loggingService, this.operationExecutor.getGenericOperationRunners(), this.operationExecutor.getPartitionOperationRunners(), this.node.getProperties(), hzName);
    }

    public OutboundResponseHandler getOutboundResponseHandler() {
        return this.outboundResponseHandler;
    }

    public InboundResponseHandlerSupplier getInboundResponseHandlerSupplier() {
        return this.inboundResponseHandlerSupplier;
    }

    public InvocationMonitor getInvocationMonitor() {
        return this.invocationMonitor;
    }

    @Override
    public List<SlowOperationDTO> getSlowOperationDTOs() {
        return this.slowOperationDetector.getSlowOperationDTOs();
    }

    public InvocationRegistry getInvocationRegistry() {
        return this.invocationRegistry;
    }

    public InboundResponseHandler getBackupHandler() {
        return this.inboundResponseHandlerSupplier.backupHandler();
    }

    @Override
    public int getPartitionThreadCount() {
        return this.operationExecutor.getPartitionThreadCount();
    }

    @Override
    public int getGenericThreadCount() {
        return this.operationExecutor.getGenericThreadCount();
    }

    @Override
    public int getRunningOperationsCount() {
        return this.operationExecutor.getRunningOperationCount();
    }

    @Override
    public long getExecutedOperationCount() {
        return this.operationExecutor.getExecutedOperationCount();
    }

    @Override
    public int getRemoteOperationsCount() {
        return this.invocationRegistry.size();
    }

    @Override
    public int getOperationExecutorQueueSize() {
        return this.operationExecutor.getQueueSize();
    }

    @Override
    public int getPriorityOperationExecutorQueueSize() {
        return this.operationExecutor.getPriorityQueueSize();
    }

    public OperationExecutor getOperationExecutor() {
        return this.operationExecutor;
    }

    @Override
    public int getResponseQueueSize() {
        return this.inboundResponseHandlerSupplier.responseQueueSize();
    }

    @Override
    public void populate(LiveOperations liveOperations) {
        this.operationExecutor.populate(liveOperations);
        for (Operation op : this.asyncOperations) {
            liveOperations.add(op.getCallerAddress(), op.getCallId());
        }
    }

    @Override
    public void execute(PartitionSpecificRunnable task) {
        this.operationExecutor.execute(task);
    }

    @Override
    public void executeOnPartitions(PartitionTaskFactory taskFactory, BitSet partitions) {
        this.operationExecutor.executeOnPartitions(taskFactory, partitions);
    }

    @Override
    public InvocationBuilder createInvocationBuilder(String serviceName, Operation op, int partitionId) {
        Preconditions.checkNotNegative(partitionId, "Partition ID cannot be negative!");
        return new InvocationBuilderImpl(this.invocationContext, serviceName, op, partitionId).setTryCount(this.invocationMaxRetryCount).setTryPauseMillis(this.invocationRetryPauseMillis).setFailOnIndeterminateOperationState(this.failOnIndeterminateOperationState);
    }

    @Override
    public InvocationBuilder createInvocationBuilder(String serviceName, Operation op, Address target) {
        Preconditions.checkNotNull(target, "Target cannot be null!");
        return new InvocationBuilderImpl(this.invocationContext, serviceName, op, target).setTryCount(this.invocationMaxRetryCount).setTryPauseMillis(this.invocationRetryPauseMillis);
    }

    @Override
    public void run(Operation op) {
        this.operationExecutor.run(op);
    }

    @Override
    public void execute(Operation op) {
        this.operationExecutor.execute(op);
    }

    @Override
    public boolean isRunAllowed(Operation op) {
        return this.operationExecutor.isRunAllowed(op);
    }

    @Override
    public <E> InternalCompletableFuture<E> invokeOnPartition(String serviceName, Operation op, int partitionId) {
        op.setServiceName(serviceName).setPartitionId(partitionId).setReplicaIndex(0);
        return new PartitionInvocation(this.invocationContext, op, this.invocationMaxRetryCount, this.invocationRetryPauseMillis, -1L, true, this.failOnIndeterminateOperationState).invoke();
    }

    @Override
    public <E> InternalCompletableFuture<E> invokeOnPartition(Operation op) {
        return new PartitionInvocation(this.invocationContext, op, this.invocationMaxRetryCount, this.invocationRetryPauseMillis, -1L, true, this.failOnIndeterminateOperationState).invoke();
    }

    @Override
    public <E> InternalCompletableFuture<E> invokeOnTarget(String serviceName, Operation op, Address target) {
        op.setServiceName(serviceName);
        return new TargetInvocation(this.invocationContext, op, target, this.invocationMaxRetryCount, this.invocationRetryPauseMillis, -1L, true).invoke();
    }

    @Override
    public <V> void asyncInvokeOnPartition(String serviceName, Operation op, int partitionId, ExecutionCallback<V> callback) {
        op.setServiceName(serviceName).setPartitionId(partitionId).setReplicaIndex(0);
        InvocationFuture future = new PartitionInvocation(this.invocationContext, op, this.invocationMaxRetryCount, this.invocationRetryPauseMillis, -1L, true, this.failOnIndeterminateOperationState).invokeAsync();
        if (callback != null) {
            future.andThen(callback);
        }
    }

    @Override
    public void onStartAsyncOperation(Operation op) {
        this.asyncOperations.add(op);
    }

    @Override
    public void onCompletionAsyncOperation(Operation op) {
        this.asyncOperations.remove(op);
    }

    @Override
    public boolean isCallTimedOut(Operation op) {
        if (Operations.isJoinOperation(op) || Operations.isWanReplicationOperation(op)) {
            return false;
        }
        long callTimeout = op.getCallTimeout();
        long invocationTime = op.getInvocationTime();
        long expireTime = invocationTime + callTimeout;
        if (expireTime <= 0L || expireTime == Long.MAX_VALUE) {
            return false;
        }
        ClusterClock clusterClock = this.nodeEngine.getClusterService().getClusterClock();
        long now = clusterClock.getClusterTime();
        return expireTime < now;
    }

    @Override
    public Map<Integer, Object> invokeOnAllPartitions(String serviceName, OperationFactory operationFactory) throws Exception {
        Map<Address, List<Integer>> memberPartitions = this.nodeEngine.getPartitionService().getMemberPartitionsMap();
        InvokeOnPartitions invokeOnPartitions = new InvokeOnPartitions(this, serviceName, operationFactory, memberPartitions);
        return invokeOnPartitions.invoke();
    }

    @Override
    public <T> ICompletableFuture<Map<Integer, T>> invokeOnAllPartitionsAsync(String serviceName, OperationFactory operationFactory) {
        Map<Address, List<Integer>> memberPartitions = this.nodeEngine.getPartitionService().getMemberPartitionsMap();
        InvokeOnPartitions invokeOnPartitions = new InvokeOnPartitions(this, serviceName, operationFactory, memberPartitions);
        return invokeOnPartitions.invokeAsync();
    }

    @Override
    public <T> Map<Integer, T> invokeOnPartitions(String serviceName, OperationFactory operationFactory, Collection<Integer> partitions) throws Exception {
        Map<Address, List<Integer>> memberPartitions = this.getMemberPartitions(partitions);
        InvokeOnPartitions invokeOnPartitions = new InvokeOnPartitions(this, serviceName, operationFactory, memberPartitions);
        return invokeOnPartitions.invoke();
    }

    private Map<Address, List<Integer>> getMemberPartitions(Collection<Integer> partitions) {
        Map<Address, List<Integer>> memberPartitions = MapUtil.createHashMap(3);
        InternalPartitionService partitionService = this.nodeEngine.getPartitionService();
        for (int partition : partitions) {
            Address owner = partitionService.getPartitionOwnerOrWait(partition);
            if (!memberPartitions.containsKey(owner)) {
                memberPartitions.put(owner, new ArrayList());
            }
            memberPartitions.get(owner).add(partition);
        }
        return memberPartitions;
    }

    @Override
    public <T> ICompletableFuture<Map<Integer, T>> invokeOnPartitionsAsync(String serviceName, OperationFactory operationFactory, Collection<Integer> partitions) {
        Map<Address, List<Integer>> memberPartitions = this.getMemberPartitions(partitions);
        InvokeOnPartitions invokeOnPartitions = new InvokeOnPartitions(this, serviceName, operationFactory, memberPartitions);
        return invokeOnPartitions.invokeAsync();
    }

    @Override
    public Map<Integer, Object> invokeOnPartitions(String serviceName, OperationFactory operationFactory, int[] partitions) throws Exception {
        return this.invokeOnPartitions(serviceName, operationFactory, CollectionUtil.toIntegerList(partitions));
    }

    @Override
    public boolean send(Operation op, Address target) {
        return this.outboundOperationHandler.send(op, target);
    }

    public void onMemberLeft(MemberImpl member) {
        this.invocationMonitor.onMemberLeft(member);
    }

    @Override
    public void onEndpointLeft(Address endpoint) {
        this.invocationMonitor.onEndpointLeft(endpoint);
    }

    public void reset() {
        LocalMemberResetException cause = new LocalMemberResetException(this.node.getLocalMember() + " has reset.");
        this.invocationRegistry.reset(cause);
    }

    @Override
    public void provideMetrics(MetricsRegistry registry) {
        registry.scanAndRegister(this, "operation");
        registry.collectMetrics(this.invocationRegistry, this.invocationMonitor, this.inboundResponseHandlerSupplier, this.operationExecutor);
    }

    public void start() {
        this.logger.finest("Starting OperationService");
        this.initInvocationContext();
        this.invocationMonitor.start();
        this.operationExecutor.start();
        this.inboundResponseHandlerSupplier.start();
        this.slowOperationDetector.start();
    }

    private void initInvocationContext() {
        this.invocationContext = new Invocation.Context(this.nodeEngine.getExecutionService().getExecutor("hz:async"), this.nodeEngine.getClusterService().getClusterClock(), this.nodeEngine.getClusterService(), this.node.networkingService, this.node.nodeEngine.getExecutionService(), this.nodeEngine.getProperties().getMillis(GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS), this.invocationRegistry, this.invocationMonitor, this.nodeEngine.getLogger(Invocation.class), this.node, this.nodeEngine, this.nodeEngine.getPartitionService(), this, this.operationExecutor, this.retryCount, this.serializationService, this.nodeEngine.getThisAddress(), this.outboundOperationHandler, this.node.getEndpointManager());
    }

    public Invocation.Context getInvocationContext() {
        return this.invocationContext;
    }

    public void shutdownInvocations() {
        this.logger.finest("Shutting down invocations");
        this.invocationRegistry.shutdown();
        this.invocationMonitor.shutdown();
        this.inboundResponseHandlerSupplier.shutdown();
        try {
            this.invocationMonitor.awaitTermination(TERMINATION_TIMEOUT_MILLIS);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void shutdownOperationExecutor() {
        this.logger.finest("Shutting down operation executors");
        this.operationExecutor.shutdown();
        this.slowOperationDetector.shutdown();
    }
}

