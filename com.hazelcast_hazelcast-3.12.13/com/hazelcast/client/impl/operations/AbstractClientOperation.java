/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.operations;

import com.hazelcast.client.impl.ClientDataSerializerHook;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;

public abstract class AbstractClientOperation
extends Operation
implements IdentifiedDataSerializable {
    @Override
    public int getFactoryId() {
        return ClientDataSerializerHook.F_ID;
    }
}

