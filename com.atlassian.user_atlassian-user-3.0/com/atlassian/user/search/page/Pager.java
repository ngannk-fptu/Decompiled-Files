/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.search.page;

import com.atlassian.user.search.page.DefaultPager;
import com.atlassian.user.search.page.PagerException;
import java.util.Iterator;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Pager<T>
extends Iterable<T> {
    public static final Pager EMPTY_PAGER = DefaultPager.emptyPager();
    public static final int PRELOAD_LIMIT = 100;
    public static final int NO_POSITION = -1;

    public boolean isEmpty();

    @Override
    public Iterator<T> iterator();

    public List<T> getCurrentPage();

    public void nextPage();

    public boolean onLastPage();

    public void skipTo(int var1) throws PagerException;

    public int getIndex();

    public int getIndexOfFirstItemInCurrentPage();
}

