/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jfree.data.UnknownKeyException;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.AbstractDataset;
import org.jfree.util.PublicCloneable;

public class SlidingCategoryDataset
extends AbstractDataset
implements CategoryDataset {
    private CategoryDataset underlying;
    private int firstCategoryIndex;
    private int maximumCategoryCount;

    public SlidingCategoryDataset(CategoryDataset underlying, int firstColumn, int maxColumns) {
        this.underlying = underlying;
        this.firstCategoryIndex = firstColumn;
        this.maximumCategoryCount = maxColumns;
    }

    public CategoryDataset getUnderlyingDataset() {
        return this.underlying;
    }

    public int getFirstCategoryIndex() {
        return this.firstCategoryIndex;
    }

    public void setFirstCategoryIndex(int first) {
        if (first < 0 || first >= this.underlying.getColumnCount()) {
            throw new IllegalArgumentException("Invalid index.");
        }
        this.firstCategoryIndex = first;
        this.fireDatasetChanged();
    }

    public int getMaximumCategoryCount() {
        return this.maximumCategoryCount;
    }

    public void setMaximumCategoryCount(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("Requires 'max' >= 0.");
        }
        this.maximumCategoryCount = max;
        this.fireDatasetChanged();
    }

    private int lastCategoryIndex() {
        if (this.maximumCategoryCount == 0) {
            return -1;
        }
        return Math.min(this.firstCategoryIndex + this.maximumCategoryCount, this.underlying.getColumnCount()) - 1;
    }

    public int getColumnIndex(Comparable key) {
        int index = this.underlying.getColumnIndex(key);
        if (index >= this.firstCategoryIndex && index <= this.lastCategoryIndex()) {
            return index - this.firstCategoryIndex;
        }
        return -1;
    }

    public Comparable getColumnKey(int column) {
        return this.underlying.getColumnKey(column + this.firstCategoryIndex);
    }

    public List getColumnKeys() {
        ArrayList<Comparable> result = new ArrayList<Comparable>();
        int last = this.lastCategoryIndex();
        for (int i = this.firstCategoryIndex; i <= last; ++i) {
            result.add(this.underlying.getColumnKey(i));
        }
        return Collections.unmodifiableList(result);
    }

    public int getRowIndex(Comparable key) {
        return this.underlying.getRowIndex(key);
    }

    public Comparable getRowKey(int row) {
        return this.underlying.getRowKey(row);
    }

    public List getRowKeys() {
        return this.underlying.getRowKeys();
    }

    public Number getValue(Comparable rowKey, Comparable columnKey) {
        int r = this.getRowIndex(rowKey);
        int c = this.getColumnIndex(columnKey);
        if (c != -1) {
            return this.underlying.getValue(r, c + this.firstCategoryIndex);
        }
        throw new UnknownKeyException("Unknown columnKey: " + columnKey);
    }

    public int getColumnCount() {
        int last = this.lastCategoryIndex();
        if (last == -1) {
            return 0;
        }
        return Math.max(last - this.firstCategoryIndex + 1, 0);
    }

    public int getRowCount() {
        return this.underlying.getRowCount();
    }

    public Number getValue(int row, int column) {
        return this.underlying.getValue(row, column + this.firstCategoryIndex);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SlidingCategoryDataset)) {
            return false;
        }
        SlidingCategoryDataset that = (SlidingCategoryDataset)obj;
        if (this.firstCategoryIndex != that.firstCategoryIndex) {
            return false;
        }
        if (this.maximumCategoryCount != that.maximumCategoryCount) {
            return false;
        }
        return this.underlying.equals(that.underlying);
    }

    public Object clone() throws CloneNotSupportedException {
        SlidingCategoryDataset clone = (SlidingCategoryDataset)super.clone();
        if (this.underlying instanceof PublicCloneable) {
            PublicCloneable pc = (PublicCloneable)((Object)this.underlying);
            clone.underlying = (CategoryDataset)pc.clone();
        }
        return clone;
    }
}

