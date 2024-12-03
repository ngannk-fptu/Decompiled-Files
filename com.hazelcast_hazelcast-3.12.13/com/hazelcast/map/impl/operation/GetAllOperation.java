/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.MapEntries;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GetAllOperation
extends MapOperation
implements ReadonlyOperation,
PartitionAwareOperation {
    private static final double SIZING_FUDGE_FACTOR = 1.3;
    private List<Data> keys = new ArrayList<Data>();
    private MapEntries entries;

    public GetAllOperation() {
    }

    public GetAllOperation(String name, List<Data> keys) {
        super(name);
        this.keys = keys;
    }

    @Override
    public void run() {
        IPartitionService partitionService = this.getNodeEngine().getPartitionService();
        int partitionId = this.getPartitionId();
        int roughSize = (int)((double)this.keys.size() * 1.3 / (double)partitionService.getPartitionCount());
        Set<Data> partitionKeySet = SetUtil.createHashSet(roughSize);
        for (Data key : this.keys) {
            if (partitionId != partitionService.getPartitionId(key)) continue;
            partitionKeySet.add(key);
        }
        this.entries = this.recordStore.getAll(partitionKeySet, this.getCallerAddress());
    }

    @Override
    public Object getResponse() {
        return this.entries;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        if (this.keys == null) {
            out.writeInt(-1);
        } else {
            out.writeInt(this.keys.size());
            for (Data key : this.keys) {
                out.writeData(key);
            }
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int size = in.readInt();
        if (size > -1) {
            for (int i = 0; i < size; ++i) {
                Data data = in.readData();
                this.keys.add(data);
            }
        }
    }

    @Override
    public int getId() {
        return 33;
    }
}

