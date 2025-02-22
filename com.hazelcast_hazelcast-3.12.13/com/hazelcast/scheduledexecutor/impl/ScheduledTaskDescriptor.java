/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorDataSerializerHook;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskResult;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskStatisticsImpl;
import com.hazelcast.scheduledexecutor.impl.TaskDefinition;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ScheduledTaskDescriptor
implements IdentifiedDataSerializable {
    private TaskDefinition definition;
    private transient ScheduledFuture<?> future;
    private final AtomicReference<ScheduledTaskResult> resultRef = new AtomicReference<Object>(null);
    private volatile ScheduledTaskStatisticsImpl stats;
    private volatile Map<?, ?> state;

    public ScheduledTaskDescriptor() {
    }

    public ScheduledTaskDescriptor(TaskDefinition definition) {
        this.definition = definition;
        this.state = new HashMap();
        this.stats = new ScheduledTaskStatisticsImpl();
    }

    public ScheduledTaskDescriptor(TaskDefinition definition, Map<?, ?> state, ScheduledTaskStatisticsImpl stats, ScheduledTaskResult result) {
        this.definition = definition;
        this.stats = stats;
        this.state = state;
        this.resultRef.set(result);
    }

    public TaskDefinition getDefinition() {
        return this.definition;
    }

    ScheduledTaskStatisticsImpl getStatsSnapshot() {
        return this.stats.snapshot();
    }

    Map<?, ?> getState() {
        return this.state;
    }

    ScheduledTaskResult getTaskResult() {
        return this.resultRef.get();
    }

    void setStats(ScheduledTaskStatisticsImpl stats) {
        this.stats = stats;
    }

    void setState(Map<?, ?> snapshot) {
        this.state = snapshot;
    }

    ScheduledFuture<?> getScheduledFuture() {
        return this.future;
    }

    void setScheduledFuture(ScheduledFuture<?> future) {
        this.future = future;
    }

    void setTaskResult(ScheduledTaskResult result) {
        this.resultRef.set(result);
    }

    Object get() throws ExecutionException, InterruptedException {
        ScheduledTaskResult result = this.resultRef.get();
        if (result != null) {
            result.checkErroneousState();
            return result.getReturnValue();
        }
        return this.future.get();
    }

    void suspend() {
        if (this.future != null) {
            this.future.cancel(true);
            this.future = null;
        }
    }

    boolean cancel(boolean mayInterrupt) {
        if (!this.resultRef.compareAndSet(null, new ScheduledTaskResult(true)) || this.future == null) {
            return false;
        }
        return this.future.cancel(mayInterrupt);
    }

    long getDelay(TimeUnit unit) {
        boolean wasDoneOrCancelled;
        boolean bl = wasDoneOrCancelled = this.resultRef.get() != null;
        if (wasDoneOrCancelled) {
            return 0L;
        }
        return this.future.getDelay(unit);
    }

    boolean isCancelled() {
        ScheduledTaskResult result = this.resultRef.get();
        boolean wasCancelled = result != null && result.wasCancelled();
        return wasCancelled || this.future != null && this.future.isCancelled();
    }

    boolean isDone() {
        boolean wasDone = this.resultRef.get() != null;
        return wasDone || this.future != null && this.future.isDone();
    }

    boolean shouldSchedule() {
        return this.future == null && this.resultRef.get() == null;
    }

    @Override
    public int getFactoryId() {
        return ScheduledExecutorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.definition);
        out.writeObject(this.state);
        out.writeObject(this.stats);
        out.writeObject(this.resultRef.get());
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.definition = (TaskDefinition)in.readObject();
        this.state = (Map)in.readObject();
        this.stats = (ScheduledTaskStatisticsImpl)in.readObject();
        this.resultRef.set((ScheduledTaskResult)in.readObject());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ScheduledTaskDescriptor that = (ScheduledTaskDescriptor)o;
        return this.definition == that.definition || this.definition != null && this.definition.equals(that.definition);
    }

    public int hashCode() {
        return Arrays.hashCode(new TaskDefinition[]{this.definition});
    }

    public String toString() {
        return "ScheduledTaskDescriptor{definition=" + this.definition + ", future=" + this.future + ", stats=" + this.stats + ", resultRef=" + this.resultRef.get() + ", state=" + this.state + '}';
    }
}

