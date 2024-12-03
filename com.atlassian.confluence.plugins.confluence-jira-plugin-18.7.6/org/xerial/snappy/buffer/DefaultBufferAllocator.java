/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy.buffer;

import org.xerial.snappy.buffer.BufferAllocator;
import org.xerial.snappy.buffer.BufferAllocatorFactory;

public class DefaultBufferAllocator
implements BufferAllocator {
    public static BufferAllocatorFactory factory = new BufferAllocatorFactory(){
        public BufferAllocator singleton = new DefaultBufferAllocator();

        @Override
        public BufferAllocator getBufferAllocator(int n) {
            return this.singleton;
        }
    };

    @Override
    public byte[] allocate(int n) {
        return new byte[n];
    }

    @Override
    public void release(byte[] byArray) {
    }
}

