/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.scheduledexecutor.impl.operations.AbstractSchedulerOperation;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;

public class CancelTaskBackupOperation
extends AbstractSchedulerOperation
implements BackupOperation {
    private String taskName;

    public CancelTaskBackupOperation() {
    }

    public CancelTaskBackupOperation(String schedulerName, String taskName) {
        super(schedulerName);
        this.taskName = taskName;
    }

    @Override
    public void run() throws Exception {
        this.getContainer().cancel(this.taskName);
    }

    @Override
    public int getId() {
        return 9;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.taskName);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.taskName = in.readUTF();
    }
}

