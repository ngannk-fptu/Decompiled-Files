/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.util;

import com.mchange.v1.util.IteratorUtils;
import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class SetUtils {
    public static Set oneElementUnmodifiableSet(final Object object) {
        return new AbstractSet(){

            @Override
            public Iterator iterator() {
                return IteratorUtils.oneElementUnmodifiableIterator(object);
            }

            @Override
            public int size() {
                return 1;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object object2) {
                return object2 == object;
            }
        };
    }

    public static Set setFromArray(Object[] objectArray) {
        HashSet<Object> hashSet = new HashSet<Object>();
        int n = objectArray.length;
        for (int i = 0; i < n; ++i) {
            hashSet.add(objectArray[i]);
        }
        return hashSet;
    }

    public static boolean equivalentDisregardingSort(Set set, Set set2) {
        return set.containsAll(set2) && set2.containsAll(set);
    }

    public static int hashContentsDisregardingSort(Set set) {
        int n = 0;
        for (Object e : set) {
            if (e == null) continue;
            n ^= e.hashCode();
        }
        return n;
    }
}

