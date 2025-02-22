/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.kems;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.kems.SecretWithEncapsulationImpl;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.util.BigIntegers;

public class RSAKEMGenerator
implements EncapsulatedSecretGenerator {
    private static final BigInteger ZERO = BigInteger.valueOf(0L);
    private static final BigInteger ONE = BigInteger.valueOf(1L);
    private final int keyLen;
    private DerivationFunction kdf;
    private SecureRandom rnd;

    public RSAKEMGenerator(int keyLen, DerivationFunction kdf, SecureRandom rnd) {
        this.keyLen = keyLen;
        this.kdf = kdf;
        this.rnd = rnd;
    }

    @Override
    public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter recipientKey) {
        RSAKeyParameters key = (RSAKeyParameters)recipientKey;
        if (key.isPrivate()) {
            throw new IllegalArgumentException("public key required for encryption");
        }
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("RSAKem", ConstraintUtils.bitsOfSecurityFor(key.getModulus()), key, CryptoServicePurpose.ENCRYPTION));
        BigInteger n = key.getModulus();
        BigInteger e = key.getExponent();
        BigInteger r = BigIntegers.createRandomInRange(ZERO, n.subtract(ONE), this.rnd);
        BigInteger c = r.modPow(e, n);
        byte[] C = BigIntegers.asUnsignedByteArray((n.bitLength() + 7) / 8, c);
        return new SecretWithEncapsulationImpl(RSAKEMGenerator.generateKey(this.kdf, n, r, this.keyLen), C);
    }

    static byte[] generateKey(DerivationFunction kdf, BigInteger n, BigInteger r, int keyLen) {
        byte[] R = BigIntegers.asUnsignedByteArray((n.bitLength() + 7) / 8, r);
        kdf.init(new KDFParameters(R, null));
        byte[] K = new byte[keyLen];
        kdf.generateBytes(K, 0, K.length);
        return K;
    }
}

