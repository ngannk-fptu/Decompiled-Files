/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cardinality.impl.operations;

import com.hazelcast.cardinality.impl.hyperloglog.HyperLogLog;
import com.hazelcast.cardinality.impl.operations.CardinalityEstimatorBackupAwareOperation;
import com.hazelcast.cardinality.impl.operations.MergeBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.merge.MergingValueFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.serialization.SerializationService;
import java.io.IOException;

public class MergeOperation
extends CardinalityEstimatorBackupAwareOperation {
    private SplitBrainMergePolicy<HyperLogLog, SplitBrainMergeTypes.CardinalityEstimatorMergeTypes> mergePolicy;
    private HyperLogLog value;
    private transient HyperLogLog backupValue;

    public MergeOperation() {
    }

    public MergeOperation(String name, SplitBrainMergePolicy<HyperLogLog, SplitBrainMergeTypes.CardinalityEstimatorMergeTypes> mergePolicy, HyperLogLog value) {
        super(name);
        this.mergePolicy = mergePolicy;
        this.value = value;
    }

    @Override
    public void run() throws Exception {
        SerializationService serializationService = this.getNodeEngine().getSerializationService();
        SplitBrainMergeTypes.CardinalityEstimatorMergeTypes mergingEntry = MergingValueFactory.createMergingEntry(serializationService, this.name, this.value);
        this.backupValue = this.getCardinalityEstimatorContainer().merge(mergingEntry, this.mergePolicy, serializationService);
    }

    @Override
    public int getId() {
        return 8;
    }

    @Override
    public boolean shouldBackup() {
        return this.backupValue != null;
    }

    @Override
    public Operation getBackupOperation() {
        return new MergeBackupOperation(this.name, this.backupValue);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.mergePolicy = (SplitBrainMergePolicy)in.readObject();
        this.value = (HyperLogLog)in.readObject();
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.mergePolicy);
        out.writeObject(this.value);
    }
}

