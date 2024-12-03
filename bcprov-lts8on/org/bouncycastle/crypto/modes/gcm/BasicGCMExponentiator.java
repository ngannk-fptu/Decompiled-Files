/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.crypto.modes.gcm.GCMExponentiator;
import org.bouncycastle.crypto.modes.gcm.GCMUtil;

public class BasicGCMExponentiator
implements GCMExponentiator {
    private long[] x;

    @Override
    public void init(byte[] x) {
        this.x = GCMUtil.asLongs(x);
    }

    @Override
    public void exponentiateX(long pow, byte[] output) {
        long[] y = GCMUtil.oneAsLongs();
        if (pow > 0L) {
            long[] powX = new long[2];
            GCMUtil.copy(this.x, powX);
            do {
                if ((pow & 1L) != 0L) {
                    GCMUtil.multiply(y, powX);
                }
                GCMUtil.square(powX, powX);
            } while ((pow >>>= 1) > 0L);
        }
        GCMUtil.asBytes(y, output);
    }
}

