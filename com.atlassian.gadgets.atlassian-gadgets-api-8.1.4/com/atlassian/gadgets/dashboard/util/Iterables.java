/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.Assertions
 *  org.apache.commons.lang3.ObjectUtils
 */
package com.atlassian.gadgets.dashboard.util;

import com.atlassian.plugin.util.Assertions;
import java.util.Iterator;
import org.apache.commons.lang3.ObjectUtils;

public class Iterables {
    private Iterables() {
        throw new AssertionError((Object)"Must not be instantiated");
    }

    public static boolean elementsEqual(Iterable<?> i1, Iterable<?> i2) {
        if (i1 == i2) {
            return true;
        }
        if (i1 == null || i2 == null) {
            return false;
        }
        Iterator<?> iter1 = i1.iterator();
        Iterator<?> iter2 = i2.iterator();
        while (iter1.hasNext() && iter2.hasNext()) {
            if (ObjectUtils.equals(iter1.next(), iter2.next())) continue;
            return false;
        }
        return !iter1.hasNext() && !iter2.hasNext();
    }

    public static <T> Iterable<T> checkContentsNotNull(Iterable<T> iterable) {
        for (Object element : (Iterable)Assertions.notNull((String)"iterable", iterable)) {
            Assertions.notNull((String)"element of iterable", element);
        }
        return iterable;
    }
}

