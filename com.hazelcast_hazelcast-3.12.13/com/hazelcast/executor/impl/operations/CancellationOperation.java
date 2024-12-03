/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.executor.impl.operations;

import com.hazelcast.executor.impl.DistributedExecutorService;
import com.hazelcast.executor.impl.ExecutorDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.NamedOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public final class CancellationOperation
extends Operation
implements NamedOperation,
MutatingOperation,
IdentifiedDataSerializable {
    private String uuid;
    private boolean interrupt;
    private boolean response;

    public CancellationOperation() {
    }

    public CancellationOperation(String uuid, boolean interrupt) {
        this.uuid = uuid;
        this.interrupt = interrupt;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:executorService";
    }

    @Override
    public void run() throws Exception {
        DistributedExecutorService service = (DistributedExecutorService)this.getService();
        this.response = service.cancel(this.uuid, this.interrupt);
    }

    @Override
    public Object getResponse() {
        return this.response;
    }

    @Override
    public String getName() {
        DistributedExecutorService service = (DistributedExecutorService)this.getService();
        return service.getName(this.uuid);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.uuid);
        out.writeBoolean(this.interrupt);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.uuid = in.readUTF();
        this.interrupt = in.readBoolean();
    }

    @Override
    public int getFactoryId() {
        return ExecutorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 3;
    }
}

