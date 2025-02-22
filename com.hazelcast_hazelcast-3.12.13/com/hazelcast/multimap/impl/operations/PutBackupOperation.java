/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.operations;

import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.multimap.impl.MultiMapValue;
import com.hazelcast.multimap.impl.operations.AbstractKeyBasedMultiMapOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class PutBackupOperation
extends AbstractKeyBasedMultiMapOperation
implements BackupOperation {
    private long recordId;
    private Data value;
    private int index;

    public PutBackupOperation() {
    }

    public PutBackupOperation(String name, Data dataKey, Data value, long recordId, int index) {
        super(name, dataKey);
        this.value = value;
        this.recordId = recordId;
        this.index = index;
    }

    @Override
    public void run() throws Exception {
        MultiMapContainer container = this.getOrCreateContainerWithoutAccess();
        MultiMapValue multiMapValue = container.getOrCreateMultiMapValue(this.dataKey);
        Collection<MultiMapRecord> collection = multiMapValue.getCollection(false);
        MultiMapRecord record = new MultiMapRecord(this.recordId, this.isBinary() ? this.value : this.toObject(this.value));
        if (this.index == -1) {
            this.response = collection.add(record);
        } else {
            try {
                ((List)collection).add(this.index, record);
                this.response = true;
            }
            catch (IndexOutOfBoundsException e) {
                this.response = e;
            }
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(this.recordId);
        out.writeInt(this.index);
        out.writeData(this.value);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.recordId = in.readLong();
        this.index = in.readInt();
        this.value = in.readData();
    }

    @Override
    public int getId() {
        return 15;
    }
}

