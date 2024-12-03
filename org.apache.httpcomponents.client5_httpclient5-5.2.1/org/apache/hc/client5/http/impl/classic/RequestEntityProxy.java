/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.function.Supplier
 *  org.apache.hc.core5.http.ClassicHttpRequest
 *  org.apache.hc.core5.http.Header
 *  org.apache.hc.core5.http.HttpEntity
 */
package org.apache.hc.client5.http.impl.classic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import org.apache.hc.core5.function.Supplier;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;

class RequestEntityProxy
implements HttpEntity {
    private final HttpEntity original;
    private boolean consumed;

    static void enhance(ClassicHttpRequest request) {
        HttpEntity entity = request.getEntity();
        if (entity != null && !entity.isRepeatable() && !RequestEntityProxy.isEnhanced(entity)) {
            request.setEntity((HttpEntity)new RequestEntityProxy(entity));
        }
    }

    static boolean isEnhanced(HttpEntity entity) {
        return entity instanceof RequestEntityProxy;
    }

    RequestEntityProxy(HttpEntity original) {
        this.original = original;
    }

    public HttpEntity getOriginal() {
        return this.original;
    }

    public boolean isConsumed() {
        return this.consumed;
    }

    public boolean isRepeatable() {
        if (!this.consumed) {
            return true;
        }
        return this.original.isRepeatable();
    }

    public boolean isChunked() {
        return this.original.isChunked();
    }

    public long getContentLength() {
        return this.original.getContentLength();
    }

    public String getContentType() {
        return this.original.getContentType();
    }

    public String getContentEncoding() {
        return this.original.getContentEncoding();
    }

    public InputStream getContent() throws IOException, IllegalStateException {
        return this.original.getContent();
    }

    public void writeTo(OutputStream outStream) throws IOException {
        this.consumed = true;
        this.original.writeTo(outStream);
    }

    public boolean isStreaming() {
        return this.original.isStreaming();
    }

    public Supplier<List<? extends Header>> getTrailers() {
        return this.original.getTrailers();
    }

    public Set<String> getTrailerNames() {
        return this.original.getTrailerNames();
    }

    public void close() throws IOException {
        this.original.close();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("RequestEntityProxy{");
        sb.append(this.original);
        sb.append('}');
        return sb.toString();
    }
}

