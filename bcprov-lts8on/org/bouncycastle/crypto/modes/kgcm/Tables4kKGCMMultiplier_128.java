/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes.kgcm;

import org.bouncycastle.crypto.modes.kgcm.KGCMMultiplier;
import org.bouncycastle.crypto.modes.kgcm.KGCMUtil_128;

public class Tables4kKGCMMultiplier_128
implements KGCMMultiplier {
    private long[][] T;

    @Override
    public void init(long[] H) {
        if (this.T == null) {
            this.T = new long[256][2];
        } else if (KGCMUtil_128.equal(H, this.T[1])) {
            return;
        }
        KGCMUtil_128.copy(H, this.T[1]);
        for (int n = 2; n < 256; n += 2) {
            KGCMUtil_128.multiplyX(this.T[n >> 1], this.T[n]);
            KGCMUtil_128.add(this.T[n], this.T[1], this.T[n + 1]);
        }
    }

    @Override
    public void multiplyH(long[] z) {
        long[] r = new long[2];
        KGCMUtil_128.copy(this.T[(int)(z[1] >>> 56) & 0xFF], r);
        for (int i = 14; i >= 0; --i) {
            KGCMUtil_128.multiplyX8(r, r);
            KGCMUtil_128.add(this.T[(int)(z[i >>> 3] >>> ((i & 7) << 3)) & 0xFF], r, r);
        }
        KGCMUtil_128.copy(r, z);
    }
}

