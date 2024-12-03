/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import org.apache.commons.collections.ArrayStack;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.buffer.BlockingBuffer;
import org.apache.commons.collections.buffer.BoundedBuffer;
import org.apache.commons.collections.buffer.PredicatedBuffer;
import org.apache.commons.collections.buffer.SynchronizedBuffer;
import org.apache.commons.collections.buffer.TransformedBuffer;
import org.apache.commons.collections.buffer.TypedBuffer;
import org.apache.commons.collections.buffer.UnmodifiableBuffer;

public class BufferUtils {
    public static final Buffer EMPTY_BUFFER = UnmodifiableBuffer.decorate(new ArrayStack(1));

    public static Buffer synchronizedBuffer(Buffer buffer) {
        return SynchronizedBuffer.decorate(buffer);
    }

    public static Buffer blockingBuffer(Buffer buffer) {
        return BlockingBuffer.decorate(buffer);
    }

    public static Buffer blockingBuffer(Buffer buffer, long timeoutMillis) {
        return BlockingBuffer.decorate(buffer, timeoutMillis);
    }

    public static Buffer boundedBuffer(Buffer buffer, int maximumSize) {
        return BoundedBuffer.decorate(buffer, maximumSize);
    }

    public static Buffer boundedBuffer(Buffer buffer, int maximumSize, long timeoutMillis) {
        return BoundedBuffer.decorate(buffer, maximumSize, timeoutMillis);
    }

    public static Buffer unmodifiableBuffer(Buffer buffer) {
        return UnmodifiableBuffer.decorate(buffer);
    }

    public static Buffer predicatedBuffer(Buffer buffer, Predicate predicate) {
        return PredicatedBuffer.decorate(buffer, predicate);
    }

    public static Buffer typedBuffer(Buffer buffer, Class type) {
        return TypedBuffer.decorate(buffer, type);
    }

    public static Buffer transformedBuffer(Buffer buffer, Transformer transformer) {
        return TransformedBuffer.decorate(buffer, transformer);
    }
}

