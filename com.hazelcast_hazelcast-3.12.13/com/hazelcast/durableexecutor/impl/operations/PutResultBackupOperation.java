/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.durableexecutor.impl.operations;

import com.hazelcast.durableexecutor.impl.operations.AbstractDurableExecutorOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;

public class PutResultBackupOperation
extends AbstractDurableExecutorOperation
implements BackupOperation {
    private int sequence;
    private Object result;

    public PutResultBackupOperation() {
    }

    public PutResultBackupOperation(String name, int sequence, Object result) {
        super(name);
        this.sequence = sequence;
        this.result = result;
    }

    @Override
    public void run() throws Exception {
        this.getExecutorContainer().putResult(this.sequence, this.result);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.sequence);
        out.writeObject(this.result);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.sequence = in.readInt();
        this.result = in.readObject();
    }

    @Override
    public int getId() {
        return 9;
    }
}

