/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.KeyWrapper;

public abstract class SymmetricKeyWrapper
implements KeyWrapper {
    private AlgorithmIdentifier algorithmId;

    protected SymmetricKeyWrapper(AlgorithmIdentifier algorithmId) {
        this.algorithmId = algorithmId;
    }

    @Override
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.algorithmId;
    }
}

