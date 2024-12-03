/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.encoders.Base64Encoder
 */
package org.bouncycastle.mime.encoding;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.util.encoders.Base64Encoder;

public class Base64OutputStream
extends FilterOutputStream {
    private static final Base64Encoder ENCODER = new Base64Encoder();
    private static final int INBUF_SIZE = 54;
    private static final int OUTBUF_SIZE = 74;
    private final byte[] inBuf = new byte[54];
    private final byte[] outBuf = new byte[74];
    private int inPos = 0;

    public Base64OutputStream(OutputStream stream) {
        super(stream);
        this.outBuf[72] = 13;
        this.outBuf[73] = 10;
    }

    @Override
    public void write(int b) throws IOException {
        this.inBuf[this.inPos++] = (byte)b;
        if (this.inPos == 54) {
            this.encodeBlock(this.inBuf, 0);
            this.inPos = 0;
        }
    }

    @Override
    public void write(byte[] buf, int off, int len) throws IOException {
        int remaining;
        int available = 54 - this.inPos;
        if (len < available) {
            System.arraycopy(buf, off, this.inBuf, this.inPos, len);
            this.inPos += len;
            return;
        }
        int count = 0;
        if (this.inPos > 0) {
            System.arraycopy(buf, off, this.inBuf, this.inPos, available);
            count += available;
            this.encodeBlock(this.inBuf, 0);
        }
        while ((remaining = len - count) >= 54) {
            this.encodeBlock(buf, off + count);
            count += 54;
        }
        System.arraycopy(buf, off + count, this.inBuf, 0, remaining);
        this.inPos = remaining;
    }

    @Override
    public void write(byte[] buf) throws IOException {
        this.write(buf, 0, buf.length);
    }

    @Override
    public void close() throws IOException {
        if (this.inPos > 0) {
            int outPos = ENCODER.encode(this.inBuf, 0, this.inPos, this.outBuf, 0);
            this.inPos = 0;
            this.outBuf[outPos++] = 13;
            this.outBuf[outPos++] = 10;
            this.out.write(this.outBuf, 0, outPos);
        }
        this.out.close();
    }

    private void encodeBlock(byte[] buf, int off) throws IOException {
        ENCODER.encode(buf, off, 54, this.outBuf, 0);
        this.out.write(this.outBuf, 0, 74);
    }
}

