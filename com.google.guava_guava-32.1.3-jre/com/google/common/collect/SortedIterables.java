/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.Ordering;
import com.google.common.collect.SortedIterable;
import java.util.Comparator;
import java.util.SortedSet;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class SortedIterables {
    private SortedIterables() {
    }

    public static boolean hasSameComparator(Comparator<?> comparator, Iterable<?> elements) {
        Comparator<Object> comparator2;
        Preconditions.checkNotNull(comparator);
        Preconditions.checkNotNull(elements);
        if (elements instanceof SortedSet) {
            comparator2 = SortedIterables.comparator((SortedSet)elements);
        } else if (elements instanceof SortedIterable) {
            comparator2 = ((SortedIterable)elements).comparator();
        } else {
            return false;
        }
        return comparator.equals(comparator2);
    }

    public static <E> Comparator<? super E> comparator(SortedSet<E> sortedSet) {
        Comparator<E> result = sortedSet.comparator();
        if (result == null) {
            result = Ordering.natural();
        }
        return result;
    }
}

