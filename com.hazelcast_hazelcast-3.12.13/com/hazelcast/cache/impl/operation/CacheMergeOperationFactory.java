/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.operation.CacheMergeOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.impl.operations.PartitionAwareOperationFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CacheMergeOperationFactory
extends PartitionAwareOperationFactory {
    private String name;
    private List<SplitBrainMergeTypes.CacheMergeTypes>[] mergingEntries;
    private SplitBrainMergePolicy<Data, SplitBrainMergeTypes.CacheMergeTypes> mergePolicy;

    public CacheMergeOperationFactory() {
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP2"})
    public CacheMergeOperationFactory(String name, int[] partitions, List<SplitBrainMergeTypes.CacheMergeTypes>[] mergingEntries, SplitBrainMergePolicy<Data, SplitBrainMergeTypes.CacheMergeTypes> mergePolicy) {
        this.name = name;
        this.partitions = partitions;
        this.mergingEntries = mergingEntries;
        this.mergePolicy = mergePolicy;
    }

    @Override
    public Operation createPartitionOperation(int partitionId) {
        for (int i = 0; i < this.partitions.length; ++i) {
            if (this.partitions[i] != partitionId) continue;
            return new CacheMergeOperation(this.name, this.mergingEntries[i], this.mergePolicy);
        }
        throw new IllegalArgumentException("Unknown partitionId " + partitionId + " (" + Arrays.toString(this.partitions) + ")");
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeIntArray(this.partitions);
        for (List<SplitBrainMergeTypes.CacheMergeTypes> list : this.mergingEntries) {
            out.writeInt(list.size());
            for (SplitBrainMergeTypes.CacheMergeTypes mergingEntry : list) {
                out.writeObject(mergingEntry);
            }
        }
        out.writeObject(this.mergePolicy);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.partitions = in.readIntArray();
        this.mergingEntries = new List[this.partitions.length];
        for (int partitionIndex = 0; partitionIndex < this.partitions.length; ++partitionIndex) {
            int size = in.readInt();
            ArrayList<SplitBrainMergeTypes.CacheMergeTypes> list = new ArrayList<SplitBrainMergeTypes.CacheMergeTypes>(size);
            for (int i = 0; i < size; ++i) {
                SplitBrainMergeTypes.CacheMergeTypes mergingEntry = (SplitBrainMergeTypes.CacheMergeTypes)in.readObject();
                list.add(mergingEntry);
            }
            this.mergingEntries[partitionIndex] = list;
        }
        this.mergePolicy = (SplitBrainMergePolicy)in.readObject();
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 64;
    }
}

