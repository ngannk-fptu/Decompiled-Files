/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.eviction;

public enum EvictionStrategyType {
    SAMPLING_BASED_EVICTION;

    public static final EvictionStrategyType DEFAULT_EVICTION_STRATEGY;

    static {
        DEFAULT_EVICTION_STRATEGY = SAMPLING_BASED_EVICTION;
    }
}

