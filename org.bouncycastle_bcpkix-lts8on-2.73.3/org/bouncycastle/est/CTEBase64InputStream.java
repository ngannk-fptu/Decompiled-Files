/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.encoders.Base64
 */
package org.bouncycastle.est;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.util.encoders.Base64;

class CTEBase64InputStream
extends InputStream {
    protected final InputStream src;
    protected final byte[] rawBuf = new byte[1024];
    protected final byte[] data = new byte[768];
    protected final OutputStream dataOutputStream;
    protected final Long max;
    protected int rp;
    protected int wp;
    protected boolean end;
    protected long read;

    public CTEBase64InputStream(InputStream src) {
        this(src, null);
    }

    public CTEBase64InputStream(InputStream src, Long limit) {
        this.src = src;
        this.dataOutputStream = new OutputStream(){

            @Override
            public void write(int b) throws IOException {
                CTEBase64InputStream.this.data[CTEBase64InputStream.this.wp++] = (byte)b;
            }
        };
        this.max = limit;
    }

    protected int pullFromSrc() throws IOException {
        int j = 0;
        int c = 0;
        do {
            if (this.max != null && this.read > this.max) {
                return -1;
            }
            j = this.src.read();
            if (j >= 33 || j == 13 || j == 10) {
                if (c >= this.rawBuf.length) {
                    throw new IOException("Content Transfer Encoding, base64 line length > 1024");
                }
                this.rawBuf[c++] = (byte)j;
                ++this.read;
                continue;
            }
            if (j < 0) continue;
            ++this.read;
        } while (j > -1 && c < this.rawBuf.length && j != 10);
        if (c > 0) {
            try {
                Base64.decode((byte[])this.rawBuf, (int)0, (int)c, (OutputStream)this.dataOutputStream);
            }
            catch (Exception ex) {
                throw new IOException("Decode Base64 Content-Transfer-Encoding: " + ex);
            }
        } else if (j == -1) {
            return -1;
        }
        return this.wp;
    }

    @Override
    public int read() throws IOException {
        if (this.rp == this.wp) {
            this.rp = 0;
            this.wp = 0;
            int i = this.pullFromSrc();
            if (i == -1) {
                return i;
            }
        }
        return this.data[this.rp++] & 0xFF;
    }

    @Override
    public void close() throws IOException {
        this.src.close();
    }
}

