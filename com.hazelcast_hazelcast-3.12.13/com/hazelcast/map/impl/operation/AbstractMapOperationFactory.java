/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.spi.OperationFactory;

public abstract class AbstractMapOperationFactory
implements OperationFactory {
    @Override
    public final int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }
}

