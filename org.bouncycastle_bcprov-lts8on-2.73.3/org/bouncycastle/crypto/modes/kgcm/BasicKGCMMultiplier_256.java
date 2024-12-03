/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes.kgcm;

import org.bouncycastle.crypto.modes.kgcm.KGCMMultiplier;
import org.bouncycastle.crypto.modes.kgcm.KGCMUtil_256;

public class BasicKGCMMultiplier_256
implements KGCMMultiplier {
    private final long[] H = new long[4];

    @Override
    public void init(long[] H) {
        KGCMUtil_256.copy(H, this.H);
    }

    @Override
    public void multiplyH(long[] z) {
        KGCMUtil_256.multiply(z, this.H, z);
    }
}

