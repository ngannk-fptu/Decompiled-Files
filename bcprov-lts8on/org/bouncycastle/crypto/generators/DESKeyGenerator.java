/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.DESParameters;

public class DESKeyGenerator
extends CipherKeyGenerator {
    @Override
    public void init(KeyGenerationParameters param) {
        super.init(param);
        if (this.strength == 0 || this.strength == 7) {
            this.strength = 8;
        } else if (this.strength != 8) {
            throw new IllegalArgumentException("DES key must be 64 bits long.");
        }
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("DESKeyGen", 56, null, CryptoServicePurpose.KEYGEN));
    }

    @Override
    public byte[] generateKey() {
        byte[] newKey = new byte[8];
        do {
            this.random.nextBytes(newKey);
            DESParameters.setOddParity(newKey);
        } while (DESParameters.isWeakKey(newKey, 0));
        return newKey;
    }
}

