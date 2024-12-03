/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;

public class ISO7816d4Padding
implements BlockCipherPadding {
    @Override
    public void init(SecureRandom random) throws IllegalArgumentException {
    }

    @Override
    public String getPaddingName() {
        return "ISO7816-4";
    }

    @Override
    public int addPadding(byte[] in, int inOff) {
        int added = in.length - inOff;
        in[inOff] = -128;
        ++inOff;
        while (inOff < in.length) {
            in[inOff] = 0;
            ++inOff;
        }
        return added;
    }

    @Override
    public int padCount(byte[] in) throws InvalidCipherTextException {
        int position = -1;
        int still00Mask = -1;
        int i = in.length;
        while (--i >= 0) {
            int next = in[i] & 0xFF;
            int match00Mask = (next ^ 0) - 1 >> 31;
            int match80Mask = (next ^ 0x80) - 1 >> 31;
            position ^= (i ^ position) & (still00Mask & match80Mask);
            still00Mask &= match00Mask;
        }
        if (position < 0) {
            throw new InvalidCipherTextException("pad block corrupted");
        }
        return in.length - position;
    }
}

