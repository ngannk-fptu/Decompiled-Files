/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.mime.encoding;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class QuotedPrintableInputStream
extends FilterInputStream {
    public QuotedPrintableInputStream(InputStream input) {
        super(input);
    }

    @Override
    public int read(byte[] buf, int bufOff, int len) throws IOException {
        int ch;
        int i;
        for (i = 0; i != len && (ch = this.read()) >= 0; ++i) {
            buf[i + bufOff] = (byte)ch;
        }
        if (i == 0) {
            return -1;
        }
        return i;
    }

    @Override
    public int read() throws IOException {
        int v = this.in.read();
        if (v == -1) {
            return -1;
        }
        while (v == 61) {
            int j = this.in.read();
            if (j == -1) {
                throw new IllegalStateException("Quoted '=' at end of stream");
            }
            if (j == 13) {
                j = this.in.read();
                if (j == 10) {
                    j = this.in.read();
                }
                v = j;
                continue;
            }
            if (j == 10) {
                v = this.in.read();
                continue;
            }
            int chr = 0;
            if (j >= 48 && j <= 57) {
                chr = j - 48;
            } else if (j >= 65 && j <= 70) {
                chr = 10 + (j - 65);
            } else {
                throw new IllegalStateException("Expecting '0123456789ABCDEF after quote that was not immediately followed by LF or CRLF");
            }
            chr <<= 4;
            j = this.in.read();
            if (j >= 48 && j <= 57) {
                chr |= j - 48;
            } else if (j >= 65 && j <= 70) {
                chr |= 10 + (j - 65);
            } else {
                throw new IllegalStateException("Expecting second '0123456789ABCDEF after quote that was not immediately followed by LF or CRLF");
            }
            return chr;
        }
        return v;
    }
}

