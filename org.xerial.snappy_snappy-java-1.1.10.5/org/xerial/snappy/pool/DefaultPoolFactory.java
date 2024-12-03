/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy.pool;

import org.xerial.snappy.pool.BufferPool;
import org.xerial.snappy.pool.CachingBufferPool;
import org.xerial.snappy.pool.QuiescentBufferPool;

public final class DefaultPoolFactory {
    public static final String DISABLE_CACHING_PROPERTY = "org.xerial.snappy.pool.disable";
    private static volatile BufferPool defaultPool = "true".equalsIgnoreCase(System.getProperty("org.xerial.snappy.pool.disable")) ? QuiescentBufferPool.getInstance() : CachingBufferPool.getInstance();

    public static BufferPool getDefaultPool() {
        return defaultPool;
    }

    public static void setDefaultPool(BufferPool bufferPool) {
        if (bufferPool == null) {
            throw new IllegalArgumentException("pool is null");
        }
        defaultPool = bufferPool;
    }
}

