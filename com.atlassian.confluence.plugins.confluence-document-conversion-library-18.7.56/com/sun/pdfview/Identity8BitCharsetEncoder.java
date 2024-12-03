/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public class Identity8BitCharsetEncoder
extends CharsetEncoder {
    public Identity8BitCharsetEncoder() {
        super(null, 1.0f, 1.0f);
    }

    @Override
    protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
        while (in.remaining() > 0) {
            if (out.remaining() < 1) {
                return CoderResult.OVERFLOW;
            }
            char c = in.get();
            if (c >= '\u0000' && c < '\u0100') {
                out.put((byte)c);
                continue;
            }
            return CoderResult.unmappableForLength(1);
        }
        return CoderResult.UNDERFLOW;
    }

    @Override
    public boolean isLegalReplacement(byte[] repl) {
        return true;
    }
}

