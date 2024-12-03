/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import com.hazelcast.util.MutableLong;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ItemCounter<T> {
    private final Map<T, MutableLong> map = new HashMap<T, MutableLong>();
    private long total;

    public long total() {
        return this.total;
    }

    public Set<T> keySet() {
        return this.map.keySet();
    }

    public List<T> descendingKeys() {
        ArrayList<T> list = new ArrayList<T>(this.map.keySet());
        Collections.sort(list, new Comparator<T>(){

            @Override
            public int compare(T o1, T o2) {
                MutableLong l1 = (MutableLong)ItemCounter.this.map.get(o1);
                MutableLong l2 = (MutableLong)ItemCounter.this.map.get(o2);
                return this.compare(l2.value, l1.value);
            }

            @Override
            private int compare(long x, long y) {
                return x < y ? -1 : (x == y ? 0 : 1);
            }
        });
        return list;
    }

    public long get(T item) {
        MutableLong count = this.map.get(item);
        return count == null ? 0L : count.value;
    }

    public void set(T item, long value) {
        MutableLong entry = this.map.get(item);
        if (entry == null) {
            entry = MutableLong.valueOf(value);
            this.map.put(item, entry);
            this.total += value;
        } else {
            this.total -= entry.value;
            this.total += value;
            entry.value = value;
        }
    }

    public void inc(T item) {
        this.add(item, 1L);
    }

    public void add(T item, long delta) {
        MutableLong entry = this.map.get(item);
        if (entry == null) {
            entry = MutableLong.valueOf(delta);
            this.map.put(item, entry);
        } else {
            entry.value += delta;
        }
        this.total += delta;
    }

    public void reset() {
        for (MutableLong entry : this.map.values()) {
            entry.value = 0L;
        }
        this.total = 0L;
    }

    public void clear() {
        this.map.clear();
        this.total = 0L;
    }

    public long getAndSet(T item, long value) {
        MutableLong entry = this.map.get(item);
        if (entry == null) {
            entry = MutableLong.valueOf(value);
            this.map.put(item, entry);
            this.total += value;
            return 0L;
        }
        long oldValue = entry.value;
        this.total = this.total - oldValue + value;
        entry.value = value;
        return oldValue;
    }

    public void remove(T item) {
        MutableLong entry = this.map.remove(item);
        this.total -= entry == null ? 0L : entry.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ItemCounter that = (ItemCounter)o;
        return this.map.equals(that.map);
    }

    public int hashCode() {
        return this.map.hashCode();
    }

    public String toString() {
        return this.map.toString();
    }
}

