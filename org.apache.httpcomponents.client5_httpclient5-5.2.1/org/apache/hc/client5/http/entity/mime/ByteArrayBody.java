/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.ContentType
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.client5.http.entity.mime;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.hc.client5.http.entity.mime.AbstractContentBody;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.util.Args;

public class ByteArrayBody
extends AbstractContentBody {
    private final byte[] data;
    private final String filename;

    public ByteArrayBody(byte[] data, ContentType contentType, String filename) {
        super(contentType);
        this.data = (byte[])Args.notNull((Object)data, (String)"data");
        this.filename = filename;
    }

    public ByteArrayBody(byte[] data, ContentType contentType) {
        this(data, contentType, null);
    }

    public ByteArrayBody(byte[] data, String filename) {
        this(data, ContentType.APPLICATION_OCTET_STREAM, filename);
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
    public long getContentLength() {
        return this.data.length;
    }
}

