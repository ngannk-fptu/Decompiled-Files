/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cardinality.impl;

import com.hazelcast.cardinality.impl.CardinalityEstimatorDataSerializerHook;
import com.hazelcast.cardinality.impl.hyperloglog.HyperLogLog;
import com.hazelcast.cardinality.impl.hyperloglog.impl.HyperLogLogImpl;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.impl.merge.MergingValueFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.serialization.SerializationService;
import java.io.IOException;

public class CardinalityEstimatorContainer
implements IdentifiedDataSerializable {
    HyperLogLog hll = new HyperLogLogImpl();
    private int backupCount;
    private int asyncBackupCount;

    public CardinalityEstimatorContainer() {
        this(1, 0);
    }

    public CardinalityEstimatorContainer(int backupCount, int asyncBackupCount) {
        this.backupCount = backupCount;
        this.asyncBackupCount = asyncBackupCount;
    }

    public void add(long hash) {
        this.hll.add(hash);
    }

    public long estimate() {
        return this.hll.estimate();
    }

    public int getBackupCount() {
        return this.backupCount;
    }

    public int getAsyncBackupCount() {
        return this.asyncBackupCount;
    }

    public int getTotalBackupCount() {
        return this.backupCount + this.asyncBackupCount;
    }

    public HyperLogLog merge(SplitBrainMergeTypes.CardinalityEstimatorMergeTypes mergingEntry, SplitBrainMergePolicy<HyperLogLog, SplitBrainMergeTypes.CardinalityEstimatorMergeTypes> mergePolicy, SerializationService serializationService) {
        serializationService.getManagedContext().initialize(mergingEntry);
        serializationService.getManagedContext().initialize(mergePolicy);
        String name = (String)mergingEntry.getKey();
        if (this.hll.estimate() != 0L) {
            SplitBrainMergeTypes.CardinalityEstimatorMergeTypes existingEntry = MergingValueFactory.createMergingEntry(serializationService, name, this.hll);
            HyperLogLog newValue = mergePolicy.merge(mergingEntry, existingEntry);
            if (newValue != null && !newValue.equals(this.hll)) {
                this.setValue(newValue);
                return this.hll;
            }
        } else {
            HyperLogLog newValue = mergePolicy.merge(mergingEntry, null);
            if (newValue != null) {
                this.setValue(newValue);
                return this.hll;
            }
        }
        return null;
    }

    public void setValue(HyperLogLog hll) {
        this.hll = hll;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.hll);
        out.writeInt(this.backupCount);
        out.writeInt(this.asyncBackupCount);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.hll = (HyperLogLog)in.readObject();
        this.backupCount = in.readInt();
        this.asyncBackupCount = in.readInt();
    }

    @Override
    public int getFactoryId() {
        return CardinalityEstimatorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 4;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CardinalityEstimatorContainer that = (CardinalityEstimatorContainer)o;
        return this.hll.equals(that.hll);
    }

    public int hashCode() {
        return this.hll.hashCode();
    }
}

