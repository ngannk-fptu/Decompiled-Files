/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.util;

import com.atlassian.confluence.plugins.gatekeeper.model.filter.Filter;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class BitSetList<T> {
    private int size = -1;
    private int totalSize;
    private List<? extends T> elements;
    private BitSet matchesFilters;
    private BitSet hasPermissions;

    public BitSetList(T element) {
        this.elements = Collections.singletonList(element);
        this.totalSize = 1;
    }

    public BitSetList(List<? extends T> elements, boolean excludeWithNoPermissions) {
        this.elements = elements;
        this.totalSize = elements.size();
        if (excludeWithNoPermissions) {
            this.hasPermissions = new BitSet(elements.size());
        }
    }

    public void applyFilter(Filter<T> filter) {
        this.matchesFilters = new BitSet(this.elements.size());
        for (int i = 0; i < this.elements.size(); ++i) {
            T element = this.elements.get(i);
            if (!filter.matches(element)) continue;
            this.matchesFilters.set(i);
        }
    }

    public void applyFilters(List<Filter<T>> filters) {
        this.matchesFilters = new BitSet(this.elements.size());
        for (int i = 0; i < this.elements.size(); ++i) {
            T element = this.elements.get(i);
            boolean matches = true;
            for (Filter<T> filter : filters) {
                if (filter.matches(element)) continue;
                matches = false;
                break;
            }
            if (!matches) continue;
            this.matchesFilters.set(i);
        }
    }

    public void setMatchesFilters(int index) {
        if (this.matchesFilters != null) {
            this.matchesFilters.set(index);
        }
    }

    public void setHasPermissions(int index) {
        if (this.hasPermissions != null) {
            this.hasPermissions.set(index);
        }
    }

    public boolean hasPermissions(int spaceIndex) {
        return this.hasPermissions != null && this.hasPermissions.get(spaceIndex);
    }

    public T get(int index) {
        return this.elements.get(index);
    }

    public int nextMatchesFilters(int index) {
        if (index >= this.totalSize) {
            return -1;
        }
        if (this.matchesFilters == null) {
            return index;
        }
        return this.matchesFilters.nextSetBit(index);
    }

    public int nextVisible(int index) {
        if (index >= this.totalSize) {
            return -1;
        }
        if (this.matchesFilters == null && this.hasPermissions == null) {
            return index;
        }
        return (this.hasPermissions != null ? this.hasPermissions : this.matchesFilters).nextSetBit(index);
    }

    public int getVisibleSize() {
        if (this.size == -1) {
            if (this.matchesFilters == null && this.hasPermissions == null) {
                this.size = this.totalSize;
            } else if (this.hasPermissions != null) {
                this.size = this.hasPermissions.cardinality();
            } else if (this.matchesFilters != null) {
                this.size = this.matchesFilters.cardinality();
            }
        }
        return this.size;
    }

    public int getLimitedVisibleSize(int hardLimit) {
        return Math.min(this.getVisibleSize(), hardLimit);
    }

    public int getTotalSize() {
        return this.totalSize;
    }
}

