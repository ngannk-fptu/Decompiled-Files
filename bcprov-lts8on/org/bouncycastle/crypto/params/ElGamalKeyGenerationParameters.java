/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.ElGamalParameters;

public class ElGamalKeyGenerationParameters
extends KeyGenerationParameters {
    private ElGamalParameters params;

    public ElGamalKeyGenerationParameters(SecureRandom random, ElGamalParameters params) {
        super(random, ElGamalKeyGenerationParameters.getStrength(params));
        this.params = params;
    }

    public ElGamalParameters getParameters() {
        return this.params;
    }

    static int getStrength(ElGamalParameters params) {
        return params.getL() != 0 ? params.getL() : params.getP().bitLength();
    }
}

