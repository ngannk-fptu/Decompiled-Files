/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;

public class TweakableBlockCipherParameters
implements CipherParameters {
    private final byte[] tweak;
    private final KeyParameter key;

    public TweakableBlockCipherParameters(KeyParameter key, byte[] tweak) {
        this.key = key;
        this.tweak = Arrays.clone(tweak);
    }

    public KeyParameter getKey() {
        return this.key;
    }

    public byte[] getTweak() {
        return this.tweak;
    }
}

