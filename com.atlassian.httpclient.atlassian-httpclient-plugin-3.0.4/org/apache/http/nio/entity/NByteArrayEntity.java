/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.entity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.entity.HttpAsyncContentProducer;
import org.apache.http.nio.entity.ProducingNHttpEntity;
import org.apache.http.util.Args;

public class NByteArrayEntity
extends AbstractHttpEntity
implements HttpAsyncContentProducer,
ProducingNHttpEntity {
    private final byte[] b;
    private final int off;
    private final int len;
    private final ByteBuffer buf;
    @Deprecated
    protected final byte[] content;
    @Deprecated
    protected final ByteBuffer buffer;

    public NByteArrayEntity(byte[] b, ContentType contentType) {
        Args.notNull(b, "Source byte array");
        this.b = b;
        this.off = 0;
        this.len = b.length;
        this.buf = ByteBuffer.wrap(b);
        this.content = b;
        this.buffer = this.buf;
        if (contentType != null) {
            this.setContentType(contentType.toString());
        }
    }

    public NByteArrayEntity(byte[] b, int off, int len, ContentType contentType) {
        Args.notNull(b, "Source byte array");
        if (off < 0 || off > b.length || len < 0 || off + len < 0 || off + len > b.length) {
            throw new IndexOutOfBoundsException("off: " + off + " len: " + len + " b.length: " + b.length);
        }
        this.b = b;
        this.off = off;
        this.len = len;
        this.buf = ByteBuffer.wrap(b, off, len);
        this.content = b;
        this.buffer = this.buf;
        if (contentType != null) {
            this.setContentType(contentType.toString());
        }
    }

    public NByteArrayEntity(byte[] b) {
        this(b, null);
    }

    public NByteArrayEntity(byte[] b, int off, int len) {
        this(b, off, len, null);
    }

    @Override
    public void close() {
        this.buf.rewind();
    }

    @Override
    @Deprecated
    public void finish() {
        this.close();
    }

    @Override
    public void produceContent(ContentEncoder encoder, IOControl ioControl) throws IOException {
        encoder.write(this.buf);
        if (!this.buf.hasRemaining()) {
            encoder.complete();
        }
    }

    @Override
    public long getContentLength() {
        return this.len;
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public InputStream getContent() {
        return new ByteArrayInputStream(this.b, this.off, this.len);
    }

    @Override
    public void writeTo(OutputStream outStream) throws IOException {
        Args.notNull(outStream, "Output stream");
        outStream.write(this.b, this.off, this.len);
        outStream.flush();
    }
}

