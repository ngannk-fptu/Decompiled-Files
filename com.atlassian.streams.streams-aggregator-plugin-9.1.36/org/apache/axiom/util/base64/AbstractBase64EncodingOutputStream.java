/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.base64;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.axiom.util.base64.Base64Constants;

public abstract class AbstractBase64EncodingOutputStream
extends OutputStream {
    private final byte[] in = new byte[3];
    private final byte[] out = new byte[4];
    private int rest;
    private boolean completed;

    public final void write(byte[] b, int off, int len) throws IOException {
        if (this.completed) {
            throw new IOException("Attempt to write data after base64 encoding has been completed");
        }
        if (this.rest > 0) {
            while (len > 0 && this.rest < 3) {
                this.in[this.rest++] = b[off++];
                --len;
            }
            if (this.rest == 3) {
                this.encode(this.in, 0, 3);
                this.rest = 0;
            }
        }
        while (len >= 3) {
            this.encode(b, off, 3);
            off += 3;
            len -= 3;
        }
        while (len > 0) {
            this.in[this.rest++] = b[off++];
            --len;
        }
    }

    public final void write(int b) throws IOException {
        this.in[this.rest++] = (byte)b;
        if (this.rest == 3) {
            this.encode(this.in, 0, 3);
            this.rest = 0;
        }
    }

    public final void complete() throws IOException {
        if (!this.completed) {
            if (this.rest > 0) {
                this.encode(this.in, 0, this.rest);
            }
            this.flushBuffer();
            this.completed = true;
        }
    }

    private void encode(byte[] data, int off, int len) throws IOException {
        if (len == 1) {
            int i = data[off] & 0xFF;
            this.out[0] = Base64Constants.S_BASE64CHAR[i >> 2];
            this.out[1] = Base64Constants.S_BASE64CHAR[i << 4 & 0x3F];
            this.out[2] = 61;
            this.out[3] = 61;
        } else if (len == 2) {
            int i = ((data[off] & 0xFF) << 8) + (data[off + 1] & 0xFF);
            this.out[0] = Base64Constants.S_BASE64CHAR[i >> 10];
            this.out[1] = Base64Constants.S_BASE64CHAR[i >> 4 & 0x3F];
            this.out[2] = Base64Constants.S_BASE64CHAR[i << 2 & 0x3F];
            this.out[3] = 61;
        } else {
            int i = ((data[off] & 0xFF) << 16) + ((data[off + 1] & 0xFF) << 8) + (data[off + 2] & 0xFF);
            this.out[0] = Base64Constants.S_BASE64CHAR[i >> 18];
            this.out[1] = Base64Constants.S_BASE64CHAR[i >> 12 & 0x3F];
            this.out[2] = Base64Constants.S_BASE64CHAR[i >> 6 & 0x3F];
            this.out[3] = Base64Constants.S_BASE64CHAR[i & 0x3F];
        }
        this.doWrite(this.out);
    }

    public final void flush() throws IOException {
        this.flushBuffer();
        this.doFlush();
    }

    public final void close() throws IOException {
        this.complete();
        this.doClose();
    }

    protected abstract void doWrite(byte[] var1) throws IOException;

    protected abstract void flushBuffer() throws IOException;

    protected abstract void doFlush() throws IOException;

    protected abstract void doClose() throws IOException;
}

