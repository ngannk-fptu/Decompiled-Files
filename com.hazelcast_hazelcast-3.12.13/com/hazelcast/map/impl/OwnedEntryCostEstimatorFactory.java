/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.map.impl.BinaryMapEntryCostEstimator;
import com.hazelcast.map.impl.EntryCostEstimator;

public final class OwnedEntryCostEstimatorFactory {
    private static final EntryCostEstimator ZERO_SIZE_ESTIMATOR = new ZeroEntryCostEstimator();

    private OwnedEntryCostEstimatorFactory() {
    }

    public static <K, V> EntryCostEstimator<K, V> createMapSizeEstimator(InMemoryFormat inMemoryFormat) {
        if (InMemoryFormat.BINARY.equals((Object)inMemoryFormat)) {
            return new BinaryMapEntryCostEstimator();
        }
        return ZERO_SIZE_ESTIMATOR;
    }

    private static class ZeroEntryCostEstimator
    implements EntryCostEstimator {
        private ZeroEntryCostEstimator() {
        }

        @Override
        public long getEstimate() {
            return 0L;
        }

        @Override
        public void adjustEstimateBy(long adjustment) {
        }

        public long calculateValueCost(Object value) {
            return 0L;
        }

        public long calculateEntryCost(Object key, Object value) {
            return 0L;
        }

        @Override
        public void reset() {
        }
    }
}

