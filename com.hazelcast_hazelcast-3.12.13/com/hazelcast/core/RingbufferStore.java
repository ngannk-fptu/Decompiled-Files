/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

public interface RingbufferStore<T> {
    public void store(long var1, T var3);

    public void storeAll(long var1, T[] var3);

    public T load(long var1);

    public long getLargestSequence();
}

