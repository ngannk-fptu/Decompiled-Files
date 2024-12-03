/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl;

import com.hazelcast.core.ManagedContext;
import com.hazelcast.core.PartitionAware;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.scheduledexecutor.NamedTask;
import com.hazelcast.scheduledexecutor.StatefulTask;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorDataSerializerHook;
import com.hazelcast.spi.NodeAware;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;

public class ScheduledRunnableAdapter<V>
implements IdentifiedDataSerializable,
Callable<V>,
NodeAware,
PartitionAware,
NamedTask,
StatefulTask {
    private Runnable task;

    public ScheduledRunnableAdapter() {
    }

    public ScheduledRunnableAdapter(Runnable task) {
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
    public void setNode(Node node) {
        ManagedContext managedContext = node.getSerializationService().getManagedContext();
        managedContext.initialize(this.task);
    }

    @Override
    public String getName() {
        if (this.task instanceof NamedTask) {
            return ((NamedTask)((Object)this.task)).getName();
        }
        return null;
    }

    public void save(Map snapshot) {
        if (this.task instanceof StatefulTask) {
            ((StatefulTask)((Object)this.task)).save(snapshot);
        }
    }

    public void load(Map snapshot) {
        if (this.task instanceof StatefulTask) {
            ((StatefulTask)((Object)this.task)).load(snapshot);
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
        return ScheduledExecutorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 4;
    }

    public String toString() {
        return "ScheduledRunnableAdapter{task=" + this.task + '}';
    }
}

