/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.hpack;

import java.nio.ByteBuffer;
import org.apache.hc.core5.util.ByteArrayBuffer;

final class HuffmanEncoder {
    private final int[] codes;
    private final byte[] lengths;

    HuffmanEncoder(int[] codes, byte[] lengths) {
        this.codes = codes;
        this.lengths = lengths;
    }

    void encode(ByteArrayBuffer out, ByteBuffer src) {
        long current = 0L;
        int n = 0;
        while (src.hasRemaining()) {
            int b = src.get() & 0xFF;
            int code = this.codes[b];
            byte nbits = this.lengths[b];
            current <<= nbits;
            current |= (long)code;
            n += nbits;
            while (n >= 8) {
                out.append((int)(current >> (n -= 8)));
            }
        }
        if (n > 0) {
            current <<= 8 - n;
            out.append((int)(current |= (long)(255 >>> n)));
        }
    }

    void encode(ByteArrayBuffer out, CharSequence src, int off, int len) {
        long current = 0L;
        int n = 0;
        for (int i = 0; i < len; ++i) {
            int b = src.charAt(off + i) & 0xFF;
            int code = this.codes[b];
            byte nbits = this.lengths[b];
            current <<= nbits;
            current |= (long)code;
            n += nbits;
            while (n >= 8) {
                out.append((int)(current >> (n -= 8)));
            }
        }
        if (n > 0) {
            current <<= 8 - n;
            out.append((int)(current |= (long)(255 >>> n)));
        }
    }
}

