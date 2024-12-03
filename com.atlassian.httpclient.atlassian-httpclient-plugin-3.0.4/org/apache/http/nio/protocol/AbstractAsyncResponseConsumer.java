/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.protocol;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;

public abstract class AbstractAsyncResponseConsumer<T>
implements HttpAsyncResponseConsumer<T> {
    private final AtomicBoolean completed = new AtomicBoolean(false);
    private volatile T result;
    private volatile Exception ex;

    protected abstract void onResponseReceived(HttpResponse var1) throws HttpException, IOException;

    protected abstract void onContentReceived(ContentDecoder var1, IOControl var2) throws IOException;

    protected abstract void onEntityEnclosed(HttpEntity var1, ContentType var2) throws IOException;

    protected abstract T buildResult(HttpContext var1) throws Exception;

    protected abstract void releaseResources();

    protected void onClose() throws IOException {
    }

    protected ContentType getContentType(HttpEntity entity) {
        return entity != null ? ContentType.getOrDefault(entity) : null;
    }

    @Override
    public final void responseReceived(HttpResponse response) throws IOException, HttpException {
        this.onResponseReceived(response);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            this.onEntityEnclosed(entity, this.getContentType(entity));
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
    public final void responseCompleted(HttpContext context) {
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
    public final boolean cancel() {
        if (this.completed.compareAndSet(false, true)) {
            this.releaseResources();
            return true;
        }
        return false;
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

