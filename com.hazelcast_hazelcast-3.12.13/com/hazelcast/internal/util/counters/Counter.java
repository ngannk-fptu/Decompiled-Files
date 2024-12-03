/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.counters;

public interface Counter {
    public long get();

    public long inc();

    public long inc(long var1);
}

