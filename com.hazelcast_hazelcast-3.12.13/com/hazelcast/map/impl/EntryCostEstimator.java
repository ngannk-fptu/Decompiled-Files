/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

public interface EntryCostEstimator<K, V> {
    public long getEstimate();

    public void adjustEstimateBy(long var1);

    public long calculateValueCost(V var1);

    public long calculateEntryCost(K var1, V var2);

    public void reset();
}

