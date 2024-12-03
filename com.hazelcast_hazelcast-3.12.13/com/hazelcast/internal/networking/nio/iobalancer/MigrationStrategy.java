/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking.nio.iobalancer;

import com.hazelcast.internal.networking.nio.MigratablePipeline;
import com.hazelcast.internal.networking.nio.iobalancer.LoadImbalance;

interface MigrationStrategy {
    public boolean imbalanceDetected(LoadImbalance var1);

    public MigratablePipeline findPipelineToMigrate(LoadImbalance var1);
}

