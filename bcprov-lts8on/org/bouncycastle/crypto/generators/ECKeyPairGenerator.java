/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.util.BigIntegers;

public class ECKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator,
ECConstants {
    private final String name;
    ECDomainParameters params;
    SecureRandom random;

    public ECKeyPairGenerator() {
        this("ECKeyGen");
    }

    protected ECKeyPairGenerator(String name) {
        this.name = name;
    }

    @Override
    public void init(KeyGenerationParameters param) {
        ECKeyGenerationParameters ecP = (ECKeyGenerationParameters)param;
        this.random = ecP.getRandom();
        this.params = ecP.getDomainParameters();
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.name, ConstraintUtils.bitsOfSecurityFor(this.params.getCurve()), ecP.getDomainParameters(), CryptoServicePurpose.KEYGEN));
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        BigInteger d;
        BigInteger n = this.params.getN();
        int nBitLength = n.bitLength();
        int minWeight = nBitLength >>> 2;
        while (this.isOutOfRangeD(d = BigIntegers.createRandomBigInteger(nBitLength, this.random), n) || WNafUtil.getNafWeight(d) < minWeight) {
        }
        ECPoint Q = this.createBasePointMultiplier().multiply(this.params.getG(), d);
        return new AsymmetricCipherKeyPair(new ECPublicKeyParameters(Q, this.params), new ECPrivateKeyParameters(d, this.params));
    }

    protected boolean isOutOfRangeD(BigInteger d, BigInteger n) {
        return d.compareTo(ONE) < 0 || d.compareTo(n) >= 0;
    }

    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }
}

