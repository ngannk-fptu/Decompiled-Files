/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;

public class ZeroBytePadding
implements BlockCipherPadding {
    @Override
    public void init(SecureRandom random) throws IllegalArgumentException {
    }

    @Override
    public String getPaddingName() {
        return "ZeroByte";
    }

    @Override
    public int addPadding(byte[] in, int inOff) {
        int added = in.length - inOff;
        while (inOff < in.length) {
            in[inOff] = 0;
            ++inOff;
        }
        return added;
    }

    @Override
    public int padCount(byte[] in) throws InvalidCipherTextException {
        int count = 0;
        int still00Mask = -1;
        int i = in.length;
        while (--i >= 0) {
            int next = in[i] & 0xFF;
            int match00Mask = (next ^ 0) - 1 >> 31;
            count -= (still00Mask &= match00Mask);
        }
        return count;
    }
}

