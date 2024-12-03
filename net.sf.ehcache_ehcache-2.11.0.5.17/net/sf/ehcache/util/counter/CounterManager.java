/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util.counter;

import net.sf.ehcache.util.counter.Counter;
import net.sf.ehcache.util.counter.CounterConfig;

public interface CounterManager {
    public Counter createCounter(CounterConfig var1);

    public void addCounter(Counter var1);

    public void shutdown();

    public void shutdownCounter(Counter var1);
}

