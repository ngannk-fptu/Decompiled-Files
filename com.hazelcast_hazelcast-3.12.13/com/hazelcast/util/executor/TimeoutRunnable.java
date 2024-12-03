/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.executor;

import java.util.concurrent.TimeUnit;

public interface TimeoutRunnable
extends Runnable {
    public long getTimeout();

    public TimeUnit getTimeUnit();
}

