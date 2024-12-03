/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy.buffer;

public interface BufferAllocator {
    public byte[] allocate(int var1);

    public void release(byte[] var1);
}

