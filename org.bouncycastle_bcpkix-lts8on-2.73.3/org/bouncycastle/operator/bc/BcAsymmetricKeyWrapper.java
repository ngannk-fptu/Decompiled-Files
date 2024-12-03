/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.crypto.AsymmetricBlockCipher
 *  org.bouncycastle.crypto.CipherParameters
 *  org.bouncycastle.crypto.InvalidCipherTextException
 *  org.bouncycastle.crypto.params.AsymmetricKeyParameter
 *  org.bouncycastle.crypto.params.ParametersWithRandom
 */
package org.bouncycastle.operator.bc;

import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.operator.AsymmetricKeyWrapper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.bc.OperatorUtils;

public abstract class BcAsymmetricKeyWrapper
extends AsymmetricKeyWrapper {
    private AsymmetricKeyParameter publicKey;
    private SecureRandom random;

    public BcAsymmetricKeyWrapper(AlgorithmIdentifier encAlgId, AsymmetricKeyParameter publicKey) {
        super(encAlgId);
        this.publicKey = publicKey;
    }

    public BcAsymmetricKeyWrapper setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    @Override
    public byte[] generateWrappedKey(GenericKey encryptionKey) throws OperatorException {
        AsymmetricBlockCipher keyEncryptionCipher = this.createAsymmetricWrapper(this.getAlgorithmIdentifier().getAlgorithm());
        AsymmetricKeyParameter params = this.publicKey;
        if (this.random != null) {
            params = new ParametersWithRandom((CipherParameters)params, this.random);
        }
        try {
            byte[] keyEnc = OperatorUtils.getKeyBytes(encryptionKey);
            keyEncryptionCipher.init(true, (CipherParameters)params);
            return keyEncryptionCipher.processBlock(keyEnc, 0, keyEnc.length);
        }
        catch (InvalidCipherTextException e) {
            throw new OperatorException("unable to encrypt contents key", e);
        }
    }

    protected abstract AsymmetricBlockCipher createAsymmetricWrapper(ASN1ObjectIdentifier var1);
}

