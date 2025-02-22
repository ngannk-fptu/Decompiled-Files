/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.CramerShoupParameters;

public class CramerShoupKeyGenerationParameters
extends KeyGenerationParameters {
    private CramerShoupParameters params;

    public CramerShoupKeyGenerationParameters(SecureRandom random, CramerShoupParameters params) {
        super(random, CramerShoupKeyGenerationParameters.getStrength(params));
        this.params = params;
    }

    public CramerShoupParameters getParameters() {
        return this.params;
    }

    static int getStrength(CramerShoupParameters params) {
        return params.getP().bitLength();
    }
}

