/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.search.page;

import com.atlassian.user.search.page.DefaultPager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Pagers {
    public static <T> DefaultPager<T> newDefaultPager(Iterable<? extends T> elements) {
        if (elements instanceof Collection) {
            return new DefaultPager(new ArrayList((Collection)elements));
        }
        ArrayList<T> list = new ArrayList<T>();
        for (T element : elements) {
            list.add(element);
        }
        return new DefaultPager(list);
    }

    public static <T> DefaultPager<T> newDefaultPager(T ... elements) {
        return Pagers.newDefaultPager(Arrays.asList(elements));
    }
}

