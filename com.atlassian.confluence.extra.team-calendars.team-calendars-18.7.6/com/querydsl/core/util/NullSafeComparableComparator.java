/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.util;

import java.io.Serializable;
import java.util.Comparator;

public class NullSafeComparableComparator<T extends Comparable<T>>
implements Comparator<T>,
Serializable {
    private static final long serialVersionUID = 5681808684776488757L;

    @Override
    public int compare(T obj1, T obj2) {
        if (obj1 == null) {
            return obj2 == null ? 0 : -1;
        }
        if (obj2 == null) {
            return 1;
        }
        return obj1.compareTo(obj2);
    }
}

