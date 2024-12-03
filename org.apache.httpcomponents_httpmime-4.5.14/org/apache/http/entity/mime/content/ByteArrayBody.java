/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.entity.ContentType
 *  org.apache.http.util.Args
 */
package org.apache.http.entity.mime.content;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.util.Args;

public class ByteArrayBody
extends AbstractContentBody {
    private final byte[] data;
    private final String filename;

    @Deprecated
    public ByteArrayBody(byte[] data, String mimeType, String filename) {
        this(data, ContentType.create((String)mimeType), filename);
    }

    public ByteArrayBody(byte[] data, ContentType contentType, String filename) {
        super(contentType);
        Args.notNull((Object)data, (String)"byte[]");
        this.data = data;
        this.filename = filename;
    }

    public ByteArrayBody(byte[] data, String filename) {
        this(data, "application/octet-stream", filename);
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        out.write(this.data);
    }

    @Override
    public String getCharset() {
        return null;
    }

    @Override
    public String getTransferEncoding() {
        return "binary";
    }

    @Override
    public long getContentLength() {
        return this.data.length;
    }
}

