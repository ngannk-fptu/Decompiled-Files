/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.NamedOperation;
import com.hazelcast.spi.Operation;
import java.io.IOException;

public abstract class AbstractNamedOperation
extends Operation
implements NamedOperation,
IdentifiedDataSerializable {
    protected String name;

    protected AbstractNamedOperation(String name) {
        this.name = name;
    }

    public AbstractNamedOperation() {
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.name);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.name = in.readUTF();
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", name=").append(this.name);
    }
}

