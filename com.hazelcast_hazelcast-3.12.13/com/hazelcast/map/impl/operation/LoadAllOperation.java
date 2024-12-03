/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.impl.MutatingOperation;
import com.hazelcast.spi.partition.IPartitionService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoadAllOperation
extends MapOperation
implements PartitionAwareOperation,
MutatingOperation {
    private List<Data> keys;
    private boolean replaceExistingValues;

    public LoadAllOperation() {
        this.keys = Collections.emptyList();
    }

    public LoadAllOperation(String name, List<Data> keys, boolean replaceExistingValues) {
        super(name);
        this.keys = keys;
        this.replaceExistingValues = replaceExistingValues;
    }

    @Override
    public void run() throws Exception {
        this.keys = this.selectThisPartitionsKeys();
        this.recordStore.loadAllFromStore(this.keys, this.replaceExistingValues);
    }

    @Override
    public void afterRun() throws Exception {
        super.afterRun();
        this.invalidateNearCache(this.keys);
    }

    private List<Data> selectThisPartitionsKeys() {
        IPartitionService partitionService = this.mapServiceContext.getNodeEngine().getPartitionService();
        int partitionId = this.getPartitionId();
        ArrayList<Data> dataKeys = null;
        for (Data key : this.keys) {
            if (partitionId != partitionService.getPartitionId(key)) continue;
            if (dataKeys == null) {
                dataKeys = new ArrayList<Data>(this.keys.size());
            }
            dataKeys.add(key);
        }
        if (dataKeys == null) {
            return Collections.emptyList();
        }
        return dataKeys;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        int size = this.keys.size();
        out.writeInt(size);
        for (Data key : this.keys) {
            out.writeData(key);
        }
        out.writeBoolean(this.replaceExistingValues);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int size = in.readInt();
        if (size > 0) {
            this.keys = new ArrayList<Data>(size);
        }
        for (int i = 0; i < size; ++i) {
            Data data = in.readData();
            this.keys.add(data);
        }
        this.replaceExistingValues = in.readBoolean();
    }

    @Override
    public int getId() {
        return 18;
    }
}

