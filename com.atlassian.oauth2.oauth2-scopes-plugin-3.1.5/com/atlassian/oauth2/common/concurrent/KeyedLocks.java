/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.common.concurrent;

import com.atlassian.oauth2.common.concurrent.StripedMonitors;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class KeyedLocks<T> {
    private final Map<T, AtomicInteger> monitors = new ConcurrentHashMap<T, AtomicInteger>();
    private final StripedMonitors<T> stripes;

    public KeyedLocks(int stripeCount) {
        this.stripes = new StripedMonitors(stripeCount);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <R> R executeWithLock(T key, Callable<R> task) throws Exception {
        AtomicInteger monitor;
        Object stripe;
        Object object = stripe = this.stripes.getMonitor(key);
        synchronized (object) {
            monitor = this.monitors.computeIfAbsent(key, k -> new AtomicInteger(0));
            monitor.incrementAndGet();
        }
        try {
            object = monitor;
            synchronized (object) {
                R r = task.call();
                return r;
            }
        }
        finally {
            Object object2 = stripe;
            synchronized (object2) {
                if (monitor.decrementAndGet() == 0) {
                    this.monitors.remove(key);
                }
            }
        }
    }

    public int size() {
        return this.monitors.size();
    }
}

