/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.decode;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class ASCIIHexDecode {
    private ByteBuffer buf;

    private ASCIIHexDecode(ByteBuffer buf) {
        this.buf = buf;
    }

    private int readHexDigit() throws PDFParseException {
        while (this.buf.remaining() > 0) {
            int c = this.buf.get();
            if (PDFFile.isWhiteSpace((char)c)) continue;
            if (c >= 48 && c <= 57) {
                c -= 48;
            } else if (c >= 97 && c <= 102) {
                c -= 87;
            } else if (c >= 65 && c <= 70) {
                c -= 55;
            } else if (c == 62) {
                c = -1;
            } else {
                throw new PDFParseException("Bad character " + c + "in ASCIIHex decode");
            }
            return c;
        }
        throw new PDFParseException("Short stream in ASCIIHex decode");
    }

    private ByteBuffer decode() throws PDFParseException {
        this.buf.rewind();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (true) {
            int first = this.readHexDigit();
            int second = this.readHexDigit();
            if (first == -1) break;
            if (second == -1) {
                baos.write((byte)(first << 4));
                break;
            }
            baos.write((byte)((first << 4) + second));
        }
        return ByteBuffer.wrap(baos.toByteArray());
    }

    public static ByteBuffer decode(ByteBuffer buf, PDFObject params) throws PDFParseException {
        ASCIIHexDecode me = new ASCIIHexDecode(buf);
        return me.decode();
    }
}

