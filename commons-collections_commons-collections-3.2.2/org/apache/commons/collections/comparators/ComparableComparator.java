/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.comparators;

import java.io.Serializable;
import java.util.Comparator;

public class ComparableComparator
implements Comparator,
Serializable {
    private static final long serialVersionUID = -291439688585137865L;
    private static final ComparableComparator instance = new ComparableComparator();

    public static ComparableComparator getInstance() {
        return instance;
    }

    public int compare(Object obj1, Object obj2) {
        return ((Comparable)obj1).compareTo(obj2);
    }

    public int hashCode() {
        return "ComparableComparator".hashCode();
    }

    public boolean equals(Object object) {
        return this == object || null != object && object.getClass().equals(this.getClass());
    }
}

