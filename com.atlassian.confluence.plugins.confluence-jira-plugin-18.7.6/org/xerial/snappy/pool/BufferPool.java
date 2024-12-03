/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy.pool;

import java.nio.ByteBuffer;

public interface BufferPool {
    public byte[] allocateArray(int var1);

    public void releaseArray(byte[] var1);

    public ByteBuffer allocateDirect(int var1);

    public void releaseDirect(ByteBuffer var1);
}

