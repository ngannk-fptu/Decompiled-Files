/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.replicatedmap.impl.operation;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.replicatedmap.impl.operation.MergeOperation;
import com.hazelcast.replicatedmap.impl.operation.ReplicatedMapDataSerializerHook;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.impl.operations.PartitionAwareOperationFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MergeOperationFactory
extends PartitionAwareOperationFactory {
    private String name;
    private List<SplitBrainMergeTypes.ReplicatedMapMergeTypes>[] mergingEntries;
    private SplitBrainMergePolicy<Object, SplitBrainMergeTypes.ReplicatedMapMergeTypes> mergePolicy;

    public MergeOperationFactory() {
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP2"})
    public MergeOperationFactory(String name, int[] partitions, List<SplitBrainMergeTypes.ReplicatedMapMergeTypes>[] mergingEntries, SplitBrainMergePolicy<Object, SplitBrainMergeTypes.ReplicatedMapMergeTypes> mergePolicy) {
        this.name = name;
        this.partitions = partitions;
        this.mergingEntries = mergingEntries;
        this.mergePolicy = mergePolicy;
    }

    @Override
    public Operation createPartitionOperation(int partitionId) {
        for (int i = 0; i < this.partitions.length; ++i) {
            if (this.partitions[i] != partitionId) continue;
            return new MergeOperation(this.name, this.mergingEntries[i], this.mergePolicy);
        }
        throw new IllegalArgumentException("Unknown partitionId " + partitionId + " (" + Arrays.toString(this.partitions) + ")");
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeIntArray(this.partitions);
        for (List<SplitBrainMergeTypes.ReplicatedMapMergeTypes> list : this.mergingEntries) {
            out.writeInt(list.size());
            for (SplitBrainMergeTypes.ReplicatedMapMergeTypes mergingEntry : list) {
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
            ArrayList<SplitBrainMergeTypes.ReplicatedMapMergeTypes> list = new ArrayList<SplitBrainMergeTypes.ReplicatedMapMergeTypes>(size);
            for (int i = 0; i < size; ++i) {
                SplitBrainMergeTypes.ReplicatedMapMergeTypes mergingEntry = (SplitBrainMergeTypes.ReplicatedMapMergeTypes)in.readObject();
                list.add(mergingEntry);
            }
            this.mergingEntries[partitionIndex] = list;
        }
        this.mergePolicy = (SplitBrainMergePolicy)in.readObject();
    }

    @Override
    public int getFactoryId() {
        return ReplicatedMapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 30;
    }
}

