/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.durableexecutor.impl.operations;

import com.hazelcast.durableexecutor.impl.DurableExecutorContainer;
import com.hazelcast.durableexecutor.impl.operations.AbstractDurableExecutorOperation;
import com.hazelcast.durableexecutor.impl.operations.DisposeResultBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class DisposeResultOperation
extends AbstractDurableExecutorOperation
implements BackupAwareOperation,
MutatingOperation {
    int sequence;

    public DisposeResultOperation() {
    }

    public DisposeResultOperation(String name, int sequence) {
        super(name);
        this.sequence = sequence;
    }

    @Override
    public void run() throws Exception {
        DurableExecutorContainer executorContainer = this.getExecutorContainer();
        executorContainer.disposeResult(this.sequence);
    }

    @Override
    public Operation getBackupOperation() {
        return new DisposeResultBackupOperation(this.name, this.sequence);
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
        return 1;
    }
}

