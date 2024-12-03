/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.agreement.DHBasicAgreement;
import org.bouncycastle.crypto.agreement.Utils;
import org.bouncycastle.crypto.params.DHUPrivateParameters;
import org.bouncycastle.crypto.params.DHUPublicParameters;
import org.bouncycastle.util.BigIntegers;

public class DHUnifiedAgreement {
    private DHUPrivateParameters privParams;

    public void init(CipherParameters key) {
        this.privParams = (DHUPrivateParameters)key;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("DHU", this.privParams.getStaticPrivateKey()));
    }

    public int getFieldSize() {
        return (this.privParams.getStaticPrivateKey().getParameters().getP().bitLength() + 7) / 8;
    }

    public byte[] calculateAgreement(CipherParameters pubKey) {
        DHUPublicParameters pubParams = (DHUPublicParameters)pubKey;
        DHBasicAgreement sAgree = new DHBasicAgreement();
        DHBasicAgreement eAgree = new DHBasicAgreement();
        sAgree.init(this.privParams.getStaticPrivateKey());
        BigInteger sComp = sAgree.calculateAgreement(pubParams.getStaticPublicKey());
        eAgree.init(this.privParams.getEphemeralPrivateKey());
        BigInteger eComp = eAgree.calculateAgreement(pubParams.getEphemeralPublicKey());
        int fieldSize = this.getFieldSize();
        byte[] result = new byte[fieldSize * 2];
        BigIntegers.asUnsignedByteArray(eComp, result, 0, fieldSize);
        BigIntegers.asUnsignedByteArray(sComp, result, fieldSize, fieldSize);
        return result;
    }
}

