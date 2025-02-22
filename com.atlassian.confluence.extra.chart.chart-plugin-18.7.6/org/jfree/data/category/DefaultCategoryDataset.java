/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.category;

import java.io.Serializable;
import java.util.List;
import org.jfree.data.DefaultKeyedValues2D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.AbstractDataset;
import org.jfree.util.PublicCloneable;

public class DefaultCategoryDataset
extends AbstractDataset
implements CategoryDataset,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = -8168173757291644622L;
    private DefaultKeyedValues2D data = new DefaultKeyedValues2D();

    public int getRowCount() {
        return this.data.getRowCount();
    }

    public int getColumnCount() {
        return this.data.getColumnCount();
    }

    public Number getValue(int row, int column) {
        return this.data.getValue(row, column);
    }

    public Comparable getRowKey(int row) {
        return this.data.getRowKey(row);
    }

    public int getRowIndex(Comparable key) {
        return this.data.getRowIndex(key);
    }

    public List getRowKeys() {
        return this.data.getRowKeys();
    }

    public Comparable getColumnKey(int column) {
        return this.data.getColumnKey(column);
    }

    public int getColumnIndex(Comparable key) {
        return this.data.getColumnIndex(key);
    }

    public List getColumnKeys() {
        return this.data.getColumnKeys();
    }

    public Number getValue(Comparable rowKey, Comparable columnKey) {
        return this.data.getValue(rowKey, columnKey);
    }

    public void addValue(Number value, Comparable rowKey, Comparable columnKey) {
        this.data.addValue(value, rowKey, columnKey);
        this.fireDatasetChanged();
    }

    public void addValue(double value, Comparable rowKey, Comparable columnKey) {
        this.addValue(new Double(value), rowKey, columnKey);
    }

    public void setValue(Number value, Comparable rowKey, Comparable columnKey) {
        this.data.setValue(value, rowKey, columnKey);
        this.fireDatasetChanged();
    }

    public void setValue(double value, Comparable rowKey, Comparable columnKey) {
        this.setValue(new Double(value), rowKey, columnKey);
    }

    public void incrementValue(double value, Comparable rowKey, Comparable columnKey) {
        double existing = 0.0;
        Number n = this.getValue(rowKey, columnKey);
        if (n != null) {
            existing = n.doubleValue();
        }
        this.setValue(existing + value, rowKey, columnKey);
    }

    public void removeValue(Comparable rowKey, Comparable columnKey) {
        this.data.removeValue(rowKey, columnKey);
        this.fireDatasetChanged();
    }

    public void removeRow(int rowIndex) {
        this.data.removeRow(rowIndex);
        this.fireDatasetChanged();
    }

    public void removeRow(Comparable rowKey) {
        this.data.removeRow(rowKey);
        this.fireDatasetChanged();
    }

    public void removeColumn(int columnIndex) {
        this.data.removeColumn(columnIndex);
        this.fireDatasetChanged();
    }

    public void removeColumn(Comparable columnKey) {
        this.data.removeColumn(columnKey);
        this.fireDatasetChanged();
    }

    public void clear() {
        this.data.clear();
        this.fireDatasetChanged();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CategoryDataset)) {
            return false;
        }
        CategoryDataset that = (CategoryDataset)obj;
        if (!((Object)this.getRowKeys()).equals(that.getRowKeys())) {
            return false;
        }
        if (!((Object)this.getColumnKeys()).equals(that.getColumnKeys())) {
            return false;
        }
        int rowCount = this.getRowCount();
        int colCount = this.getColumnCount();
        for (int r = 0; r < rowCount; ++r) {
            for (int c = 0; c < colCount; ++c) {
                Number v1 = this.getValue(r, c);
                Number v2 = that.getValue(r, c);
                if (!(v1 == null ? v2 != null : !v1.equals(v2))) continue;
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        return this.data.hashCode();
    }

    public Object clone() throws CloneNotSupportedException {
        DefaultCategoryDataset clone = (DefaultCategoryDataset)super.clone();
        clone.data = (DefaultKeyedValues2D)this.data.clone();
        return clone;
    }
}

