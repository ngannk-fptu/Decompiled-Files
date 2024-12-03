/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.icc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

class CachingInputStream
extends InputStream {
    private final InputStream is;
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    CachingInputStream(InputStream is) {
        this.is = is;
    }

    public byte[] getCache() {
        return this.baos.toByteArray();
    }

    @Override
    public int read() throws IOException {
        int result = this.is.read();
        this.baos.write(result);
        return result;
    }

    @Override
    public int available() throws IOException {
        return this.is.available();
    }

    @Override
    public void close() throws IOException {
        this.is.close();
    }
}

