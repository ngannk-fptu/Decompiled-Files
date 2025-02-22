/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement.kdf;

import org.bouncycastle.crypto.DerivationParameters;

public class GSKKDFParameters
implements DerivationParameters {
    private final byte[] z;
    private final int startCounter;
    private final byte[] nonce;

    public GSKKDFParameters(byte[] z, int startCounter) {
        this(z, startCounter, null);
    }

    public GSKKDFParameters(byte[] z, int startCounter, byte[] nonce) {
        this.z = z;
        this.startCounter = startCounter;
        this.nonce = nonce;
    }

    public byte[] getZ() {
        return this.z;
    }

    public int getStartCounter() {
        return this.startCounter;
    }

    public byte[] getNonce() {
        return this.nonce;
    }
}

