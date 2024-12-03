/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.decode;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class ASCII85Decode {
    private ByteBuffer buf;

    private ASCII85Decode(ByteBuffer buf) {
        this.buf = buf;
    }

    private int nextChar() {
        while (this.buf.remaining() > 0) {
            char c = (char)this.buf.get();
            if (PDFFile.isWhiteSpace(c)) continue;
            return c;
        }
        return -1;
    }

    private boolean decode5(ByteArrayOutputStream baos) throws PDFParseException {
        int i;
        int[] five = new int[5];
        for (i = 0; i < 5; ++i) {
            five[i] = this.nextChar();
            if (five[i] == 126) {
                if (this.nextChar() == 62) break;
                throw new PDFParseException("Bad character in ASCII85Decode: not ~>");
            }
            if (five[i] >= 33 && five[i] <= 117) {
                int n = i;
                five[n] = five[n] - 33;
                continue;
            }
            if (five[i] == 122) {
                if (i == 0) {
                    five[i] = 0;
                    i = 4;
                    continue;
                }
                throw new PDFParseException("Inappropriate 'z' in ASCII85Decode");
            }
            throw new PDFParseException("Bad character in ASCII85Decode: " + five[i] + " (" + (char)five[i] + ")");
        }
        if (i > 0) {
            --i;
        }
        int value = five[0] * 85 * 85 * 85 * 85 + five[1] * 85 * 85 * 85 + five[2] * 85 * 85 + five[3] * 85 + five[4];
        for (int j = 0; j < i; ++j) {
            int shift = 8 * (3 - j);
            baos.write((byte)(value >> shift & 0xFF));
        }
        return i == 4;
    }

    private ByteBuffer decode() throws PDFParseException {
        this.buf.rewind();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (this.decode5(baos)) {
        }
        return ByteBuffer.wrap(baos.toByteArray());
    }

    public static ByteBuffer decode(ByteBuffer buf, PDFObject params) throws PDFParseException {
        ASCII85Decode me = new ASCII85Decode(buf);
        return me.decode();
    }
}

