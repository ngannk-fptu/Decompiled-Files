/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.util;

import java.nio.ByteBuffer;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.nio.util.ByteBufferAllocator;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class DirectByteBufferAllocator
implements ByteBufferAllocator {
    public static final DirectByteBufferAllocator INSTANCE = new DirectByteBufferAllocator();

    @Override
    public ByteBuffer allocate(int size) {
        return ByteBuffer.allocateDirect(size);
    }
}

