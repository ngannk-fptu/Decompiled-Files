/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.executor.impl.operations;

import com.hazelcast.executor.impl.operations.AbstractCallableTaskOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.impl.MutatingOperation;

public final class CallableTaskOperation
extends AbstractCallableTaskOperation
implements IdentifiedDataSerializable,
MutatingOperation {
    public CallableTaskOperation() {
    }

    public CallableTaskOperation(String name, String uuid, Data callableData) {
        super(name, uuid, callableData);
    }

    @Override
    public int getId() {
        return 0;
    }
}

