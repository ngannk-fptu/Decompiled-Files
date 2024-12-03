/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.KeyedValues2D;
import org.jfree.data.UnknownKeyException;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class DefaultKeyedValues2D
implements KeyedValues2D,
PublicCloneable,
Cloneable,
Serializable {
    private static final long serialVersionUID = -5514169970951994748L;
    private List rowKeys = new ArrayList();
    private List columnKeys = new ArrayList();
    private List rows = new ArrayList();
    private boolean sortRowKeys;

    public DefaultKeyedValues2D() {
        this(false);
    }

    public DefaultKeyedValues2D(boolean sortRowKeys) {
        this.sortRowKeys = sortRowKeys;
    }

    public int getRowCount() {
        return this.rowKeys.size();
    }

    public int getColumnCount() {
        return this.columnKeys.size();
    }

    public Number getValue(int row, int column) {
        Comparable columnKey;
        int index;
        Number result = null;
        DefaultKeyedValues rowData = (DefaultKeyedValues)this.rows.get(row);
        if (rowData != null && (index = rowData.getIndex(columnKey = (Comparable)this.columnKeys.get(column))) >= 0) {
            result = rowData.getValue(index);
        }
        return result;
    }

    public Comparable getRowKey(int row) {
        return (Comparable)this.rowKeys.get(row);
    }

    public int getRowIndex(Comparable key) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        if (this.sortRowKeys) {
            return Collections.binarySearch(this.rowKeys, key);
        }
        return this.rowKeys.indexOf(key);
    }

    public List getRowKeys() {
        return Collections.unmodifiableList(this.rowKeys);
    }

    public Comparable getColumnKey(int column) {
        return (Comparable)this.columnKeys.get(column);
    }

    public int getColumnIndex(Comparable key) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        return this.columnKeys.indexOf(key);
    }

    public List getColumnKeys() {
        return Collections.unmodifiableList(this.columnKeys);
    }

    public Number getValue(Comparable rowKey, Comparable columnKey) {
        if (rowKey == null) {
            throw new IllegalArgumentException("Null 'rowKey' argument.");
        }
        if (columnKey == null) {
            throw new IllegalArgumentException("Null 'columnKey' argument.");
        }
        if (!this.columnKeys.contains(columnKey)) {
            throw new UnknownKeyException("Unrecognised columnKey: " + columnKey);
        }
        int row = this.getRowIndex(rowKey);
        if (row >= 0) {
            DefaultKeyedValues rowData = (DefaultKeyedValues)this.rows.get(row);
            int col = rowData.getIndex(columnKey);
            return col >= 0 ? (Number)rowData.getValue(col) : (Number)null;
        }
        throw new UnknownKeyException("Unrecognised rowKey: " + rowKey);
    }

    public void addValue(Number value, Comparable rowKey, Comparable columnKey) {
        this.setValue(value, rowKey, columnKey);
    }

    public void setValue(Number value, Comparable rowKey, Comparable columnKey) {
        DefaultKeyedValues row;
        int rowIndex = this.getRowIndex(rowKey);
        if (rowIndex >= 0) {
            row = (DefaultKeyedValues)this.rows.get(rowIndex);
        } else {
            row = new DefaultKeyedValues();
            if (this.sortRowKeys) {
                rowIndex = -rowIndex - 1;
                this.rowKeys.add(rowIndex, rowKey);
                this.rows.add(rowIndex, row);
            } else {
                this.rowKeys.add(rowKey);
                this.rows.add(row);
            }
        }
        row.setValue(columnKey, value);
        int columnIndex = this.columnKeys.indexOf(columnKey);
        if (columnIndex < 0) {
            this.columnKeys.add(columnKey);
        }
    }

    public void removeValue(Comparable rowKey, Comparable columnKey) {
        int columnIndex;
        int item;
        this.setValue(null, rowKey, columnKey);
        boolean allNull = true;
        int rowIndex = this.getRowIndex(rowKey);
        DefaultKeyedValues row = (DefaultKeyedValues)this.rows.get(rowIndex);
        int itemCount = row.getItemCount();
        for (item = 0; item < itemCount; ++item) {
            if (row.getValue(item) == null) continue;
            allNull = false;
            break;
        }
        if (allNull) {
            this.rowKeys.remove(rowIndex);
            this.rows.remove(rowIndex);
        }
        allNull = true;
        itemCount = this.rows.size();
        for (item = 0; item < itemCount; ++item) {
            row = (DefaultKeyedValues)this.rows.get(item);
            columnIndex = row.getIndex(columnKey);
            if (columnIndex < 0 || row.getValue(columnIndex) == null) continue;
            allNull = false;
            break;
        }
        if (allNull) {
            itemCount = this.rows.size();
            for (item = 0; item < itemCount; ++item) {
                row = (DefaultKeyedValues)this.rows.get(item);
                columnIndex = row.getIndex(columnKey);
                if (columnIndex < 0) continue;
                row.removeValue(columnIndex);
            }
            this.columnKeys.remove(columnKey);
        }
    }

    public void removeRow(int rowIndex) {
        this.rowKeys.remove(rowIndex);
        this.rows.remove(rowIndex);
    }

    public void removeRow(Comparable rowKey) {
        if (rowKey == null) {
            throw new IllegalArgumentException("Null 'rowKey' argument.");
        }
        int index = this.getRowIndex(rowKey);
        if (index < 0) {
            throw new UnknownKeyException("Unknown key: " + rowKey);
        }
        this.removeRow(index);
    }

    public void removeColumn(int columnIndex) {
        Comparable columnKey = this.getColumnKey(columnIndex);
        this.removeColumn(columnKey);
    }

    public void removeColumn(Comparable columnKey) {
        if (columnKey == null) {
            throw new IllegalArgumentException("Null 'columnKey' argument.");
        }
        if (!this.columnKeys.contains(columnKey)) {
            throw new UnknownKeyException("Unknown key: " + columnKey);
        }
        Iterator iterator = this.rows.iterator();
        while (iterator.hasNext()) {
            DefaultKeyedValues rowData = (DefaultKeyedValues)iterator.next();
            int index = rowData.getIndex(columnKey);
            if (index < 0) continue;
            rowData.removeValue(columnKey);
        }
        this.columnKeys.remove(columnKey);
    }

    public void clear() {
        this.rowKeys.clear();
        this.columnKeys.clear();
        this.rows.clear();
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof KeyedValues2D)) {
            return false;
        }
        KeyedValues2D kv2D = (KeyedValues2D)o;
        if (!((Object)this.getRowKeys()).equals(kv2D.getRowKeys())) {
            return false;
        }
        if (!((Object)this.getColumnKeys()).equals(kv2D.getColumnKeys())) {
            return false;
        }
        int rowCount = this.getRowCount();
        if (rowCount != kv2D.getRowCount()) {
            return false;
        }
        int colCount = this.getColumnCount();
        if (colCount != kv2D.getColumnCount()) {
            return false;
        }
        for (int r = 0; r < rowCount; ++r) {
            for (int c = 0; c < colCount; ++c) {
                Number v1 = this.getValue(r, c);
                Number v2 = kv2D.getValue(r, c);
                if (!(v1 == null ? v2 != null : !v1.equals(v2))) continue;
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int result = ((Object)this.rowKeys).hashCode();
        result = 29 * result + ((Object)this.columnKeys).hashCode();
        result = 29 * result + ((Object)this.rows).hashCode();
        return result;
    }

    public Object clone() throws CloneNotSupportedException {
        DefaultKeyedValues2D clone = (DefaultKeyedValues2D)super.clone();
        clone.columnKeys = new ArrayList(this.columnKeys);
        clone.rowKeys = new ArrayList(this.rowKeys);
        clone.rows = (List)ObjectUtilities.deepClone(this.rows);
        return clone;
    }
}

