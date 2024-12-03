/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes.kgcm;

import org.bouncycastle.crypto.modes.kgcm.KGCMMultiplier;
import org.bouncycastle.crypto.modes.kgcm.KGCMUtil_512;

public class Tables16kKGCMMultiplier_512
implements KGCMMultiplier {
    private long[][] T;

    @Override
    public void init(long[] H) {
        if (this.T == null) {
            this.T = new long[256][8];
        } else if (KGCMUtil_512.equal(H, this.T[1])) {
            return;
        }
        KGCMUtil_512.copy(H, this.T[1]);
        for (int n = 2; n < 256; n += 2) {
            KGCMUtil_512.multiplyX(this.T[n >> 1], this.T[n]);
            KGCMUtil_512.add(this.T[n], this.T[1], this.T[n + 1]);
        }
    }

    @Override
    public void multiplyH(long[] z) {
        long[] r = new long[8];
        KGCMUtil_512.copy(this.T[(int)(z[7] >>> 56) & 0xFF], r);
        for (int i = 62; i >= 0; --i) {
            KGCMUtil_512.multiplyX8(r, r);
            KGCMUtil_512.add(this.T[(int)(z[i >>> 3] >>> ((i & 7) << 3)) & 0xFF], r, r);
        }
        KGCMUtil_512.copy(r, z);
    }
}

