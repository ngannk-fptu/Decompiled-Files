/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.map.impl;

import com.hazelcast.map.impl.EntryCostEstimator;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.util.JVMUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

class BinaryMapEntryCostEstimator
implements EntryCostEstimator<Data, Record> {
    private static final int HASH_ENTRY_HASH_COST_IN_BYTES = JVMUtil.REFERENCE_COST_IN_BYTES;
    private static final int HASH_ENTRY_VALUE_REF_COST_IN_BYTES = JVMUtil.REFERENCE_COST_IN_BYTES;
    private static final int HASH_ENTRY_KEY_REF_COST_IN_BYTES = JVMUtil.REFERENCE_COST_IN_BYTES;
    private static final int HASH_ENTRY_NEXT_REF_COST_IN_BYTES = JVMUtil.REFERENCE_COST_IN_BYTES;
    private static final int HASH_ENTRY_COST_IN_BYTES = HASH_ENTRY_HASH_COST_IN_BYTES + HASH_ENTRY_KEY_REF_COST_IN_BYTES + HASH_ENTRY_VALUE_REF_COST_IN_BYTES + HASH_ENTRY_NEXT_REF_COST_IN_BYTES;
    private volatile long estimate;

    BinaryMapEntryCostEstimator() {
    }

    @Override
    public long getEstimate() {
        return this.estimate;
    }

    @Override
    @SuppressFBWarnings(value={"VO_VOLATILE_INCREMENT"}, justification="We have the guarantee that only a single partition thread at any given time can change the volatile field, but multiple threads can read it.")
    public void adjustEstimateBy(long adjustment) {
        this.estimate += adjustment;
    }

    @Override
    public void reset() {
        this.estimate = 0L;
    }

    @Override
    public long calculateValueCost(Record value) {
        return value.getCost();
    }

    @Override
    public long calculateEntryCost(Data key, Record value) {
        long totalMapEntryCost = 0L;
        totalMapEntryCost += (long)HASH_ENTRY_COST_IN_BYTES;
        totalMapEntryCost += (long)key.getHeapCost();
        return totalMapEntryCost += value.getCost();
    }
}

