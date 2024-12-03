/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import com.amazonaws.util.AbstractBase32Codec;
import com.amazonaws.util.CodecUtils;

class Base32Codec
extends AbstractBase32Codec {
    private static final int OFFSET_OF_2 = 24;

    private static byte[] alphabets() {
        return CodecUtils.toBytesDirect("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567");
    }

    Base32Codec() {
        super(Base32Codec.alphabets());
    }

    @Override
    protected int pos(byte in) {
        byte pos = LazyHolder.DECODED[in];
        if (pos > -1) {
            return pos;
        }
        throw new IllegalArgumentException("Invalid base 32 character: '" + (char)in + "'");
    }

    private static class LazyHolder {
        private static final byte[] DECODED = LazyHolder.decodeTable();

        private LazyHolder() {
        }

        private static byte[] decodeTable() {
            byte[] dest = new byte[123];
            for (int i = 0; i <= 122; ++i) {
                dest[i] = i >= 65 && i <= 90 ? (int)(i - 65) : (i >= 50 && i <= 55 ? (int)(i - 24) : (i >= 97 && i <= 122 ? (int)(i - 97) : -1));
            }
            return dest;
        }
    }
}

