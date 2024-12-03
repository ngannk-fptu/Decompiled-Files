/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.crypto.DSA
 *  org.bouncycastle.crypto.Digest
 *  org.bouncycastle.crypto.ExtendedDigest
 *  org.bouncycastle.crypto.Signer
 *  org.bouncycastle.crypto.signers.DSADigestSigner
 *  org.bouncycastle.crypto.signers.DSASigner
 */
package org.bouncycastle.operator.bc;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.signers.DSADigestSigner;
import org.bouncycastle.crypto.signers.DSASigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcContentSignerBuilder;

public class BcDSAContentSignerBuilder
extends BcContentSignerBuilder {
    public BcDSAContentSignerBuilder(AlgorithmIdentifier sigAlgId, AlgorithmIdentifier digAlgId) {
        super(sigAlgId, digAlgId);
    }

    @Override
    protected Signer createSigner(AlgorithmIdentifier sigAlgId, AlgorithmIdentifier digAlgId) throws OperatorCreationException {
        ExtendedDigest dig = this.digestProvider.get(digAlgId);
        return new DSADigestSigner((DSA)new DSASigner(), (Digest)dig);
    }
}

