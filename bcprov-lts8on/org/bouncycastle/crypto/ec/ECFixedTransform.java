/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.ec;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.ec.ECPair;
import org.bouncycastle.crypto.ec.ECPairFactorTransform;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;

public class ECFixedTransform
implements ECPairFactorTransform {
    private ECPublicKeyParameters key;
    private BigInteger k;

    public ECFixedTransform(BigInteger k) {
        this.k = k;
    }

    @Override
    public void init(CipherParameters param) {
        if (!(param instanceof ECPublicKeyParameters)) {
            throw new IllegalArgumentException("ECPublicKeyParameters are required for fixed transform.");
        }
        this.key = (ECPublicKeyParameters)param;
    }

    @Override
    public ECPair transform(ECPair cipherText) {
        if (this.key == null) {
            throw new IllegalStateException("ECFixedTransform not initialised");
        }
        ECDomainParameters ec = this.key.getParameters();
        BigInteger n = ec.getN();
        ECMultiplier basePointMultiplier = this.createBasePointMultiplier();
        BigInteger k = this.k.mod(n);
        ECPoint[] gamma_phi = new ECPoint[]{basePointMultiplier.multiply(ec.getG(), k).add(ECAlgorithms.cleanPoint(ec.getCurve(), cipherText.getX())), this.key.getQ().multiply(k).add(ECAlgorithms.cleanPoint(ec.getCurve(), cipherText.getY()))};
        ec.getCurve().normalizeAll(gamma_phi);
        return new ECPair(gamma_phi[0], gamma_phi[1]);
    }

    @Override
    public BigInteger getTransformValue() {
        return this.k;
    }

    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }
}

