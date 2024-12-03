/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl.operations;

import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import com.hazelcast.transaction.impl.TransactionDataSerializerHook;

public abstract class AbstractTxOperation
extends Operation
implements IdentifiedDataSerializable {
    @Override
    public String getServiceName() {
        return "hz:core:txManagerService";
    }

    @Override
    public int getFactoryId() {
        return TransactionDataSerializerHook.F_ID;
    }
}

