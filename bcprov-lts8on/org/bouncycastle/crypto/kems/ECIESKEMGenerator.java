/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.kems;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.kems.SecretWithEncapsulationImpl;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class ECIESKEMGenerator
implements EncapsulatedSecretGenerator {
    private static final BigInteger ONE = BigInteger.valueOf(1L);
    private DerivationFunction kdf;
    private SecureRandom rnd;
    private final int keySize;
    private boolean CofactorMode;
    private boolean OldCofactorMode;
    private boolean SingleHashMode;

    public ECIESKEMGenerator(int keySize, DerivationFunction kdf, SecureRandom rnd) {
        this.keySize = keySize;
        this.kdf = kdf;
        this.rnd = rnd;
        this.CofactorMode = false;
        this.OldCofactorMode = false;
        this.SingleHashMode = false;
    }

    public ECIESKEMGenerator(int keyLen, DerivationFunction kdf, SecureRandom rnd, boolean cofactorMode, boolean oldCofactorMode, boolean singleHashMode) {
        this.kdf = kdf;
        this.rnd = rnd;
        this.keySize = keyLen;
        this.CofactorMode = cofactorMode;
        this.OldCofactorMode = cofactorMode ? false : oldCofactorMode;
        this.SingleHashMode = singleHashMode;
    }

    private ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }

    @Override
    public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter recipientKey) {
        if (!(recipientKey instanceof ECKeyParameters)) {
            throw new IllegalArgumentException("EC key required");
        }
        ECPublicKeyParameters ecPubKey = (ECPublicKeyParameters)recipientKey;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("ECIESKem", ConstraintUtils.bitsOfSecurityFor(ecPubKey.getParameters().getCurve()), recipientKey, CryptoServicePurpose.ENCRYPTION));
        ECDomainParameters ecParams = ecPubKey.getParameters();
        ECCurve curve = ecParams.getCurve();
        BigInteger n = ecParams.getN();
        BigInteger h = ecParams.getH();
        BigInteger r = BigIntegers.createRandomInRange(ONE, n, this.rnd);
        BigInteger rPrime = this.OldCofactorMode ? r.multiply(h).mod(n) : r;
        ECMultiplier basePointMultiplier = this.createBasePointMultiplier();
        ECPoint[] ghTilde = new ECPoint[]{basePointMultiplier.multiply(ecParams.getG(), r), ecPubKey.getQ().multiply(rPrime)};
        curve.normalizeAll(ghTilde);
        ECPoint gTilde = ghTilde[0];
        ECPoint hTilde = ghTilde[1];
        byte[] C = gTilde.getEncoded(false);
        byte[] enc = new byte[C.length];
        System.arraycopy(C, 0, enc, 0, C.length);
        byte[] PEH = hTilde.getAffineXCoord().getEncoded();
        return new SecretWithEncapsulationImpl(ECIESKEMGenerator.deriveKey(this.SingleHashMode, this.kdf, this.keySize, C, PEH), enc);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static byte[] deriveKey(boolean SingleHashMode, DerivationFunction kdf, int keyLen, byte[] C, byte[] PEH) {
        byte[] kdfInput = PEH;
        if (!SingleHashMode) {
            kdfInput = Arrays.concatenate(C, PEH);
            Arrays.fill(PEH, (byte)0);
        }
        try {
            kdf.init(new KDFParameters(kdfInput, null));
            byte[] K = new byte[keyLen];
            kdf.generateBytes(K, 0, K.length);
            byte[] byArray = K;
            return byArray;
        }
        finally {
            Arrays.fill(kdfInput, (byte)0);
        }
    }
}

