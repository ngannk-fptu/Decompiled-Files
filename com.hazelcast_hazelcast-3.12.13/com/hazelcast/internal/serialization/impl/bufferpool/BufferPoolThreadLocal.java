/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl.bufferpool;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.bufferpool.BufferPool;
import com.hazelcast.internal.serialization.impl.bufferpool.BufferPoolFactory;
import com.hazelcast.util.ConcurrentReferenceHashMap;
import com.hazelcast.util.function.Supplier;
import java.lang.ref.WeakReference;
import java.util.Map;

public final class BufferPoolThreadLocal {
    private final ThreadLocal<WeakReference<BufferPool>> threadLocal = new ThreadLocal();
    private final InternalSerializationService serializationService;
    private final BufferPoolFactory bufferPoolFactory;
    private final Map<Thread, BufferPool> strongReferences = new ConcurrentReferenceHashMap<Thread, BufferPool>(ConcurrentReferenceHashMap.ReferenceType.WEAK, ConcurrentReferenceHashMap.ReferenceType.STRONG);
    private final Supplier<RuntimeException> notActiveExceptionSupplier;

    public BufferPoolThreadLocal(InternalSerializationService serializationService, BufferPoolFactory bufferPoolFactory, Supplier<RuntimeException> notActiveExceptionSupplier) {
        this.serializationService = serializationService;
        this.bufferPoolFactory = bufferPoolFactory;
        this.notActiveExceptionSupplier = notActiveExceptionSupplier;
    }

    public BufferPool get() {
        WeakReference<BufferPool> ref = this.threadLocal.get();
        if (ref == null) {
            BufferPool pool = this.bufferPoolFactory.create(this.serializationService);
            ref = new WeakReference<BufferPool>(pool);
            this.strongReferences.put(Thread.currentThread(), pool);
            this.threadLocal.set(ref);
            return pool;
        }
        BufferPool pool = (BufferPool)ref.get();
        if (pool == null) {
            throw this.notActiveExceptionSupplier.get();
        }
        return pool;
    }

    public void clear() {
        this.strongReferences.clear();
    }
}

