/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.zip;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.tools.zip.ZipEncoding;

class FallbackZipEncoding
implements ZipEncoding {
    private final String charset;

    public FallbackZipEncoding() {
        this.charset = null;
    }

    public FallbackZipEncoding(String charset) {
        this.charset = charset;
    }

    @Override
    public boolean canEncode(String name) {
        return true;
    }

    @Override
    public ByteBuffer encode(String name) throws IOException {
        if (this.charset == null) {
            return ByteBuffer.wrap(name.getBytes());
        }
        return ByteBuffer.wrap(name.getBytes(this.charset));
    }

    @Override
    public String decode(byte[] data) throws IOException {
        if (this.charset == null) {
            return new String(data);
        }
        return new String(data, this.charset);
    }
}

