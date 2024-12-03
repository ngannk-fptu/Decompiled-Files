/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class Poly1305KeyGenerator
extends CipherKeyGenerator {
    private static final byte R_MASK_LOW_2 = -4;
    private static final byte R_MASK_HIGH_4 = 15;

    @Override
    public void init(KeyGenerationParameters param) {
        super.init(new KeyGenerationParameters(param.getRandom(), 256));
    }

    @Override
    public byte[] generateKey() {
        byte[] key = super.generateKey();
        Poly1305KeyGenerator.clamp(key);
        return key;
    }

    public static void clamp(byte[] key) {
        if (key.length != 32) {
            throw new IllegalArgumentException("Poly1305 key must be 256 bits.");
        }
        key[3] = (byte)(key[3] & 0xF);
        key[7] = (byte)(key[7] & 0xF);
        key[11] = (byte)(key[11] & 0xF);
        key[15] = (byte)(key[15] & 0xF);
        key[4] = (byte)(key[4] & 0xFFFFFFFC);
        key[8] = (byte)(key[8] & 0xFFFFFFFC);
        key[12] = (byte)(key[12] & 0xFFFFFFFC);
    }

    public static void checkKey(byte[] key) {
        if (key.length != 32) {
            throw new IllegalArgumentException("Poly1305 key must be 256 bits.");
        }
        Poly1305KeyGenerator.checkMask(key[3], (byte)15);
        Poly1305KeyGenerator.checkMask(key[7], (byte)15);
        Poly1305KeyGenerator.checkMask(key[11], (byte)15);
        Poly1305KeyGenerator.checkMask(key[15], (byte)15);
        Poly1305KeyGenerator.checkMask(key[4], (byte)-4);
        Poly1305KeyGenerator.checkMask(key[8], (byte)-4);
        Poly1305KeyGenerator.checkMask(key[12], (byte)-4);
    }

    private static void checkMask(byte b, byte mask) {
        if ((b & ~mask) != 0) {
            throw new IllegalArgumentException("Invalid format for r portion of Poly1305 key.");
        }
    }
}

