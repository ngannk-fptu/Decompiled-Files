/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomiclong.operations;

import com.hazelcast.concurrent.atomiclong.AtomicLongContainer;
import com.hazelcast.concurrent.atomiclong.AtomicLongService;
import com.hazelcast.concurrent.atomiclong.operations.AbstractAtomicLongOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;

public class MergeBackupOperation
extends AbstractAtomicLongOperation
implements BackupOperation {
    private Long newValue;

    public MergeBackupOperation() {
    }

    public MergeBackupOperation(String name, Long newValue) {
        super(name);
        this.newValue = newValue;
    }

    @Override
    public void run() throws Exception {
        if (this.newValue == null) {
            AtomicLongService service = (AtomicLongService)this.getService();
            service.destroyDistributedObject(this.name);
        } else {
            AtomicLongContainer container = this.getLongContainer();
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
        out.writeObject(this.newValue);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.newValue = (Long)in.readObject();
    }
}

