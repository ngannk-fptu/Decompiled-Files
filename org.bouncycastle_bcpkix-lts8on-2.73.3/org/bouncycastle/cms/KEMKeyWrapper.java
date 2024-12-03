/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.cms;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.AsymmetricKeyWrapper;

public abstract class KEMKeyWrapper
extends AsymmetricKeyWrapper {
    protected KEMKeyWrapper(AlgorithmIdentifier algorithmId) {
        super(algorithmId);
    }

    public abstract byte[] getEncapsulation();

    public abstract AlgorithmIdentifier getKdfAlgorithmIdentifier();

    public abstract int getKekLength();

    public abstract AlgorithmIdentifier getWrapAlgorithmIdentifier();
}

