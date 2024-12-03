/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.RawAgreement;
import org.bouncycastle.crypto.params.XDHUPrivateParameters;
import org.bouncycastle.crypto.params.XDHUPublicParameters;

public class XDHUnifiedAgreement
implements RawAgreement {
    private final RawAgreement xAgreement;
    private XDHUPrivateParameters privParams;

    public XDHUnifiedAgreement(RawAgreement xAgreement) {
        this.xAgreement = xAgreement;
    }

    @Override
    public void init(CipherParameters key) {
        this.privParams = (XDHUPrivateParameters)key;
        this.xAgreement.init(this.privParams.getStaticPrivateKey());
    }

    @Override
    public int getAgreementSize() {
        return this.xAgreement.getAgreementSize() * 2;
    }

    @Override
    public void calculateAgreement(CipherParameters publicKey, byte[] buf, int off) {
        XDHUPublicParameters pubParams = (XDHUPublicParameters)publicKey;
        this.xAgreement.init(this.privParams.getEphemeralPrivateKey());
        this.xAgreement.calculateAgreement(pubParams.getEphemeralPublicKey(), buf, off);
        this.xAgreement.init(this.privParams.getStaticPrivateKey());
        this.xAgreement.calculateAgreement(pubParams.getStaticPublicKey(), buf, off + this.xAgreement.getAgreementSize());
    }
}

