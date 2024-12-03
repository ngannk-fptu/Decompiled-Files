/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sourcemap;

import com.atlassian.sourcemap.Base64;
import java.io.IOException;

class Base64VLQ {
    private static final int VLQ_BASE_SHIFT = 5;
    private static final int VLQ_BASE = 32;
    private static final int VLQ_BASE_MASK = 31;
    private static final int VLQ_CONTINUATION_BIT = 32;

    private Base64VLQ() {
    }

    private static int toVLQSigned(int value) {
        return value < 0 ? (-value << 1) + 1 : (value << 1) + 0;
    }

    private static int fromVLQSigned(int value) {
        boolean negate = (value & 1) == 1;
        return negate ? -value : (value >>= 1);
    }

    public static void encode(Appendable out, int value) throws IOException {
        value = Base64VLQ.toVLQSigned(value);
        do {
            int digit = value & 0x1F;
            if ((value >>>= 5) > 0) {
                digit |= 0x20;
            }
            out.append(Base64.toBase64(digit));
        } while (value > 0);
    }

    public static int decode(CharIterator in) {
        boolean continuation;
        int result = 0;
        int shift = 0;
        do {
            char c;
            int digit;
            continuation = ((digit = Base64.fromBase64(c = in.next())) & 0x20) != 0;
            result += (digit &= 0x1F) << shift;
            shift += 5;
        } while (continuation);
        return Base64VLQ.fromVLQSigned(result);
    }

    static interface CharIterator {
        public boolean hasNext();

        public char next();
    }
}

