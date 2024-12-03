/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorDataSerializerHook;
import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class ScheduledTaskResult
implements IdentifiedDataSerializable {
    private boolean done;
    private Object result;
    private Throwable exception;
    private boolean cancelled;

    ScheduledTaskResult() {
    }

    ScheduledTaskResult(boolean cancelled) {
        this.cancelled = cancelled;
    }

    ScheduledTaskResult(Throwable exception) {
        this.exception = exception;
    }

    ScheduledTaskResult(Object result) {
        this.result = result;
        this.done = true;
    }

    public Object getReturnValue() {
        return this.result;
    }

    public Throwable getException() {
        return this.exception;
    }

    boolean wasCancelled() {
        return this.cancelled;
    }

    void checkErroneousState() {
        if (this.wasCancelled()) {
            throw new CancellationException();
        }
        if (this.exception != null) {
            throw new ExecutionExceptionDecorator(new ExecutionException(this.exception));
        }
    }

    @Override
    public int getFactoryId() {
        return ScheduledExecutorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 24;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.result);
        out.writeBoolean(this.done);
        out.writeBoolean(this.cancelled);
        out.writeObject(this.exception);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.result = in.readObject();
        this.done = in.readBoolean();
        this.cancelled = in.readBoolean();
        this.exception = (Throwable)in.readObject();
    }

    public String toString() {
        return "ScheduledTaskResult{result=" + this.result + ", exception=" + this.exception + ", cancelled=" + this.cancelled + '}';
    }

    public static class ExecutionExceptionDecorator
    extends RuntimeException {
        public ExecutionExceptionDecorator(Throwable cause) {
            super(cause);
        }
    }
}

