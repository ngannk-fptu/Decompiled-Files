/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.util;

import com.mchange.v1.util.IteratorUtils;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

public final class ListUtils {
    public static List oneElementUnmodifiableList(final Object object) {
        return new AbstractList(){

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

            @Override
            public Object get(int n) {
                if (n != 0) {
                    throw new IndexOutOfBoundsException("One element list has no element index " + n);
                }
                return object;
            }
        };
    }

    public static boolean equivalent(List list, List list2) {
        if (list.size() != list2.size()) {
            return false;
        }
        Iterator iterator = list.iterator();
        Iterator iterator2 = list2.iterator();
        return IteratorUtils.equivalent(iterator, iterator2);
    }

    public static int hashContents(List list) {
        int n = 0;
        int n2 = 0;
        for (Object e : list) {
            if (e != null) {
                n ^= e.hashCode() ^ n2;
            }
            ++n2;
        }
        return n;
    }
}

