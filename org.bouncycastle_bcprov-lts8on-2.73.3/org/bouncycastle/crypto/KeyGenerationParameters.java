/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoServicesRegistrar;

public class KeyGenerationParameters {
    private SecureRandom random;
    private int strength;

    public KeyGenerationParameters(SecureRandom random, int strength) {
        this.random = CryptoServicesRegistrar.getSecureRandom(random);
        this.strength = strength;
    }

    public SecureRandom getRandom() {
        return this.random;
    }

    public int getStrength() {
        return this.strength;
    }
}

