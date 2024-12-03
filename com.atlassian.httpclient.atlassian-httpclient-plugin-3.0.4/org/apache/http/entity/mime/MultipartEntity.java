/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.entity.mime;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Random;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.MultipartFormEntity;
import org.apache.http.entity.mime.content.ContentBody;

@Deprecated
public class MultipartEntity
implements HttpEntity {
    private static final char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private final MultipartEntityBuilder builder;
    private volatile MultipartFormEntity entity;

    public MultipartEntity(HttpMultipartMode mode, String boundary, Charset charset) {
        this.builder = new MultipartEntityBuilder().setMode(mode).setCharset(charset != null ? charset : MIME.DEFAULT_CHARSET).setBoundary(boundary);
        this.entity = null;
    }

    public MultipartEntity(HttpMultipartMode mode) {
        this(mode, null, null);
    }

    public MultipartEntity() {
        this(HttpMultipartMode.STRICT, null, null);
    }

    protected String generateContentType(String boundary, Charset charset) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("multipart/form-data; boundary=");
        buffer.append(boundary);
        if (charset != null) {
            buffer.append("; charset=");
            buffer.append(charset.name());
        }
        return buffer.toString();
    }

    protected String generateBoundary() {
        StringBuilder buffer = new StringBuilder();
        Random rand = new Random();
        int count = rand.nextInt(11) + 30;
        for (int i = 0; i < count; ++i) {
            buffer.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        return buffer.toString();
    }

    private MultipartFormEntity getEntity() {
        if (this.entity == null) {
            this.entity = this.builder.buildEntity();
        }
        return this.entity;
    }

    public void addPart(FormBodyPart bodyPart) {
        this.builder.addPart(bodyPart);
        this.entity = null;
    }

    public void addPart(String name, ContentBody contentBody) {
        this.addPart(new FormBodyPart(name, contentBody));
    }

    @Override
    public boolean isRepeatable() {
        return this.getEntity().isRepeatable();
    }

    @Override
    public boolean isChunked() {
        return this.getEntity().isChunked();
    }

    @Override
    public boolean isStreaming() {
        return this.getEntity().isStreaming();
    }

    @Override
    public long getContentLength() {
        return this.getEntity().getContentLength();
    }

    @Override
    public Header getContentType() {
        return this.getEntity().getContentType();
    }

    @Override
    public Header getContentEncoding() {
        return this.getEntity().getContentEncoding();
    }

    @Override
    public void consumeContent() throws IOException, UnsupportedOperationException {
        if (this.isStreaming()) {
            throw new UnsupportedOperationException("Streaming entity does not implement #consumeContent()");
        }
    }

    @Override
    public InputStream getContent() throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Multipart form entity does not implement #getContent()");
    }

    @Override
    public void writeTo(OutputStream outStream) throws IOException {
        this.getEntity().writeTo(outStream);
    }
}

