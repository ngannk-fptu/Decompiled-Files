/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.util.Args;

public class InputStreamEntity
extends AbstractHttpEntity {
    private final InputStream content;
    private final long length;

    public InputStreamEntity(InputStream inStream) {
        this(inStream, -1L);
    }

    public InputStreamEntity(InputStream inStream, long length) {
        this(inStream, length, null);
    }

    public InputStreamEntity(InputStream inStream, ContentType contentType) {
        this(inStream, -1L, contentType);
    }

    public InputStreamEntity(InputStream inStream, long length, ContentType contentType) {
        this.content = Args.notNull(inStream, "Source input stream");
        this.length = length;
        if (contentType != null) {
            this.setContentType(contentType.toString());
        }
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public long getContentLength() {
        return this.length;
    }

    @Override
    public InputStream getContent() throws IOException {
        return this.content;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void writeTo(OutputStream outStream) throws IOException {
        block7: {
            Args.notNull(outStream, "Output stream");
            InputStream inStream = this.content;
            try {
                int readLen;
                byte[] buffer = new byte[4096];
                if (this.length < 0L) {
                    int readLen2;
                    while ((readLen2 = inStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, readLen2);
                    }
                    break block7;
                }
                for (long remaining = this.length; remaining > 0L; remaining -= (long)readLen) {
                    readLen = inStream.read(buffer, 0, (int)Math.min(4096L, remaining));
                    if (readLen == -1) {
                        break;
                    }
                    outStream.write(buffer, 0, readLen);
                }
            }
            finally {
                inStream.close();
            }
        }
    }

    @Override
    public boolean isStreaming() {
        return true;
    }
}

