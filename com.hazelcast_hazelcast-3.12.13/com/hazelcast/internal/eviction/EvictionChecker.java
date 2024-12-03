/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.eviction;

public interface EvictionChecker {
    public static final EvictionChecker EVICT_ALWAYS = new EvictionChecker(){

        @Override
        public boolean isEvictionRequired() {
            return true;
        }
    };

    public boolean isEvictionRequired();
}

