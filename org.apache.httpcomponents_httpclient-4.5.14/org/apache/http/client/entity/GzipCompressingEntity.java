/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.Header
 *  org.apache.http.HttpEntity
 *  org.apache.http.entity.HttpEntityWrapper
 *  org.apache.http.message.BasicHeader
 *  org.apache.http.util.Args
 */
package org.apache.http.client.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.Args;

public class GzipCompressingEntity
extends HttpEntityWrapper {
    private static final String GZIP_CODEC = "gzip";

    public GzipCompressingEntity(HttpEntity entity) {
        super(entity);
    }

    public Header getContentEncoding() {
        return new BasicHeader("Content-Encoding", GZIP_CODEC);
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
        this.wrappedEntity.writeTo((OutputStream)gzip);
        gzip.close();
    }
}

