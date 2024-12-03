/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.util.Collection;
import java.util.Comparator;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.comparators.BooleanComparator;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.collections.comparators.TransformingComparator;

public class ComparatorUtils {
    public static final Comparator NATURAL_COMPARATOR = ComparableComparator.getInstance();

    public static Comparator naturalComparator() {
        return NATURAL_COMPARATOR;
    }

    public static Comparator chainedComparator(Comparator comparator1, Comparator comparator2) {
        return ComparatorUtils.chainedComparator(new Comparator[]{comparator1, comparator2});
    }

    public static Comparator chainedComparator(Comparator[] comparators) {
        ComparatorChain chain = new ComparatorChain();
        for (int i = 0; i < comparators.length; ++i) {
            if (comparators[i] == null) {
                throw new NullPointerException("Comparator cannot be null");
            }
            chain.addComparator(comparators[i]);
        }
        return chain;
    }

    public static Comparator chainedComparator(Collection comparators) {
        return ComparatorUtils.chainedComparator(comparators.toArray(new Comparator[comparators.size()]));
    }

    public static Comparator reversedComparator(Comparator comparator) {
        if (comparator == null) {
            comparator = NATURAL_COMPARATOR;
        }
        return new ReverseComparator(comparator);
    }

    public static Comparator booleanComparator(boolean trueFirst) {
        return BooleanComparator.getBooleanComparator(trueFirst);
    }

    public static Comparator nullLowComparator(Comparator comparator) {
        if (comparator == null) {
            comparator = NATURAL_COMPARATOR;
        }
        return new NullComparator(comparator, false);
    }

    public static Comparator nullHighComparator(Comparator comparator) {
        if (comparator == null) {
            comparator = NATURAL_COMPARATOR;
        }
        return new NullComparator(comparator, true);
    }

    public static Comparator transformedComparator(Comparator comparator, Transformer transformer) {
        if (comparator == null) {
            comparator = NATURAL_COMPARATOR;
        }
        return new TransformingComparator(transformer, comparator);
    }

    public static Object min(Object o1, Object o2, Comparator comparator) {
        int c;
        if (comparator == null) {
            comparator = NATURAL_COMPARATOR;
        }
        return (c = comparator.compare(o1, o2)) < 0 ? o1 : o2;
    }

    public static Object max(Object o1, Object o2, Comparator comparator) {
        int c;
        if (comparator == null) {
            comparator = NATURAL_COMPARATOR;
        }
        return (c = comparator.compare(o1, o2)) > 0 ? o1 : o2;
    }
}

