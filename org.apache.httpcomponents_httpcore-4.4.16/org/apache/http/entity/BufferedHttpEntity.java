/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.util.Args;

public class BufferedHttpEntity
extends HttpEntityWrapper {
    private final byte[] buffer;

    public BufferedHttpEntity(HttpEntity entity) throws IOException {
        super(entity);
        if (!entity.isRepeatable() || entity.getContentLength() < 0L) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            entity.writeTo(out);
            out.flush();
            this.buffer = out.toByteArray();
        } else {
            this.buffer = null;
        }
    }

    @Override
    public long getContentLength() {
        return this.buffer != null ? (long)this.buffer.length : super.getContentLength();
    }

    @Override
    public InputStream getContent() throws IOException {
        return this.buffer != null ? new ByteArrayInputStream(this.buffer) : super.getContent();
    }

    @Override
    public boolean isChunked() {
        return this.buffer == null && super.isChunked();
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }

    @Override
    public void writeTo(OutputStream outStream) throws IOException {
        Args.notNull(outStream, "Output stream");
        if (this.buffer != null) {
            outStream.write(this.buffer);
        } else {
            super.writeTo(outStream);
        }
    }

    @Override
    public boolean isStreaming() {
        return this.buffer == null && super.isStreaming();
    }
}

