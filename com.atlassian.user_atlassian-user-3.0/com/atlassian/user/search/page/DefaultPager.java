/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.search.page;

import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.page.PagerException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DefaultPager<T>
implements Pager<T> {
    private final List<T> page = new ArrayList<T>();
    private Iterator<T> iter;
    private int index;

    public static <T> DefaultPager<T> emptyPager() {
        return new DefaultPager(Collections.emptyList());
    }

    public DefaultPager() {
        this(Collections.emptyList());
    }

    public DefaultPager(Collection<T> col) {
        if (col != null) {
            this.page.addAll(col);
        }
        this.iter = this.page.iterator();
    }

    @Override
    public boolean isEmpty() {
        if (this.page == null) {
            return true;
        }
        return this.page.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return this.iter;
    }

    @Override
    public List<T> getCurrentPage() {
        return new ArrayList<T>(this.page);
    }

    @Override
    public void nextPage() {
    }

    @Override
    public boolean onLastPage() {
        return true;
    }

    @Override
    public void skipTo(int index) throws PagerException {
        int distance;
        if (index < 0) {
            throw new PagerException("Cannot skipTo a negative amount [" + index + "]");
        }
        int originalIndex = this.index;
        if (index > this.page.size()) {
            distance = this.page.size();
            this.index = this.page.size();
        } else {
            distance = index - this.index;
            this.index = index;
        }
        for (int i = originalIndex; i < distance; ++i) {
            this.iter.next();
        }
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public int getIndexOfFirstItemInCurrentPage() {
        return 0;
    }

    public void remove() {
        throw new UnsupportedOperationException("This iterator does not support removal");
    }

    public boolean hasNext() {
        return this.iter.hasNext();
    }

    public T next() {
        T o = this.iter.next();
        ++this.index;
        return o;
    }
}

