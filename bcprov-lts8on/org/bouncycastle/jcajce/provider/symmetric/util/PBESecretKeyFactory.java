/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.symmetric.util.PBE;

public class PBESecretKeyFactory
extends BaseSecretKeyFactory
implements PBE {
    private boolean forCipher;
    private int scheme;
    private int digest;
    private int keySize;
    private int ivSize;

    public PBESecretKeyFactory(String algorithm, ASN1ObjectIdentifier oid, boolean forCipher, int scheme, int digest, int keySize, int ivSize) {
        super(algorithm, oid);
        this.forCipher = forCipher;
        this.scheme = scheme;
        this.digest = digest;
        this.keySize = keySize;
        this.ivSize = ivSize;
    }

    @Override
    protected SecretKey engineGenerateSecret(KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof PBEKeySpec) {
            PBEKeySpec pbeSpec = (PBEKeySpec)keySpec;
            if (pbeSpec.getSalt() == null) {
                return new BCPBEKey(this.algName, this.algOid, this.scheme, this.digest, this.keySize, this.ivSize, pbeSpec, null);
            }
            CipherParameters param = this.forCipher ? PBE.Util.makePBEParameters(pbeSpec, this.scheme, this.digest, this.keySize, this.ivSize) : PBE.Util.makePBEMacParameters(pbeSpec, this.scheme, this.digest, this.keySize);
            return new BCPBEKey(this.algName, this.algOid, this.scheme, this.digest, this.keySize, this.ivSize, pbeSpec, param);
        }
        throw new InvalidKeySpecException("Invalid KeySpec");
    }
}

