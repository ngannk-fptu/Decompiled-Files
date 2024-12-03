/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.BERGenerator;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DEROutputStream;

public class BEROctetStringGenerator
extends BERGenerator {
    public BEROctetStringGenerator(OutputStream out) throws IOException {
        super(out);
        this.writeBERHeader(36);
    }

    public BEROctetStringGenerator(OutputStream out, int tagNo, boolean isExplicit) throws IOException {
        super(out, tagNo, isExplicit);
        this.writeBERHeader(36);
    }

    public OutputStream getOctetOutputStream() {
        return this.getOctetOutputStream(new byte[1000]);
    }

    public OutputStream getOctetOutputStream(byte[] buf) {
        return new BufferedBEROctetStream(buf);
    }

    private class BufferedBEROctetStream
    extends OutputStream {
        private byte[] _buf;
        private int _off;
        private DEROutputStream _derOut;

        BufferedBEROctetStream(byte[] buf) {
            this._buf = buf;
            this._off = 0;
            this._derOut = new DEROutputStream(BEROctetStringGenerator.this._out);
        }

        @Override
        public void write(int b) throws IOException {
            this._buf[this._off++] = (byte)b;
            if (this._off == this._buf.length) {
                DEROctetString.encode(this._derOut, true, this._buf, 0, this._buf.length);
                this._off = 0;
            }
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            int remaining;
            int bufLen = this._buf.length;
            int available = bufLen - this._off;
            if (len < available) {
                System.arraycopy(b, off, this._buf, this._off, len);
                this._off += len;
                return;
            }
            int count = 0;
            if (this._off > 0) {
                System.arraycopy(b, off, this._buf, this._off, available);
                count += available;
                DEROctetString.encode(this._derOut, true, this._buf, 0, bufLen);
            }
            while ((remaining = len - count) >= bufLen) {
                DEROctetString.encode(this._derOut, true, b, off + count, bufLen);
                count += bufLen;
            }
            System.arraycopy(b, off + count, this._buf, 0, remaining);
            this._off = remaining;
        }

        @Override
        public void close() throws IOException {
            if (this._off != 0) {
                DEROctetString.encode(this._derOut, true, this._buf, 0, this._off);
            }
            this._derOut.flushInternal();
            BEROctetStringGenerator.this.writeBEREnd();
        }
    }
}

