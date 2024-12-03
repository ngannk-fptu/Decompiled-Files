/*
 * Decompiled with CFR 0.152.
 */
package bucket.core;

import java.util.List;

@Deprecated
public interface PaginationSupport<T> {
    public int getStartIndex();

    public int getNiceStartIndex();

    public int getTotal();

    public List<T> getPage();

    public int getStartIndexValue();

    public int getNextStartIndex();

    public int getPreviousStartIndex();

    public int[] getNextStartIndexes();

    public int[] getPreviousStartIndexes();

    public int getNiceEndIndex();

    public int getPageSize();
}

