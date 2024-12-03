/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.protocol;

import java.io.IOException;
import org.apache.http.ContentTooLongException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.entity.ContentBufferEntity;
import org.apache.http.nio.protocol.AbstractAsyncResponseConsumer;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.nio.util.SimpleInputBuffer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Asserts;

public class BasicAsyncResponseConsumer
extends AbstractAsyncResponseConsumer<HttpResponse> {
    private static final int MAX_INITIAL_BUFFER_SIZE = 262144;
    private volatile HttpResponse response;
    private volatile SimpleInputBuffer buf;

    @Override
    protected void onResponseReceived(HttpResponse response) throws IOException {
        this.response = response;
    }

    @Override
    protected void onEntityEnclosed(HttpEntity entity, ContentType contentType) throws IOException {
        long len = entity.getContentLength();
        if (len > Integer.MAX_VALUE) {
            throw new ContentTooLongException("Entity content is too long: %,d", len);
        }
        if (len < 0L) {
            len = 4096L;
        }
        int initialBufferSize = Math.min((int)len, 262144);
        this.buf = new SimpleInputBuffer(initialBufferSize, new HeapByteBufferAllocator());
        this.response.setEntity(new ContentBufferEntity(entity, this.buf));
    }

    @Override
    protected void onContentReceived(ContentDecoder decoder, IOControl ioControl) throws IOException {
        Asserts.notNull(this.buf, "Content buffer");
        this.buf.consumeContent(decoder);
    }

    @Override
    protected void releaseResources() {
        this.response = null;
        this.buf = null;
    }

    @Override
    protected HttpResponse buildResult(HttpContext context) {
        return this.response;
    }
}

