/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.protocol;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer;
import org.apache.http.protocol.HttpContext;

public abstract class AbstractAsyncRequestConsumer<T>
implements HttpAsyncRequestConsumer<T> {
    private final AtomicBoolean completed = new AtomicBoolean(false);
    private volatile T result;
    private volatile Exception ex;

    protected abstract void onRequestReceived(HttpRequest var1) throws HttpException, IOException;

    protected abstract void onEntityEnclosed(HttpEntity var1, ContentType var2) throws IOException;

    protected abstract void onContentReceived(ContentDecoder var1, IOControl var2) throws IOException;

    protected abstract T buildResult(HttpContext var1) throws Exception;

    protected abstract void releaseResources();

    protected void onClose() throws IOException {
    }

    @Override
    public final void requestReceived(HttpRequest request) throws HttpException, IOException {
        HttpEntity entity;
        this.onRequestReceived(request);
        if (request instanceof HttpEntityEnclosingRequest && (entity = ((HttpEntityEnclosingRequest)request).getEntity()) != null) {
            ContentType contentType = ContentType.getOrDefault(entity);
            this.onEntityEnclosed(entity, contentType);
        }
    }

    @Override
    public final void consumeContent(ContentDecoder decoder, IOControl ioControl) throws IOException {
        this.onContentReceived(decoder, ioControl);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void requestCompleted(HttpContext context) {
        if (this.completed.compareAndSet(false, true)) {
            try {
                this.result = this.buildResult(context);
            }
            catch (Exception ex) {
                this.ex = ex;
            }
            finally {
                this.releaseResources();
            }
        }
    }

    @Override
    public final void failed(Exception ex) {
        if (this.completed.compareAndSet(false, true)) {
            this.ex = ex;
            this.releaseResources();
        }
    }

    @Override
    public final void close() throws IOException {
        if (this.completed.compareAndSet(false, true)) {
            this.releaseResources();
            this.onClose();
        }
    }

    @Override
    public Exception getException() {
        return this.ex;
    }

    @Override
    public T getResult() {
        return this.result;
    }

    @Override
    public boolean isDone() {
        return this.completed.get();
    }
}

