/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking.nio.iobalancer;

import com.hazelcast.internal.networking.nio.MigratablePipeline;
import com.hazelcast.internal.networking.nio.iobalancer.LoadImbalance;
import com.hazelcast.internal.networking.nio.iobalancer.MigrationStrategy;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

class MonkeyMigrationStrategy
implements MigrationStrategy {
    private final Random random = new Random();

    MonkeyMigrationStrategy() {
    }

    @Override
    public boolean imbalanceDetected(LoadImbalance imbalance) {
        Set<MigratablePipeline> candidates = imbalance.getPipelinesOwnedBy(imbalance.srcOwner);
        return candidates.size() > 0;
    }

    @Override
    public MigratablePipeline findPipelineToMigrate(LoadImbalance imbalance) {
        Set<MigratablePipeline> candidates = imbalance.getPipelinesOwnedBy(imbalance.srcOwner);
        int pipelineCount = candidates.size();
        int selected = this.random.nextInt(pipelineCount);
        Iterator<MigratablePipeline> iterator = candidates.iterator();
        for (int i = 0; i < selected; ++i) {
            iterator.next();
        }
        return iterator.next();
    }
}

