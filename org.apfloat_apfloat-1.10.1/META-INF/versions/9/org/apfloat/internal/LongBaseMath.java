/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigInteger;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.LongRadixConstants;
import org.apfloat.spi.DataStorage;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class LongBaseMath
implements Serializable {
    private static final long serialVersionUID = -6469225916787810664L;
    private static final long[] INVERSE_BASE = new long[37];
    private int radix;
    private double inverseBase;
    private transient long inverseBaseLong;

    public LongBaseMath(int n) {
        this.radix = n;
        this.inverseBase = 1.0 / (double)LongRadixConstants.BASE[n];
        this.inverseBaseLong = INVERSE_BASE[n];
    }

    public long baseAdd(DataStorage.Iterator iterator, DataStorage.Iterator iterator2, long l, DataStorage.Iterator iterator3, long l2) throws ApfloatRuntimeException {
        assert (iterator == null || iterator != iterator2);
        boolean bl = iterator == iterator3 || iterator2 == iterator3;
        long l3 = LongRadixConstants.BASE[this.radix];
        for (long i = 0L; i < l2; ++i) {
            long l4 = (iterator == null ? 0L : iterator.getLong()) + l + (iterator2 == null ? 0L : iterator2.getLong());
            l = l4 >= l3 ? 1 : 0;
            iterator3.setLong(l4 -= l4 >= l3 ? l3 : 0L);
            if (iterator != null) {
                iterator.next();
            }
            if (iterator2 != null) {
                iterator2.next();
            }
            if (bl) continue;
            iterator3.next();
        }
        return l;
    }

    public long baseSubtract(DataStorage.Iterator iterator, DataStorage.Iterator iterator2, long l, DataStorage.Iterator iterator3, long l2) throws ApfloatRuntimeException {
        assert (iterator == null || iterator != iterator2);
        assert (iterator2 != iterator3);
        long l3 = LongRadixConstants.BASE[this.radix];
        for (long i = 0L; i < l2; ++i) {
            long l4 = (iterator == null ? 0L : iterator.getLong()) - l - (iterator2 == null ? 0L : iterator2.getLong());
            l = l4 < 0L ? 1 : 0;
            iterator3.setLong(l4 += l4 < 0L ? l3 : 0L);
            if (iterator != null && iterator != iterator3) {
                iterator.next();
            }
            if (iterator2 != null) {
                iterator2.next();
            }
            iterator3.next();
        }
        return l;
    }

    public long baseMultiplyAdd(DataStorage.Iterator iterator, DataStorage.Iterator iterator2, long l, long l2, DataStorage.Iterator iterator3, long l3) throws ApfloatRuntimeException {
        assert (iterator != iterator2);
        assert (iterator != iterator3);
        long l4 = LongRadixConstants.BASE[this.radix];
        for (long i = 0L; i < l3; ++i) {
            long l5 = iterator.getLong();
            long l6 = l;
            long l7 = iterator2 == null ? 0L : iterator2.getLong();
            long l8 = l5 * l6;
            long l9 = ((l8 & 0x3FFFFFFFFFFFFL) + ((l2 += l7) & 0x3FFFFFFFFFFFFL) >>> 50) + (l8 >>> 50) + (l2 >>> 50);
            l8 += l2;
            l2 = Math.multiplyHigh(l9 += Math.multiplyHigh(l5, l6) << 14, this.inverseBaseLong);
            l2 += (long)((l8 -= l2 * l4) >= l4 ? 1 : 0);
            iterator3.setLong(l8 -= l8 >= l4 ? l4 : 0L);
            iterator.next();
            if (iterator2 != null && iterator2 != iterator3) {
                iterator2.next();
            }
            iterator3.next();
        }
        return l2;
    }

    public long baseDivide(DataStorage.Iterator iterator, long l, long l2, DataStorage.Iterator iterator2, long l3) throws ApfloatRuntimeException {
        assert (iterator != iterator2);
        long l4 = LongRadixConstants.BASE[this.radix];
        double d = 1.0 / (double)l;
        for (long i = 0L; i < l3; ++i) {
            long l5 = iterator == null ? 0L : iterator.getLong();
            long l6 = l2 * l4 + l5;
            long l7 = (long)(((double)l2 * (double)l4 + (double)l5) * d);
            l2 = l6 - l7 * l;
            int n = (int)((double)l2 * d);
            l7 += (long)n;
            l7 += (long)((l2 -= (long)n * l) >= l ? 1 : 0);
            l7 += (long)((l2 -= l2 >= l ? l : 0L) >= l ? 1 : 0);
            l7 -= (long)((l2 -= l2 >= l ? l : 0L) < 0L ? 1 : 0);
            int n2 = (l2 += l2 < 0L ? l : 0L) < 0L ? 1 : 0;
            l2 += l2 < 0L ? l : 0L;
            iterator2.setLong(l7 -= (long)n2);
            if (iterator != null) {
                iterator.next();
            }
            iterator2.next();
        }
        return l2;
    }

    int radix() {
        return this.radix;
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.inverseBaseLong = INVERSE_BASE[this.radix];
    }

    static {
        for (int i = 2; i <= 36; ++i) {
            LongBaseMath.INVERSE_BASE[i] = BigInteger.ONE.shiftLeft(114).divide(BigInteger.valueOf(LongRadixConstants.BASE[i])).longValueExact();
        }
    }
}

