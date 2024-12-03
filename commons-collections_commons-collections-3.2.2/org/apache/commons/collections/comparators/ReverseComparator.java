/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.comparators;

import java.io.Serializable;
import java.util.Comparator;
import org.apache.commons.collections.comparators.ComparableComparator;

public class ReverseComparator
implements Comparator,
Serializable {
    private static final long serialVersionUID = 2858887242028539265L;
    private Comparator comparator;

    public ReverseComparator() {
        this(null);
    }

    public ReverseComparator(Comparator comparator) {
        this.comparator = comparator != null ? comparator : ComparableComparator.getInstance();
    }

    public int compare(Object obj1, Object obj2) {
        return this.comparator.compare(obj2, obj1);
    }

    public int hashCode() {
        return "ReverseComparator".hashCode() ^ this.comparator.hashCode();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (null == object) {
            return false;
        }
        if (object.getClass().equals(this.getClass())) {
            ReverseComparator thatrc = (ReverseComparator)object;
            return this.comparator.equals(thatrc.comparator);
        }
        return false;
    }
}

