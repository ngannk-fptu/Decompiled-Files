/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import com.google.inject.internal.util.$Lists;
import com.google.inject.internal.util.$Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class $Collections2 {
    private $Collections2() {
    }

    static <E> Collection<E> toCollection(Iterable<E> iterable) {
        return iterable instanceof Collection ? (ArrayList<E>)iterable : $Lists.newArrayList(iterable);
    }

    static boolean setEquals(Set<?> thisSet, @$Nullable Object object) {
        if (object == thisSet) {
            return true;
        }
        if (object instanceof Set) {
            Set thatSet = (Set)object;
            return thisSet.size() == thatSet.size() && thisSet.containsAll(thatSet);
        }
        return false;
    }
}

