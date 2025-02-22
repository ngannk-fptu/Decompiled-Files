/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.GOST3410Parameters;

public class GOST3410KeyGenerationParameters
extends KeyGenerationParameters {
    private GOST3410Parameters params;

    public GOST3410KeyGenerationParameters(SecureRandom random, GOST3410Parameters params) {
        super(random, params.getP().bitLength() - 1);
        this.params = params;
    }

    public GOST3410Parameters getParameters() {
        return this.params;
    }
}

