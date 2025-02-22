/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice.impl;

import com.hazelcast.core.IndeterminateOperationState;
import com.hazelcast.core.IndeterminateOperationStateException;
import com.hazelcast.core.OperationTimeoutException;
import com.hazelcast.nio.Packet;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.impl.AbstractInvocationFuture;
import com.hazelcast.spi.impl.operationservice.impl.Invocation;
import com.hazelcast.spi.impl.operationservice.impl.InvocationConstant;
import com.hazelcast.spi.impl.operationservice.impl.responses.NormalResponse;
import com.hazelcast.util.Clock;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.StringUtil;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

final class InvocationFuture<E>
extends AbstractInvocationFuture<E> {
    volatile boolean interrupted;
    final Invocation invocation;
    private final boolean deserialize;

    InvocationFuture(Invocation invocation, boolean deserialize) {
        super(invocation.context.asyncExecutor, invocation.context.logger);
        this.invocation = invocation;
        this.deserialize = deserialize;
    }

    @Override
    protected String invocationToString() {
        return this.invocation.toString();
    }

    @Override
    protected TimeoutException newTimeoutException(long timeout, TimeUnit unit) {
        return new TimeoutException(String.format("%s failed to complete within %d %s. %s", new Object[]{this.invocation.op.getClass().getSimpleName(), timeout, unit, this.invocation}));
    }

    @Override
    protected void onInterruptDetected() {
        this.interrupted = true;
    }

    @Override
    protected E resolveAndThrowIfException(Object unresolved) throws ExecutionException, InterruptedException {
        Object value = this.resolve(unresolved);
        if (value == null || !(value instanceof Throwable)) {
            return (E)value;
        }
        if (value instanceof CancellationException) {
            throw (CancellationException)value;
        }
        if (value instanceof ExecutionException) {
            throw (ExecutionException)value;
        }
        if (value instanceof InterruptedException) {
            throw (InterruptedException)value;
        }
        if (value instanceof Error) {
            throw (Error)value;
        }
        throw new ExecutionException((Throwable)value);
    }

    @Override
    protected Object resolve(Object unresolved) {
        if (unresolved == null) {
            return null;
        }
        if (unresolved == InvocationConstant.INTERRUPTED) {
            return new InterruptedException(this.invocation.op.getClass().getSimpleName() + " was interrupted. " + this.invocation);
        }
        if (unresolved == InvocationConstant.CALL_TIMEOUT) {
            return this.newOperationTimeoutException(false);
        }
        if (unresolved == InvocationConstant.HEARTBEAT_TIMEOUT) {
            return this.newOperationTimeoutException(true);
        }
        if (unresolved.getClass() == Packet.class) {
            NormalResponse response = (NormalResponse)this.invocation.context.serializationService.toObject(unresolved);
            unresolved = response.getValue();
        }
        Object value = unresolved;
        if (this.deserialize && value instanceof Data && (value = this.invocation.context.serializationService.toObject(value)) == null) {
            return null;
        }
        if (this.invocation.shouldFailOnIndeterminateOperationState() && value instanceof IndeterminateOperationState) {
            value = new IndeterminateOperationStateException("indeterminate operation state", (Throwable)value);
        }
        if (value instanceof Throwable) {
            Throwable throwable = (Throwable)value;
            ExceptionUtil.fixAsyncStackTrace((Throwable)value, Thread.currentThread().getStackTrace());
            return throwable;
        }
        return value;
    }

    private Object newOperationTimeoutException(boolean heartbeatTimeout) {
        StringBuilder sb = new StringBuilder();
        if (heartbeatTimeout) {
            sb.append(this.invocation.op.getClass().getSimpleName()).append(" invocation failed to complete due to operation-heartbeat-timeout. ");
            sb.append("Current time: ").append(StringUtil.timeToString(Clock.currentTimeMillis())).append(". ");
            sb.append("Start time: ").append(StringUtil.timeToString(this.invocation.firstInvocationTimeMillis)).append(". ");
            sb.append("Total elapsed time: ").append(Clock.currentTimeMillis() - this.invocation.firstInvocationTimeMillis).append(" ms. ");
            long lastHeartbeatMillis = this.invocation.lastHeartbeatMillis;
            sb.append("Last operation heartbeat: ");
            InvocationFuture.appendHeartbeat(sb, lastHeartbeatMillis);
            long lastHeartbeatFromMemberMillis = this.invocation.context.invocationMonitor.getLastMemberHeartbeatMillis(this.invocation.getTargetAddress());
            sb.append("Last operation heartbeat from member: ");
            InvocationFuture.appendHeartbeat(sb, lastHeartbeatFromMemberMillis);
        } else {
            sb.append(this.invocation.op.getClass().getSimpleName()).append(" got rejected before execution due to not starting within the operation-call-timeout of: ").append(this.invocation.callTimeoutMillis).append(" ms. ");
            sb.append("Current time: ").append(StringUtil.timeToString(Clock.currentTimeMillis())).append(". ");
            sb.append("Start time: ").append(StringUtil.timeToString(this.invocation.firstInvocationTimeMillis)).append(". ");
            sb.append("Total elapsed time: ").append(Clock.currentTimeMillis() - this.invocation.firstInvocationTimeMillis).append(" ms. ");
        }
        sb.append(this.invocation);
        String msg = sb.toString();
        return new ExecutionException(msg, new OperationTimeoutException(msg));
    }

    private static void appendHeartbeat(StringBuilder sb, long lastHeartbeatMillis) {
        if (lastHeartbeatMillis == 0L) {
            sb.append("never. ");
        } else {
            sb.append(StringUtil.timeToString(lastHeartbeatMillis)).append(". ");
        }
    }
}

