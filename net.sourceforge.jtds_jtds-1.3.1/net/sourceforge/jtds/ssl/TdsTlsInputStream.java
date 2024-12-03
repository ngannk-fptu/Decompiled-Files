/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.ssl;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

class TdsTlsInputStream
extends FilterInputStream {
    int bytesOutstanding;
    final byte[] readBuffer = new byte[6144];
    InputStream bufferStream;
    boolean pureSSL;

    public TdsTlsInputStream(InputStream in) {
        super(in);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int ret;
        if (this.pureSSL && this.bufferStream == null) {
            return this.in.read(b, off, len);
        }
        if (!this.pureSSL && this.bufferStream == null) {
            this.primeBuffer();
        }
        this.bytesOutstanding -= (ret = this.bufferStream.read(b, off, len)) < 0 ? 0 : ret;
        if (this.bytesOutstanding == 0) {
            this.bufferStream = null;
        }
        return ret;
    }

    private void primeBuffer() throws IOException {
        int len;
        this.readFully(this.readBuffer, 0, 5);
        if (this.readBuffer[0] == 4 || this.readBuffer[0] == 18) {
            len = (this.readBuffer[2] & 0xFF) << 8 | this.readBuffer[3] & 0xFF;
            this.readFully(this.readBuffer, 5, 3);
            this.readFully(this.readBuffer, 0, len -= 8);
        } else {
            len = (this.readBuffer[3] & 0xFF) << 8 | this.readBuffer[4] & 0xFF;
            this.readFully(this.readBuffer, 5, len - 5);
            this.pureSSL = true;
        }
        this.bufferStream = new ByteArrayInputStream(this.readBuffer, 0, len);
        this.bytesOutstanding = len;
    }

    private void readFully(byte[] b, int off, int len) throws IOException {
        int res = 0;
        while (len > 0 && (res = this.in.read(b, off, len)) >= 0) {
            off += res;
            len -= res;
        }
        if (res < 0) {
            throw new IOException();
        }
    }
}

