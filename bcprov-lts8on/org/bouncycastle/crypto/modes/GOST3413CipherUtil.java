/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes;

import org.bouncycastle.util.Arrays;

class GOST3413CipherUtil {
    GOST3413CipherUtil() {
    }

    public static byte[] MSB(byte[] from, int size) {
        return Arrays.copyOf(from, size);
    }

    public static byte[] LSB(byte[] from, int size) {
        byte[] result = new byte[size];
        System.arraycopy(from, from.length - size, result, 0, size);
        return result;
    }

    public static byte[] sum(byte[] in, byte[] gamma) {
        byte[] out = new byte[in.length];
        for (int i = 0; i < in.length; ++i) {
            out[i] = (byte)(in[i] ^ gamma[i]);
        }
        return out;
    }

    public static byte[] copyFromInput(byte[] input, int size, int offset) {
        if (input.length < size + offset) {
            size = input.length - offset;
        }
        byte[] newIn = new byte[size];
        System.arraycopy(input, offset, newIn, 0, size);
        return newIn;
    }
}

