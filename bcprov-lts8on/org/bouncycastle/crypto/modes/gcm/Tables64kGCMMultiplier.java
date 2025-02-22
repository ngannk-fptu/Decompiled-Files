/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.crypto.modes.gcm.GCMMultiplier;
import org.bouncycastle.crypto.modes.gcm.GCMUtil;
import org.bouncycastle.util.Pack;

public class Tables64kGCMMultiplier
implements GCMMultiplier {
    private byte[] H;
    private long[][][] T;

    @Override
    public void init(byte[] H) {
        if (this.T == null) {
            this.T = new long[16][256][2];
        } else if (0 != GCMUtil.areEqual(this.H, H)) {
            return;
        }
        this.H = new byte[16];
        GCMUtil.copy(H, this.H);
        for (int i = 0; i < 16; ++i) {
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
        long[] t00 = this.T[0][x[0] & 0xFF];
        long[] t01 = this.T[1][x[1] & 0xFF];
        long[] t02 = this.T[2][x[2] & 0xFF];
        long[] t03 = this.T[3][x[3] & 0xFF];
        long[] t04 = this.T[4][x[4] & 0xFF];
        long[] t05 = this.T[5][x[5] & 0xFF];
        long[] t06 = this.T[6][x[6] & 0xFF];
        long[] t07 = this.T[7][x[7] & 0xFF];
        long[] t08 = this.T[8][x[8] & 0xFF];
        long[] t09 = this.T[9][x[9] & 0xFF];
        long[] t10 = this.T[10][x[10] & 0xFF];
        long[] t11 = this.T[11][x[11] & 0xFF];
        long[] t12 = this.T[12][x[12] & 0xFF];
        long[] t13 = this.T[13][x[13] & 0xFF];
        long[] t14 = this.T[14][x[14] & 0xFF];
        long[] t15 = this.T[15][x[15] & 0xFF];
        long z0 = t00[0] ^ t01[0] ^ t02[0] ^ t03[0] ^ t04[0] ^ t05[0] ^ t06[0] ^ t07[0] ^ t08[0] ^ t09[0] ^ t10[0] ^ t11[0] ^ t12[0] ^ t13[0] ^ t14[0] ^ t15[0];
        long z1 = t00[1] ^ t01[1] ^ t02[1] ^ t03[1] ^ t04[1] ^ t05[1] ^ t06[1] ^ t07[1] ^ t08[1] ^ t09[1] ^ t10[1] ^ t11[1] ^ t12[1] ^ t13[1] ^ t14[1] ^ t15[1];
        Pack.longToBigEndian(z0, x, 0);
        Pack.longToBigEndian(z1, x, 8);
    }
}

