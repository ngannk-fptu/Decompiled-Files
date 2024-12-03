/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DSAExt;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.DSAKCalculator;
import org.bouncycastle.crypto.signers.RandomDSAKCalculator;
import org.bouncycastle.crypto.signers.Utils;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.BigIntegers;

public class ECDSASigner
implements ECConstants,
DSAExt {
    private final DSAKCalculator kCalculator;
    private ECKeyParameters key;
    private SecureRandom random;

    public ECDSASigner() {
        this.kCalculator = new RandomDSAKCalculator();
    }

    public ECDSASigner(DSAKCalculator kCalculator) {
        this.kCalculator = kCalculator;
    }

    @Override
    public void init(boolean forSigning, CipherParameters param) {
        SecureRandom providedRandom = null;
        if (forSigning) {
            if (param instanceof ParametersWithRandom) {
                ParametersWithRandom rParam = (ParametersWithRandom)param;
                this.key = (ECPrivateKeyParameters)rParam.getParameters();
                providedRandom = rParam.getRandom();
            } else {
                this.key = (ECPrivateKeyParameters)param;
            }
        } else {
            this.key = (ECPublicKeyParameters)param;
        }
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("ECDSA", this.key, forSigning));
        this.random = this.initSecureRandom(forSigning && !this.kCalculator.isDeterministic(), providedRandom);
    }

    @Override
    public BigInteger getOrder() {
        return this.key.getParameters().getN();
    }

    @Override
    public BigInteger[] generateSignature(byte[] message) {
        BigInteger s;
        BigInteger k;
        ECPoint p;
        BigInteger r;
        ECDomainParameters ec = this.key.getParameters();
        BigInteger n = ec.getN();
        BigInteger e = this.calculateE(n, message);
        BigInteger d = ((ECPrivateKeyParameters)this.key).getD();
        if (this.kCalculator.isDeterministic()) {
            this.kCalculator.init(n, d, message);
        } else {
            this.kCalculator.init(n, this.random);
        }
        ECMultiplier basePointMultiplier = this.createBasePointMultiplier();
        do {
            k = this.kCalculator.nextK();
        } while ((r = (p = basePointMultiplier.multiply(ec.getG(), k).normalize()).getAffineXCoord().toBigInteger().mod(n)).equals(ZERO) || (s = BigIntegers.modOddInverse(n, k).multiply(e.add(d.multiply(r))).mod(n)).equals(ZERO));
        return new BigInteger[]{r, s};
    }

    @Override
    public boolean verifySignature(byte[] message, BigInteger r, BigInteger s) {
        ECFieldElement D;
        BigInteger cofactor;
        ECPoint Q;
        ECDomainParameters ec = this.key.getParameters();
        BigInteger n = ec.getN();
        BigInteger e = this.calculateE(n, message);
        if (r.compareTo(ONE) < 0 || r.compareTo(n) >= 0) {
            return false;
        }
        if (s.compareTo(ONE) < 0 || s.compareTo(n) >= 0) {
            return false;
        }
        BigInteger c = BigIntegers.modOddInverseVar(n, s);
        BigInteger u1 = e.multiply(c).mod(n);
        BigInteger u2 = r.multiply(c).mod(n);
        ECPoint G = ec.getG();
        ECPoint point = ECAlgorithms.sumOfTwoMultiplies(G, u1, Q = ((ECPublicKeyParameters)this.key).getQ(), u2);
        if (point.isInfinity()) {
            return false;
        }
        ECCurve curve = point.getCurve();
        if (curve != null && (cofactor = curve.getCofactor()) != null && cofactor.compareTo(EIGHT) <= 0 && (D = this.getDenominator(curve.getCoordinateSystem(), point)) != null && !D.isZero()) {
            ECFieldElement X = point.getXCoord();
            while (curve.isValidFieldElement(r)) {
                ECFieldElement R = curve.fromBigInteger(r).multiply(D);
                if (R.equals(X)) {
                    return true;
                }
                r = r.add(n);
            }
            return false;
        }
        BigInteger v = point.normalize().getAffineXCoord().toBigInteger().mod(n);
        return v.equals(r);
    }

    protected BigInteger calculateE(BigInteger n, byte[] message) {
        int log2n = n.bitLength();
        int messageBitLength = message.length * 8;
        BigInteger e = new BigInteger(1, message);
        if (log2n < messageBitLength) {
            e = e.shiftRight(messageBitLength - log2n);
        }
        return e;
    }

    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }

    protected ECFieldElement getDenominator(int coordinateSystem, ECPoint p) {
        switch (coordinateSystem) {
            case 1: 
            case 6: 
            case 7: {
                return p.getZCoord(0);
            }
            case 2: 
            case 3: 
            case 4: {
                return p.getZCoord(0).square();
            }
        }
        return null;
    }

    protected SecureRandom initSecureRandom(boolean needed, SecureRandom provided) {
        return needed ? CryptoServicesRegistrar.getSecureRandom(provided) : null;
    }
}

