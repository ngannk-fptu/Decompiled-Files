/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.icns;

final class Rle24Compression {
    private Rle24Compression() {
    }

    public static byte[] decompress(int width, int height, byte[] data) {
        int pixelCount = width * height;
        byte[] result = new byte[4 * pixelCount];
        int dataPos = 0;
        if (width >= 128 && height >= 128) {
            dataPos = 4;
        }
        for (int band = 1; band <= 3; ++band) {
            int remaining = pixelCount;
            int resultPos = 0;
            while (remaining > 0) {
                int i;
                int count;
                if ((data[dataPos] & 0x80) != 0) {
                    count = (0xFF & data[dataPos]) - 125;
                    for (i = 0; i < count; ++i) {
                        result[band + 4 * resultPos++] = data[dataPos + 1];
                    }
                    dataPos += 2;
                    remaining -= count;
                    continue;
                }
                count = (0xFF & data[dataPos]) + 1;
                ++dataPos;
                for (i = 0; i < count; ++i) {
                    result[band + 4 * resultPos++] = data[dataPos++];
                }
                remaining -= count;
            }
        }
        return result;
    }
}

