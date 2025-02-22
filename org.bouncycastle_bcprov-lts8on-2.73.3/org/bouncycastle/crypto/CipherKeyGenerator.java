/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;

public class CipherKeyGenerator {
    protected SecureRandom random;
    protected int strength;

    public void init(KeyGenerationParameters param) {
        this.random = param.getRandom();
        this.strength = (param.getStrength() + 7) / 8;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("SymKeyGen", param.getStrength()));
    }

    public byte[] generateKey() {
        byte[] key = new byte[this.strength];
        this.random.nextBytes(key);
        return key;
    }
}

