/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.kems;

import java.math.BigInteger;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.kems.RSAKEMGenerator;
import org.bouncycastle.crypto.params.RSAKeyParameters;

public class RSAKEMExtractor
implements EncapsulatedSecretExtractor {
    private final RSAKeyParameters privKey;
    private final int keyLen;
    private DerivationFunction kdf;

    public RSAKEMExtractor(RSAKeyParameters privKey, int keyLen, DerivationFunction kdf) {
        if (!privKey.isPrivate()) {
            throw new IllegalArgumentException("private key required for encryption");
        }
        this.privKey = privKey;
        this.keyLen = keyLen;
        this.kdf = kdf;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("RSAKem", ConstraintUtils.bitsOfSecurityFor(this.privKey.getModulus()), privKey, CryptoServicePurpose.DECRYPTION));
    }

    @Override
    public byte[] extractSecret(byte[] encapsulation) {
        BigInteger n = this.privKey.getModulus();
        BigInteger d = this.privKey.getExponent();
        BigInteger c = new BigInteger(1, encapsulation);
        BigInteger r = c.modPow(d, n);
        return RSAKEMGenerator.generateKey(this.kdf, n, r, this.keyLen);
    }

    @Override
    public int getEncapsulationLength() {
        return (this.privKey.getModulus().bitLength() + 7) / 8;
    }
}

