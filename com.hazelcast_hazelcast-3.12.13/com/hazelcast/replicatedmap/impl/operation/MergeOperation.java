/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.operation;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.operation.AbstractNamedSerializableOperation;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MergeOperation
extends AbstractNamedSerializableOperation {
    private String name;
    private List<SplitBrainMergeTypes.ReplicatedMapMergeTypes> mergingEntries;
    private SplitBrainMergePolicy<Object, SplitBrainMergeTypes.ReplicatedMapMergeTypes> mergePolicy;
    private transient boolean hasMergedValues;

    public MergeOperation() {
    }

    MergeOperation(String name, List<SplitBrainMergeTypes.ReplicatedMapMergeTypes> mergingEntries, SplitBrainMergePolicy<Object, SplitBrainMergeTypes.ReplicatedMapMergeTypes> mergePolicy) {
        this.name = name;
        this.mergingEntries = mergingEntries;
        this.mergePolicy = mergePolicy;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void run() {
        ReplicatedMapService service = (ReplicatedMapService)this.getService();
        ReplicatedRecordStore recordStore = service.getReplicatedRecordStore(this.name, true, this.getPartitionId());
        for (SplitBrainMergeTypes.ReplicatedMapMergeTypes mergingEntry : this.mergingEntries) {
            if (!recordStore.merge(mergingEntry, this.mergePolicy)) continue;
            this.hasMergedValues = true;
        }
    }

    @Override
    public Object getResponse() {
        return this.hasMergedValues;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.name);
        out.writeInt(this.mergingEntries.size());
        for (SplitBrainMergeTypes.ReplicatedMapMergeTypes mergingEntry : this.mergingEntries) {
            out.writeObject(mergingEntry);
        }
        out.writeObject(this.mergePolicy);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.name = in.readUTF();
        int size = in.readInt();
        this.mergingEntries = new ArrayList<SplitBrainMergeTypes.ReplicatedMapMergeTypes>(size);
        for (int i = 0; i < size; ++i) {
            SplitBrainMergeTypes.ReplicatedMapMergeTypes mergingEntry = (SplitBrainMergeTypes.ReplicatedMapMergeTypes)in.readObject();
            this.mergingEntries.add(mergingEntry);
        }
        this.mergePolicy = (SplitBrainMergePolicy)in.readObject();
    }

    @Override
    public int getId() {
        return 31;
    }
}

