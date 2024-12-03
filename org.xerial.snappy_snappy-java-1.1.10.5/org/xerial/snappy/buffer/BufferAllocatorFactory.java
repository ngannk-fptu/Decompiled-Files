/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy.buffer;

import org.xerial.snappy.buffer.BufferAllocator;

public interface BufferAllocatorFactory {
    public BufferAllocator getBufferAllocator(int var1);
}

