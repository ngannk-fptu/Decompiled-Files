/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes.kgcm;

import org.bouncycastle.crypto.modes.kgcm.KGCMMultiplier;
import org.bouncycastle.crypto.modes.kgcm.KGCMUtil_128;

public class BasicKGCMMultiplier_128
implements KGCMMultiplier {
    private final long[] H = new long[2];

    @Override
    public void init(long[] H) {
        KGCMUtil_128.copy(H, this.H);
    }

    @Override
    public void multiplyH(long[] z) {
        KGCMUtil_128.multiply(z, this.H, z);
    }
}

