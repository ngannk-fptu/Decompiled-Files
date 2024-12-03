/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl;

import com.hazelcast.core.ManagedContext;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.scheduledexecutor.NamedTask;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorDataSerializerHook;
import java.io.IOException;
import java.util.concurrent.Callable;

public class NamedTaskDecorator<V>
implements Runnable,
Callable<V>,
NamedTask,
IdentifiedDataSerializable {
    private String name;
    private Object delegate;

    NamedTaskDecorator() {
    }

    private NamedTaskDecorator(String name, Runnable runnable) {
        this.name = name;
        this.delegate = runnable;
    }

    private NamedTaskDecorator(String name, Callable<V> callable) {
        this.name = name;
        this.delegate = callable;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void run() {
        ((Runnable)this.delegate).run();
    }

    @Override
    public V call() throws Exception {
        return ((Callable)this.delegate).call();
    }

    public void initializeContext(ManagedContext context) {
        context.initialize(this.delegate);
    }

    @Override
    public int getFactoryId() {
        return ScheduledExecutorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeObject(this.delegate);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.delegate = in.readObject();
    }

    public static Runnable named(String name, Runnable runnable) {
        return new NamedTaskDecorator(name, runnable);
    }

    public static <V> Callable<V> named(String name, Callable<V> callable) {
        return new NamedTaskDecorator<V>(name, callable);
    }
}

