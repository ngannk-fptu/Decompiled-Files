/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Category
 */
package com.atlassian.user.search.page;

import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.page.PagerException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Category;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MergedListPager<T>
implements Pager<T> {
    private static final Category log = Category.getInstance(MergedListPager.class);
    private List<? extends Pager<T>> pagers;
    private int combinedIndex;
    public Pager<? extends T> currentPager;
    private List<T> currentPage;
    private int indexOfFirstItemInCurrentPage;
    private boolean onLastPage = false;

    MergedListPager(List<? extends Pager<T>> pagers) {
        this.pagers = pagers;
    }

    @Override
    public boolean isEmpty() {
        for (Pager<T> pager : this.pagers) {
            if (pager.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public Iterator<T> iterator() {
        if (this.pagers == null) {
            return Collections.emptyList().iterator();
        }
        return new MergedListIterator(this.pagers);
    }

    @Override
    public List<T> getCurrentPage() {
        if (this.currentPage == null) {
            this.currentPage = new ArrayList<T>();
            this.indexOfFirstItemInCurrentPage = this.combinedIndex;
            Iterator<T> iterator = this.iterator();
            while (iterator.hasNext() && this.currentPage.size() < 100) {
                this.currentPage.add(iterator.next());
                ++this.combinedIndex;
            }
            if (this.currentPage.size() < 100 || !this.iterator().hasNext()) {
                this.onLastPage = true;
            }
        }
        return this.currentPage;
    }

    @Override
    public void nextPage() {
        try {
            this.skipTo(this.combinedIndex + (this.currentPage == null ? 100 : 0));
        }
        catch (PagerException e) {
            log.error((Object)"Erroring calling nextPage()", (Throwable)e);
        }
    }

    @Override
    public boolean onLastPage() {
        return this.onLastPage;
    }

    @Override
    public void skipTo(int index) throws PagerException {
        if (index < this.combinedIndex) {
            throw new PagerException("Cannot run the index back to [" + index + "] from [" + this.combinedIndex + "]");
        }
        while (this.combinedIndex < index) {
            this.iterator().next();
            ++this.combinedIndex;
        }
        this.currentPage = null;
        this.getCurrentPage();
    }

    @Override
    public int getIndex() {
        return this.combinedIndex;
    }

    @Override
    public int getIndexOfFirstItemInCurrentPage() {
        this.getCurrentPage();
        return this.indexOfFirstItemInCurrentPage;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class MergedListIterator
    implements Iterator<T> {
        private List<Iterator<T>> iterators = new ArrayList();

        public MergedListIterator(List<? extends Pager<T>> listOfPagers) {
            for (Pager listOfPager : listOfPagers) {
                this.iterators.add(listOfPager.iterator());
            }
        }

        private Iterator<T> getCurrentIterator() {
            for (Iterator iterator : this.iterators) {
                if (!iterator.hasNext()) continue;
                return iterator;
            }
            return Collections.emptyList().iterator();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("This iterator does not support removal");
        }

        @Override
        public boolean hasNext() {
            return this.getCurrentIterator().hasNext();
        }

        @Override
        public T next() {
            Object nextElement = this.getCurrentIterator().next();
            if (!this.hasNext()) {
                MergedListPager.this.onLastPage = true;
            }
            return nextElement;
        }
    }
}

