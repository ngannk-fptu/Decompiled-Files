/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl;

import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorContainer;

public interface ScheduledExecutorContainerHolder {
    public ScheduledExecutorContainer getContainer(String var1);

    public ScheduledExecutorContainer getOrCreateContainer(String var1);

    public void destroy();

    public void destroyContainer(String var1);
}

