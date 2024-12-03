/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.spi;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.CallStatus;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.OperationResponseHandler;
import com.hazelcast.spi.UrgentSystemOperation;
import com.hazelcast.spi.exception.RetryableException;
import com.hazelcast.spi.exception.SilentException;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.StringUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.logging.Level;

public abstract class Operation
implements DataSerializable {
    public static final int GENERIC_PARTITION_ID = -1;
    static final int BITMASK_VALIDATE_TARGET = 1;
    static final int BITMASK_CALLER_UUID_SET = 2;
    static final int BITMASK_REPLICA_INDEX_SET = 4;
    static final int BITMASK_WAIT_TIMEOUT_SET = 8;
    static final int BITMASK_PARTITION_ID_32_BIT = 16;
    static final int BITMASK_CALL_TIMEOUT_64_BIT = 32;
    static final int BITMASK_SERVICE_NAME_SET = 64;
    private static final AtomicLongFieldUpdater<Operation> CALL_ID = AtomicLongFieldUpdater.newUpdater(Operation.class, "callId");
    private volatile long callId;
    private String serviceName;
    private int partitionId = -1;
    private int replicaIndex;
    private short flags;
    private long invocationTime = -1L;
    private long callTimeout = Long.MAX_VALUE;
    private long waitTimeout = -1L;
    private String callerUuid;
    private transient NodeEngine nodeEngine;
    private transient Object service;
    private transient Address callerAddress;
    private transient Connection connection;
    private transient OperationResponseHandler responseHandler;

    protected Operation() {
        this.setFlag(true, 1);
        this.setFlag(true, 32);
    }

    public boolean executedLocally() {
        return this.nodeEngine.getThisAddress().equals(this.callerAddress);
    }

    public boolean isUrgent() {
        return this instanceof UrgentSystemOperation;
    }

    public void beforeRun() throws Exception {
    }

    public void run() throws Exception {
    }

    public CallStatus call() throws Exception {
        BlockingOperation blockingOperation;
        if (this instanceof BlockingOperation && (blockingOperation = (BlockingOperation)((Object)this)).shouldWait()) {
            return CallStatus.WAIT;
        }
        this.run();
        return this.returnsResponse() ? CallStatus.DONE_RESPONSE : CallStatus.DONE_VOID;
    }

    public void afterRun() throws Exception {
    }

    public boolean returnsResponse() {
        return true;
    }

    public Object getResponse() {
        return null;
    }

    String getRawServiceName() {
        return this.serviceName;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    @SuppressFBWarnings(value={"ES_COMPARING_PARAMETER_STRING_WITH_EQ"})
    public final Operation setServiceName(String serviceName) {
        if (serviceName == this.getServiceName()) {
            return this;
        }
        this.serviceName = serviceName;
        this.setFlag(serviceName != null, 64);
        return this;
    }

    public final int getPartitionId() {
        return this.partitionId;
    }

    public final Operation setPartitionId(int partitionId) {
        this.partitionId = partitionId;
        this.setFlag(partitionId > Short.MAX_VALUE, 16);
        return this;
    }

    public final int getReplicaIndex() {
        return this.replicaIndex;
    }

    public final Operation setReplicaIndex(int replicaIndex) {
        if (replicaIndex < 0 || replicaIndex >= 7) {
            throw new IllegalArgumentException("Replica index is out of range [0-6]: " + replicaIndex);
        }
        this.setFlag(replicaIndex != 0, 4);
        this.replicaIndex = replicaIndex;
        return this;
    }

    public final long getCallId() {
        return Math.abs(this.callId);
    }

    final boolean isActive() {
        return this.callId > 0L;
    }

    final boolean deactivate() {
        long c = this.callId;
        if (c <= 0L) {
            return false;
        }
        if (CALL_ID.compareAndSet(this, c, -c)) {
            return true;
        }
        if (this.callId > 0L) {
            throw new IllegalStateException("Operation concurrently re-activated while executing deactivate(). " + this);
        }
        return false;
    }

    final void setCallId(long newId) {
        if (newId <= 0L) {
            throw new IllegalArgumentException(String.format("Attempted to set non-positive call ID %d on %s", newId, this));
        }
        long c = this.callId;
        if (c > 0L) {
            throw new IllegalStateException(String.format("Attempt to overwrite the call ID of an active operation: current %d, requested %d. %s", this.callId, newId, this));
        }
        if (!CALL_ID.compareAndSet(this, c, newId)) {
            throw new IllegalStateException(String.format("Concurrent modification of call ID. Initially observed %d, then attempted to set %d, then observed %d. %s", c, newId, this.callId, this));
        }
        this.onSetCallId(newId);
    }

    protected void onSetCallId(long callId) {
    }

    public boolean validatesTarget() {
        return this.isFlagSet(1);
    }

    public final Operation setValidateTarget(boolean validateTarget) {
        this.setFlag(validateTarget, 1);
        return this;
    }

    public final NodeEngine getNodeEngine() {
        return this.nodeEngine;
    }

    public final Operation setNodeEngine(NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
        return this;
    }

    public final <T> T getService() {
        if (this.service == null) {
            String name = this.serviceName != null ? this.serviceName : this.getServiceName();
            this.service = this.nodeEngine.getService(name);
        }
        return (T)this.service;
    }

    public final Operation setService(Object service) {
        this.service = service;
        return this;
    }

    public final Address getCallerAddress() {
        return this.callerAddress;
    }

    final Operation setCallerAddress(Address callerAddress) {
        this.callerAddress = callerAddress;
        return this;
    }

    public final Connection getConnection() {
        return this.connection;
    }

    final Operation setConnection(Connection connection) {
        this.connection = connection;
        return this;
    }

    public final OperationResponseHandler getOperationResponseHandler() {
        return this.responseHandler;
    }

    public final Operation setOperationResponseHandler(OperationResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
        return this;
    }

    public final void sendResponse(Object value) {
        OperationResponseHandler responseHandler = this.getOperationResponseHandler();
        if (responseHandler == null) {
            if (value instanceof Throwable) {
                this.getLogger().warning("Missing responseHandler for " + this.toString(), (Throwable)value);
            } else {
                this.getLogger().warning("Missing responseHandler for " + this.toString() + " value[" + value + "]");
            }
        } else {
            responseHandler.sendResponse(this, value);
        }
    }

    public final long getInvocationTime() {
        return this.invocationTime;
    }

    final Operation setInvocationTime(long invocationTime) {
        this.invocationTime = invocationTime;
        return this;
    }

    public final long getCallTimeout() {
        return this.callTimeout;
    }

    final Operation setCallTimeout(long callTimeout) {
        this.callTimeout = callTimeout;
        this.setFlag(callTimeout > Integer.MAX_VALUE, 32);
        return this;
    }

    public final long getWaitTimeout() {
        return this.waitTimeout;
    }

    public final void setWaitTimeout(long timeout) {
        this.waitTimeout = timeout;
        this.setFlag(timeout != -1L, 8);
    }

    public ExceptionAction onInvocationException(Throwable throwable) {
        return throwable instanceof RetryableException ? ExceptionAction.RETRY_INVOCATION : ExceptionAction.THROW_EXCEPTION;
    }

    public String getCallerUuid() {
        return this.callerUuid;
    }

    public Operation setCallerUuid(String callerUuid) {
        this.callerUuid = callerUuid;
        this.setFlag(callerUuid != null, 2);
        return this;
    }

    protected final ILogger getLogger() {
        NodeEngine ne = this.nodeEngine;
        return ne != null ? ne.getLogger(this.getClass()) : Logger.getLogger(this.getClass());
    }

    void setFlag(boolean value, int bitmask) {
        this.flags = value ? (short)(this.flags | bitmask) : (short)(this.flags & ~bitmask);
    }

    boolean isFlagSet(int bitmask) {
        return (this.flags & bitmask) != 0;
    }

    short getFlags() {
        return this.flags;
    }

    public void onExecutionFailure(Throwable e) {
    }

    public void logError(Throwable e) {
        ILogger logger = this.getLogger();
        if (e instanceof SilentException) {
            logger.finest(e.getMessage(), e);
        } else if (e instanceof RetryableException) {
            Level level;
            Level level2 = level = this.returnsResponse() ? Level.FINEST : Level.WARNING;
            if (logger.isLoggable(level)) {
                logger.log(level, e.getClass().getName() + ": " + e.getMessage());
            }
        } else if (e instanceof OutOfMemoryError) {
            try {
                logger.severe(e.getMessage(), e);
            }
            catch (Throwable ignored) {
                EmptyStatement.ignore(ignored);
            }
        } else {
            Level level;
            Level level3 = level = this.nodeEngine != null && this.nodeEngine.isRunning() ? Level.SEVERE : Level.FINEST;
            if (logger.isLoggable(level)) {
                logger.log(level, e.getMessage(), e);
            }
        }
    }

    @Override
    public final void writeData(ObjectDataOutput out) throws IOException {
        String explicitServiceName;
        out.writeLong(this.callId);
        if (!this.isFlagSet(64) && this.requiresExplicitServiceName() && (explicitServiceName = this.getServiceName()) != null) {
            this.serviceName = explicitServiceName;
            this.setFlag(true, 64);
        }
        out.writeShort(this.flags);
        if (this.isFlagSet(64)) {
            out.writeUTF(this.serviceName);
        }
        if (this.isFlagSet(16)) {
            out.writeInt(this.partitionId);
        } else {
            out.writeShort(this.partitionId);
        }
        if (this.isFlagSet(4)) {
            out.writeByte(this.replicaIndex);
        }
        out.writeLong(this.invocationTime);
        if (this.isFlagSet(32)) {
            out.writeLong(this.callTimeout);
        } else {
            out.writeInt((int)this.callTimeout);
        }
        if (this.isFlagSet(8)) {
            out.writeLong(this.waitTimeout);
        }
        if (this.isFlagSet(2)) {
            out.writeUTF(this.callerUuid);
        }
        this.writeInternal(out);
    }

    @Override
    public final void readData(ObjectDataInput in) throws IOException {
        this.callId = in.readLong();
        this.flags = in.readShort();
        if (this.isFlagSet(64)) {
            this.serviceName = in.readUTF();
        }
        this.partitionId = this.isFlagSet(16) ? in.readInt() : (int)in.readShort();
        if (this.isFlagSet(4)) {
            this.replicaIndex = in.readByte();
        }
        this.invocationTime = in.readLong();
        this.callTimeout = this.isFlagSet(32) ? in.readLong() : (long)in.readInt();
        if (this.isFlagSet(8)) {
            this.waitTimeout = in.readLong();
        }
        if (this.isFlagSet(2)) {
            this.callerUuid = in.readUTF();
        }
        this.readInternal(in);
    }

    protected boolean requiresExplicitServiceName() {
        return false;
    }

    protected void writeInternal(ObjectDataOutput out) throws IOException {
    }

    protected void readInternal(ObjectDataInput in) throws IOException {
    }

    protected void toString(StringBuilder sb) {
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(this.getClass().getName()).append('{');
        sb.append("serviceName='").append(this.getServiceName()).append('\'');
        sb.append(", identityHash=").append(System.identityHashCode(this));
        sb.append(", partitionId=").append(this.partitionId);
        sb.append(", replicaIndex=").append(this.replicaIndex);
        sb.append(", callId=").append(this.callId);
        sb.append(", invocationTime=").append(this.invocationTime).append(" (").append(StringUtil.timeToString(this.invocationTime)).append(")");
        sb.append(", waitTimeout=").append(this.waitTimeout);
        sb.append(", callTimeout=").append(this.callTimeout);
        this.toString(sb);
        sb.append('}');
        return sb.toString();
    }
}

