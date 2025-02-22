/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.kems;

import java.math.BigInteger;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.kems.ECIESKEMGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class ECIESKEMExtractor
implements EncapsulatedSecretExtractor {
    private final ECPrivateKeyParameters decKey;
    private int keyLen;
    private DerivationFunction kdf;
    private boolean CofactorMode;
    private boolean OldCofactorMode;
    private boolean SingleHashMode;

    public ECIESKEMExtractor(ECPrivateKeyParameters decKey, int keyLen, DerivationFunction kdf) {
        this.decKey = decKey;
        this.keyLen = keyLen;
        this.kdf = kdf;
        this.CofactorMode = false;
        this.OldCofactorMode = false;
        this.SingleHashMode = false;
    }

    public ECIESKEMExtractor(ECPrivateKeyParameters decKey, int keyLen, DerivationFunction kdf, boolean cofactorMode, boolean oldCofactorMode, boolean singleHashMode) {
        this.decKey = decKey;
        this.keyLen = keyLen;
        this.kdf = kdf;
        this.CofactorMode = cofactorMode;
        this.OldCofactorMode = cofactorMode ? false : oldCofactorMode;
        this.SingleHashMode = singleHashMode;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("ECIESKem", ConstraintUtils.bitsOfSecurityFor(this.decKey.getParameters().getCurve()), decKey, CryptoServicePurpose.DECRYPTION));
    }

    @Override
    public byte[] extractSecret(byte[] encapsulation) {
        ECPrivateKeyParameters ecPrivKey = this.decKey;
        ECDomainParameters ecParams = ecPrivKey.getParameters();
        ECCurve curve = ecParams.getCurve();
        BigInteger n = ecParams.getN();
        BigInteger h = ecParams.getH();
        ECPoint gHat = curve.decodePoint(encapsulation);
        if (this.CofactorMode || this.OldCofactorMode) {
            gHat = gHat.multiply(h);
        }
        BigInteger xHat = ecPrivKey.getD();
        if (this.CofactorMode) {
            xHat = xHat.multiply(ecParams.getHInv()).mod(n);
        }
        ECPoint hTilde = gHat.multiply(xHat).normalize();
        byte[] PEH = hTilde.getAffineXCoord().getEncoded();
        return ECIESKEMGenerator.deriveKey(this.SingleHashMode, this.kdf, this.keyLen, encapsulation, PEH);
    }

    @Override
    public int getEncapsulationLength() {
        return this.decKey.getParameters().getCurve().getFieldSize() / 8 * 2 + 1;
    }
}

