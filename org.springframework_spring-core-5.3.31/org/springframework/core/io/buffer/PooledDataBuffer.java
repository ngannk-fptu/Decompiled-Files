/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.io.buffer;

import org.springframework.core.io.buffer.DataBuffer;

public interface PooledDataBuffer
extends DataBuffer {
    public boolean isAllocated();

    public PooledDataBuffer retain();

    public PooledDataBuffer touch(Object var1);

    public boolean release();
}

