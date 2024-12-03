/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.map.impl.operation.BaseRemoveOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.impl.Versioned;
import java.io.IOException;

public class RemoveOperation
extends BaseRemoveOperation
implements Versioned {
    protected boolean successful;

    public RemoveOperation() {
    }

    public RemoveOperation(String name, Data dataKey, boolean disableWanReplicationEvent) {
        super(name, dataKey, disableWanReplicationEvent);
    }

    @Override
    public void run() {
        this.dataOldValue = this.mapServiceContext.toData(this.recordStore.remove(this.dataKey, this.getCallerProvenance()));
        this.successful = this.dataOldValue != null;
    }

    @Override
    public void afterRun() {
        if (this.successful) {
            super.afterRun();
        }
    }

    @Override
    public boolean shouldBackup() {
        return this.successful;
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        if (out.getVersion().isGreaterOrEqual(Versions.V3_11)) {
            out.writeBoolean(this.disableWanReplicationEvent);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        if (in.getVersion().isGreaterOrEqual(Versions.V3_11)) {
            this.disableWanReplicationEvent = in.readBoolean();
        }
    }
}

