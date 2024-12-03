/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.crypto.modes.gcm.GCMMultiplier;
import org.bouncycastle.crypto.modes.gcm.GCMUtil;
import org.bouncycastle.util.Pack;

public class Tables4kGCMMultiplier
implements GCMMultiplier {
    private byte[] H;
    private long[][] T;

    @Override
    public void init(byte[] H) {
        if (this.T == null) {
            this.T = new long[256][2];
        } else if (0 != GCMUtil.areEqual(this.H, H)) {
            return;
        }
        this.H = new byte[16];
        GCMUtil.copy(H, this.H);
        GCMUtil.asLongs(this.H, this.T[1]);
        GCMUtil.multiplyP7(this.T[1], this.T[1]);
        for (int n = 2; n < 256; n += 2) {
            GCMUtil.divideP(this.T[n >> 1], this.T[n]);
            GCMUtil.xor(this.T[n], this.T[1], this.T[n + 1]);
        }
    }

    @Override
    public void multiplyH(byte[] x) {
        long[] t = this.T[x[15] & 0xFF];
        long z0 = t[0];
        long z1 = t[1];
        for (int i = 14; i >= 0; --i) {
            t = this.T[x[i] & 0xFF];
            long c = z1 << 56;
            z1 = t[1] ^ (z1 >>> 8 | z0 << 56);
            z0 = t[0] ^ z0 >>> 8 ^ c ^ c >>> 1 ^ c >>> 2 ^ c >>> 7;
        }
        Pack.longToBigEndian(z0, x, 0);
        Pack.longToBigEndian(z1, x, 8);
    }
}

