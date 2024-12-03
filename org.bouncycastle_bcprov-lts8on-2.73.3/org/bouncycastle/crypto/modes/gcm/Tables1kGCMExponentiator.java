/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes.gcm;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.crypto.modes.gcm.GCMExponentiator;
import org.bouncycastle.crypto.modes.gcm.GCMUtil;

public class Tables1kGCMExponentiator
implements GCMExponentiator {
    private List lookupPowX2;

    @Override
    public void init(byte[] x) {
        long[] y = GCMUtil.asLongs(x);
        if (this.lookupPowX2 != null && 0L != GCMUtil.areEqual(y, (long[])this.lookupPowX2.get(0))) {
            return;
        }
        this.lookupPowX2 = new ArrayList(8);
        this.lookupPowX2.add(y);
    }

    @Override
    public void exponentiateX(long pow, byte[] output) {
        long[] y = GCMUtil.oneAsLongs();
        int bit = 0;
        while (pow > 0L) {
            if ((pow & 1L) != 0L) {
                GCMUtil.multiply(y, this.getPowX2(bit));
            }
            ++bit;
            pow >>>= 1;
        }
        GCMUtil.asBytes(y, output);
    }

    private long[] getPowX2(int bit) {
        int last = this.lookupPowX2.size() - 1;
        if (last < bit) {
            long[] prev = (long[])this.lookupPowX2.get(last);
            do {
                long[] next = new long[2];
                GCMUtil.square(prev, next);
                this.lookupPowX2.add(next);
                prev = next;
            } while (++last < bit);
        }
        return (long[])this.lookupPowX2.get(bit);
    }
}

