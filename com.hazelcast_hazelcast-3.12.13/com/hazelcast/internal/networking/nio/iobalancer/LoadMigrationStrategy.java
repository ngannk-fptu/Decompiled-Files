/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking.nio.iobalancer;

import com.hazelcast.internal.networking.nio.MigratablePipeline;
import com.hazelcast.internal.networking.nio.iobalancer.LoadImbalance;
import com.hazelcast.internal.networking.nio.iobalancer.MigrationStrategy;
import java.util.Set;

class LoadMigrationStrategy
implements MigrationStrategy {
    private static final double MIN_MAX_RATIO_MIGRATION_THRESHOLD = 0.8;
    private static final double MAXIMUM_NO_OF_EVENTS_AFTER_MIGRATION_COEFFICIENT = 0.9;

    LoadMigrationStrategy() {
    }

    @Override
    public boolean imbalanceDetected(LoadImbalance imbalance) {
        long min = imbalance.minimumLoad;
        long max = imbalance.maximumLoad;
        if (min == Long.MIN_VALUE || max == Long.MAX_VALUE) {
            return false;
        }
        long lowerBound = (long)(0.8 * (double)max);
        return min < lowerBound;
    }

    @Override
    public MigratablePipeline findPipelineToMigrate(LoadImbalance imbalance) {
        Set<MigratablePipeline> candidates = imbalance.getPipelinesOwnedBy(imbalance.srcOwner);
        long migrationThreshold = (long)((double)(imbalance.maximumLoad - imbalance.minimumLoad) * 0.9);
        MigratablePipeline candidate = null;
        long loadInSelectedPipeline = 0L;
        for (MigratablePipeline pipeline : candidates) {
            long load = imbalance.getLoad(pipeline);
            if (load <= loadInSelectedPipeline || load >= migrationThreshold) continue;
            loadInSelectedPipeline = load;
            candidate = pipeline;
        }
        return candidate;
    }
}

