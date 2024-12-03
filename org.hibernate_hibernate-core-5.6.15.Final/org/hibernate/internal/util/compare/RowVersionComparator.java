/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.compare;

import java.util.Comparator;
import org.hibernate.internal.util.compare.ComparableComparator;

public final class RowVersionComparator
implements Comparator<byte[]> {
    public static final RowVersionComparator INSTANCE = new RowVersionComparator();

    private RowVersionComparator() {
    }

    @Override
    public int compare(byte[] o1, byte[] o2) {
        int lengthToCheck = Math.min(o1.length, o2.length);
        for (int i = 0; i < lengthToCheck; ++i) {
            int comparison = ComparableComparator.INSTANCE.compare(Byte.toUnsignedInt(o1[i]), Byte.toUnsignedInt(o2[i]));
            if (comparison == 0) continue;
            return comparison;
        }
        return o1.length - o2.length;
    }
}

