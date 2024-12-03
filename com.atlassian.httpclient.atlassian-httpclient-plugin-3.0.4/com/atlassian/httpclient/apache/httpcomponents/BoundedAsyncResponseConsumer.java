/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Ints
 */
package com.atlassian.httpclient.apache.httpcomponents;

import com.atlassian.httpclient.apache.httpcomponents.EntityTooLargeException;
import com.google.common.primitives.Ints;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.entity.ContentBufferEntity;
import org.apache.http.nio.protocol.AbstractAsyncResponseConsumer;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.nio.util.SimpleInputBuffer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Asserts;

public class BoundedAsyncResponseConsumer
extends AbstractAsyncResponseConsumer<HttpResponse> {
    private static final int MAX_INITIAL_BUFFER_SIZE = 262144;
    private final int maxEntitySize;
    private volatile BoundedInputBuffer buf;
    private volatile HttpResponse response;

    BoundedAsyncResponseConsumer(int maxEntitySize) {
        this.maxEntitySize = maxEntitySize;
    }

    @Override
    protected HttpResponse buildResult(HttpContext context) {
        return this.response;
    }

    @Override
    protected void onContentReceived(ContentDecoder decoder, IOControl ioctrl) throws IOException {
        Asserts.notNull(this.buf, "Content buffer");
        try {
            this.buf.consumeContent(decoder);
        }
        catch (BufferFullException e) {
            throw new EntityTooLargeException(this.response, "Entity content is too long; larger than " + this.maxEntitySize + " bytes");
        }
    }

    @Override
    protected void onEntityEnclosed(HttpEntity entity, ContentType contentType) throws IOException {
        int length = Math.min(Ints.saturatedCast((long)entity.getContentLength()), this.maxEntitySize);
        if ((long)length < 0L) {
            length = Math.min(4096, this.maxEntitySize);
        }
        int initialBufferSize = Math.min(262144, length);
        this.buf = new BoundedInputBuffer(initialBufferSize, this.maxEntitySize, new HeapByteBufferAllocator());
        Asserts.notNull(this.response, "response");
        this.response.setEntity(new ContentBufferEntity(entity, this.buf));
    }

    @Override
    protected void onResponseReceived(HttpResponse response) throws IOException {
        this.response = response;
    }

    @Override
    protected void releaseResources() {
        this.response = null;
        this.buf = null;
    }

    private static class BufferFullException
    extends RuntimeException {
        private BufferFullException() {
        }
    }

    private static class BoundedInputBuffer
    extends SimpleInputBuffer {
        private final int maxSize;

        BoundedInputBuffer(int initialSize, int maxSize, ByteBufferAllocator allocator) {
            super(Math.min(maxSize, initialSize), allocator);
            this.maxSize = maxSize;
        }

        @Override
        protected void expand() {
            int newCapacity;
            int capacity = this.buffer.capacity();
            int n = newCapacity = capacity < 2 ? 2 : capacity + (capacity >>> 1);
            if (newCapacity < capacity) {
                newCapacity = Integer.MAX_VALUE;
            }
            this.ensureCapacity(newCapacity);
        }

        @Override
        protected void ensureCapacity(int requiredCapacity) {
            if (this.buffer.capacity() == this.maxSize && requiredCapacity > this.maxSize) {
                throw new BufferFullException();
            }
            super.ensureCapacity(Math.min(requiredCapacity, this.maxSize));
        }
    }
}

