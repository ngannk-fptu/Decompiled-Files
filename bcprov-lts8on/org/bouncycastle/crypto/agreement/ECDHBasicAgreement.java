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
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECPoint;

public class ECDHBasicAgreement
implements BasicAgreement {
    private ECPrivateKeyParameters key;

    @Override
    public void init(CipherParameters key) {
        this.key = (ECPrivateKeyParameters)key;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("ECDH", this.key));
    }

    @Override
    public int getFieldSize() {
        return (this.key.getParameters().getCurve().getFieldSize() + 7) / 8;
    }

    @Override
    public BigInteger calculateAgreement(CipherParameters pubKey) {
        ECPoint P;
        ECPublicKeyParameters pub = (ECPublicKeyParameters)pubKey;
        ECDomainParameters params = this.key.getParameters();
        if (!params.equals(pub.getParameters())) {
            throw new IllegalStateException("ECDH public key has wrong domain parameters");
        }
        BigInteger d = this.key.getD();
        ECPoint Q = ECAlgorithms.cleanPoint(params.getCurve(), pub.getQ());
        if (Q.isInfinity()) {
            throw new IllegalStateException("Infinity is not a valid public key for ECDH");
        }
        BigInteger h = params.getH();
        if (!h.equals(ECConstants.ONE)) {
            d = params.getHInv().multiply(d).mod(params.getN());
            Q = ECAlgorithms.referenceMultiply(Q, h);
        }
        if ((P = Q.multiply(d).normalize()).isInfinity()) {
            throw new IllegalStateException("Infinity is not a valid agreement value for ECDH");
        }
        return P.getAffineXCoord().toBigInteger();
    }
}

