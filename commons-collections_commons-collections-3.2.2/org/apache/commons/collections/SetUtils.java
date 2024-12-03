/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.set.ListOrderedSet;
import org.apache.commons.collections.set.PredicatedSet;
import org.apache.commons.collections.set.PredicatedSortedSet;
import org.apache.commons.collections.set.SynchronizedSet;
import org.apache.commons.collections.set.SynchronizedSortedSet;
import org.apache.commons.collections.set.TransformedSet;
import org.apache.commons.collections.set.TransformedSortedSet;
import org.apache.commons.collections.set.TypedSet;
import org.apache.commons.collections.set.TypedSortedSet;
import org.apache.commons.collections.set.UnmodifiableSet;
import org.apache.commons.collections.set.UnmodifiableSortedSet;

public class SetUtils {
    public static final Set EMPTY_SET = Collections.EMPTY_SET;
    public static final SortedSet EMPTY_SORTED_SET = UnmodifiableSortedSet.decorate(new TreeSet());

    public static boolean isEqualSet(Collection set1, Collection set2) {
        if (set1 == set2) {
            return true;
        }
        if (set1 == null || set2 == null || set1.size() != set2.size()) {
            return false;
        }
        return set1.containsAll(set2);
    }

    public static int hashCodeForSet(Collection set) {
        if (set == null) {
            return 0;
        }
        int hashCode = 0;
        Iterator it = set.iterator();
        Object obj = null;
        while (it.hasNext()) {
            obj = it.next();
            if (obj == null) continue;
            hashCode += obj.hashCode();
        }
        return hashCode;
    }

    public static Set synchronizedSet(Set set) {
        return SynchronizedSet.decorate(set);
    }

    public static Set unmodifiableSet(Set set) {
        return UnmodifiableSet.decorate(set);
    }

    public static Set predicatedSet(Set set, Predicate predicate) {
        return PredicatedSet.decorate(set, predicate);
    }

    public static Set typedSet(Set set, Class type) {
        return TypedSet.decorate(set, type);
    }

    public static Set transformedSet(Set set, Transformer transformer) {
        return TransformedSet.decorate(set, transformer);
    }

    public static Set orderedSet(Set set) {
        return ListOrderedSet.decorate(set);
    }

    public static SortedSet synchronizedSortedSet(SortedSet set) {
        return SynchronizedSortedSet.decorate(set);
    }

    public static SortedSet unmodifiableSortedSet(SortedSet set) {
        return UnmodifiableSortedSet.decorate(set);
    }

    public static SortedSet predicatedSortedSet(SortedSet set, Predicate predicate) {
        return PredicatedSortedSet.decorate(set, predicate);
    }

    public static SortedSet typedSortedSet(SortedSet set, Class type) {
        return TypedSortedSet.decorate(set, type);
    }

    public static SortedSet transformedSortedSet(SortedSet set, Transformer transformer) {
        return TransformedSortedSet.decorate(set, transformer);
    }
}

