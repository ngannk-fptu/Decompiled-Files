/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.operation;

import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.operation.AbstractNamedSerializableOperation;
import com.hazelcast.replicatedmap.impl.record.ReplicatedMapEntryView;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.replicatedmap.merge.ReplicatedMapMergePolicy;
import java.io.IOException;

public class LegacyMergeOperation
extends AbstractNamedSerializableOperation {
    private String name;
    private Object key;
    private ReplicatedMapEntryView entryView;
    private ReplicatedMapMergePolicy policy;

    public LegacyMergeOperation() {
    }

    public LegacyMergeOperation(String name, Object key, ReplicatedMapEntryView entryView, ReplicatedMapMergePolicy policy) {
        this.name = name;
        this.key = key;
        this.entryView = entryView;
        this.policy = policy;
    }

    @Override
    public void run() throws Exception {
        ReplicatedMapService service = (ReplicatedMapService)this.getService();
        ReplicatedRecordStore store = service.getReplicatedRecordStore(this.name, true, this.key);
        store.merge(this.key, this.entryView, this.policy);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        IOUtil.writeObject(out, this.key);
        out.writeObject(this.entryView);
        out.writeObject(this.policy);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.key = IOUtil.readObject(in);
        this.entryView = (ReplicatedMapEntryView)in.readObject();
        this.policy = (ReplicatedMapMergePolicy)in.readObject();
    }

    @Override
    public int getId() {
        return 9;
    }

    @Override
    public String getName() {
        return this.name;
    }
}

