/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl.xa.operations;

import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import com.hazelcast.transaction.impl.TransactionDataSerializerHook;

public abstract class AbstractXAOperation
extends Operation
implements IdentifiedDataSerializable {
    @Override
    public String getServiceName() {
        return "hz:impl:xaService";
    }

    @Override
    public int getFactoryId() {
        return TransactionDataSerializerHook.F_ID;
    }
}

