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
import org.bouncycastle.crypto.signers.Utils;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class DSTU4145Signer
implements DSAExt {
    private static final BigInteger ONE = BigInteger.valueOf(1L);
    private ECKeyParameters key;
    private SecureRandom random;

    @Override
    public void init(boolean forSigning, CipherParameters param) {
        if (forSigning) {
            if (param instanceof ParametersWithRandom) {
                ParametersWithRandom rParam = (ParametersWithRandom)param;
                this.random = rParam.getRandom();
                param = rParam.getParameters();
            } else {
                this.random = CryptoServicesRegistrar.getSecureRandom();
            }
            this.key = (ECPrivateKeyParameters)param;
        } else {
            this.key = (ECPublicKeyParameters)param;
        }
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("DSTU4145", this.key, forSigning));
    }

    @Override
    public BigInteger getOrder() {
        return this.key.getParameters().getN();
    }

    @Override
    public BigInteger[] generateSignature(byte[] message) {
        BigInteger s;
        ECFieldElement y;
        BigInteger r;
        BigInteger e;
        ECFieldElement Fe;
        ECDomainParameters ec = this.key.getParameters();
        ECCurve curve = ec.getCurve();
        ECFieldElement h = DSTU4145Signer.hash2FieldElement(curve, message);
        if (h.isZero()) {
            h = curve.fromBigInteger(ONE);
        }
        BigInteger n = ec.getN();
        BigInteger d = ((ECPrivateKeyParameters)this.key).getD();
        ECMultiplier basePointMultiplier = this.createBasePointMultiplier();
        do {
            e = DSTU4145Signer.generateRandomInteger(n, this.random);
        } while ((Fe = basePointMultiplier.multiply(ec.getG(), e).normalize().getAffineXCoord()).isZero() || (r = DSTU4145Signer.fieldElement2Integer(n, y = h.multiply(Fe))).signum() == 0 || (s = r.multiply(d).add(e).mod(n)).signum() == 0);
        return new BigInteger[]{r, s};
    }

    @Override
    public boolean verifySignature(byte[] message, BigInteger r, BigInteger s) {
        ECPoint R;
        if (r.signum() <= 0 || s.signum() <= 0) {
            return false;
        }
        ECDomainParameters parameters = this.key.getParameters();
        BigInteger n = parameters.getN();
        if (r.compareTo(n) >= 0 || s.compareTo(n) >= 0) {
            return false;
        }
        ECCurve curve = parameters.getCurve();
        ECFieldElement h = DSTU4145Signer.hash2FieldElement(curve, message);
        if (h.isZero()) {
            h = curve.fromBigInteger(ONE);
        }
        if ((R = ECAlgorithms.sumOfTwoMultiplies(parameters.getG(), s, ((ECPublicKeyParameters)this.key).getQ(), r).normalize()).isInfinity()) {
            return false;
        }
        ECFieldElement y = h.multiply(R.getAffineXCoord());
        return DSTU4145Signer.fieldElement2Integer(n, y).compareTo(r) == 0;
    }

    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }

    private static BigInteger generateRandomInteger(BigInteger n, SecureRandom random) {
        return BigIntegers.createRandomBigInteger(n.bitLength() - 1, random);
    }

    private static ECFieldElement hash2FieldElement(ECCurve curve, byte[] hash) {
        byte[] data = Arrays.reverse(hash);
        return curve.fromBigInteger(DSTU4145Signer.truncate(new BigInteger(1, data), curve.getFieldSize()));
    }

    private static BigInteger fieldElement2Integer(BigInteger n, ECFieldElement fe) {
        return DSTU4145Signer.truncate(fe.toBigInteger(), n.bitLength() - 1);
    }

    private static BigInteger truncate(BigInteger x, int bitLength) {
        if (x.bitLength() > bitLength) {
            x = x.mod(ONE.shiftLeft(bitLength));
        }
        return x;
    }
}

