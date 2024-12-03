/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.operation;

import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.replicatedmap.impl.operation.ReplicatedMapDataSerializerHook;
import com.hazelcast.spi.Operation;

public abstract class AbstractSerializableOperation
extends Operation
implements IdentifiedDataSerializable {
    @Override
    public int getFactoryId() {
        return ReplicatedMapDataSerializerHook.F_ID;
    }
}

