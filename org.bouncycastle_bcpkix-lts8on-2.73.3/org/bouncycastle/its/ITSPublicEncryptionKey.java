/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.SymmAlgorithm
 */
package org.bouncycastle.its;

import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SymmAlgorithm;

public class ITSPublicEncryptionKey {
    protected final PublicEncryptionKey encryptionKey;

    public ITSPublicEncryptionKey(PublicEncryptionKey encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public PublicEncryptionKey toASN1Structure() {
        return this.encryptionKey;
    }

    public static enum symmAlgorithm {
        aes128Ccm(SymmAlgorithm.aes128Ccm.intValueExact());

        private final int tagValue;

        private symmAlgorithm(int tagValue) {
            this.tagValue = tagValue;
        }
    }
}

