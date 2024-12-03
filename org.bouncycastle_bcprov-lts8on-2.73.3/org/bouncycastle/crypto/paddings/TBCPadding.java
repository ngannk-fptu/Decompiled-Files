/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;

public class TBCPadding
implements BlockCipherPadding {
    @Override
    public void init(SecureRandom random) throws IllegalArgumentException {
    }

    @Override
    public String getPaddingName() {
        return "TBC";
    }

    @Override
    public int addPadding(byte[] in, int inOff) {
        int count = in.length - inOff;
        byte code = inOff > 0 ? (byte)((in[inOff - 1] & 1) == 0 ? 255 : 0) : (byte)((in[in.length - 1] & 1) == 0 ? 255 : 0);
        while (inOff < in.length) {
            in[inOff] = code;
            ++inOff;
        }
        return count;
    }

    @Override
    public int padCount(byte[] in) throws InvalidCipherTextException {
        int i = in.length;
        int code = in[--i] & 0xFF;
        int count = 1;
        int countingMask = -1;
        while (--i >= 0) {
            int next = in[i] & 0xFF;
            int matchMask = (next ^ code) - 1 >> 31;
            count -= (countingMask &= matchMask);
        }
        return count;
    }
}

