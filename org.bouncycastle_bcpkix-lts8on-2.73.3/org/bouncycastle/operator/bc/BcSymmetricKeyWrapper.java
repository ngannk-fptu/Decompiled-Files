/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.crypto.CipherParameters
 *  org.bouncycastle.crypto.Wrapper
 *  org.bouncycastle.crypto.params.KeyParameter
 *  org.bouncycastle.crypto.params.ParametersWithRandom
 */
package org.bouncycastle.operator.bc;

import java.security.SecureRandom;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.SymmetricKeyWrapper;
import org.bouncycastle.operator.bc.OperatorUtils;

public class BcSymmetricKeyWrapper
extends SymmetricKeyWrapper {
    private SecureRandom random;
    private Wrapper wrapper;
    private KeyParameter wrappingKey;

    public BcSymmetricKeyWrapper(AlgorithmIdentifier wrappingAlgorithm, Wrapper wrapper, KeyParameter wrappingKey) {
        super(wrappingAlgorithm);
        this.wrapper = wrapper;
        this.wrappingKey = wrappingKey;
    }

    public BcSymmetricKeyWrapper setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    @Override
    public byte[] generateWrappedKey(GenericKey encryptionKey) throws OperatorException {
        byte[] contentEncryptionKeySpec = OperatorUtils.getKeyBytes(encryptionKey);
        if (this.random == null) {
            this.wrapper.init(true, (CipherParameters)this.wrappingKey);
        } else {
            this.wrapper.init(true, (CipherParameters)new ParametersWithRandom((CipherParameters)this.wrappingKey, this.random));
        }
        return this.wrapper.wrap(contentEncryptionKeySpec, 0, contentEncryptionKeySpec.length);
    }
}

