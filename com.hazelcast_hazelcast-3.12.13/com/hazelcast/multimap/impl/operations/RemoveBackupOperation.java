/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.operations;

import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.multimap.impl.MultiMapValue;
import com.hazelcast.multimap.impl.operations.AbstractKeyBasedMultiMapOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class RemoveBackupOperation
extends AbstractKeyBasedMultiMapOperation
implements BackupOperation,
Versioned {
    private long recordId;
    private Data value;

    public RemoveBackupOperation() {
    }

    public RemoveBackupOperation(String name, Data dataKey, long recordId, Data value) {
        super(name, dataKey);
        this.recordId = recordId;
        this.value = value;
    }

    @Override
    public void run() throws Exception {
        this.response = false;
        MultiMapContainer container = this.getOrCreateContainerWithoutAccess();
        MultiMapValue multiMapValue = container.getMultiMapValueOrNull(this.dataKey);
        if (multiMapValue == null) {
            return;
        }
        Collection<MultiMapRecord> coll = multiMapValue.getCollection(false);
        if (this.value != null) {
            MultiMapRecord record = new MultiMapRecord(this.isBinary() ? this.value : this.toObject(this.value));
            this.response = coll.remove(record);
        } else {
            Iterator<MultiMapRecord> iterator = coll.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getRecordId() != this.recordId) continue;
                iterator.remove();
                this.response = true;
                break;
            }
        }
        if (coll.isEmpty()) {
            container.delete(this.dataKey);
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(this.recordId);
        if (out.getVersion().isGreaterOrEqual(Versions.V3_12)) {
            out.writeData(this.value);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.recordId = in.readLong();
        if (in.getVersion().isGreaterOrEqual(Versions.V3_12)) {
            this.value = in.readData();
        }
    }

    @Override
    public int getId() {
        return 19;
    }
}

