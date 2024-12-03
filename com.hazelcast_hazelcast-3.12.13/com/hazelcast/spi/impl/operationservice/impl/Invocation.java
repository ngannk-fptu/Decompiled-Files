/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.spi.impl.operationservice.impl;

import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.IndeterminateOperationStateException;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.instance.Node;
import com.hazelcast.instance.NodeState;
import com.hazelcast.internal.cluster.ClusterClock;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.util.counters.MwCounter;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.EndpointManager;
import com.hazelcast.nio.NetworkingService;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationAccessor;
import com.hazelcast.spi.OperationResponseHandler;
import com.hazelcast.spi.exception.ResponseAlreadySentException;
import com.hazelcast.spi.exception.RetryableException;
import com.hazelcast.spi.exception.RetryableIOException;
import com.hazelcast.spi.exception.TargetNotMemberException;
import com.hazelcast.spi.exception.WrongTargetException;
import com.hazelcast.spi.impl.AllowedDuringPassiveState;
import com.hazelcast.spi.impl.executionservice.InternalExecutionService;
import com.hazelcast.spi.impl.operationexecutor.OperationExecutor;
import com.hazelcast.spi.impl.operationservice.TargetAware;
import com.hazelcast.spi.impl.operationservice.impl.InvocationConstant;
import com.hazelcast.spi.impl.operationservice.impl.InvocationFuture;
import com.hazelcast.spi.impl.operationservice.impl.InvocationMonitor;
import com.hazelcast.spi.impl.operationservice.impl.InvocationRegistry;
import com.hazelcast.spi.impl.operationservice.impl.OperationServiceImpl;
import com.hazelcast.spi.impl.operationservice.impl.OutboundOperationHandler;
import com.hazelcast.spi.impl.operationservice.impl.responses.CallTimeoutResponse;
import com.hazelcast.spi.impl.operationservice.impl.responses.ErrorResponse;
import com.hazelcast.spi.impl.operationservice.impl.responses.NormalResponse;
import com.hazelcast.spi.impl.operationutil.Operations;
import com.hazelcast.util.Clock;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.StringUtil;
import com.hazelcast.util.executor.ManagedExecutorService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.logging.Level;

public abstract class Invocation<T>
implements OperationResponseHandler {
    private static final AtomicReferenceFieldUpdater<Invocation, Boolean> RESPONSE_RECEIVED = AtomicReferenceFieldUpdater.newUpdater(Invocation.class, Boolean.class, "responseReceived");
    private static final AtomicIntegerFieldUpdater<Invocation> BACKUP_ACKS_RECEIVED = AtomicIntegerFieldUpdater.newUpdater(Invocation.class, "backupsAcksReceived");
    private static final long MIN_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(10L);
    private static final int MAX_FAST_INVOCATION_COUNT = 5;
    private static final int LOG_MAX_INVOCATION_COUNT = 99;
    private static final int LOG_INVOCATION_COUNT_MOD = 10;
    public final Operation op;
    public final long firstInvocationTimeMillis = Clock.currentTimeMillis();
    volatile Object pendingResponse = InvocationConstant.VOID;
    volatile long pendingResponseReceivedMillis = -1L;
    volatile int backupsAcksExpected;
    volatile int backupsAcksReceived;
    volatile Boolean responseReceived = Boolean.FALSE;
    volatile long lastHeartbeatMillis;
    final Context context;
    final InvocationFuture future;
    final long callTimeoutMillis;
    private volatile int invokeCount;
    private Address targetAddress;
    private Member targetMember;
    private Connection connection;
    private int memberListVersion;
    private final EndpointManager endpointManager;
    private final int tryCount;
    private final long tryPauseMillis;
    private final Runnable taskDoneCallback;

    Invocation(Context context, Operation op, Runnable taskDoneCallback, int tryCount, long tryPauseMillis, long callTimeoutMillis, boolean deserialize, EndpointManager endpointManager) {
        this.context = context;
        this.op = op;
        this.taskDoneCallback = taskDoneCallback;
        this.tryCount = tryCount;
        this.tryPauseMillis = tryPauseMillis;
        this.callTimeoutMillis = this.getCallTimeoutMillis(callTimeoutMillis);
        this.future = new InvocationFuture(this, deserialize);
        this.endpointManager = this.getEndpointManager(endpointManager);
    }

    public void sendResponse(Operation op, Object response) {
        if (!RESPONSE_RECEIVED.compareAndSet(this, Boolean.FALSE, Boolean.TRUE)) {
            throw new ResponseAlreadySentException("NormalResponse already responseReceived for callback: " + this + ", current-response: " + response);
        }
        if (response instanceof CallTimeoutResponse) {
            this.notifyCallTimeout();
        } else if (response instanceof ErrorResponse || response instanceof Throwable) {
            this.notifyError(response);
        } else if (response instanceof NormalResponse) {
            NormalResponse normalResponse = (NormalResponse)response;
            this.notifyNormalResponse(normalResponse.getValue(), normalResponse.getBackupAcks());
        } else {
            this.complete(response);
        }
    }

    public final InvocationFuture invoke() {
        this.invoke0(false);
        return this.future;
    }

    public final InvocationFuture invokeAsync() {
        this.invoke0(true);
        return this.future;
    }

    protected boolean shouldFailOnIndeterminateOperationState() {
        return false;
    }

    abstract ExceptionAction onException(Throwable var1);

    boolean isActive() {
        return OperationAccessor.hasActiveInvocation(this.op);
    }

    boolean isRetryCandidate() {
        return this.op.getCallId() != 0L;
    }

    final void initInvocationTarget() throws Exception {
        Member previousTargetMember = this.targetMember;
        T target = this.getInvocationTarget();
        if (target == null) {
            throw this.newTargetNullException();
        }
        this.targetMember = this.toTargetMember(target);
        this.targetAddress = this.targetMember != null ? this.targetMember.getAddress() : this.toTargetAddress(target);
        this.memberListVersion = this.context.clusterService.getMemberListVersion();
        if (this.targetMember == null) {
            if (previousTargetMember != null) {
                throw new MemberLeftException(previousTargetMember);
            }
            if (!Operations.isJoinOperation(this.op) && !Operations.isWanReplicationOperation(this.op)) {
                throw new TargetNotMemberException(target, this.op.getPartitionId(), this.op.getClass().getName(), this.op.getServiceName());
            }
        }
        if (this.op instanceof TargetAware) {
            ((TargetAware)((Object)this.op)).setTarget(this.targetAddress);
        }
    }

    abstract T getInvocationTarget();

    abstract Address toTargetAddress(T var1);

    abstract Member toTargetMember(T var1);

    Exception newTargetNullException() {
        return new WrongTargetException(this.context.clusterService.getLocalMember(), null, this.op.getPartitionId(), this.op.getReplicaIndex(), this.op.getClass().getName(), this.op.getServiceName());
    }

    void notifyError(Object error) {
        assert (error != null);
        Throwable cause = error instanceof Throwable ? (Throwable)error : ((ErrorResponse)error).getCause();
        switch (this.onException(cause)) {
            case THROW_EXCEPTION: {
                this.notifyNormalResponse(cause, 0);
                break;
            }
            case RETRY_INVOCATION: {
                if (this.invokeCount < this.tryCount) {
                    this.handleRetry(cause);
                    break;
                }
                this.notifyNormalResponse(cause, 0);
                break;
            }
            default: {
                throw new IllegalStateException("Unhandled ExceptionAction");
            }
        }
    }

    void notifyNormalResponse(Object value, int expectedBackups) {
        if (expectedBackups > this.backupsAcksReceived) {
            this.pendingResponseReceivedMillis = Clock.currentTimeMillis();
            this.backupsAcksExpected = expectedBackups;
            this.pendingResponse = value;
            if (this.backupsAcksReceived != expectedBackups) {
                return;
            }
        }
        this.complete(value);
    }

    @SuppressFBWarnings(value={"VO_VOLATILE_INCREMENT"}, justification="We have the guarantee that only a single thread at any given time can change the volatile field")
    void notifyCallTimeout() {
        long newWaitTimeout;
        long oldWaitTimeout;
        if (!(this.op instanceof BlockingOperation)) {
            this.complete(InvocationConstant.CALL_TIMEOUT);
            return;
        }
        if (this.context.logger.isFinestEnabled()) {
            this.context.logger.finest("Call timed-out either in operation queue or during wait-notify phase, retrying call: " + this);
        }
        if ((oldWaitTimeout = this.op.getWaitTimeout()) < 0L) {
            newWaitTimeout = oldWaitTimeout;
        } else {
            long elapsedTime = Math.max(0L, this.context.clusterClock.getClusterTime() - this.op.getInvocationTime());
            newWaitTimeout = Math.max(0L, oldWaitTimeout - elapsedTime);
        }
        this.op.setWaitTimeout(newWaitTimeout);
        --this.invokeCount;
        this.handleRetry("invocation timeout");
    }

    void notifyBackupComplete() {
        int newBackupAcksCompleted = BACKUP_ACKS_RECEIVED.incrementAndGet(this);
        Object pendingResponse = this.pendingResponse;
        if (pendingResponse == InvocationConstant.VOID) {
            return;
        }
        int backupAcksExpected = this.backupsAcksExpected;
        if (backupAcksExpected < newBackupAcksCompleted) {
            return;
        }
        if (backupAcksExpected != newBackupAcksCompleted) {
            return;
        }
        this.complete(pendingResponse);
    }

    boolean detectAndHandleTimeout(long heartbeatTimeoutMillis) {
        if (this.skipTimeoutDetection()) {
            return false;
        }
        HeartbeatTimeout heartbeatTimeout = this.detectTimeout(heartbeatTimeoutMillis);
        if (heartbeatTimeout == HeartbeatTimeout.TIMEOUT) {
            this.complete(InvocationConstant.HEARTBEAT_TIMEOUT);
            return true;
        }
        return false;
    }

    boolean skipTimeoutDetection() {
        return this.isLocal() && !(this.op instanceof BackupAwareOperation);
    }

    HeartbeatTimeout detectTimeout(long heartbeatTimeoutMillis) {
        long heartbeatExpirationTimeMillis;
        if (this.pendingResponse != InvocationConstant.VOID) {
            return HeartbeatTimeout.NO_TIMEOUT__RESPONSE_AVAILABLE;
        }
        long callTimeoutMillis = this.op.getCallTimeout();
        if (callTimeoutMillis <= 0L || callTimeoutMillis == Long.MAX_VALUE) {
            return HeartbeatTimeout.NO_TIMEOUT__CALL_TIMEOUT_DISABLED;
        }
        long deadlineMillis = this.op.getInvocationTime() + callTimeoutMillis;
        if (deadlineMillis > this.context.clusterClock.getClusterTime()) {
            return HeartbeatTimeout.NO_TIMEOUT__CALL_TIMEOUT_NOT_EXPIRED;
        }
        long lastHeartbeatMillis = this.lastHeartbeatMillis;
        long l = heartbeatExpirationTimeMillis = lastHeartbeatMillis == 0L ? this.op.getInvocationTime() + callTimeoutMillis + heartbeatTimeoutMillis : lastHeartbeatMillis + heartbeatTimeoutMillis;
        if (heartbeatExpirationTimeMillis > Clock.currentTimeMillis()) {
            return HeartbeatTimeout.NO_TIMEOUT__HEARTBEAT_TIMEOUT_NOT_EXPIRED;
        }
        return HeartbeatTimeout.TIMEOUT;
    }

    boolean detectAndHandleBackupTimeout(long timeoutMillis) {
        boolean targetDead;
        boolean responseReceived;
        boolean backupsCompleted = this.backupsAcksExpected == this.backupsAcksReceived;
        long responseReceivedMillis = this.pendingResponseReceivedMillis;
        long expirationTime = responseReceivedMillis + timeoutMillis;
        boolean timeout = expirationTime > 0L && expirationTime < Clock.currentTimeMillis();
        boolean bl = responseReceived = this.pendingResponse != InvocationConstant.VOID;
        if (backupsCompleted || !responseReceived || !timeout) {
            return false;
        }
        if (this.shouldFailOnIndeterminateOperationState()) {
            this.complete(new IndeterminateOperationStateException(this + " failed because backup acks missed."));
            return true;
        }
        boolean bl2 = targetDead = this.context.clusterService.getMember(this.targetAddress) == null;
        if (targetDead) {
            this.resetAndReInvoke();
            return false;
        }
        this.complete(this.pendingResponse);
        return true;
    }

    private boolean engineActive() {
        boolean allowed;
        NodeState state = this.context.node.getState();
        if (state == NodeState.ACTIVE) {
            return true;
        }
        boolean bl = allowed = state == NodeState.PASSIVE && this.op instanceof AllowedDuringPassiveState;
        if (!allowed) {
            this.notifyError(new HazelcastInstanceNotActiveException("State: " + (Object)((Object)state) + " Operation: " + this.op.getClass()));
        }
        return allowed;
    }

    private void invoke0(boolean isAsync) {
        if (this.invokeCount > 0) {
            throw new IllegalStateException("This invocation is already in progress");
        }
        if (this.isActive()) {
            throw new IllegalStateException("Attempt to reuse the same operation in multiple invocations. Operation is " + this.op);
        }
        try {
            OperationAccessor.setCallTimeout(this.op, this.callTimeoutMillis);
            OperationAccessor.setCallerAddress(this.op, this.context.thisAddress);
            this.op.setNodeEngine(this.context.nodeEngine);
            boolean isAllowed = this.context.operationExecutor.isInvocationAllowed(this.op, isAsync);
            if (!isAllowed && !Operations.isMigrationOperation(this.op)) {
                throw new IllegalThreadStateException(Thread.currentThread() + " cannot make remote call: " + this.op);
            }
            this.doInvoke(isAsync);
        }
        catch (Exception e) {
            this.handleInvocationException(e);
        }
    }

    @SuppressFBWarnings(value={"VO_VOLATILE_INCREMENT"}, justification="We have the guarantee that only a single thread at any given time can change the volatile field")
    private void doInvoke(boolean isAsync) {
        if (!this.engineActive()) {
            return;
        }
        ++this.invokeCount;
        OperationAccessor.setInvocationTime(this.op, this.context.clusterClock.getClusterTime());
        Exception initializationFailure = null;
        try {
            this.initInvocationTarget();
        }
        catch (Exception e) {
            initializationFailure = e;
        }
        if (!this.context.invocationRegistry.register(this)) {
            return;
        }
        if (initializationFailure != null) {
            this.notifyError(initializationFailure);
            return;
        }
        if (this.isLocal()) {
            this.doInvokeLocal(isAsync);
        } else {
            this.doInvokeRemote();
        }
    }

    private boolean isLocal() {
        return this.context.thisAddress.equals(this.targetAddress);
    }

    private void doInvokeLocal(boolean isAsync) {
        if (this.op.getCallerUuid() == null) {
            this.op.setCallerUuid(this.context.node.getThisUuid());
        }
        this.responseReceived = Boolean.FALSE;
        this.op.setOperationResponseHandler(this);
        if (isAsync) {
            this.context.operationExecutor.execute(this.op);
        } else {
            this.context.operationExecutor.runOrExecute(this.op);
        }
    }

    private void doInvokeRemote() {
        assert (this.endpointManager != null) : "Endpoint manager was null";
        Object connection = this.endpointManager.getOrConnect(this.targetAddress);
        this.connection = connection;
        if (!this.context.outboundOperationHandler.send(this.op, (Connection)connection)) {
            this.notifyError(new RetryableIOException("Packet not sent to -> " + this.targetAddress + " over " + connection));
        }
    }

    private EndpointManager getEndpointManager(EndpointManager endpointManager) {
        return endpointManager != null ? endpointManager : this.context.defaultEndpointManager;
    }

    private long getCallTimeoutMillis(long callTimeoutMillis) {
        if (callTimeoutMillis > 0L) {
            return callTimeoutMillis;
        }
        long defaultCallTimeoutMillis = this.context.defaultCallTimeoutMillis;
        if (!(this.op instanceof BlockingOperation)) {
            return defaultCallTimeoutMillis;
        }
        long waitTimeoutMillis = this.op.getWaitTimeout();
        if (waitTimeoutMillis > 0L && waitTimeoutMillis < Long.MAX_VALUE) {
            long max = Math.max(waitTimeoutMillis, MIN_TIMEOUT_MILLIS);
            return Math.min(max, defaultCallTimeoutMillis);
        }
        return defaultCallTimeoutMillis;
    }

    private void handleInvocationException(Exception e) {
        if (!(e instanceof RetryableException)) {
            throw ExceptionUtil.rethrow(e);
        }
        this.notifyError(e);
    }

    private void complete(Object value) {
        this.future.complete(value);
        if (this.context.invocationRegistry.deregister(this) && this.taskDoneCallback != null) {
            this.context.asyncExecutor.execute(this.taskDoneCallback);
        }
    }

    private void handleRetry(Object cause) {
        this.context.retryCount.inc();
        if (this.invokeCount % 10 == 0) {
            Level level;
            Level level2 = level = this.invokeCount > 99 ? Level.WARNING : Level.FINEST;
            if (this.context.logger.isLoggable(level)) {
                this.context.logger.log(level, "Retrying invocation: " + this.toString() + ", Reason: " + cause);
            }
        }
        if (this.future.interrupted) {
            this.complete(InvocationConstant.INTERRUPTED);
        } else {
            try {
                InvocationRetryTask retryTask = new InvocationRetryTask();
                if (this.invokeCount < 5) {
                    this.context.invocationMonitor.execute(retryTask);
                } else {
                    long delayMillis = Math.min((long)(1 << this.invokeCount - 5), this.tryPauseMillis);
                    this.context.invocationMonitor.schedule(retryTask, delayMillis);
                }
            }
            catch (RejectedExecutionException e) {
                this.completeWhenRetryRejected(e);
            }
        }
    }

    private void completeWhenRetryRejected(RejectedExecutionException e) {
        if (this.context.logger.isFinestEnabled()) {
            this.context.logger.finest(e);
        }
        this.complete(new HazelcastInstanceNotActiveException(e.getMessage()));
    }

    private void resetAndReInvoke() {
        if (!this.context.invocationRegistry.deregister(this)) {
            return;
        }
        this.invokeCount = 0;
        this.pendingResponse = InvocationConstant.VOID;
        this.pendingResponseReceivedMillis = -1L;
        this.backupsAcksExpected = 0;
        this.backupsAcksReceived = 0;
        this.lastHeartbeatMillis = 0L;
        this.doInvoke(false);
    }

    Address getTargetAddress() {
        return this.targetAddress;
    }

    Member getTargetMember() {
        return this.targetMember;
    }

    int getMemberListVersion() {
        return this.memberListVersion;
    }

    public String toString() {
        return "Invocation{op=" + this.op + ", tryCount=" + this.tryCount + ", tryPauseMillis=" + this.tryPauseMillis + ", invokeCount=" + this.invokeCount + ", callTimeoutMillis=" + this.callTimeoutMillis + ", firstInvocationTimeMs=" + this.firstInvocationTimeMillis + ", firstInvocationTime='" + StringUtil.timeToString(this.firstInvocationTimeMillis) + '\'' + ", lastHeartbeatMillis=" + this.lastHeartbeatMillis + ", lastHeartbeatTime='" + StringUtil.timeToString(this.lastHeartbeatMillis) + '\'' + ", target=" + this.targetAddress + ", pendingResponse={" + this.pendingResponse + '}' + ", backupsAcksExpected=" + this.backupsAcksExpected + ", backupsAcksReceived=" + this.backupsAcksReceived + ", connection=" + this.connection + '}';
    }

    static class Context {
        final ManagedExecutorService asyncExecutor;
        final ClusterClock clusterClock;
        final ClusterService clusterService;
        final NetworkingService networkingService;
        final InternalExecutionService executionService;
        final long defaultCallTimeoutMillis;
        final InvocationRegistry invocationRegistry;
        final InvocationMonitor invocationMonitor;
        final ILogger logger;
        final Node node;
        final NodeEngine nodeEngine;
        final InternalPartitionService partitionService;
        final OperationServiceImpl operationService;
        final OperationExecutor operationExecutor;
        final MwCounter retryCount;
        final InternalSerializationService serializationService;
        final Address thisAddress;
        final OutboundOperationHandler outboundOperationHandler;
        final EndpointManager defaultEndpointManager;

        Context(ManagedExecutorService asyncExecutor, ClusterClock clusterClock, ClusterService clusterService, NetworkingService networkingService, InternalExecutionService executionService, long defaultCallTimeoutMillis, InvocationRegistry invocationRegistry, InvocationMonitor invocationMonitor, ILogger logger, Node node, NodeEngine nodeEngine, InternalPartitionService partitionService, OperationServiceImpl operationService, OperationExecutor operationExecutor, MwCounter retryCount, InternalSerializationService serializationService, Address thisAddress, OutboundOperationHandler outboundOperationHandler, EndpointManager endpointManager) {
            this.asyncExecutor = asyncExecutor;
            this.clusterClock = clusterClock;
            this.clusterService = clusterService;
            this.networkingService = networkingService;
            this.executionService = executionService;
            this.defaultCallTimeoutMillis = defaultCallTimeoutMillis;
            this.invocationRegistry = invocationRegistry;
            this.invocationMonitor = invocationMonitor;
            this.logger = logger;
            this.node = node;
            this.nodeEngine = nodeEngine;
            this.partitionService = partitionService;
            this.operationService = operationService;
            this.operationExecutor = operationExecutor;
            this.retryCount = retryCount;
            this.serializationService = serializationService;
            this.thisAddress = thisAddress;
            this.outboundOperationHandler = outboundOperationHandler;
            this.defaultEndpointManager = endpointManager;
        }
    }

    static enum HeartbeatTimeout {
        NO_TIMEOUT__CALL_TIMEOUT_DISABLED,
        NO_TIMEOUT__RESPONSE_AVAILABLE,
        NO_TIMEOUT__HEARTBEAT_TIMEOUT_NOT_EXPIRED,
        NO_TIMEOUT__CALL_TIMEOUT_NOT_EXPIRED,
        TIMEOUT;

    }

    private class InvocationRetryTask
    implements Runnable {
        private InvocationRetryTask() {
        }

        @Override
        public void run() {
            if (!(Invocation.this.context.clusterService.isJoined() || Operations.isJoinOperation(Invocation.this.op) || Invocation.this.op instanceof AllowedDuringPassiveState)) {
                if (!Invocation.this.engineActive()) {
                    Invocation.this.context.invocationRegistry.deregister(Invocation.this);
                    return;
                }
                if (Invocation.this.context.logger.isFinestEnabled()) {
                    Invocation.this.context.logger.finest("Node is not joined. Re-scheduling " + this + " to be executed in " + Invocation.this.tryPauseMillis + " ms.");
                }
                try {
                    Invocation.this.context.invocationMonitor.schedule(new InvocationRetryTask(), Invocation.this.tryPauseMillis);
                }
                catch (RejectedExecutionException e) {
                    Invocation.this.completeWhenRetryRejected(e);
                }
                return;
            }
            if (!Invocation.this.context.invocationRegistry.deregister(Invocation.this)) {
                return;
            }
            Invocation.this.lastHeartbeatMillis = 0L;
            Invocation.this.doInvoke(true);
        }
    }
}

