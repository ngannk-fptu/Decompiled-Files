/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class RC5Parameters
implements CipherParameters {
    private byte[] key;
    private int rounds;

    public RC5Parameters(byte[] key, int rounds) {
        if (key.length > 255) {
            throw new IllegalArgumentException("RC5 key length can be no greater than 255");
        }
        this.key = new byte[key.length];
        this.rounds = rounds;
        System.arraycopy(key, 0, this.key, 0, key.length);
    }

    public byte[] getKey() {
        return this.key;
    }

    public int getRounds() {
        return this.rounds;
    }
}

