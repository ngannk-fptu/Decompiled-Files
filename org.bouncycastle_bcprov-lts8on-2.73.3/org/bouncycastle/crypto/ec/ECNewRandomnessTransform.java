/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.ec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.ec.ECPair;
import org.bouncycastle.crypto.ec.ECPairFactorTransform;
import org.bouncycastle.crypto.ec.ECUtil;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;

public class ECNewRandomnessTransform
implements ECPairFactorTransform {
    private ECPublicKeyParameters key;
    private SecureRandom random;
    private BigInteger lastK;

    @Override
    public void init(CipherParameters param) {
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom p = (ParametersWithRandom)param;
            if (!(p.getParameters() instanceof ECPublicKeyParameters)) {
                throw new IllegalArgumentException("ECPublicKeyParameters are required for new randomness transform.");
            }
            this.key = (ECPublicKeyParameters)p.getParameters();
            this.random = p.getRandom();
        } else {
            if (!(param instanceof ECPublicKeyParameters)) {
                throw new IllegalArgumentException("ECPublicKeyParameters are required for new randomness transform.");
            }
            this.key = (ECPublicKeyParameters)param;
            this.random = CryptoServicesRegistrar.getSecureRandom();
        }
    }

    @Override
    public ECPair transform(ECPair cipherText) {
        if (this.key == null) {
            throw new IllegalStateException("ECNewRandomnessTransform not initialised");
        }
        ECDomainParameters ec = this.key.getParameters();
        BigInteger n = ec.getN();
        ECMultiplier basePointMultiplier = this.createBasePointMultiplier();
        BigInteger k = ECUtil.generateK(n, this.random);
        ECPoint[] gamma_phi = new ECPoint[]{basePointMultiplier.multiply(ec.getG(), k).add(ECAlgorithms.cleanPoint(ec.getCurve(), cipherText.getX())), this.key.getQ().multiply(k).add(ECAlgorithms.cleanPoint(ec.getCurve(), cipherText.getY()))};
        ec.getCurve().normalizeAll(gamma_phi);
        this.lastK = k;
        return new ECPair(gamma_phi[0], gamma_phi[1]);
    }

    @Override
    public BigInteger getTransformValue() {
        return this.lastK;
    }

    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }
}

