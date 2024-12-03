/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.macs.CMac;

public class CMacWithIV
extends CMac {
    public CMacWithIV(BlockCipher cipher) {
        super(cipher);
    }

    public CMacWithIV(BlockCipher cipher, int macSizeInBits) {
        super(cipher, macSizeInBits);
    }

    @Override
    void validate(CipherParameters params) {
    }
}

