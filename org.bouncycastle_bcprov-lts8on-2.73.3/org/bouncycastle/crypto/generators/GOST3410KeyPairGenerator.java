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
import org.bouncycastle.crypto.params.GOST3410KeyGenerationParameters;
import org.bouncycastle.crypto.params.GOST3410Parameters;
import org.bouncycastle.crypto.params.GOST3410PrivateKeyParameters;
import org.bouncycastle.crypto.params.GOST3410PublicKeyParameters;
import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.util.BigIntegers;

public class GOST3410KeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private GOST3410KeyGenerationParameters param;

    @Override
    public void init(KeyGenerationParameters param) {
        this.param = (GOST3410KeyGenerationParameters)param;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("GOST3410KeyGen", ConstraintUtils.bitsOfSecurityFor(this.param.getParameters().getP()), this.param.getParameters(), CryptoServicePurpose.KEYGEN));
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        BigInteger x;
        GOST3410Parameters GOST3410Params2 = this.param.getParameters();
        SecureRandom random = this.param.getRandom();
        BigInteger q = GOST3410Params2.getQ();
        BigInteger p = GOST3410Params2.getP();
        BigInteger a = GOST3410Params2.getA();
        int minWeight = 64;
        while ((x = BigIntegers.createRandomBigInteger(256, random)).signum() < 1 || x.compareTo(q) >= 0 || WNafUtil.getNafWeight(x) < minWeight) {
        }
        BigInteger y = a.modPow(x, p);
        return new AsymmetricCipherKeyPair(new GOST3410PublicKeyParameters(y, GOST3410Params2), new GOST3410PrivateKeyParameters(x, GOST3410Params2));
    }
}

