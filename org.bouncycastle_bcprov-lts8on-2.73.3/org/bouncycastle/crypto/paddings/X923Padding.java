/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;

public class X923Padding
implements BlockCipherPadding {
    SecureRandom random = null;

    @Override
    public void init(SecureRandom random) throws IllegalArgumentException {
        this.random = random;
    }

    @Override
    public String getPaddingName() {
        return "X9.23";
    }

    @Override
    public int addPadding(byte[] in, int inOff) {
        byte code = (byte)(in.length - inOff);
        while (inOff < in.length - 1) {
            in[inOff] = this.random == null ? (byte)0 : (byte)this.random.nextInt();
            ++inOff;
        }
        in[inOff] = code;
        return code;
    }

    @Override
    public int padCount(byte[] in) throws InvalidCipherTextException {
        int count = in[in.length - 1] & 0xFF;
        int position = in.length - count;
        int failed = (position | count - 1) >> 31;
        if (failed != 0) {
            throw new InvalidCipherTextException("pad block corrupted");
        }
        return count;
    }
}

