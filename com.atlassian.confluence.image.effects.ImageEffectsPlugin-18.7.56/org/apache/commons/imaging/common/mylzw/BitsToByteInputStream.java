/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common.mylzw;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.imaging.common.mylzw.MyBitInputStream;

public class BitsToByteInputStream
extends InputStream {
    private final MyBitInputStream is;
    private final int desiredDepth;

    public BitsToByteInputStream(MyBitInputStream is, int desiredDepth) {
        this.is = is;
        this.desiredDepth = desiredDepth;
    }

    @Override
    public int read() throws IOException {
        return this.readBits(8);
    }

    public int readBits(int bitCount) throws IOException {
        int i = this.is.readBits(bitCount);
        if (bitCount < this.desiredDepth) {
            i <<= this.desiredDepth - bitCount;
        } else if (bitCount > this.desiredDepth) {
            i >>= bitCount - this.desiredDepth;
        }
        return i;
    }

    public int[] readBitsArray(int sampleBits, int length) throws IOException {
        int[] result = new int[length];
        for (int i = 0; i < length; ++i) {
            result[i] = this.readBits(sampleBits);
        }
        return result;
    }
}

