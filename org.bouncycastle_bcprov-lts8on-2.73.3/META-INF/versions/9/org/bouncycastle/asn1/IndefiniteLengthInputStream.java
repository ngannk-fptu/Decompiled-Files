/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.LimitedInputStream;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class IndefiniteLengthInputStream
extends LimitedInputStream {
    private int _b1;
    private int _b2;
    private boolean _eofReached = false;
    private boolean _eofOn00 = true;

    IndefiniteLengthInputStream(InputStream in, int limit) throws IOException {
        super(in, limit);
        this._b1 = in.read();
        this._b2 = in.read();
        if (this._b2 < 0) {
            throw new EOFException();
        }
        this.checkForEof();
    }

    void setEofOn00(boolean eofOn00) {
        this._eofOn00 = eofOn00;
        this.checkForEof();
    }

    private boolean checkForEof() {
        if (!this._eofReached && this._eofOn00 && this._b1 == 0 && this._b2 == 0) {
            this._eofReached = true;
            this.setParentEofDetect(true);
        }
        return this._eofReached;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (this._eofOn00 || len < 3) {
            return super.read(b, off, len);
        }
        if (this._eofReached) {
            return -1;
        }
        int numRead = this._in.read(b, off + 2, len - 2);
        if (numRead < 0) {
            throw new EOFException();
        }
        b[off] = (byte)this._b1;
        b[off + 1] = (byte)this._b2;
        this._b1 = this._in.read();
        this._b2 = this._in.read();
        if (this._b2 < 0) {
            throw new EOFException();
        }
        return numRead + 2;
    }

    @Override
    public int read() throws IOException {
        if (this.checkForEof()) {
            return -1;
        }
        int b = this._in.read();
        if (b < 0) {
            throw new EOFException();
        }
        int v = this._b1;
        this._b1 = this._b2;
        this._b2 = b;
        return v;
    }
}

