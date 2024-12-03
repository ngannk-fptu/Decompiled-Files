/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.accumulator;

public interface AccumulatorHandler<T> {
    public void handle(T var1, boolean var2);

    public void reset();
}

