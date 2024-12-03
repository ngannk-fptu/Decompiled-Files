/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.durableexecutor.impl.operations;

import com.hazelcast.durableexecutor.impl.DurableExecutorContainer;
import com.hazelcast.durableexecutor.impl.operations.AbstractDurableExecutorOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;

public class DisposeResultBackupOperation
extends AbstractDurableExecutorOperation
implements BackupOperation {
    private int sequence;

    public DisposeResultBackupOperation() {
    }

    DisposeResultBackupOperation(String name, int sequence) {
        super(name);
        this.sequence = sequence;
    }

    @Override
    public void run() throws Exception {
        DurableExecutorContainer executorContainer = this.getExecutorContainer();
        executorContainer.disposeResult(this.sequence);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.sequence);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.sequence = in.readInt();
    }

    @Override
    public int getId() {
        return 0;
    }
}

