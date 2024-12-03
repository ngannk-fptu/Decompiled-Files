/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data;

import java.util.Comparator;
import org.jfree.data.KeyedValue;
import org.jfree.data.KeyedValueComparatorType;
import org.jfree.util.SortOrder;

public class KeyedValueComparator
implements Comparator {
    private KeyedValueComparatorType type;
    private SortOrder order;

    public KeyedValueComparator(KeyedValueComparatorType type, SortOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Null 'order' argument.");
        }
        this.type = type;
        this.order = order;
    }

    public KeyedValueComparatorType getType() {
        return this.type;
    }

    public SortOrder getOrder() {
        return this.order;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public int compare(Object o1, Object o2) {
        if (o2 == null) {
            return -1;
        }
        if (o1 == null) {
            return 1;
        }
        KeyedValue kv1 = (KeyedValue)o1;
        KeyedValue kv2 = (KeyedValue)o2;
        if (this.type == KeyedValueComparatorType.BY_KEY) {
            if (this.order.equals(SortOrder.ASCENDING)) {
                return kv1.getKey().compareTo(kv2.getKey());
            }
            if (!this.order.equals(SortOrder.DESCENDING)) throw new IllegalArgumentException("Unrecognised sort order.");
            return kv2.getKey().compareTo(kv1.getKey());
        }
        if (this.type != KeyedValueComparatorType.BY_VALUE) throw new IllegalArgumentException("Unrecognised type.");
        Number n1 = kv1.getValue();
        Number n2 = kv2.getValue();
        if (n2 == null) {
            return -1;
        }
        if (n1 == null) {
            return 1;
        }
        double d1 = n1.doubleValue();
        double d2 = n2.doubleValue();
        if (this.order.equals(SortOrder.ASCENDING)) {
            if (d1 > d2) {
                return 1;
            }
            if (!(d1 < d2)) return 0;
            return -1;
        }
        if (!this.order.equals(SortOrder.DESCENDING)) throw new IllegalArgumentException("Unrecognised sort order.");
        if (d1 > d2) {
            return -1;
        }
        if (!(d1 < d2)) return 0;
        return 1;
    }
}

