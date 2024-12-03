/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.RawAgreement;
import org.bouncycastle.crypto.agreement.Utils;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;

public final class X25519Agreement
implements RawAgreement {
    private X25519PrivateKeyParameters privateKey;

    @Override
    public void init(CipherParameters parameters) {
        this.privateKey = (X25519PrivateKeyParameters)parameters;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("X25519", this.privateKey));
    }

    @Override
    public int getAgreementSize() {
        return 32;
    }

    @Override
    public void calculateAgreement(CipherParameters publicKey, byte[] buf, int off) {
        this.privateKey.generateSecret((X25519PublicKeyParameters)publicKey, buf, off);
    }
}

