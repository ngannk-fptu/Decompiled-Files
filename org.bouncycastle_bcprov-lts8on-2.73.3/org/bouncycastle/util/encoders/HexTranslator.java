/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.encoders;

import org.bouncycastle.util.encoders.Translator;

public class HexTranslator
implements Translator {
    private static final byte[] hexTable = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};

    @Override
    public int getEncodedBlockSize() {
        return 2;
    }

    @Override
    public int encode(byte[] in, int inOff, int length, byte[] out, int outOff) {
        int i = 0;
        int j = 0;
        while (i < length) {
            out[outOff + j] = hexTable[in[inOff] >> 4 & 0xF];
            out[outOff + j + 1] = hexTable[in[inOff] & 0xF];
            ++inOff;
            ++i;
            j += 2;
        }
        return length * 2;
    }

    @Override
    public int getDecodedBlockSize() {
        return 1;
    }

    @Override
    public int decode(byte[] in, int inOff, int length, byte[] out, int outOff) {
        int halfLength = length / 2;
        for (int i = 0; i < halfLength; ++i) {
            byte left = in[inOff + i * 2];
            byte right = in[inOff + i * 2 + 1];
            out[outOff] = left < 97 ? (byte)(left - 48 << 4) : (byte)(left - 97 + 10 << 4);
            if (right < 97) {
                int n = outOff;
                out[n] = (byte)(out[n] + (byte)(right - 48));
            } else {
                int n = outOff;
                out[n] = (byte)(out[n] + (byte)(right - 97 + 10));
            }
            ++outOff;
        }
        return halfLength;
    }
}

