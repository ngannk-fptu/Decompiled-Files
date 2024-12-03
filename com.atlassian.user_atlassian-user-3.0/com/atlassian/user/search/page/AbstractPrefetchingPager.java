/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Category
 */
package com.atlassian.user.search.page;

import com.atlassian.user.EntityException;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.page.PagerException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.log4j.Category;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractPrefetchingPager<T>
implements Pager<T>,
Iterator<T> {
    public static final Category log = Category.getInstance(AbstractPrefetchingPager.class);
    protected int idx = 0;
    private final int preloadLimit = 100;
    private final List<T> prefetched = new ArrayList<T>(100);
    protected int indexOfFirstItemInCurrentPage = 0;
    public boolean lastPage;

    @Override
    public Iterator<T> iterator() {
        return this;
    }

    @Override
    public boolean isEmpty() {
        return !this.hasNext();
    }

    @Override
    public List<T> getCurrentPage() {
        return new ArrayList<T>(this.prefetched);
    }

    @Override
    public boolean hasNext() {
        int indexWithinPage = this.getIndexWithinPage();
        if (indexWithinPage == this.prefetched.size() && !this.lastPage) {
            this.preload();
            indexWithinPage = this.getIndexWithinPage();
        }
        return indexWithinPage < this.prefetched.size() || !this.lastPage;
    }

    protected abstract void preload();

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    protected void preload(Iterator iterator) {
        this.prefetched.clear();
        int currentPos = 0;
        try {
            while (!this.lastPage && this.prefetched.size() < 100) {
                if (!iterator.hasNext()) {
                    this.lastPage = true;
                    break;
                }
                Object element = iterator.next();
                ArrayList entities = new ArrayList();
                this.fetch(element, entities);
                Iterator i = entities.iterator();
                while (i.hasNext() && this.prefetched.size() < 100) {
                    Object r = i.next();
                    if (currentPos >= this.idx) {
                        this.prefetched.add(r);
                    }
                    ++currentPos;
                }
            }
            if (this.prefetched.size() < 100) {
                this.lastPage = true;
            }
        }
        catch (Exception e) {
            log.error((Object)("At index [" + this.idx + "]: " + e.getMessage()), (Throwable)e);
            this.lastPage = true;
        }
    }

    protected abstract List<T> fetch(Object var1, List<T> var2) throws EntityException;

    @Override
    public void nextPage() {
        this.idx += 100;
        this.preload();
    }

    @Override
    public int getIndex() {
        return this.idx;
    }

    @Override
    public void skipTo(int idx) throws PagerException {
        this.idx = idx;
        this.preload();
        if (this.prefetched == null || this.prefetched.size() == 0) {
            this.idx = -1;
        }
    }

    @Override
    public boolean onLastPage() {
        return this.lastPage;
    }

    @Override
    public int getIndexOfFirstItemInCurrentPage() {
        return this.indexOfFirstItemInCurrentPage;
    }

    @Override
    public T next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        int indexWithinPage = this.getIndexWithinPage();
        T nextObj = this.prefetched.get(indexWithinPage);
        ++this.idx;
        return nextObj;
    }

    protected int getIndexWithinPage() {
        return this.idx - this.indexOfFirstItemInCurrentPage;
    }
}

