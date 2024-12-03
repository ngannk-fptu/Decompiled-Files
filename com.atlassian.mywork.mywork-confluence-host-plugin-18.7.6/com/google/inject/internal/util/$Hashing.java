/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import com.google.inject.internal.util.$Preconditions;

final class $Hashing {
    private static final int MAX_TABLE_SIZE = 0x40000000;
    private static final int CUTOFF = 0x20000000;

    private $Hashing() {
    }

    static int smear(int hashCode) {
        hashCode ^= hashCode >>> 20 ^ hashCode >>> 12;
        return hashCode ^ hashCode >>> 7 ^ hashCode >>> 4;
    }

    static int chooseTableSize(int setSize) {
        if (setSize < 0x20000000) {
            return Integer.highestOneBit(setSize) << 2;
        }
        $Preconditions.checkArgument(setSize < 0x40000000, "collection too large");
        return 0x40000000;
    }
}

