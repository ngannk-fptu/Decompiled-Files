/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import java.io.IOException;

public abstract class AbstractLocalOperation
extends Operation
implements IdentifiedDataSerializable {
    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getName() + " is only used locally!");
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getName() + " is only used locally!");
    }

    @Override
    public int getFactoryId() {
        throw new UnsupportedOperationException(this.getClass().getName() + " is only used locally!");
    }

    @Override
    public int getId() {
        throw new UnsupportedOperationException(this.getClass().getName() + " is only used locally!");
    }
}

