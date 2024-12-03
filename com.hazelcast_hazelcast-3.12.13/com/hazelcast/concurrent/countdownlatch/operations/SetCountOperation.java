/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.countdownlatch.operations;

import com.hazelcast.concurrent.countdownlatch.CountDownLatchService;
import com.hazelcast.concurrent.countdownlatch.operations.BackupAwareCountDownLatchOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class SetCountOperation
extends BackupAwareCountDownLatchOperation
implements MutatingOperation {
    private int count;
    private boolean response;

    public SetCountOperation() {
    }

    public SetCountOperation(String name, int count) {
        super(name);
        this.count = count;
    }

    @Override
    public void run() throws Exception {
        CountDownLatchService service = (CountDownLatchService)this.getService();
        this.response = service.setCount(this.name, this.count);
    }

    @Override
    public Object getResponse() {
        return this.response;
    }

    @Override
    public boolean shouldBackup() {
        return this.response;
    }

    @Override
    public int getId() {
        return 6;
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

