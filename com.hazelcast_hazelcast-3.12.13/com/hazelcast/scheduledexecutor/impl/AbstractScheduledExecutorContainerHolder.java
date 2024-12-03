/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl;

import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorContainer;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorContainerHolder;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.executionservice.InternalExecutionService;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractScheduledExecutorContainerHolder
implements ScheduledExecutorContainerHolder {
    final NodeEngine nodeEngine;
    final ConcurrentMap<String, ScheduledExecutorContainer> containers = new ConcurrentHashMap<String, ScheduledExecutorContainer>();

    public AbstractScheduledExecutorContainerHolder(NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
    }

    @Override
    public ScheduledExecutorContainer getContainer(String name) {
        Preconditions.checkNotNull(name, "Name can't be null");
        return (ScheduledExecutorContainer)this.containers.get(name);
    }

    @Override
    public ScheduledExecutorContainer getOrCreateContainer(String name) {
        Preconditions.checkNotNull(name, "Name can't be null");
        return ConcurrencyUtil.getOrPutIfAbsent(this.containers, name, this.getContainerConstructorFunction());
    }

    public Collection<ScheduledExecutorContainer> getContainers() {
        return Collections.unmodifiableCollection(this.containers.values());
    }

    public Iterator<ScheduledExecutorContainer> iterator() {
        return this.containers.values().iterator();
    }

    @Override
    public void destroy() {
        for (ScheduledExecutorContainer container : this.containers.values()) {
            ((InternalExecutionService)this.nodeEngine.getExecutionService()).shutdownScheduledDurableExecutor(container.getName());
        }
    }

    @Override
    public void destroyContainer(String name) {
        ScheduledExecutorContainer container = (ScheduledExecutorContainer)this.containers.remove(name);
        if (container != null) {
            container.destroy();
        }
    }

    protected abstract ConstructorFunction<String, ScheduledExecutorContainer> getContainerConstructorFunction();
}

