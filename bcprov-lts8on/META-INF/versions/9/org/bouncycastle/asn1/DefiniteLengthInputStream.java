/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.LimitedInputStream;
import org.bouncycastle.util.io.Streams;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class DefiniteLengthInputStream
extends LimitedInputStream {
    private static final byte[] EMPTY_BYTES = new byte[0];
    private final int _originalLength;
    private int _remaining;

    DefiniteLengthInputStream(InputStream in, int length, int limit) {
        super(in, limit);
        if (length <= 0) {
            if (length < 0) {
                throw new IllegalArgumentException("negative lengths not allowed");
            }
            this.setParentEofDetect(true);
        }
        this._originalLength = length;
        this._remaining = length;
    }

    int getRemaining() {
        return this._remaining;
    }

    @Override
    public int read() throws IOException {
        if (this._remaining == 0) {
            return -1;
        }
        int b = this._in.read();
        if (b < 0) {
            throw new EOFException("DEF length " + this._originalLength + " object truncated by " + this._remaining);
        }
        if (--this._remaining == 0) {
            this.setParentEofDetect(true);
        }
        return b;
    }

    @Override
    public int read(byte[] buf, int off, int len) throws IOException {
        if (this._remaining == 0) {
            return -1;
        }
        int toRead = Math.min(len, this._remaining);
        int numRead = this._in.read(buf, off, toRead);
        if (numRead < 0) {
            throw new EOFException("DEF length " + this._originalLength + " object truncated by " + this._remaining);
        }
        if ((this._remaining -= numRead) == 0) {
            this.setParentEofDetect(true);
        }
        return numRead;
    }

    void readAllIntoByteArray(byte[] buf) throws IOException {
        if (this._remaining != buf.length) {
            throw new IllegalArgumentException("buffer length not right for data");
        }
        if (this._remaining == 0) {
            return;
        }
        int limit = this.getLimit();
        if (this._remaining >= limit) {
            throw new IOException("corrupted stream - out of bounds length found: " + this._remaining + " >= " + limit);
        }
        if ((this._remaining -= Streams.readFully(this._in, buf, 0, buf.length)) != 0) {
            throw new EOFException("DEF length " + this._originalLength + " object truncated by " + this._remaining);
        }
        this.setParentEofDetect(true);
    }

    byte[] toByteArray() throws IOException {
        if (this._remaining == 0) {
            return EMPTY_BYTES;
        }
        int limit = this.getLimit();
        if (this._remaining >= limit) {
            throw new IOException("corrupted stream - out of bounds length found: " + this._remaining + " >= " + limit);
        }
        byte[] bytes = new byte[this._remaining];
        if ((this._remaining -= Streams.readFully(this._in, bytes, 0, bytes.length)) != 0) {
            throw new EOFException("DEF length " + this._originalLength + " object truncated by " + this._remaining);
        }
        this.setParentEofDetect(true);
        return bytes;
    }
}

