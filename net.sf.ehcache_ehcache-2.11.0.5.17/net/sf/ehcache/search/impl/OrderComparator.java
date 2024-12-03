/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.sf.ehcache.search.impl.BaseResult;
import net.sf.ehcache.store.StoreQuery;

public class OrderComparator<T extends BaseResult>
implements Comparator<T> {
    private final List<Comparator<T>> comparators = new ArrayList<Comparator<T>>();

    public OrderComparator(List<StoreQuery.Ordering> orderings) {
        int pos = 0;
        for (StoreQuery.Ordering ordering : orderings) {
            switch (ordering.getDirection()) {
                case ASCENDING: {
                    this.comparators.add(new AscendingComparator(pos));
                    break;
                }
                case DESCENDING: {
                    this.comparators.add(new DescendingComparator(pos));
                    break;
                }
                default: {
                    throw new AssertionError((Object)ordering.getDirection());
                }
            }
            ++pos;
        }
    }

    @Override
    public int compare(T o1, T o2) {
        for (Comparator<T> c : this.comparators) {
            int cmp = c.compare(o1, o2);
            if (cmp == 0) continue;
            return cmp;
        }
        return 0;
    }

    private class DescendingComparator
    implements Comparator<T>,
    Serializable {
        private final int pos;

        DescendingComparator(int pos) {
            this.pos = pos;
        }

        @Override
        public int compare(T o1, T o2) {
            Object attr1 = ((BaseResult)o1).getSortAttribute(this.pos);
            Object attr2 = ((BaseResult)o2).getSortAttribute(this.pos);
            if (attr1 == null && attr2 == null) {
                return 0;
            }
            if (attr1 == null) {
                return 1;
            }
            if (attr2 == null) {
                return -1;
            }
            return ((Comparable)attr2).compareTo(attr1);
        }
    }

    private class AscendingComparator
    implements Comparator<T>,
    Serializable {
        private final int pos;

        AscendingComparator(int pos) {
            this.pos = pos;
        }

        @Override
        public int compare(T o1, T o2) {
            Object attr1 = ((BaseResult)o1).getSortAttribute(this.pos);
            Object attr2 = ((BaseResult)o2).getSortAttribute(this.pos);
            if (attr1 == null && attr2 == null) {
                return 0;
            }
            if (attr1 == null) {
                return -1;
            }
            if (attr2 == null) {
                return 1;
            }
            return ((Comparable)attr1).compareTo(attr2);
        }
    }
}

