/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.io.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.function.Supplier;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EmptyInputStream;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public final class NullEntity
implements HttpEntity {
    public static final NullEntity INSTANCE = new NullEntity();

    private NullEntity() {
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }

    @Override
    public InputStream getContent() throws IOException, UnsupportedOperationException {
        return EmptyInputStream.INSTANCE;
    }

    @Override
    public void writeTo(OutputStream outStream) throws IOException {
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public Supplier<List<? extends Header>> getTrailers() {
        return null;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public long getContentLength() {
        return 0L;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public String getContentEncoding() {
        return null;
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public Set<String> getTrailerNames() {
        return Collections.emptySet();
    }
}

