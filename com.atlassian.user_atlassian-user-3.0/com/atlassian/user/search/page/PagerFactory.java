/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.search.page;

import com.atlassian.user.search.page.DefaultPager;
import com.atlassian.user.search.page.MergedListPager;
import com.atlassian.user.search.page.Pager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PagerFactory {
    public static <T> Pager<T> getPager(Pager<T> pagerOne, Pager<T> pagerTwo) {
        return PagerFactory.getPager(Arrays.asList(pagerOne, pagerTwo));
    }

    public static <T> Pager<T> getPager(List<Pager<T>> pagers) {
        ArrayList<Pager<T>> pagersCopy = new ArrayList<Pager<T>>(pagers);
        Iterator it = pagersCopy.iterator();
        while (it.hasNext()) {
            Pager pager = (Pager)it.next();
            if (pager != null && !pager.isEmpty()) continue;
            it.remove();
        }
        if (pagersCopy.size() == 0) {
            return DefaultPager.emptyPager();
        }
        if (pagersCopy.size() == 1) {
            return (Pager)pagersCopy.get(0);
        }
        return new MergedListPager<T>(pagersCopy);
    }
}

