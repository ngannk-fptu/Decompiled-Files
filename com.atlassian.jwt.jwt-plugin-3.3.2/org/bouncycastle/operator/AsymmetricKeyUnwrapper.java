/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.KeyUnwrapper;

public abstract class AsymmetricKeyUnwrapper
implements KeyUnwrapper {
    private AlgorithmIdentifier algorithmId;

    protected AsymmetricKeyUnwrapper(AlgorithmIdentifier algorithmIdentifier) {
        this.algorithmId = algorithmIdentifier;
    }

    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.algorithmId;
    }
}

