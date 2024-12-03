/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.entity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.entity.HttpAsyncContentProducer;
import org.apache.http.nio.entity.ProducingNHttpEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;

public class NStringEntity
extends AbstractHttpEntity
implements HttpAsyncContentProducer,
ProducingNHttpEntity {
    private final byte[] b;
    private final ByteBuffer buf;
    @Deprecated
    protected final byte[] content;
    @Deprecated
    protected final ByteBuffer buffer;

    public NStringEntity(String s, ContentType contentType) {
        Charset charset;
        Args.notNull(s, "Source string");
        Charset charset2 = charset = contentType != null ? contentType.getCharset() : null;
        if (charset == null) {
            charset = HTTP.DEF_CONTENT_CHARSET;
        }
        this.b = s.getBytes(charset);
        this.buf = ByteBuffer.wrap(this.b);
        this.content = this.b;
        this.buffer = this.buf;
        if (contentType != null) {
            this.setContentType(contentType.toString());
        }
    }

    public NStringEntity(String s, String charset) throws UnsupportedEncodingException {
        this(s, ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), charset));
    }

    public NStringEntity(String s, Charset charset) {
        this(s, ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), charset));
    }

    public NStringEntity(String s) throws UnsupportedEncodingException {
        this(s, ContentType.DEFAULT_TEXT);
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }

    @Override
    public long getContentLength() {
        return this.b.length;
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
    public boolean isStreaming() {
        return false;
    }

    @Override
    public InputStream getContent() {
        return new ByteArrayInputStream(this.b);
    }

    @Override
    public void writeTo(OutputStream outStream) throws IOException {
        Args.notNull(outStream, "Output stream");
        outStream.write(this.b);
        outStream.flush();
    }
}

