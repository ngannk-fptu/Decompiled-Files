/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.generators.DESKeyGenerator;
import org.bouncycastle.crypto.params.DESedeParameters;

public class DESedeKeyGenerator
extends DESKeyGenerator {
    private static final int MAX_IT = 20;

    @Override
    public void init(KeyGenerationParameters param) {
        this.random = param.getRandom();
        this.strength = (param.getStrength() + 7) / 8;
        if (this.strength == 0 || this.strength == 21) {
            this.strength = 24;
        } else if (this.strength == 14) {
            this.strength = 16;
        } else if (this.strength != 24 && this.strength != 16) {
            throw new IllegalArgumentException("DESede key must be 192 or 128 bits long.");
        }
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("DESedeKeyGen", 112, null, CryptoServicePurpose.KEYGEN));
    }

    @Override
    public byte[] generateKey() {
        byte[] newKey = new byte[this.strength];
        int count = 0;
        do {
            this.random.nextBytes(newKey);
            DESedeParameters.setOddParity(newKey);
        } while (++count < 20 && (DESedeParameters.isWeakKey(newKey, 0, newKey.length) || !DESedeParameters.isRealEDEKey(newKey, 0)));
        if (DESedeParameters.isWeakKey(newKey, 0, newKey.length) || !DESedeParameters.isRealEDEKey(newKey, 0)) {
            throw new IllegalStateException("Unable to generate DES-EDE key");
        }
        return newKey;
    }
}

