/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.HttpEntity
 *  org.apache.hc.core5.http.io.entity.HttpEntityWrapper
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.client5.http.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.HttpEntityWrapper;
import org.apache.hc.core5.util.Args;

public class GzipCompressingEntity
extends HttpEntityWrapper {
    private static final String GZIP_CODEC = "gzip";

    public GzipCompressingEntity(HttpEntity entity) {
        super(entity);
    }

    public String getContentEncoding() {
        return GZIP_CODEC;
    }

    public long getContentLength() {
        return -1L;
    }

    public boolean isChunked() {
        return true;
    }

    public InputStream getContent() throws IOException {
        throw new UnsupportedOperationException();
    }

    public void writeTo(OutputStream outStream) throws IOException {
        Args.notNull((Object)outStream, (String)"Output stream");
        GZIPOutputStream gzip = new GZIPOutputStream(outStream);
        super.writeTo((OutputStream)gzip);
        gzip.close();
    }
}

