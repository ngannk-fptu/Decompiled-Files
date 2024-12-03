/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.BufferUtil
 */
package org.eclipse.jetty.io;

import java.nio.ByteBuffer;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.RetainableByteBufferPool;
import org.eclipse.jetty.util.BufferUtil;

public class NullByteBufferPool
implements ByteBufferPool {
    private final RetainableByteBufferPool _retainableByteBufferPool = RetainableByteBufferPool.from(this);

    @Override
    public ByteBuffer acquire(int size, boolean direct) {
        if (direct) {
            return BufferUtil.allocateDirect((int)size);
        }
        return BufferUtil.allocate((int)size);
    }

    @Override
    public void release(ByteBuffer buffer) {
        BufferUtil.clear((ByteBuffer)buffer);
    }

    @Override
    public RetainableByteBufferPool asRetainableByteBufferPool() {
        return this._retainableByteBufferPool;
    }
}

