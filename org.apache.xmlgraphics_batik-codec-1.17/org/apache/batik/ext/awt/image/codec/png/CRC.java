/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.codec.png;

class CRC {
    private static int[] crcTable = new int[256];

    CRC() {
    }

    public static int updateCRC(int crc, byte[] data, int off, int len) {
        int c = crc;
        for (int n = 0; n < len; ++n) {
            c = crcTable[(c ^ data[off + n]) & 0xFF] ^ c >>> 8;
        }
        return c;
    }

    static {
        for (int n = 0; n < 256; ++n) {
            int c = n;
            for (int k = 0; k < 8; ++k) {
                c = (c & 1) == 1 ? 0xEDB88320 ^ c >>> 1 : (c >>>= 1);
                CRC.crcTable[n] = c;
            }
        }
    }
}

