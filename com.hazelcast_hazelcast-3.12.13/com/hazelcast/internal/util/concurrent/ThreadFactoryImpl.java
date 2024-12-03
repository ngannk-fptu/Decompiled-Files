/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadFactoryImpl
implements ThreadFactory {
    private final String basename;
    private final AtomicInteger id = new AtomicInteger();

    public ThreadFactoryImpl(String basename) {
        this.basename = basename;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, this.basename + this.id.incrementAndGet());
    }
}

