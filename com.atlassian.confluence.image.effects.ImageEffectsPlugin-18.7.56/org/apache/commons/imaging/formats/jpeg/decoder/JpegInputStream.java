/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.decoder;

import java.util.Arrays;
import org.apache.commons.imaging.ImageReadException;

class JpegInputStream {
    private final int[] interval;
    private int nextPos;
    private int cnt;
    private int b;

    JpegInputStream(int[] interval) {
        this.interval = Arrays.copyOf(interval, interval.length);
        this.nextPos = 0;
    }

    public boolean hasNext() {
        return this.nextPos < this.interval.length;
    }

    public int nextBit() throws ImageReadException {
        if (this.cnt == 0) {
            this.b = this.read();
            if (this.b < 0) {
                throw new ImageReadException("Premature End of File");
            }
            this.cnt = 8;
            if (this.b == 255) {
                int b2 = this.read();
                if (b2 < 0) {
                    throw new ImageReadException("Premature End of File");
                }
                if (b2 != 0) {
                    if (b2 == 220) {
                        throw new ImageReadException("DNL not yet supported");
                    }
                    throw new ImageReadException("Invalid marker found in entropy data: 0xFF " + Integer.toHexString(b2));
                }
            }
        }
        int bit = this.b >> 7 & 1;
        --this.cnt;
        this.b <<= 1;
        return bit;
    }

    int read() {
        if (!this.hasNext()) {
            throw new IllegalStateException("This stream hasn't any other value, all values were already read.");
        }
        int value = this.interval[this.nextPos];
        ++this.nextPos;
        return value;
    }
}

