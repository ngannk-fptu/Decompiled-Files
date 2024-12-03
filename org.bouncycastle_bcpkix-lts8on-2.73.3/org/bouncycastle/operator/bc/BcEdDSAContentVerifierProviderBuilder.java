/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.edec.EdECObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.crypto.Signer
 *  org.bouncycastle.crypto.params.AsymmetricKeyParameter
 *  org.bouncycastle.crypto.signers.Ed25519Signer
 *  org.bouncycastle.crypto.signers.Ed448Signer
 *  org.bouncycastle.crypto.util.PublicKeyFactory
 */
package org.bouncycastle.operator.bc;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.crypto.signers.Ed448Signer;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcContentVerifierProviderBuilder;

public class BcEdDSAContentVerifierProviderBuilder
extends BcContentVerifierProviderBuilder {
    public static final byte[] DEFAULT_CONTEXT = new byte[0];

    @Override
    protected Signer createSigner(AlgorithmIdentifier sigAlgId) throws OperatorCreationException {
        if (sigAlgId.getAlgorithm().equals((ASN1Primitive)EdECObjectIdentifiers.id_Ed448)) {
            return new Ed448Signer(DEFAULT_CONTEXT);
        }
        return new Ed25519Signer();
    }

    @Override
    protected AsymmetricKeyParameter extractKeyParameters(SubjectPublicKeyInfo publicKeyInfo) throws IOException {
        return PublicKeyFactory.createKey((SubjectPublicKeyInfo)publicKeyInfo);
    }
}

