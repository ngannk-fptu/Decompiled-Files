/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.ec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.ec.ECEncryptor;
import org.bouncycastle.crypto.ec.ECPair;
import org.bouncycastle.crypto.ec.ECUtil;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;

public class ECElGamalEncryptor
implements ECEncryptor {
    private ECPublicKeyParameters key;
    private SecureRandom random;

    @Override
    public void init(CipherParameters param) {
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom p = (ParametersWithRandom)param;
            if (!(p.getParameters() instanceof ECPublicKeyParameters)) {
                throw new IllegalArgumentException("ECPublicKeyParameters are required for encryption.");
            }
            this.key = (ECPublicKeyParameters)p.getParameters();
            this.random = p.getRandom();
        } else {
            if (!(param instanceof ECPublicKeyParameters)) {
                throw new IllegalArgumentException("ECPublicKeyParameters are required for encryption.");
            }
            this.key = (ECPublicKeyParameters)param;
            this.random = CryptoServicesRegistrar.getSecureRandom();
        }
    }

    @Override
    public ECPair encrypt(ECPoint point) {
        if (this.key == null) {
            throw new IllegalStateException("ECElGamalEncryptor not initialised");
        }
        ECDomainParameters ec = this.key.getParameters();
        BigInteger k = ECUtil.generateK(ec.getN(), this.random);
        ECMultiplier basePointMultiplier = this.createBasePointMultiplier();
        ECPoint[] gamma_phi = new ECPoint[]{basePointMultiplier.multiply(ec.getG(), k), this.key.getQ().multiply(k).add(ECAlgorithms.cleanPoint(ec.getCurve(), point))};
        ec.getCurve().normalizeAll(gamma_phi);
        return new ECPair(gamma_phi[0], gamma_phi[1]);
    }

    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }
}

