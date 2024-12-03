/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.search.page;

import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.page.PagerException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class MergedPager
implements Pager {
    private final Pager leftPager;
    private final Pager rightPager;

    MergedPager(Pager pager1, Pager rightPager) {
        this.leftPager = pager1;
        this.rightPager = rightPager;
    }

    public boolean isEmpty() {
        return !(this.leftPager != null && !this.leftPager.isEmpty() || this.rightPager != null && !this.rightPager.isEmpty());
    }

    public Iterator iterator() {
        if (this.leftPager == null && this.rightPager == null) {
            return new Iterator(){

                public void remove() {
                    throw new UnsupportedOperationException("This iterator does not support removal");
                }

                public boolean hasNext() {
                    return false;
                }

                public Object next() {
                    throw new NoSuchElementException("Empty iterator");
                }
            };
        }
        if (this.leftPager != null && this.rightPager != null) {
            return new Iterator(){
                Iterator iter1;
                Iterator iter2;
                {
                    this.iter1 = MergedPager.this.leftPager.iterator();
                    this.iter2 = MergedPager.this.rightPager.iterator();
                }

                public void remove() {
                    throw new UnsupportedOperationException("This iterator does not support removal");
                }

                public boolean hasNext() {
                    return this.iter1.hasNext() || this.iter2.hasNext();
                }

                public Object next() {
                    if (this.iter1.hasNext()) {
                        return this.iter1.next();
                    }
                    if (this.iter2.hasNext()) {
                        return this.iter2.next();
                    }
                    throw new NoSuchElementException("Exhausted iterator");
                }
            };
        }
        if (this.leftPager == null) {
            return this.rightPager.iterator();
        }
        return this.leftPager.iterator();
    }

    public List getCurrentPage() {
        List currentPage = this.leftPager.getCurrentPage();
        for (Object o : this.rightPager.getCurrentPage()) {
            if (currentPage.size() >= 100) break;
            currentPage.add(o);
        }
        return currentPage;
    }

    public void nextPage() {
        this.leftPager.nextPage();
        if (this.leftPager.getCurrentPage().isEmpty()) {
            this.rightPager.nextPage();
        }
    }

    public boolean onLastPage() {
        return this.leftPager.getCurrentPage().isEmpty() && this.rightPager.onLastPage();
    }

    public void skipTo(int index) throws PagerException {
        this.leftPager.skipTo(index);
        if (this.leftPager.getIndex() != index) {
            this.rightPager.skipTo(index - this.leftPager.getIndex() - this.leftPager.getCurrentPage().size());
        }
    }

    public int getIndex() {
        return this.leftPager.getIndex() + this.rightPager.getIndex();
    }

    public int getIndexOfFirstItemInCurrentPage() {
        if (!this.leftPager.getCurrentPage().isEmpty()) {
            return this.leftPager.getIndexOfFirstItemInCurrentPage();
        }
        return this.leftPager.getIndexOfFirstItemInCurrentPage() + this.rightPager.getIndexOfFirstItemInCurrentPage();
    }
}

