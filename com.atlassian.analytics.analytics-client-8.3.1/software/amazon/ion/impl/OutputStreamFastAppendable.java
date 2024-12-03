/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import software.amazon.ion.impl.PrivateIonConstants;
import software.amazon.ion.util.PrivateFastAppendable;

final class OutputStreamFastAppendable
implements PrivateFastAppendable,
Closeable,
Flushable {
    private static final int MAX_BYTES_LEN = 4096;
    private final OutputStream _out;
    private final byte[] _byteBuffer;
    private int _pos;

    OutputStreamFastAppendable(OutputStream out) {
        out.getClass();
        this._out = out;
        this._pos = 0;
        this._byteBuffer = new byte[4096];
    }

    public Appendable append(char c) throws IOException {
        if (c < '\u0080') {
            this.appendAscii(c);
        } else {
            this.appendUtf16(c);
        }
        return this;
    }

    public Appendable append(CharSequence csq) throws IOException {
        this.append(csq, 0, csq.length());
        return this;
    }

    public Appendable append(CharSequence csq, int start, int end) throws IOException {
        for (int ii = start; ii < end; ++ii) {
            this.append(csq.charAt(ii));
        }
        return this;
    }

    public final void appendAscii(char c) throws IOException {
        if (this._pos == this._byteBuffer.length) {
            this._out.write(this._byteBuffer, 0, this._pos);
            this._pos = 0;
        }
        assert (c < '\u0080');
        this._byteBuffer[this._pos++] = (byte)c;
    }

    public final void appendAscii(CharSequence csq) throws IOException {
        this.appendAscii(csq, 0, csq.length());
    }

    public final void appendAscii(CharSequence csq, int start, int end) throws IOException {
        if (csq instanceof String) {
            String str = (String)csq;
            int len = end - start;
            if (this._pos + len < this._byteBuffer.length) {
                str.getBytes(start, end, this._byteBuffer, this._pos);
                this._pos += len;
            } else {
                do {
                    this._out.write(this._byteBuffer, 0, this._pos);
                    this._pos = end - start > this._byteBuffer.length ? this._byteBuffer.length : end - start;
                    str.getBytes(start, start + this._pos, this._byteBuffer, 0);
                } while ((start += this._pos) < end);
            }
        } else {
            for (int ii = start; ii < end; ++ii) {
                if (this._pos == this._byteBuffer.length) {
                    this._out.write(this._byteBuffer, 0, this._pos);
                    this._pos = 0;
                }
                char c = csq.charAt(ii);
                assert (c < '\u0080');
                this._byteBuffer[this._pos++] = (byte)c;
            }
        }
    }

    public final void appendUtf16(char c) throws IOException {
        assert (c >= '\u0080');
        if (this._pos > this._byteBuffer.length - 3) {
            this._out.write(this._byteBuffer, 0, this._pos);
            this._pos = 0;
        }
        if (c < '\u0800') {
            this._byteBuffer[this._pos++] = (byte)(0xFF & (0xC0 | c >> 6));
            this._byteBuffer[this._pos++] = (byte)(0xFF & (0x80 | c & 0x3F));
        } else if (c < '\u10000') {
            this._byteBuffer[this._pos++] = (byte)(0xFF & (0xE0 | c >> 12));
            this._byteBuffer[this._pos++] = (byte)(0xFF & (0x80 | c >> 6 & 0x3F));
            this._byteBuffer[this._pos++] = (byte)(0xFF & (0x80 | c & 0x3F));
        }
    }

    public final void appendUtf16Surrogate(char leadSurrogate, char trailSurrogate) throws IOException {
        int c = PrivateIonConstants.makeUnicodeScalar(leadSurrogate, trailSurrogate);
        assert (c >= 65536);
        if (this._pos > this._byteBuffer.length - 4) {
            this._out.write(this._byteBuffer, 0, this._pos);
            this._pos = 0;
        }
        this._byteBuffer[this._pos++] = (byte)(0xFF & (0xF0 | c >> 18));
        this._byteBuffer[this._pos++] = (byte)(0xFF & (0x80 | c >> 12 & 0x3F));
        this._byteBuffer[this._pos++] = (byte)(0xFF & (0x80 | c >> 6 & 0x3F));
        this._byteBuffer[this._pos++] = (byte)(0xFF & (0x80 | c & 0x3F));
    }

    public final void flush() throws IOException {
        if (this._pos > 0) {
            this._out.write(this._byteBuffer, 0, this._pos);
            this._pos = 0;
        }
        this._out.flush();
    }

    public final void close() throws IOException {
        try {
            this.flush();
        }
        finally {
            this._out.close();
        }
    }
}

