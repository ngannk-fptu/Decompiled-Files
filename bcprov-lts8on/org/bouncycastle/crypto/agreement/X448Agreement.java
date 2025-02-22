/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.RawAgreement;
import org.bouncycastle.crypto.agreement.Utils;
import org.bouncycastle.crypto.params.X448PrivateKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;

public final class X448Agreement
implements RawAgreement {
    private X448PrivateKeyParameters privateKey;

    @Override
    public void init(CipherParameters parameters) {
        this.privateKey = (X448PrivateKeyParameters)parameters;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("X448", this.privateKey));
    }

    @Override
    public int getAgreementSize() {
        return 56;
    }

    @Override
    public void calculateAgreement(CipherParameters publicKey, byte[] buf, int off) {
        this.privateKey.generateSecret((X448PublicKeyParameters)publicKey, buf, off);
    }
}

