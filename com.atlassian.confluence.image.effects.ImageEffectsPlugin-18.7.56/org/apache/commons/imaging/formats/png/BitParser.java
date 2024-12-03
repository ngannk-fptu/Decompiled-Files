/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png;

import org.apache.commons.imaging.ImageReadException;

class BitParser {
    private final byte[] bytes;
    private final int bitsPerPixel;
    private final int bitDepth;

    BitParser(byte[] bytes, int bitsPerPixel, int bitDepth) {
        this.bytes = (byte[])bytes.clone();
        this.bitsPerPixel = bitsPerPixel;
        this.bitDepth = bitDepth;
    }

    public int getSample(int pixelIndexInScanline, int sampleIndex) throws ImageReadException {
        int pixelIndexBits = this.bitsPerPixel * pixelIndexInScanline;
        int sampleIndexBits = pixelIndexBits + sampleIndex * this.bitDepth;
        int sampleIndexBytes = sampleIndexBits >> 3;
        if (this.bitDepth == 8) {
            return 0xFF & this.bytes[sampleIndexBytes];
        }
        if (this.bitDepth < 8) {
            int b = 0xFF & this.bytes[sampleIndexBytes];
            int bitsToShift = 8 - ((pixelIndexBits & 7) + this.bitDepth);
            int bitmask = (1 << this.bitDepth) - 1;
            return (b >>= bitsToShift) & bitmask;
        }
        if (this.bitDepth == 16) {
            return (0xFF & this.bytes[sampleIndexBytes]) << 8 | 0xFF & this.bytes[sampleIndexBytes + 1];
        }
        throw new ImageReadException("PNG: bad BitDepth: " + this.bitDepth);
    }

    public int getSampleAsByte(int pixelIndexInScanline, int sampleIndex) throws ImageReadException {
        int sample = this.getSample(pixelIndexInScanline, sampleIndex);
        int rot = 8 - this.bitDepth;
        if (rot > 0) {
            sample = sample * 255 / ((1 << this.bitDepth) - 1);
        } else if (rot < 0) {
            sample >>= -rot;
        }
        return 0xFF & sample;
    }
}

