/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomicreference.operations;

import com.hazelcast.concurrent.atomicreference.AtomicReferenceContainer;
import com.hazelcast.concurrent.atomicreference.AtomicReferenceDataSerializerHook;
import com.hazelcast.concurrent.atomicreference.AtomicReferenceService;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.NamedOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionAwareOperation;
import java.io.IOException;

public abstract class AbstractAtomicReferenceOperation
extends Operation
implements NamedOperation,
PartitionAwareOperation,
IdentifiedDataSerializable {
    protected String name;

    public AbstractAtomicReferenceOperation() {
    }

    public AbstractAtomicReferenceOperation(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:atomicReferenceService";
    }

    public AtomicReferenceContainer getReferenceContainer() {
        AtomicReferenceService service = (AtomicReferenceService)this.getService();
        return service.getReferenceContainer(this.name);
    }

    @Override
    public final int getFactoryId() {
        return AtomicReferenceDataSerializerHook.F_ID;
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

