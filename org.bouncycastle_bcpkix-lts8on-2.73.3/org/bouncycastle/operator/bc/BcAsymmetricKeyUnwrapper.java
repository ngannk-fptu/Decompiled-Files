/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.crypto.AsymmetricBlockCipher
 *  org.bouncycastle.crypto.CipherParameters
 *  org.bouncycastle.crypto.InvalidCipherTextException
 *  org.bouncycastle.crypto.params.AsymmetricKeyParameter
 */
package org.bouncycastle.operator.bc;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.operator.AsymmetricKeyUnwrapper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;

public abstract class BcAsymmetricKeyUnwrapper
extends AsymmetricKeyUnwrapper {
    private AsymmetricKeyParameter privateKey;

    public BcAsymmetricKeyUnwrapper(AlgorithmIdentifier encAlgId, AsymmetricKeyParameter privateKey) {
        super(encAlgId);
        this.privateKey = privateKey;
    }

    @Override
    public GenericKey generateUnwrappedKey(AlgorithmIdentifier encryptedKeyAlgorithm, byte[] encryptedKey) throws OperatorException {
        AsymmetricBlockCipher keyCipher = this.createAsymmetricUnwrapper(this.getAlgorithmIdentifier().getAlgorithm());
        keyCipher.init(false, (CipherParameters)this.privateKey);
        try {
            byte[] key = keyCipher.processBlock(encryptedKey, 0, encryptedKey.length);
            if (encryptedKeyAlgorithm.getAlgorithm().equals((ASN1Primitive)PKCSObjectIdentifiers.des_EDE3_CBC)) {
                return new GenericKey(encryptedKeyAlgorithm, key);
            }
            return new GenericKey(encryptedKeyAlgorithm, key);
        }
        catch (InvalidCipherTextException e) {
            throw new OperatorException("unable to recover secret key: " + e.getMessage(), e);
        }
    }

    protected abstract AsymmetricBlockCipher createAsymmetricUnwrapper(ASN1ObjectIdentifier var1);
}

