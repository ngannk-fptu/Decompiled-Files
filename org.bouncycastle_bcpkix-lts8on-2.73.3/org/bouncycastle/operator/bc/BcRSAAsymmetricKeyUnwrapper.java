/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.crypto.AsymmetricBlockCipher
 *  org.bouncycastle.crypto.encodings.PKCS1Encoding
 *  org.bouncycastle.crypto.engines.RSABlindedEngine
 *  org.bouncycastle.crypto.params.AsymmetricKeyParameter
 */
package org.bouncycastle.operator.bc;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.operator.bc.BcAsymmetricKeyUnwrapper;

public class BcRSAAsymmetricKeyUnwrapper
extends BcAsymmetricKeyUnwrapper {
    public BcRSAAsymmetricKeyUnwrapper(AlgorithmIdentifier encAlgId, AsymmetricKeyParameter privateKey) {
        super(encAlgId, privateKey);
    }

    @Override
    protected AsymmetricBlockCipher createAsymmetricUnwrapper(ASN1ObjectIdentifier algorithm) {
        return new PKCS1Encoding((AsymmetricBlockCipher)new RSABlindedEngine());
    }
}

