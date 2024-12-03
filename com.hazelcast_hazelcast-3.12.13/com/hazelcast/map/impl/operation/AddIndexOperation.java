/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.AddIndexBackupOperation;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.impl.record.Records;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.impl.Index;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.InternalIndex;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.impl.MutatingOperation;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.Clock;
import java.io.IOException;
import java.util.Iterator;

public class AddIndexOperation
extends MapOperation
implements PartitionAwareOperation,
MutatingOperation,
BackupAwareOperation {
    private String attributeName;
    private boolean ordered;

    public AddIndexOperation() {
    }

    public AddIndexOperation(String name, String attributeName, boolean ordered) {
        super(name);
        this.attributeName = attributeName;
        this.ordered = ordered;
    }

    @Override
    public boolean shouldBackup() {
        return this.mapContainer.getTotalBackupCount() > 0;
    }

    @Override
    public int getSyncBackupCount() {
        return this.mapContainer.getTotalBackupCount();
    }

    @Override
    public int getAsyncBackupCount() {
        return 0;
    }

    @Override
    public Operation getBackupOperation() {
        return new AddIndexBackupOperation(this.name, this.attributeName, this.ordered);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public void run() throws Exception {
        int partitionId = this.getPartitionId();
        Indexes indexes = this.mapContainer.getIndexes(partitionId);
        InternalIndex index = indexes.addOrGetIndex(this.attributeName, this.ordered);
        if (index.hasPartitionIndexed(partitionId)) {
            return;
        }
        long now = this.getNow();
        Iterator<Record> iterator = this.recordStore.iterator(now, false);
        SerializationService serializationService = this.getNodeEngine().getSerializationService();
        while (iterator.hasNext()) {
            Record record = iterator.next();
            Data key = record.getKey();
            Object value = Records.getValueOrCachedValue(record, serializationService);
            QueryableEntry queryEntry = this.mapContainer.newQueryEntry(key, value);
            index.putEntry(queryEntry, null, Index.OperationSource.USER);
        }
        index.markPartitionAsIndexed(partitionId);
    }

    private long getNow() {
        return Clock.currentTimeMillis();
    }

    @Override
    public Object getResponse() {
        return Boolean.TRUE;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.attributeName);
        out.writeBoolean(this.ordered);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.attributeName = in.readUTF();
        this.ordered = in.readBoolean();
    }

    @Override
    public int getId() {
        return 43;
    }
}

