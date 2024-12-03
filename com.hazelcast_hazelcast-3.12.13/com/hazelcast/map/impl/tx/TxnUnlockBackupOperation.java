/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.tx;

import com.hazelcast.map.impl.operation.KeyBasedMapOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;

public class TxnUnlockBackupOperation
extends KeyBasedMapOperation
implements BackupOperation {
    private String ownerUuid;

    public TxnUnlockBackupOperation() {
    }

    public TxnUnlockBackupOperation(String name, Data dataKey, String ownerUuid) {
        super(name, dataKey, -1L, -1L);
        this.ownerUuid = ownerUuid;
    }

    @Override
    public void run() {
        this.recordStore.unlock(this.dataKey, this.ownerUuid, this.getThreadId(), this.getCallId());
    }

    @Override
    public Object getResponse() {
        return Boolean.TRUE;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.ownerUuid);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.ownerUuid = in.readUTF();
    }

    @Override
    public int getId() {
        return 73;
    }
}

