/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.compare;

import java.io.Serializable;
import java.util.Comparator;

public class ComparableComparator<T extends Comparable>
implements Comparator<T>,
Serializable {
    public static final Comparator INSTANCE = new ComparableComparator();

    @Override
    public int compare(Comparable one, Comparable another) {
        return one.compareTo(another);
    }
}

