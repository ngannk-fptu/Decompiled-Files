/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomiclong.operations;

import com.hazelcast.concurrent.atomiclong.AtomicLongContainer;
import com.hazelcast.concurrent.atomiclong.AtomicLongDataSerializerHook;
import com.hazelcast.concurrent.atomiclong.AtomicLongService;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.NamedOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionAwareOperation;
import java.io.IOException;

public abstract class AbstractAtomicLongOperation
extends Operation
implements NamedOperation,
PartitionAwareOperation,
IdentifiedDataSerializable {
    protected String name;

    public AbstractAtomicLongOperation() {
    }

    public AbstractAtomicLongOperation(String name) {
        this.name = name;
    }

    public AtomicLongContainer getLongContainer() {
        AtomicLongService service = (AtomicLongService)this.getService();
        return service.getLongContainer(this.name);
    }

    @Override
    public final String getServiceName() {
        return "hz:impl:atomicLongService";
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final int getFactoryId() {
        return AtomicLongDataSerializerHook.F_ID;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", name=").append(this.name);
    }
}

