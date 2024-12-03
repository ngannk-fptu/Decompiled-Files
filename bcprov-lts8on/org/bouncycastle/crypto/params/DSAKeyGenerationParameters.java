/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameters;

public class DSAKeyGenerationParameters
extends KeyGenerationParameters {
    private DSAParameters params;

    public DSAKeyGenerationParameters(SecureRandom random, DSAParameters params) {
        super(random, params.getP().bitLength() - 1);
        this.params = params;
    }

    public DSAParameters getParameters() {
        return this.params;
    }
}

