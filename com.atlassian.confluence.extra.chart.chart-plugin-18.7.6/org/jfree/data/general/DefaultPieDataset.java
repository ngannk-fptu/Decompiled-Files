/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.general;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.KeyedValues;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.PublicCloneable;
import org.jfree.util.SortOrder;

public class DefaultPieDataset
extends AbstractDataset
implements PieDataset,
Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = 2904745139106540618L;
    private DefaultKeyedValues data;

    public DefaultPieDataset() {
        this.data = new DefaultKeyedValues();
    }

    public DefaultPieDataset(KeyedValues data) {
        if (data == null) {
            throw new IllegalArgumentException("Null 'data' argument.");
        }
        this.data = new DefaultKeyedValues();
        for (int i = 0; i < data.getItemCount(); ++i) {
            this.data.addValue(data.getKey(i), data.getValue(i));
        }
    }

    public int getItemCount() {
        return this.data.getItemCount();
    }

    public List getKeys() {
        return Collections.unmodifiableList(this.data.getKeys());
    }

    public Comparable getKey(int item) {
        return this.data.getKey(item);
    }

    public int getIndex(Comparable key) {
        return this.data.getIndex(key);
    }

    public Number getValue(int item) {
        Number result = null;
        if (this.getItemCount() > item) {
            result = this.data.getValue(item);
        }
        return result;
    }

    public Number getValue(Comparable key) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        return this.data.getValue(key);
    }

    public void setValue(Comparable key, Number value) {
        this.data.setValue(key, value);
        this.fireDatasetChanged();
    }

    public void setValue(Comparable key, double value) {
        this.setValue(key, new Double(value));
    }

    public void insertValue(int position, Comparable key, double value) {
        this.insertValue(position, key, new Double(value));
    }

    public void insertValue(int position, Comparable key, Number value) {
        this.data.insertValue(position, key, value);
        this.fireDatasetChanged();
    }

    public void remove(Comparable key) {
        this.data.removeValue(key);
        this.fireDatasetChanged();
    }

    public void clear() {
        if (this.getItemCount() > 0) {
            this.data.clear();
            this.fireDatasetChanged();
        }
    }

    public void sortByKeys(SortOrder order) {
        this.data.sortByKeys(order);
        this.fireDatasetChanged();
    }

    public void sortByValues(SortOrder order) {
        this.data.sortByValues(order);
        this.fireDatasetChanged();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PieDataset)) {
            return false;
        }
        PieDataset that = (PieDataset)obj;
        int count = this.getItemCount();
        if (that.getItemCount() != count) {
            return false;
        }
        for (int i = 0; i < count; ++i) {
            Comparable k2;
            Comparable k1 = this.getKey(i);
            if (!k1.equals(k2 = that.getKey(i))) {
                return false;
            }
            Number v1 = this.getValue(i);
            Number v2 = that.getValue(i);
            if (!(v1 == null ? v2 != null : !v1.equals(v2))) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.data.hashCode();
    }

    public Object clone() throws CloneNotSupportedException {
        DefaultPieDataset clone = (DefaultPieDataset)super.clone();
        clone.data = (DefaultKeyedValues)this.data.clone();
        return clone;
    }
}

