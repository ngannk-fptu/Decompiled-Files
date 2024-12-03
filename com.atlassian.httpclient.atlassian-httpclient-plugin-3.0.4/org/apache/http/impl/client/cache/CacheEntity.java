/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.client.cache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.impl.client.cache.IOUtils;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
class CacheEntity
implements HttpEntity,
Serializable {
    private static final long serialVersionUID = -3467082284120936233L;
    private final HttpCacheEntry cacheEntry;

    public CacheEntity(HttpCacheEntry cacheEntry) {
        this.cacheEntry = cacheEntry;
    }

    @Override
    public Header getContentType() {
        return this.cacheEntry.getFirstHeader("Content-Type");
    }

    @Override
    public Header getContentEncoding() {
        return this.cacheEntry.getFirstHeader("Content-Encoding");
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }

    @Override
    public long getContentLength() {
        return this.cacheEntry.getResource().length();
    }

    @Override
    public InputStream getContent() throws IOException {
        return this.cacheEntry.getResource().getInputStream();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void writeTo(OutputStream outStream) throws IOException {
        Args.notNull(outStream, "Output stream");
        InputStream inStream = this.cacheEntry.getResource().getInputStream();
        try {
            IOUtils.copy(inStream, outStream);
        }
        finally {
            inStream.close();
        }
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public void consumeContent() throws IOException {
    }
}

