/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;

public class PKCS7Padding
implements BlockCipherPadding {
    @Override
    public void init(SecureRandom random) throws IllegalArgumentException {
    }

    @Override
    public String getPaddingName() {
        return "PKCS7";
    }

    @Override
    public int addPadding(byte[] in, int inOff) {
        byte code = (byte)(in.length - inOff);
        while (inOff < in.length) {
            in[inOff] = code;
            ++inOff;
        }
        return code;
    }

    @Override
    public int padCount(byte[] in) throws InvalidCipherTextException {
        byte countAsByte = in[in.length - 1];
        int count = countAsByte & 0xFF;
        int position = in.length - count;
        int failed = (position | count - 1) >> 31;
        for (int i = 0; i < in.length; ++i) {
            failed |= (in[i] ^ countAsByte) & ~(i - position >> 31);
        }
        if (failed != 0) {
            throw new InvalidCipherTextException("pad block corrupted");
        }
        return count;
    }
}

