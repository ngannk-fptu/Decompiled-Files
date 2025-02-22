/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.crypto.modes.gcm.GCMMultiplier;
import org.bouncycastle.crypto.modes.gcm.GCMUtil;
import org.bouncycastle.util.Pack;

public class Tables8kGCMMultiplier
implements GCMMultiplier {
    private byte[] H;
    private long[][][] T;

    @Override
    public void init(byte[] H) {
        if (this.T == null) {
            this.T = new long[2][256][2];
        } else if (0 != GCMUtil.areEqual(this.H, H)) {
            return;
        }
        this.H = new byte[16];
        GCMUtil.copy(H, this.H);
        for (int i = 0; i < 2; ++i) {
            long[][] t = this.T[i];
            if (i == 0) {
                GCMUtil.asLongs(this.H, t[1]);
                GCMUtil.multiplyP7(t[1], t[1]);
            } else {
                GCMUtil.multiplyP8(this.T[i - 1][1], t[1]);
            }
            for (int n = 2; n < 256; n += 2) {
                GCMUtil.divideP(t[n >> 1], t[n]);
                GCMUtil.xor(t[n], t[1], t[n + 1]);
            }
        }
    }

    @Override
    public void multiplyH(byte[] x) {
        long[][] T0 = this.T[0];
        long[][] T1 = this.T[1];
        long[] u = T0[x[14] & 0xFF];
        long[] v = T1[x[15] & 0xFF];
        long z0 = u[0] ^ v[0];
        long z1 = u[1] ^ v[1];
        for (int i = 12; i >= 0; i -= 2) {
            u = T0[x[i] & 0xFF];
            v = T1[x[i + 1] & 0xFF];
            long c = z1 << 48;
            z1 = u[1] ^ v[1] ^ (z1 >>> 16 | z0 << 48);
            z0 = u[0] ^ v[0] ^ z0 >>> 16 ^ c ^ c >>> 1 ^ c >>> 2 ^ c >>> 7;
        }
        Pack.longToBigEndian(z0, x, 0);
        Pack.longToBigEndian(z1, x, 8);
    }
}

