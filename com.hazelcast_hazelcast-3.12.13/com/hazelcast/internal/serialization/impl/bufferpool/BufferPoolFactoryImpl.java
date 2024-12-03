/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl.bufferpool;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.bufferpool.BufferPool;
import com.hazelcast.internal.serialization.impl.bufferpool.BufferPoolFactory;
import com.hazelcast.internal.serialization.impl.bufferpool.BufferPoolImpl;

public class BufferPoolFactoryImpl
implements BufferPoolFactory {
    @Override
    public BufferPool create(InternalSerializationService serializationService) {
        return new BufferPoolImpl(serializationService);
    }
}

