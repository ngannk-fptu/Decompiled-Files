/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.agreement.Utils;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.MQVPrivateParameters;
import org.bouncycastle.crypto.params.MQVPublicParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Properties;

public class ECMQVBasicAgreement
implements BasicAgreement {
    MQVPrivateParameters privParams;

    @Override
    public void init(CipherParameters key) {
        this.privParams = (MQVPrivateParameters)key;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("ECMQV", this.privParams.getStaticPrivateKey()));
    }

    @Override
    public int getFieldSize() {
        return (this.privParams.getStaticPrivateKey().getParameters().getCurve().getFieldSize() + 7) / 8;
    }

    @Override
    public BigInteger calculateAgreement(CipherParameters pubKey) {
        if (Properties.isOverrideSet("org.bouncycastle.ec.disable_mqv")) {
            throw new IllegalStateException("ECMQV explicitly disabled");
        }
        MQVPublicParameters pubParams = (MQVPublicParameters)pubKey;
        ECPrivateKeyParameters staticPrivateKey = this.privParams.getStaticPrivateKey();
        ECDomainParameters parameters = staticPrivateKey.getParameters();
        if (!parameters.equals(pubParams.getStaticPublicKey().getParameters())) {
            throw new IllegalStateException("ECMQV public key components have wrong domain parameters");
        }
        ECPoint agreement = this.calculateMqvAgreement(parameters, staticPrivateKey, this.privParams.getEphemeralPrivateKey(), this.privParams.getEphemeralPublicKey(), pubParams.getStaticPublicKey(), pubParams.getEphemeralPublicKey()).normalize();
        if (agreement.isInfinity()) {
            throw new IllegalStateException("Infinity is not a valid agreement value for MQV");
        }
        return agreement.getAffineXCoord().toBigInteger();
    }

    private ECPoint calculateMqvAgreement(ECDomainParameters parameters, ECPrivateKeyParameters d1U, ECPrivateKeyParameters d2U, ECPublicKeyParameters Q2U, ECPublicKeyParameters Q1V, ECPublicKeyParameters Q2V) {
        BigInteger n = parameters.getN();
        int e = (n.bitLength() + 1) / 2;
        BigInteger powE = ECConstants.ONE.shiftLeft(e);
        ECCurve curve = parameters.getCurve();
        ECPoint q2u = ECAlgorithms.cleanPoint(curve, Q2U.getQ());
        ECPoint q1v = ECAlgorithms.cleanPoint(curve, Q1V.getQ());
        ECPoint q2v = ECAlgorithms.cleanPoint(curve, Q2V.getQ());
        BigInteger x = q2u.getAffineXCoord().toBigInteger();
        BigInteger xBar = x.mod(powE);
        BigInteger Q2UBar = xBar.setBit(e);
        BigInteger s = d1U.getD().multiply(Q2UBar).add(d2U.getD()).mod(n);
        BigInteger xPrime = q2v.getAffineXCoord().toBigInteger();
        BigInteger xPrimeBar = xPrime.mod(powE);
        BigInteger Q2VBar = xPrimeBar.setBit(e);
        BigInteger hs = parameters.getH().multiply(s).mod(n);
        return ECAlgorithms.sumOfTwoMultiplies(q1v, Q2VBar.multiply(hs).mod(n), q2v, hs);
    }
}

