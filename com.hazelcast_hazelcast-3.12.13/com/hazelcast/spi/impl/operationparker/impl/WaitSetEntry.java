/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationparker.impl;

import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.AbstractLocalOperation;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationResponseHandler;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.exception.RetryableException;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.impl.operationservice.impl.responses.CallTimeoutResponse;
import com.hazelcast.util.Clock;
import com.hazelcast.util.EmptyStatement;
import java.util.Queue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

class WaitSetEntry
extends AbstractLocalOperation
implements Delayed,
PartitionAwareOperation,
IdentifiedDataSerializable {
    final Queue<WaitSetEntry> queue;
    final Operation op;
    final BlockingOperation blockingOperation;
    final long expirationTimeMs;
    volatile boolean valid = true;
    volatile Object cancelResponse;

    WaitSetEntry(Queue<WaitSetEntry> queue, BlockingOperation blockingOperation) {
        this.op = (Operation)((Object)blockingOperation);
        this.blockingOperation = blockingOperation;
        this.queue = queue;
        this.expirationTimeMs = this.getExpirationTimeMs(blockingOperation);
        this.setPartitionId(this.op.getPartitionId());
    }

    private long getExpirationTimeMs(BlockingOperation blockingOperation) {
        long waitTimeout = blockingOperation.getWaitTimeout();
        if (waitTimeout < 0L) {
            return -1L;
        }
        long expirationTime = Clock.currentTimeMillis() + waitTimeout;
        if (expirationTime < 0L) {
            return -1L;
        }
        return expirationTime;
    }

    public Operation getOperation() {
        return this.op;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return this.valid;
    }

    public boolean needsInvalidation() {
        return this.isExpired() || this.isCancelled() || this.isCallTimedOut();
    }

    public boolean isExpired() {
        return this.expirationTimeMs > 0L && Clock.currentTimeMillis() >= this.expirationTimeMs;
    }

    public boolean isCancelled() {
        return this.cancelResponse != null;
    }

    public boolean isCallTimedOut() {
        NodeEngineImpl nodeEngine = (NodeEngineImpl)this.getNodeEngine();
        InternalOperationService operationService = nodeEngine.getOperationService();
        if (operationService.isCallTimedOut(this.op)) {
            this.cancel(new CallTimeoutResponse(this.op.getCallId(), this.op.isUrgent()));
            return true;
        }
        return false;
    }

    public boolean shouldWait() {
        return this.blockingOperation.shouldWait();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.expirationTimeMs - Clock.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed other) {
        if (other == this) {
            return 0;
        }
        long d = this.getDelay(TimeUnit.NANOSECONDS) - other.getDelay(TimeUnit.NANOSECONDS);
        return d == 0L ? 0 : (d < 0L ? -1 : 1);
    }

    @Override
    public void run() throws Exception {
        if (!this.valid) {
            return;
        }
        boolean expired = this.isExpired();
        boolean cancelled = this.isCancelled();
        if (!expired && !cancelled) {
            return;
        }
        if (!this.queue.remove(this)) {
            return;
        }
        this.valid = false;
        if (expired) {
            this.onExpire();
        } else {
            this.onCancel();
        }
    }

    public int hashCode() {
        assert (false) : "hashCode not designed";
        return 42;
    }

    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public void logError(Throwable e) {
        ILogger logger = this.getLogger();
        if (e instanceof RetryableException) {
            logger.warning("Op: " + this.op + ", " + e.getClass().getName() + ": " + e.getMessage());
        } else if (e instanceof OutOfMemoryError) {
            try {
                logger.severe(e.getMessage(), e);
            }
            catch (Throwable ignored) {
                EmptyStatement.ignore(ignored);
            }
        } else {
            logger.severe("Op: " + this.op + ", Error: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean returnsResponse() {
        return false;
    }

    @Override
    public String getServiceName() {
        return this.op.getServiceName();
    }

    public void onExpire() {
        this.blockingOperation.onWaitExpire();
    }

    public void onCancel() {
        OperationResponseHandler responseHandler = this.op.getOperationResponseHandler();
        responseHandler.sendResponse(this.op, this.cancelResponse);
    }

    public void cancel(Object error) {
        this.cancelResponse = error;
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", op=").append(this.op);
        sb.append(", expirationTimeMs=").append(this.expirationTimeMs);
        sb.append(", valid=").append(this.valid);
    }
}

