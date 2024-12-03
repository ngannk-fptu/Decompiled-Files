/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.operations;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.multimap.impl.MultiMapValue;
import com.hazelcast.multimap.impl.operations.AbstractBackupAwareMultiMapOperation;
import com.hazelcast.multimap.impl.operations.RemoveBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class RemoveOperation
extends AbstractBackupAwareMultiMapOperation
implements MutatingOperation {
    private Data value;
    private long recordId;

    public RemoveOperation() {
    }

    public RemoveOperation(String name, Data dataKey, long threadId, Data value) {
        super(name, dataKey, threadId);
        this.value = value;
    }

    @Override
    public void run() throws Exception {
        this.response = false;
        MultiMapContainer container = this.getOrCreateContainer();
        MultiMapValue multiMapValue = container.getMultiMapValueOrNull(this.dataKey);
        if (multiMapValue == null) {
            return;
        }
        Collection<MultiMapRecord> coll = multiMapValue.getCollection(false);
        MultiMapRecord record = new MultiMapRecord(this.isBinary() ? this.value : this.toObject(this.value));
        if (this.getNodeEngine().getClusterService().getClusterVersion().isGreaterOrEqual(Versions.V3_12)) {
            this.response = coll.remove(record);
        } else {
            Iterator<MultiMapRecord> iterator = coll.iterator();
            while (iterator.hasNext()) {
                MultiMapRecord r = iterator.next();
                if (!r.equals(record)) continue;
                iterator.remove();
                this.recordId = r.getRecordId();
                this.response = true;
                break;
            }
        }
        if (coll.isEmpty()) {
            container.delete(this.dataKey);
        }
    }

    @Override
    public void afterRun() throws Exception {
        if (Boolean.TRUE.equals(this.response)) {
            this.getOrCreateContainer().update();
            this.publishEvent(EntryEventType.REMOVED, this.dataKey, null, this.value);
        }
    }

    @Override
    public boolean shouldBackup() {
        return Boolean.TRUE.equals(this.response);
    }

    @Override
    public Operation getBackupOperation() {
        return new RemoveBackupOperation(this.name, this.dataKey, this.recordId, this.value);
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(false);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeData(this.value);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.value = in.readData();
    }

    @Override
    public int getId() {
        return 20;
    }
}

