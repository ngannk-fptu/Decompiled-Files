/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.fileupload.util.mime;

import java.io.IOException;
import java.io.OutputStream;

final class QuotedPrintableDecoder {
    private static final int UPPER_NIBBLE_SHIFT = 4;

    private QuotedPrintableDecoder() {
    }

    public static int decode(byte[] data, OutputStream out) throws IOException {
        int off = 0;
        int length = data.length;
        int endOffset = off + length;
        int bytesWritten = 0;
        while (off < endOffset) {
            byte ch;
            if ((ch = data[off++]) == 95) {
                out.write(32);
                continue;
            }
            if (ch == 61) {
                if (off + 1 >= endOffset) {
                    throw new IOException("Invalid quoted printable encoding; truncated escape sequence");
                }
                byte b1 = data[off++];
                byte b2 = data[off++];
                if (b1 == 13) {
                    if (b2 == 10) continue;
                    throw new IOException("Invalid quoted printable encoding; CR must be followed by LF");
                }
                int c1 = QuotedPrintableDecoder.hexToBinary(b1);
                int c2 = QuotedPrintableDecoder.hexToBinary(b2);
                out.write(c1 << 4 | c2);
                ++bytesWritten;
                continue;
            }
            out.write(ch);
            ++bytesWritten;
        }
        return bytesWritten;
    }

    private static int hexToBinary(byte b) throws IOException {
        int i = Character.digit((char)b, 16);
        if (i == -1) {
            throw new IOException("Invalid quoted printable encoding: not a valid hex digit: " + b);
        }
        return i;
    }
}

