/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import com.amazonaws.annotation.SdkInternalApi;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@SdkInternalApi
public class NamedDefaultThreadFactory
implements ThreadFactory {
    private static final Map<String, AtomicInteger> poolNumberMap = new ConcurrentHashMap<String, AtomicInteger>();
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public static NamedDefaultThreadFactory of(String name) {
        return new NamedDefaultThreadFactory(name);
    }

    private NamedDefaultThreadFactory(String name) {
        AtomicInteger poolNumber = NamedDefaultThreadFactory.getPoolNumberForName(name);
        SecurityManager s = System.getSecurityManager();
        this.group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = "pool-" + name + "-" + poolNumber.getAndIncrement() + "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != 5) {
            t.setPriority(5);
        }
        return t;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static AtomicInteger getPoolNumberForName(String name) {
        AtomicInteger poolNumber = poolNumberMap.get(name);
        if (poolNumber != null) return poolNumber;
        Class<NamedDefaultThreadFactory> clazz = NamedDefaultThreadFactory.class;
        synchronized (NamedDefaultThreadFactory.class) {
            poolNumber = poolNumberMap.get(name);
            if (poolNumber != null) return poolNumber;
            poolNumber = new AtomicInteger(1);
            poolNumberMap.put(name, poolNumber);
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return poolNumber;
        }
    }
}

