/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.search.page.Pager
 *  com.atlassian.user.search.page.PagerException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bucket.core.actions;

import bucket.core.PaginationSupport;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.page.PagerException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class PagerPaginationSupport
implements PaginationSupport {
    private static final Logger log = LoggerFactory.getLogger(PagerPaginationSupport.class);
    private Pager items;
    private int startIndex = 0;
    private int countOnEachPage;
    public static final int DEFAULT_COUNT_ON_EACH_PAGE = 10;
    public List page;
    public Integer pagerSize;
    public int[] nextStartIndexes;
    public int[] previousStartIndexes;
    boolean tryNext;

    public PagerPaginationSupport() {
        this(10);
    }

    public PagerPaginationSupport(int countOnEachPage) {
        if (countOnEachPage < 1) {
            throw new IllegalArgumentException("Count should be greater than zero!");
        }
        this.countOnEachPage = countOnEachPage;
    }

    public int getCountOnEachPage() {
        return this.countOnEachPage;
    }

    public Pager getItems() {
        return this.items;
    }

    public void setItems(Pager items) {
        this.items = items;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
        this.page = null;
    }

    @Override
    public int getNiceEndIndex() {
        int endIndex = this.getStartIndex() + this.countOnEachPage;
        if (endIndex > this.getTotal()) {
            return this.getTotal();
        }
        return endIndex;
    }

    public int getEndIndex() {
        return this.getNiceEndIndex();
    }

    @Override
    public int getStartIndex() {
        if (this.startIndex < 0) {
            return 0;
        }
        return this.startIndex;
    }

    @Override
    public int getStartIndexValue() {
        return this.startIndex;
    }

    public int getNextIndex() {
        return this.getNextStartIndex();
    }

    @Override
    public int getNextStartIndex() {
        int[] nextIndexes = this.getNextStartIndexes();
        if (nextIndexes == null) {
            return 0;
        }
        if (nextIndexes.length == 0) {
            if (this.items != null && !this.items.onLastPage()) {
                return this.items.getIndexOfFirstItemInCurrentPage() + this.items.getCurrentPage().size();
            }
            return 0;
        }
        return nextIndexes[0];
    }

    public int getPreviousIndex() {
        return this.getPreviousStartIndex();
    }

    @Override
    public int getPreviousStartIndex() {
        int[] previousIndexes = this.getPreviousStartIndexes();
        if (previousIndexes.length == 0) {
            return 0;
        }
        return previousIndexes[previousIndexes.length - 1];
    }

    @Override
    public int[] getNextStartIndexes() {
        if (this.nextStartIndexes != null) {
            return this.nextStartIndexes;
        }
        if (this.items == null || this.items.isEmpty()) {
            return new int[0];
        }
        int index = this.getEndIndex();
        if (index == this.getTotal() && this.items.onLastPage()) {
            return null;
        }
        int count = (this.getTotal() - index) / this.countOnEachPage;
        if ((this.getTotal() - index) % this.countOnEachPage > 0) {
            ++count;
        }
        this.nextStartIndexes = new int[count];
        for (int i = 0; i < count; ++i) {
            this.nextStartIndexes[i] = index;
            index += this.countOnEachPage;
        }
        return this.nextStartIndexes;
    }

    @Override
    public int[] getPreviousStartIndexes() {
        if (this.previousStartIndexes != null) {
            return this.previousStartIndexes;
        }
        if (this.items == null || this.items.isEmpty()) {
            return new int[0];
        }
        int index = this.getStartIndex();
        if (index == 0) {
            return null;
        }
        int count = index / this.countOnEachPage;
        if (index % this.countOnEachPage > 0) {
            ++count;
        }
        this.previousStartIndexes = new int[count];
        for (int i = count - 1; i > 0; --i) {
            this.previousStartIndexes[i] = index -= this.countOnEachPage;
        }
        return this.previousStartIndexes;
    }

    @Override
    public int getNiceStartIndex() {
        return this.getStartIndex() + 1;
    }

    public List getPage() {
        if (this.page == null) {
            List cache;
            if (this.items == null) {
                return null;
            }
            if (this.getStartIndex() >= this.items.getCurrentPage().size()) {
                this.loadMoreDataFromPager();
            }
            this.page = new ArrayList();
            if (this.tryNext) {
                try {
                    this.items.skipTo(this.items.getCurrentPage().size() + 1);
                }
                catch (PagerException e) {
                    log.error(e.getMessage());
                }
            }
            if (!(cache = this.items.getCurrentPage()).isEmpty()) {
                this.page = cache.subList(this.getStartIndex() - this.items.getIndexOfFirstItemInCurrentPage(), this.getEndIndex() - this.items.getIndexOfFirstItemInCurrentPage());
            }
        }
        return this.page;
    }

    private void loadMoreDataFromPager() {
        try {
            this.items.skipTo(this.getStartIndex());
        }
        catch (PagerException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public int getTotal() {
        if (this.pagerSize == null && this.items != null) {
            this.pagerSize = this.items.getCurrentPage().size();
        }
        if (this.pagerSize == null) {
            return 0;
        }
        return this.pagerSize + this.items.getIndexOfFirstItemInCurrentPage();
    }

    public void skipTo(int indexPosition) {
        try {
            this.items.skipTo(indexPosition);
        }
        catch (PagerException pagerException) {
            // empty catch block
        }
    }

    public boolean isTryNext() {
        return this.tryNext;
    }

    public void setTryNext(boolean tryNext) {
        this.tryNext = tryNext;
    }

    @Override
    public int getPageSize() {
        return this.countOnEachPage;
    }
}

