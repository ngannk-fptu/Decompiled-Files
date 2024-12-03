/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient.methods;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class InputStreamRequestEntity
implements RequestEntity {
    public static final int CONTENT_LENGTH_AUTO = -2;
    private static final Log LOG = LogFactory.getLog(InputStreamRequestEntity.class);
    private long contentLength;
    private InputStream content;
    private byte[] buffer = null;
    private String contentType;

    public InputStreamRequestEntity(InputStream content) {
        this(content, null);
    }

    public InputStreamRequestEntity(InputStream content, String contentType) {
        this(content, -2L, contentType);
    }

    public InputStreamRequestEntity(InputStream content, long contentLength) {
        this(content, contentLength, null);
    }

    public InputStreamRequestEntity(InputStream content, long contentLength, String contentType) {
        if (content == null) {
            throw new IllegalArgumentException("The content cannot be null");
        }
        this.content = content;
        this.contentLength = contentLength;
        this.contentType = contentType;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    private void bufferContent() {
        if (this.buffer != null) {
            return;
        }
        if (this.content != null) {
            try {
                ByteArrayOutputStream tmp = new ByteArrayOutputStream();
                byte[] data = new byte[4096];
                int l = 0;
                while ((l = this.content.read(data)) >= 0) {
                    tmp.write(data, 0, l);
                }
                this.buffer = tmp.toByteArray();
                this.content = null;
                this.contentLength = this.buffer.length;
            }
            catch (IOException e) {
                LOG.error((Object)e.getMessage(), (Throwable)e);
                this.buffer = null;
                this.content = null;
                this.contentLength = 0L;
            }
        }
    }

    @Override
    public boolean isRepeatable() {
        return this.buffer != null;
    }

    @Override
    public void writeRequest(OutputStream out) throws IOException {
        if (this.content != null) {
            byte[] tmp = new byte[4096];
            int total = 0;
            int i = 0;
            while ((i = this.content.read(tmp)) >= 0) {
                out.write(tmp, 0, i);
                total += i;
            }
        } else if (this.buffer != null) {
            out.write(this.buffer);
        } else {
            throw new IllegalStateException("Content must be set before entity is written");
        }
    }

    @Override
    public long getContentLength() {
        if (this.contentLength == -2L && this.buffer == null) {
            this.bufferContent();
        }
        return this.contentLength;
    }

    public InputStream getContent() {
        return this.content;
    }
}

