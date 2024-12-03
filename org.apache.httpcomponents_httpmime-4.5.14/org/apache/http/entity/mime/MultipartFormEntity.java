/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.ContentTooLongException
 *  org.apache.http.Header
 *  org.apache.http.HttpEntity
 *  org.apache.http.entity.ContentType
 *  org.apache.http.message.BasicHeader
 */
package org.apache.http.entity.mime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.ContentTooLongException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.AbstractMultipartForm;
import org.apache.http.message.BasicHeader;

class MultipartFormEntity
implements HttpEntity {
    private final AbstractMultipartForm multipart;
    private final Header contentType;
    private final long contentLength;

    MultipartFormEntity(AbstractMultipartForm multipart, ContentType contentType, long contentLength) {
        this.multipart = multipart;
        this.contentType = new BasicHeader("Content-Type", contentType.toString());
        this.contentLength = contentLength;
    }

    AbstractMultipartForm getMultipart() {
        return this.multipart;
    }

    public boolean isRepeatable() {
        return this.contentLength != -1L;
    }

    public boolean isChunked() {
        return !this.isRepeatable();
    }

    public boolean isStreaming() {
        return !this.isRepeatable();
    }

    public long getContentLength() {
        return this.contentLength;
    }

    public Header getContentType() {
        return this.contentType;
    }

    public Header getContentEncoding() {
        return null;
    }

    public void consumeContent() {
    }

    public InputStream getContent() throws IOException {
        if (this.contentLength < 0L) {
            throw new ContentTooLongException("Content length is unknown");
        }
        if (this.contentLength > 25600L) {
            throw new ContentTooLongException("Content length is too long: " + this.contentLength);
        }
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        this.writeTo(outStream);
        outStream.flush();
        return new ByteArrayInputStream(outStream.toByteArray());
    }

    public void writeTo(OutputStream outStream) throws IOException {
        this.multipart.writeTo(outStream);
    }
}

