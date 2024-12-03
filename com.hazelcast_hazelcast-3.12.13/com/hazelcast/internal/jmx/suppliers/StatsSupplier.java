/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx.suppliers;

public interface StatsSupplier<T> {
    public T getEmpty();

    public T get();
}

