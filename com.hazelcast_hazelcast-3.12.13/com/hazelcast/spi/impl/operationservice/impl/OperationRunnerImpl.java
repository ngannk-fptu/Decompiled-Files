/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice.impl;

import com.hazelcast.client.impl.protocol.task.MessageTask;
import com.hazelcast.cluster.ClusterState;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.instance.NodeState;
import com.hazelcast.instance.OutOfMemoryErrorDispatcher;
import com.hazelcast.internal.metrics.MetricsProvider;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.internal.serialization.impl.SerializationServiceV1;
import com.hazelcast.internal.util.counters.Counter;
import com.hazelcast.internal.util.counters.MwCounter;
import com.hazelcast.internal.util.counters.SwCounter;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.Packet;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.quorum.impl.QuorumServiceImpl;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.CallStatus;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.Offload;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationAccessor;
import com.hazelcast.spi.OperationResponseHandler;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.exception.CallerNotMemberException;
import com.hazelcast.spi.exception.PartitionMigratingException;
import com.hazelcast.spi.exception.ResponseAlreadySentException;
import com.hazelcast.spi.exception.RetryableException;
import com.hazelcast.spi.exception.RetryableHazelcastException;
import com.hazelcast.spi.exception.WrongTargetException;
import com.hazelcast.spi.impl.AllowedDuringPassiveState;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.OperationResponseHandlerFactory;
import com.hazelcast.spi.impl.operationexecutor.OperationRunner;
import com.hazelcast.spi.impl.operationservice.impl.OperationBackupHandler;
import com.hazelcast.spi.impl.operationservice.impl.OperationServiceImpl;
import com.hazelcast.spi.impl.operationservice.impl.OutboundResponseHandler;
import com.hazelcast.spi.impl.operationservice.impl.operations.Backup;
import com.hazelcast.spi.impl.operationservice.impl.responses.CallTimeoutResponse;
import com.hazelcast.spi.impl.operationservice.impl.responses.ErrorResponse;
import com.hazelcast.spi.impl.operationservice.impl.responses.NormalResponse;
import com.hazelcast.spi.impl.operationutil.Operations;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.util.ExceptionUtil;
import java.io.IOException;
import java.util.logging.Level;

class OperationRunnerImpl
extends OperationRunner
implements MetricsProvider {
    static final int AD_HOC_PARTITION_ID = -2;
    private final ILogger logger;
    private final OperationServiceImpl operationService;
    private final Node node;
    private final NodeEngineImpl nodeEngine;
    @Probe(level=ProbeLevel.DEBUG)
    private final Counter executedOperationsCounter;
    private final Address thisAddress;
    private final boolean staleReadOnMigrationEnabled;
    private final Counter failedBackupsCounter;
    private final OperationBackupHandler backupHandler;
    private final int genericId;
    private InternalPartition internalPartition;
    private final OutboundResponseHandler outboundResponseHandler;

    OperationRunnerImpl(OperationServiceImpl operationService, int partitionId, int genericId, Counter failedBackupsCounter) {
        super(partitionId);
        this.genericId = genericId;
        this.operationService = operationService;
        this.logger = operationService.node.getLogger(OperationRunnerImpl.class);
        this.node = operationService.node;
        this.thisAddress = this.node.getThisAddress();
        this.nodeEngine = operationService.nodeEngine;
        this.outboundResponseHandler = operationService.outboundResponseHandler;
        this.staleReadOnMigrationEnabled = !this.node.getProperties().getBoolean(GroupProperty.DISABLE_STALE_READ_ON_PARTITION_MIGRATION);
        this.failedBackupsCounter = failedBackupsCounter;
        this.backupHandler = operationService.backupHandler;
        this.executedOperationsCounter = partitionId == -2 ? MwCounter.newMwCounter() : SwCounter.newSwCounter();
    }

    @Override
    public long executedOperationsCount() {
        return this.executedOperationsCounter.get();
    }

    @Override
    public void provideMetrics(MetricsRegistry registry) {
        if (this.partitionId >= 0) {
            registry.scanAndRegister(this, "operation.partition[" + this.partitionId + "]");
        } else if (this.partitionId == -1) {
            registry.scanAndRegister(this, "operation.generic[" + this.genericId + "]");
        } else {
            registry.scanAndRegister(this, "operation.adhoc");
        }
    }

    @Override
    public void run(Runnable task) {
        boolean publishCurrentTask = this.publishCurrentTask();
        if (publishCurrentTask) {
            this.currentTask = task;
        }
        try {
            task.run();
        }
        finally {
            if (publishCurrentTask) {
                this.currentTask = null;
            }
        }
    }

    private boolean publishCurrentTask() {
        boolean isClientRunnable = this.currentTask instanceof MessageTask;
        return this.getPartitionId() != -2 && (this.currentTask == null || isClientRunnable);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run(Operation op) {
        this.executedOperationsCounter.inc();
        boolean publishCurrentTask = this.publishCurrentTask();
        if (publishCurrentTask) {
            this.currentTask = op;
        }
        try {
            this.checkNodeState(op);
            if (this.timeout(op)) {
                return;
            }
            this.ensureNoPartitionProblems(op);
            this.ensureQuorumPresent(op);
            op.beforeRun();
            this.call(op);
        }
        catch (Throwable e) {
            this.handleOperationError(op, e);
        }
        finally {
            if (publishCurrentTask) {
                this.currentTask = null;
            }
        }
    }

    private void call(Operation op) throws Exception {
        CallStatus callStatus = op.call();
        switch (callStatus.ordinal()) {
            case 0: {
                int backupAcks = this.backupHandler.sendBackups(op);
                Object response = op.getResponse();
                if (backupAcks > 0) {
                    response = new NormalResponse(response, op.getCallId(), backupAcks, op.isUrgent());
                }
                try {
                    op.sendResponse(response);
                }
                catch (ResponseAlreadySentException e) {
                    this.logOperationError(op, e);
                }
                this.afterRun(op);
                break;
            }
            case 1: {
                this.backupHandler.sendBackups(op);
                this.afterRun(op);
                break;
            }
            case 3: {
                op.afterRun();
                Offload offload = (Offload)callStatus;
                offload.init(this.nodeEngine, this.operationService.asyncOperations);
                offload.start();
                break;
            }
            case 2: {
                this.nodeEngine.getOperationParker().park((BlockingOperation)((Object)op));
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }

    private void checkNodeState(Operation op) {
        NodeState state = this.node.getState();
        if (state == NodeState.ACTIVE) {
            return;
        }
        Address localAddress = this.node.getThisAddress();
        if (state == NodeState.SHUT_DOWN) {
            throw new HazelcastInstanceNotActiveException("Member " + localAddress + " is shut down! Operation: " + op);
        }
        if (op instanceof AllowedDuringPassiveState) {
            return;
        }
        if (this.nodeEngine.getClusterService().getClusterState() == ClusterState.PASSIVE) {
            throw new IllegalStateException("Cluster is in " + (Object)((Object)ClusterState.PASSIVE) + " state! Operation: " + op);
        }
        if (op.getPartitionId() < 0) {
            throw new HazelcastInstanceNotActiveException("Member " + localAddress + " is currently passive! Operation: " + op);
        }
        throw new RetryableHazelcastException("Member " + localAddress + " is currently shutting down! Operation: " + op);
    }

    private void ensureQuorumPresent(Operation op) {
        QuorumServiceImpl quorumService = this.operationService.nodeEngine.getQuorumService();
        quorumService.ensureQuorumPresent(op);
    }

    private boolean timeout(Operation op) {
        if (!this.operationService.isCallTimedOut(op)) {
            return false;
        }
        op.sendResponse(new CallTimeoutResponse(op.getCallId(), op.isUrgent()));
        return true;
    }

    private void afterRun(Operation op) {
        try {
            Notifier notifier;
            op.afterRun();
            if (op instanceof Notifier && (notifier = (Notifier)((Object)op)).shouldNotify()) {
                this.operationService.nodeEngine.getOperationParker().unpark(notifier);
            }
        }
        catch (Throwable e) {
            this.logOperationError(op, e);
        }
    }

    private void ensureNoPartitionProblems(Operation op) {
        int partitionId = op.getPartitionId();
        if (partitionId < 0) {
            return;
        }
        if (partitionId != this.getPartitionId()) {
            throw new IllegalStateException("wrong partition, expected: " + this.getPartitionId() + " but found:" + partitionId);
        }
        if (this.internalPartition == null) {
            this.internalPartition = this.nodeEngine.getPartitionService().getPartition(partitionId);
        }
        if (!this.isAllowedToRetryDuringMigration(op) && this.internalPartition.isMigrating()) {
            throw new PartitionMigratingException(this.thisAddress, partitionId, op.getClass().getName(), op.getServiceName());
        }
        PartitionReplica owner = this.internalPartition.getReplica(op.getReplicaIndex());
        if (op.validatesTarget() && (owner == null || !owner.isIdentical(this.node.getLocalMember()))) {
            MemberImpl target = owner != null ? this.node.getClusterService().getMember(owner.address(), owner.uuid()) : null;
            throw new WrongTargetException(this.node.getLocalMember(), target, partitionId, op.getReplicaIndex(), op.getClass().getName(), op.getServiceName());
        }
    }

    private boolean isAllowedToRetryDuringMigration(Operation op) {
        return op instanceof ReadonlyOperation && this.staleReadOnMigrationEnabled || Operations.isMigrationOperation(op);
    }

    private void handleOperationError(Operation operation, Throwable e) {
        if (e instanceof OutOfMemoryError) {
            OutOfMemoryErrorDispatcher.onOutOfMemory((OutOfMemoryError)e);
        }
        try {
            operation.onExecutionFailure(e);
        }
        catch (Throwable t) {
            this.logger.warning("While calling 'operation.onFailure(e)'... op: " + operation + ", error: " + e, t);
        }
        operation.logError(e);
        if (operation instanceof Backup) {
            this.failedBackupsCounter.inc();
            return;
        }
        this.sendResponseAfterOperationError(operation, e);
    }

    private void sendResponseAfterOperationError(Operation operation, Throwable e) {
        try {
            if (this.node.getState() != NodeState.SHUT_DOWN) {
                operation.sendResponse(e);
            } else if (operation.executedLocally()) {
                operation.sendResponse(new HazelcastInstanceNotActiveException());
            }
        }
        catch (Throwable t) {
            this.logger.warning("While sending op error... op: " + operation + ", error: " + e, t);
        }
    }

    private void logOperationError(Operation op, Throwable e) {
        if (e instanceof OutOfMemoryError) {
            OutOfMemoryErrorDispatcher.onOutOfMemory((OutOfMemoryError)e);
        }
        op.logError(e);
    }

    @Override
    public void run(Packet packet) throws Exception {
        boolean publishCurrentTask = this.publishCurrentTask();
        if (publishCurrentTask) {
            this.currentTask = packet;
        }
        Connection connection = packet.getConn();
        Address caller = connection.getEndPoint();
        try {
            Object object = this.nodeEngine.toObject(packet);
            Operation op = (Operation)object;
            op.setNodeEngine(this.nodeEngine);
            OperationAccessor.setCallerAddress(op, caller);
            OperationAccessor.setConnection(op, connection);
            this.setCallerUuidIfNotSet(caller, op);
            this.setOperationResponseHandler(op);
            if (!this.ensureValidMember(op)) {
                return;
            }
            if (publishCurrentTask) {
                this.currentTask = null;
            }
            this.run(op);
        }
        catch (Throwable throwable) {
            long callId = this.extractOperationCallId(packet);
            this.outboundResponseHandler.send(connection.getEndpointManager(), caller, new ErrorResponse(throwable, callId, packet.isUrgent()));
            this.logOperationDeserializationException(throwable, callId);
            throw ExceptionUtil.rethrow(throwable);
        }
        finally {
            if (publishCurrentTask) {
                this.currentTask = null;
            }
        }
    }

    private long extractOperationCallId(Data data) throws IOException {
        ObjectDataInput input = ((SerializationServiceV1)this.node.getSerializationService()).initDataSerializableInputAndSkipTheHeader(data);
        return input.readLong();
    }

    private void setOperationResponseHandler(Operation op) {
        OperationResponseHandler handler = this.outboundResponseHandler;
        if (op.getCallId() == 0L) {
            if (op.returnsResponse()) {
                throw new HazelcastException("Operation " + op + " wants to return a response, but doesn't have a call ID");
            }
            handler = OperationResponseHandlerFactory.createEmptyResponseHandler();
        }
        op.setOperationResponseHandler(handler);
    }

    private boolean ensureValidMember(Operation op) {
        if (this.node.clusterService.getMember(op.getCallerAddress()) != null || Operations.isJoinOperation(op) || Operations.isWanReplicationOperation(op)) {
            return true;
        }
        CallerNotMemberException error = new CallerNotMemberException(this.thisAddress, op.getCallerAddress(), op.getPartitionId(), op.getClass().getName(), op.getServiceName());
        this.handleOperationError(op, error);
        return false;
    }

    private void setCallerUuidIfNotSet(Address caller, Operation op) {
        if (op.getCallerUuid() != null) {
            return;
        }
        MemberImpl callerMember = this.node.clusterService.getMember(caller);
        if (callerMember != null) {
            op.setCallerUuid(callerMember.getUuid());
        }
    }

    private void logOperationDeserializationException(Throwable t, long callId) {
        boolean returnsResponse;
        boolean bl = returnsResponse = callId != 0L;
        if (t instanceof RetryableException) {
            Level level;
            Level level2 = level = returnsResponse ? Level.FINEST : Level.WARNING;
            if (this.logger.isLoggable(level)) {
                this.logger.log(level, t.getClass().getName() + ": " + t.getMessage());
            }
        } else if (t instanceof OutOfMemoryError) {
            try {
                this.logException(t.getMessage(), t, Level.SEVERE);
            }
            catch (Throwable ignored) {
                this.logger.severe(ignored.getMessage(), t);
            }
        } else if (t instanceof HazelcastSerializationException) {
            if (!this.node.getClusterService().isJoined()) {
                this.logException("A serialization exception occurred while joining a cluster, is this member compatible with other members of the cluster?", t, Level.SEVERE);
            } else {
                this.logException(t.getMessage(), t, this.nodeEngine.isRunning() ? Level.SEVERE : Level.FINEST);
            }
        } else {
            this.logException(t.getMessage(), t, this.nodeEngine.isRunning() ? Level.SEVERE : Level.FINEST);
        }
    }

    private void logException(String message, Throwable t, Level level) {
        if (this.logger.isLoggable(level)) {
            this.logger.log(level, message, t);
        }
    }
}

