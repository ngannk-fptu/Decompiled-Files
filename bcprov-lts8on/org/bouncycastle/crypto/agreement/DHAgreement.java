/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.agreement.Utils;
import org.bouncycastle.crypto.generators.DHKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DHKeyGenerationParameters;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;

public class DHAgreement {
    private static final BigInteger ONE = BigInteger.valueOf(1L);
    private DHPrivateKeyParameters key;
    private DHParameters dhParams;
    private BigInteger privateValue;
    private SecureRandom random;

    public void init(CipherParameters param) {
        AsymmetricKeyParameter kParam;
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom rParam = (ParametersWithRandom)param;
            this.random = rParam.getRandom();
            kParam = (AsymmetricKeyParameter)rParam.getParameters();
        } else {
            this.random = CryptoServicesRegistrar.getSecureRandom();
            kParam = (AsymmetricKeyParameter)param;
        }
        if (!(kParam instanceof DHPrivateKeyParameters)) {
            throw new IllegalArgumentException("DHEngine expects DHPrivateKeyParameters");
        }
        this.key = (DHPrivateKeyParameters)kParam;
        this.dhParams = this.key.getParameters();
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("DH", this.key));
    }

    public BigInteger calculateMessage() {
        DHKeyPairGenerator dhGen = new DHKeyPairGenerator();
        dhGen.init(new DHKeyGenerationParameters(this.random, this.dhParams));
        AsymmetricCipherKeyPair dhPair = dhGen.generateKeyPair();
        this.privateValue = ((DHPrivateKeyParameters)dhPair.getPrivate()).getX();
        return ((DHPublicKeyParameters)dhPair.getPublic()).getY();
    }

    public BigInteger calculateAgreement(DHPublicKeyParameters pub, BigInteger message) {
        if (!pub.getParameters().equals(this.dhParams)) {
            throw new IllegalArgumentException("Diffie-Hellman public key has wrong parameters.");
        }
        BigInteger p = this.dhParams.getP();
        BigInteger peerY = pub.getY();
        if (peerY == null || peerY.compareTo(ONE) <= 0 || peerY.compareTo(p.subtract(ONE)) >= 0) {
            throw new IllegalArgumentException("Diffie-Hellman public key is weak");
        }
        BigInteger result = peerY.modPow(this.privateValue, p);
        if (result.equals(ONE)) {
            throw new IllegalStateException("Shared key can't be 1");
        }
        return message.modPow(this.key.getX(), p).multiply(result).mod(p);
    }
}

