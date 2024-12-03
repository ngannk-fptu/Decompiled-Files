/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util.counter;

import net.sf.ehcache.util.counter.Counter;
import net.sf.ehcache.util.counter.CounterImpl;

public class CounterConfig {
    private final long initialValue;

    public CounterConfig(long initialValue) {
        this.initialValue = initialValue;
    }

    public final long getInitialValue() {
        return this.initialValue;
    }

    public Counter createCounter() {
        return new CounterImpl(this.initialValue);
    }
}

