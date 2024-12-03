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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class TxnRemoveAllBackupOperation
extends AbstractKeyBasedMultiMapOperation
implements BackupOperation {
    private Collection<Long> recordIds;

    public TxnRemoveAllBackupOperation() {
    }

    public TxnRemoveAllBackupOperation(String name, Data dataKey, Collection<Long> recordIds) {
        super(name, dataKey);
        this.recordIds = recordIds;
    }

    @Override
    public void run() throws Exception {
        MultiMapContainer container = this.getOrCreateContainerWithoutAccess();
        MultiMapValue multiMapValue = container.getOrCreateMultiMapValue(this.dataKey);
        for (Long recordId : this.recordIds) {
            if (multiMapValue.containsRecordId(recordId)) continue;
            this.response = false;
            return;
        }
        this.response = true;
        Collection<MultiMapRecord> coll = multiMapValue.getCollection(false);
        block1: for (Long recordId : this.recordIds) {
            Iterator<MultiMapRecord> iterator = coll.iterator();
            while (iterator.hasNext()) {
                MultiMapRecord record = iterator.next();
                if (record.getRecordId() != recordId.longValue()) continue;
                iterator.remove();
                continue block1;
            }
        }
        if (coll.isEmpty()) {
            container.delete(this.dataKey);
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.recordIds.size());
        for (Long recordId : this.recordIds) {
            out.writeLong(recordId);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int size = in.readInt();
        this.recordIds = new ArrayList<Long>();
        for (int i = 0; i < size; ++i) {
            this.recordIds.add(in.readLong());
        }
    }

    @Override
    public int getId() {
        return 38;
    }
}

