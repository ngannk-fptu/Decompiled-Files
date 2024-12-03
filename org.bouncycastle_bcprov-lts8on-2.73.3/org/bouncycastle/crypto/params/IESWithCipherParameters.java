/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.params.IESParameters;

public class IESWithCipherParameters
extends IESParameters {
    private int cipherKeySize;

    public IESWithCipherParameters(byte[] derivation, byte[] encoding, int macKeySize, int cipherKeySize) {
        super(derivation, encoding, macKeySize);
        this.cipherKeySize = cipherKeySize;
    }

    public int getCipherKeySize() {
        return this.cipherKeySize;
    }
}

