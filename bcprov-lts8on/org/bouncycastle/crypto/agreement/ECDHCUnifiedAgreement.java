/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.agreement.ECDHCBasicAgreement;
import org.bouncycastle.crypto.agreement.Utils;
import org.bouncycastle.crypto.params.ECDHUPrivateParameters;
import org.bouncycastle.crypto.params.ECDHUPublicParameters;
import org.bouncycastle.util.BigIntegers;

public class ECDHCUnifiedAgreement {
    private ECDHUPrivateParameters privParams;

    public void init(CipherParameters key) {
        this.privParams = (ECDHUPrivateParameters)key;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("ECCDHU", this.privParams.getStaticPrivateKey()));
    }

    public int getFieldSize() {
        return (this.privParams.getStaticPrivateKey().getParameters().getCurve().getFieldSize() + 7) / 8;
    }

    public byte[] calculateAgreement(CipherParameters pubKey) {
        ECDHUPublicParameters pubParams = (ECDHUPublicParameters)pubKey;
        ECDHCBasicAgreement sAgree = new ECDHCBasicAgreement();
        ECDHCBasicAgreement eAgree = new ECDHCBasicAgreement();
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

