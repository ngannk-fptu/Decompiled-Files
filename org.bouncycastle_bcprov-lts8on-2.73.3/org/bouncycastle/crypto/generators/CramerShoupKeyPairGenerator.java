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
import org.bouncycastle.crypto.params.CramerShoupKeyGenerationParameters;
import org.bouncycastle.crypto.params.CramerShoupParameters;
import org.bouncycastle.crypto.params.CramerShoupPrivateKeyParameters;
import org.bouncycastle.crypto.params.CramerShoupPublicKeyParameters;
import org.bouncycastle.util.BigIntegers;

public class CramerShoupKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private static final BigInteger ONE = BigInteger.valueOf(1L);
    private CramerShoupKeyGenerationParameters param;

    @Override
    public void init(KeyGenerationParameters param) {
        this.param = (CramerShoupKeyGenerationParameters)param;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("CramerShoupKeyGen", ConstraintUtils.bitsOfSecurityFor(this.param.getParameters().getP()), this.param.getParameters(), CryptoServicePurpose.KEYGEN));
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        CramerShoupParameters csParams = this.param.getParameters();
        CramerShoupPrivateKeyParameters sk = this.generatePrivateKey(this.param.getRandom(), csParams);
        CramerShoupPublicKeyParameters pk = this.calculatePublicKey(csParams, sk);
        sk.setPk(pk);
        return new AsymmetricCipherKeyPair(pk, sk);
    }

    private BigInteger generateRandomElement(BigInteger p, SecureRandom random) {
        return BigIntegers.createRandomInRange(ONE, p.subtract(ONE), random);
    }

    private CramerShoupPrivateKeyParameters generatePrivateKey(SecureRandom random, CramerShoupParameters csParams) {
        BigInteger p = csParams.getP();
        CramerShoupPrivateKeyParameters key = new CramerShoupPrivateKeyParameters(csParams, this.generateRandomElement(p, random), this.generateRandomElement(p, random), this.generateRandomElement(p, random), this.generateRandomElement(p, random), this.generateRandomElement(p, random));
        return key;
    }

    private CramerShoupPublicKeyParameters calculatePublicKey(CramerShoupParameters csParams, CramerShoupPrivateKeyParameters sk) {
        BigInteger g1 = csParams.getG1();
        BigInteger g2 = csParams.getG2();
        BigInteger p = csParams.getP();
        BigInteger c = g1.modPow(sk.getX1(), p).multiply(g2.modPow(sk.getX2(), p));
        BigInteger d = g1.modPow(sk.getY1(), p).multiply(g2.modPow(sk.getY2(), p));
        BigInteger h = g1.modPow(sk.getZ(), p);
        return new CramerShoupPublicKeyParameters(csParams, c, d, h);
    }
}

