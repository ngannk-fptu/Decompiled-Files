/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl.bufferpool;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.bufferpool.BufferPool;

public interface BufferPoolFactory {
    public BufferPool create(InternalSerializationService var1);
}

