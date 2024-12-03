/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.executor.impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.PartitionAware;
import com.hazelcast.executor.impl.ExecutorDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.concurrent.Callable;

public final class RunnableAdapter<V>
implements IdentifiedDataSerializable,
Callable<V>,
HazelcastInstanceAware,
PartitionAware {
    private Runnable task;

    public RunnableAdapter() {
    }

    public RunnableAdapter(Runnable task) {
        this.task = task;
    }

    public Runnable getRunnable() {
        return this.task;
    }

    public void setRunnable(Runnable runnable) {
        this.task = runnable;
    }

    @Override
    public V call() throws Exception {
        this.task.run();
        return null;
    }

    public Object getPartitionKey() {
        if (this.task instanceof PartitionAware) {
            return ((PartitionAware)((Object)this.task)).getPartitionKey();
        }
        return null;
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        if (this.task instanceof HazelcastInstanceAware) {
            HazelcastInstanceAware instanceAwareTask = (HazelcastInstanceAware)((Object)this.task);
            instanceAwareTask.setHazelcastInstance(hazelcastInstance);
        }
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.task);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.task = (Runnable)in.readObject();
    }

    @Override
    public int getFactoryId() {
        return ExecutorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 2;
    }

    public String toString() {
        return "RunnableAdapter{task=" + this.task + '}';
    }
}

