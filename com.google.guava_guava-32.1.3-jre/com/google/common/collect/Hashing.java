/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class Hashing {
    private static final long C1 = -862048943L;
    private static final long C2 = 461845907L;
    private static final int MAX_TABLE_SIZE = 0x40000000;

    private Hashing() {
    }

    static int smear(int hashCode) {
        return (int)(461845907L * (long)Integer.rotateLeft((int)((long)hashCode * -862048943L), 15));
    }

    static int smearedHash(@CheckForNull Object o) {
        return Hashing.smear(o == null ? 0 : o.hashCode());
    }

    static int closedTableSize(int expectedEntries, double loadFactor) {
        int tableSize;
        if ((expectedEntries = Math.max(expectedEntries, 2)) > (int)(loadFactor * (double)(tableSize = Integer.highestOneBit(expectedEntries)))) {
            return (tableSize <<= 1) > 0 ? tableSize : 0x40000000;
        }
        return tableSize;
    }

    static boolean needsResizing(int size, int tableSize, double loadFactor) {
        return (double)size > loadFactor * (double)tableSize && tableSize < 0x40000000;
    }
}

