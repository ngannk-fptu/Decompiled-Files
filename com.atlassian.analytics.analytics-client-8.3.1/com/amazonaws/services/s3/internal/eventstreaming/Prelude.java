/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal.eventstreaming;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

final class Prelude {
    static final int LENGTH = 8;
    static final int LENGTH_WITH_CRC = 12;
    private final int totalLength;
    private final long headersLength;

    private Prelude(int totalLength, long headersLength) {
        this.totalLength = totalLength;
        this.headersLength = headersLength;
    }

    static Prelude decode(ByteBuffer buf) {
        buf = buf.duplicate();
        long computedPreludeCrc = Prelude.computePreludeCrc(buf);
        long totalLength = Prelude.intToUnsignedLong(buf.getInt());
        long headersLength = Prelude.intToUnsignedLong(buf.getInt());
        long wirePreludeCrc = Prelude.intToUnsignedLong(buf.getInt());
        if (computedPreludeCrc != wirePreludeCrc) {
            throw new IllegalArgumentException(String.format("Prelude checksum failure: expected 0x%x, computed 0x%x", wirePreludeCrc, computedPreludeCrc));
        }
        if (headersLength < 0L || headersLength > 131072L) {
            throw new IllegalArgumentException("Illegal headers_length value: " + headersLength);
        }
        long payloadLength = totalLength - headersLength - 16L;
        if (payloadLength < 0L || payloadLength > 0x1000000L) {
            throw new IllegalArgumentException("Illegal payload size: " + payloadLength);
        }
        return new Prelude(Prelude.toIntExact(totalLength), headersLength);
    }

    private static long intToUnsignedLong(int i) {
        return (long)i & 0xFFFFFFFFL;
    }

    private static int toIntExact(long value) {
        if ((long)((int)value) != value) {
            throw new ArithmeticException("integer overflow");
        }
        return (int)value;
    }

    private static long computePreludeCrc(ByteBuffer buf) {
        byte[] prelude = new byte[8];
        buf.duplicate().get(prelude);
        CRC32 crc = new CRC32();
        crc.update(prelude, 0, prelude.length);
        return crc.getValue();
    }

    int getTotalLength() {
        return this.totalLength;
    }

    long getHeadersLength() {
        return this.headersLength;
    }
}

