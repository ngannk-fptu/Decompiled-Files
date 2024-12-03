/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.crypto.AsymmetricBlockCipher
 *  org.bouncycastle.crypto.encodings.PKCS1Encoding
 *  org.bouncycastle.crypto.engines.RSABlindedEngine
 *  org.bouncycastle.crypto.params.AsymmetricKeyParameter
 *  org.bouncycastle.crypto.util.PublicKeyFactory
 */
package org.bouncycastle.operator.bc;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.operator.bc.BcAsymmetricKeyWrapper;

public class BcRSAAsymmetricKeyWrapper
extends BcAsymmetricKeyWrapper {
    public BcRSAAsymmetricKeyWrapper(AlgorithmIdentifier encAlgId, AsymmetricKeyParameter publicKey) {
        super(encAlgId, publicKey);
    }

    public BcRSAAsymmetricKeyWrapper(AlgorithmIdentifier encAlgId, SubjectPublicKeyInfo publicKeyInfo) throws IOException {
        super(encAlgId, PublicKeyFactory.createKey((SubjectPublicKeyInfo)publicKeyInfo));
    }

    @Override
    protected AsymmetricBlockCipher createAsymmetricWrapper(ASN1ObjectIdentifier algorithm) {
        return new PKCS1Encoding((AsymmetricBlockCipher)new RSABlindedEngine());
    }
}

