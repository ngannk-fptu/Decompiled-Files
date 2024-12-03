/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.http.compression;

import java.nio.ByteBuffer;

public class NBitIntegerEncoder {
    private NBitIntegerEncoder() {
    }

    public static int octetsNeeded(int prefix, long value) {
        if (prefix <= 0 || prefix > 8) {
            throw new IllegalArgumentException();
        }
        int nbits = 255 >>> 8 - prefix;
        if ((value -= (long)nbits) < 0L) {
            return 1;
        }
        if (value == 0L) {
            return 2;
        }
        int lz = Long.numberOfLeadingZeros(value);
        int log = 64 - lz;
        return 1 + (log + 6) / 7;
    }

    public static void encode(ByteBuffer buffer, int prefix, long value) {
        if (prefix <= 0 || prefix > 8) {
            throw new IllegalArgumentException();
        }
        if (prefix == 8) {
            buffer.put((byte)0);
        }
        int bits = 255 >>> 8 - prefix;
        int p = buffer.position() - 1;
        if (value >= (long)bits) {
            buffer.put(p, (byte)(buffer.get(p) | bits));
            long length = value - (long)bits;
            while (true) {
                if ((length & 0xFFFFFFFFFFFFFF80L) == 0L) {
                    buffer.put((byte)length);
                    return;
                }
                buffer.put((byte)(length & 0x7FL | 0x80L));
                length >>>= 7;
            }
        }
        buffer.put(p, (byte)((long)(buffer.get(p) & ~bits) | value));
    }
}

