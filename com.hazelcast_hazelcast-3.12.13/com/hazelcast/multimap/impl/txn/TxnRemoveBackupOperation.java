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
import java.util.Iterator;

public class TxnRemoveBackupOperation
extends AbstractKeyBasedMultiMapOperation
implements BackupOperation {
    private long recordId;
    private Data value;

    public TxnRemoveBackupOperation() {
    }

    public TxnRemoveBackupOperation(String name, Data dataKey, long recordId, Data value) {
        super(name, dataKey);
        this.recordId = recordId;
        this.value = value;
    }

    @Override
    public void run() throws Exception {
        MultiMapContainer container = this.getOrCreateContainerWithoutAccess();
        MultiMapValue multiMapValue = container.getMultiMapValueOrNull(this.dataKey);
        if (multiMapValue == null || !multiMapValue.containsRecordId(this.recordId)) {
            this.response = false;
            return;
        }
        this.response = true;
        Collection<MultiMapRecord> coll = multiMapValue.getCollection(false);
        Iterator<MultiMapRecord> iterator = coll.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getRecordId() != this.recordId) continue;
            iterator.remove();
            break;
        }
        if (coll.isEmpty()) {
            container.delete(this.dataKey);
        }
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
        return 36;
    }
}

