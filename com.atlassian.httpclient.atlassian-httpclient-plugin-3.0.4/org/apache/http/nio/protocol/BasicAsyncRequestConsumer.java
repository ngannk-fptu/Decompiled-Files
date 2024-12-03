/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.protocol;

import java.io.IOException;
import org.apache.http.ContentTooLongException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.entity.ContentBufferEntity;
import org.apache.http.nio.protocol.AbstractAsyncRequestConsumer;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.nio.util.SimpleInputBuffer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Asserts;

public class BasicAsyncRequestConsumer
extends AbstractAsyncRequestConsumer<HttpRequest> {
    private static final int MAX_INITIAL_BUFFER_SIZE = 262144;
    private volatile HttpRequest request;
    private volatile SimpleInputBuffer buf;

    @Override
    protected void onRequestReceived(HttpRequest request) throws IOException {
        this.request = request;
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
        ((HttpEntityEnclosingRequest)this.request).setEntity(new ContentBufferEntity(entity, this.buf));
    }

    @Override
    protected void onContentReceived(ContentDecoder decoder, IOControl ioControl) throws IOException {
        Asserts.notNull(this.buf, "Content buffer");
        this.buf.consumeContent(decoder);
    }

    @Override
    protected void releaseResources() {
        this.request = null;
        this.buf = null;
    }

    @Override
    protected HttpRequest buildResult(HttpContext context) {
        return this.request;
    }
}

