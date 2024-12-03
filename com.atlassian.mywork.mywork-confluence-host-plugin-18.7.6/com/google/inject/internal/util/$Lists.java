/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import com.google.inject.internal.util.$Nullable;
import com.google.inject.internal.util.$Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class $Lists {
    private $Lists() {
    }

    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList();
    }

    public static <E> ArrayList<E> newArrayList(E ... elements) {
        int capacity = $Lists.computeArrayListCapacity(elements.length);
        ArrayList list = new ArrayList(capacity);
        Collections.addAll(list, elements);
        return list;
    }

    static int computeArrayListCapacity(int arraySize) {
        $Preconditions.checkArgument(arraySize >= 0);
        return (int)Math.min(5L + (long)arraySize + (long)(arraySize / 10), Integer.MAX_VALUE);
    }

    public static <E> ArrayList<E> newArrayList(Iterable<? extends E> elements) {
        if (elements instanceof Collection) {
            Collection collection = (Collection)elements;
            return new ArrayList(collection);
        }
        return $Lists.newArrayList(elements.iterator());
    }

    public static <E> ArrayList<E> newArrayList(Iterator<? extends E> elements) {
        ArrayList<E> list = $Lists.newArrayList();
        while (elements.hasNext()) {
            list.add(elements.next());
        }
        return list;
    }

    public static <E> ArrayList<E> newArrayList(@$Nullable E first, E[] rest) {
        ArrayList<E> result = new ArrayList<E>(rest.length + 1);
        result.add(first);
        for (E element : rest) {
            result.add(element);
        }
        return result;
    }
}

