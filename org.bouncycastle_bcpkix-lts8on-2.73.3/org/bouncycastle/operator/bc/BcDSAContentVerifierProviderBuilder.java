/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.crypto.DSA
 *  org.bouncycastle.crypto.Digest
 *  org.bouncycastle.crypto.ExtendedDigest
 *  org.bouncycastle.crypto.Signer
 *  org.bouncycastle.crypto.params.AsymmetricKeyParameter
 *  org.bouncycastle.crypto.signers.DSADigestSigner
 *  org.bouncycastle.crypto.signers.DSASigner
 *  org.bouncycastle.crypto.util.PublicKeyFactory
 */
package org.bouncycastle.operator.bc;

import java.io.IOException;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.signers.DSADigestSigner;
import org.bouncycastle.crypto.signers.DSASigner;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcContentVerifierProviderBuilder;

public class BcDSAContentVerifierProviderBuilder
extends BcContentVerifierProviderBuilder {
    private DigestAlgorithmIdentifierFinder digestAlgorithmFinder;

    public BcDSAContentVerifierProviderBuilder(DigestAlgorithmIdentifierFinder digestAlgorithmFinder) {
        this.digestAlgorithmFinder = digestAlgorithmFinder;
    }

    @Override
    protected Signer createSigner(AlgorithmIdentifier sigAlgId) throws OperatorCreationException {
        AlgorithmIdentifier digAlg = this.digestAlgorithmFinder.find(sigAlgId);
        ExtendedDigest dig = this.digestProvider.get(digAlg);
        return new DSADigestSigner((DSA)new DSASigner(), (Digest)dig);
    }

    @Override
    protected AsymmetricKeyParameter extractKeyParameters(SubjectPublicKeyInfo publicKeyInfo) throws IOException {
        return PublicKeyFactory.createKey((SubjectPublicKeyInfo)publicKeyInfo);
    }
}

