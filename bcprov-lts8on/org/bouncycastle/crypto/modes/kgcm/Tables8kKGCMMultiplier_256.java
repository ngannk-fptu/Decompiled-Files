/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes.kgcm;

import org.bouncycastle.crypto.modes.kgcm.KGCMMultiplier;
import org.bouncycastle.crypto.modes.kgcm.KGCMUtil_256;

public class Tables8kKGCMMultiplier_256
implements KGCMMultiplier {
    private long[][] T;

    @Override
    public void init(long[] H) {
        if (this.T == null) {
            this.T = new long[256][4];
        } else if (KGCMUtil_256.equal(H, this.T[1])) {
            return;
        }
        KGCMUtil_256.copy(H, this.T[1]);
        for (int n = 2; n < 256; n += 2) {
            KGCMUtil_256.multiplyX(this.T[n >> 1], this.T[n]);
            KGCMUtil_256.add(this.T[n], this.T[1], this.T[n + 1]);
        }
    }

    @Override
    public void multiplyH(long[] z) {
        long[] r = new long[4];
        KGCMUtil_256.copy(this.T[(int)(z[3] >>> 56) & 0xFF], r);
        for (int i = 30; i >= 0; --i) {
            KGCMUtil_256.multiplyX8(r, r);
            KGCMUtil_256.add(this.T[(int)(z[i >>> 3] >>> ((i & 7) << 3)) & 0xFF], r, r);
        }
        KGCMUtil_256.copy(r, z);
    }
}

