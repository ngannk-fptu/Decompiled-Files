/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.txn;

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

public class TxnPutBackupOperation
extends AbstractKeyBasedMultiMapOperation
implements BackupOperation {
    private long recordId;
    private Data value;

    public TxnPutBackupOperation() {
    }

    public TxnPutBackupOperation(String name, Data dataKey, long recordId, Data value) {
        super(name, dataKey);
        this.recordId = recordId;
        this.value = value;
    }

    @Override
    public void run() throws Exception {
        MultiMapContainer container = this.getOrCreateContainerWithoutAccess();
        MultiMapValue multiMapValue = container.getOrCreateMultiMapValue(this.dataKey);
        this.response = true;
        if (multiMapValue.containsRecordId(this.recordId)) {
            this.response = false;
            return;
        }
        Collection<MultiMapRecord> coll = multiMapValue.getCollection(false);
        MultiMapRecord record = new MultiMapRecord(this.recordId, this.isBinary() ? this.value : this.toObject(this.value));
        coll.add(record);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(this.recordId);
        out.writeData(this.value);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.recordId = in.readLong();
        this.value = in.readData();
    }

    @Override
    public int getId() {
        return 34;
    }
}

