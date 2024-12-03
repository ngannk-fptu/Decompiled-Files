/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.countdownlatch.operations;

import com.hazelcast.concurrent.countdownlatch.CountDownLatchService;
import com.hazelcast.concurrent.countdownlatch.operations.AbstractCountDownLatchOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;

public class CountDownLatchBackupOperation
extends AbstractCountDownLatchOperation
implements BackupOperation {
    private int count;

    public CountDownLatchBackupOperation() {
    }

    public CountDownLatchBackupOperation(String name, int count) {
        super(name);
        this.count = count;
    }

    @Override
    public void run() throws Exception {
        CountDownLatchService service = (CountDownLatchService)this.getService();
        service.setCountDirect(this.name, this.count);
    }

    @Override
    public Object getResponse() {
        return Boolean.TRUE;
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.count);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.count = in.readInt();
    }
}

