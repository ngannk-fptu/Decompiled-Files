/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.methods;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.httpclient.methods.RequestEntity;

public class ByteArrayRequestEntity
implements RequestEntity {
    private byte[] content;
    private String contentType;

    public ByteArrayRequestEntity(byte[] content) {
        this(content, null);
    }

    public ByteArrayRequestEntity(byte[] content, String contentType) {
        if (content == null) {
            throw new IllegalArgumentException("The content cannot be null");
        }
        this.content = content;
        this.contentType = contentType;
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public void writeRequest(OutputStream out) throws IOException {
        out.write(this.content);
    }

    @Override
    public long getContentLength() {
        return this.content.length;
    }

    public byte[] getContent() {
        return this.content;
    }
}

