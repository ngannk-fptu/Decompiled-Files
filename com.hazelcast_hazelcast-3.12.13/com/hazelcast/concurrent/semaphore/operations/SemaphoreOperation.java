/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.semaphore.operations;

import com.hazelcast.concurrent.semaphore.SemaphoreContainer;
import com.hazelcast.concurrent.semaphore.SemaphoreDataSerializerHook;
import com.hazelcast.concurrent.semaphore.SemaphoreService;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.impl.AbstractNamedOperation;
import java.io.IOException;

public abstract class SemaphoreOperation
extends AbstractNamedOperation
implements PartitionAwareOperation,
IdentifiedDataSerializable {
    protected int permitCount;
    protected transient Object response;

    protected SemaphoreOperation() {
    }

    protected SemaphoreOperation(String name, int permitCount) {
        super(name);
        this.permitCount = permitCount;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:semaphoreService";
    }

    @Override
    public Object getResponse() {
        return this.response;
    }

    public SemaphoreContainer getSemaphoreContainer() {
        SemaphoreService service = (SemaphoreService)this.getService();
        return service.getSemaphoreContainer(this.name);
    }

    @Override
    public final int getFactoryId() {
        return SemaphoreDataSerializerHook.F_ID;
    }

    @Override
    public void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.permitCount);
    }

    @Override
    public void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.permitCount = in.readInt();
    }
}

