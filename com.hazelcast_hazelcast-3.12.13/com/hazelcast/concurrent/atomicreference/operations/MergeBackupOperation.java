/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomicreference.operations;

import com.hazelcast.concurrent.atomicreference.AtomicReferenceContainer;
import com.hazelcast.concurrent.atomicreference.AtomicReferenceService;
import com.hazelcast.concurrent.atomicreference.operations.AbstractAtomicReferenceOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;

public class MergeBackupOperation
extends AbstractAtomicReferenceOperation
implements BackupOperation {
    private Data newValue;

    public MergeBackupOperation() {
    }

    public MergeBackupOperation(String name, Data newValue) {
        super(name);
        this.newValue = newValue;
    }

    @Override
    public void run() throws Exception {
        if (this.newValue == null) {
            AtomicReferenceService service = (AtomicReferenceService)this.getService();
            service.destroyDistributedObject(this.name);
        } else {
            AtomicReferenceContainer container = this.getReferenceContainer();
            container.set(this.newValue);
        }
    }

    @Override
    public int getId() {
        return 14;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeData(this.newValue);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.newValue = in.readData();
    }
}

