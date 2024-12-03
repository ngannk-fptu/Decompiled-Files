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

public class DeleteOperation
extends BaseRemoveOperation
implements Versioned {
    private boolean success;

    public DeleteOperation(String name, Data dataKey) {
        super(name, dataKey);
    }

    public DeleteOperation(String name, Data dataKey, boolean disableWanReplicationEvent) {
        super(name, dataKey, disableWanReplicationEvent);
    }

    public DeleteOperation() {
    }

    @Override
    public void run() {
        this.success = this.recordStore.delete(this.dataKey, this.getCallerProvenance());
    }

    @Override
    public Object getResponse() {
        return this.success;
    }

    @Override
    public void afterRun() {
        if (this.success) {
            super.afterRun();
        }
    }

    @Override
    public boolean shouldBackup() {
        return this.success;
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(false);
    }

    @Override
    public int getId() {
        return 29;
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

