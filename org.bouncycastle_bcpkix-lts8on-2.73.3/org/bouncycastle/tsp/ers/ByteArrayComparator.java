/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

import java.util.Comparator;

class ByteArrayComparator
implements Comparator {
    ByteArrayComparator() {
    }

    public int compare(Object l, Object r) {
        byte[] left = (byte[])l;
        byte[] right = (byte[])r;
        for (int i = 0; i < left.length && i < right.length; ++i) {
            int a = left[i] & 0xFF;
            int b = right[i] & 0xFF;
            if (a == b) continue;
            return a - b;
        }
        return left.length - right.length;
    }
}

