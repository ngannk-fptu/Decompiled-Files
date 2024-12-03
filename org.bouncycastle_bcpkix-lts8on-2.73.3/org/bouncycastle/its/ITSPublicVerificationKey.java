/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey
 */
package org.bouncycastle.its;

import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey;

public class ITSPublicVerificationKey {
    protected final PublicVerificationKey verificationKey;

    public ITSPublicVerificationKey(PublicVerificationKey encryptionKey) {
        this.verificationKey = encryptionKey;
    }

    public PublicVerificationKey toASN1Structure() {
        return this.verificationKey;
    }
}

