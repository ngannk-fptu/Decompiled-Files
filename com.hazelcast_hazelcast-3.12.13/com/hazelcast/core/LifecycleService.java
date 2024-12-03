/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.LifecycleListener;

public interface LifecycleService {
    public boolean isRunning();

    public void shutdown();

    public void terminate();

    public String addLifecycleListener(LifecycleListener var1);

    public boolean removeLifecycleListener(String var1);
}

