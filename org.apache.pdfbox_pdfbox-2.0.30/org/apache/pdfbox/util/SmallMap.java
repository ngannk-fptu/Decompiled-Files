/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class SmallMap<K, V>
implements Map<K, V> {
    private Object[] mapArr;

    public SmallMap() {
    }

    public SmallMap(Map<? extends K, ? extends V> initMap) {
        this.putAll(initMap);
    }

    private int findKey(Object key) {
        if (this.isEmpty() || key == null) {
            return -1;
        }
        for (int aIdx = 0; aIdx < this.mapArr.length; aIdx += 2) {
            if (!key.equals(this.mapArr[aIdx])) continue;
            return aIdx;
        }
        return -1;
    }

    private int findValue(Object value) {
        if (this.isEmpty() || value == null) {
            return -1;
        }
        for (int aIdx = 1; aIdx < this.mapArr.length; aIdx += 2) {
            if (!value.equals(this.mapArr[aIdx])) continue;
            return aIdx;
        }
        return -1;
    }

    @Override
    public int size() {
        return this.mapArr == null ? 0 : this.mapArr.length >> 1;
    }

    @Override
    public boolean isEmpty() {
        return this.mapArr == null || this.mapArr.length == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.findKey(key) >= 0;
    }

    @Override
    public boolean containsValue(Object value) {
        return this.findValue(value) >= 0;
    }

    @Override
    public V get(Object key) {
        int kIdx = this.findKey(key);
        return (V)(kIdx < 0 ? null : this.mapArr[kIdx + 1]);
    }

    @Override
    public V put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException("Key or value must not be null.");
        }
        if (this.mapArr == null) {
            this.mapArr = new Object[]{key, value};
            return null;
        }
        int kIdx = this.findKey(key);
        if (kIdx < 0) {
            int oldLen = this.mapArr.length;
            Object[] newMapArr = new Object[oldLen + 2];
            System.arraycopy(this.mapArr, 0, newMapArr, 0, oldLen);
            newMapArr[oldLen] = key;
            newMapArr[oldLen + 1] = value;
            this.mapArr = newMapArr;
            return null;
        }
        Object oldValue = this.mapArr[kIdx + 1];
        this.mapArr[kIdx + 1] = value;
        return (V)oldValue;
    }

    @Override
    public V remove(Object key) {
        int kIdx = this.findKey(key);
        if (kIdx < 0) {
            return null;
        }
        Object oldValue = this.mapArr[kIdx + 1];
        int oldLen = this.mapArr.length;
        if (oldLen == 2) {
            this.mapArr = null;
        } else {
            Object[] newMapArr = new Object[oldLen - 2];
            System.arraycopy(this.mapArr, 0, newMapArr, 0, kIdx);
            System.arraycopy(this.mapArr, kIdx + 2, newMapArr, kIdx, oldLen - kIdx - 2);
            this.mapArr = newMapArr;
        }
        return (V)oldValue;
    }

    @Override
    public final void putAll(Map<? extends K, ? extends V> otherMap) {
        if (this.mapArr == null || this.mapArr.length == 0) {
            this.mapArr = new Object[otherMap.size() << 1];
            int aIdx = 0;
            for (Map.Entry<K, V> entry : otherMap.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    throw new NullPointerException("Key or value must not be null.");
                }
                this.mapArr[aIdx++] = entry.getKey();
                this.mapArr[aIdx++] = entry.getValue();
            }
        } else {
            int oldLen = this.mapArr.length;
            Object[] newMapArr = new Object[oldLen + (otherMap.size() << 1)];
            System.arraycopy(this.mapArr, 0, newMapArr, 0, oldLen);
            int newIdx = oldLen;
            for (Map.Entry<K, V> entry : otherMap.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    throw new NullPointerException("Key or value must not be null.");
                }
                int existKeyIdx = this.findKey(entry.getKey());
                if (existKeyIdx >= 0) {
                    newMapArr[existKeyIdx + 1] = entry.getValue();
                    continue;
                }
                newMapArr[newIdx++] = entry.getKey();
                newMapArr[newIdx++] = entry.getValue();
            }
            if (newIdx < newMapArr.length) {
                Object[] reducedMapArr = new Object[newIdx];
                System.arraycopy(newMapArr, 0, reducedMapArr, 0, newIdx);
                newMapArr = reducedMapArr;
            }
            this.mapArr = newMapArr;
        }
    }

    @Override
    public void clear() {
        this.mapArr = null;
    }

    @Override
    public Set<K> keySet() {
        if (this.isEmpty()) {
            return Collections.emptySet();
        }
        LinkedHashSet<Object> keys = new LinkedHashSet<Object>();
        for (int kIdx = 0; kIdx < this.mapArr.length; kIdx += 2) {
            keys.add(this.mapArr[kIdx]);
        }
        return Collections.unmodifiableSet(keys);
    }

    @Override
    public Collection<V> values() {
        if (this.isEmpty()) {
            return Collections.emptySet();
        }
        ArrayList<Object> values = new ArrayList<Object>(this.mapArr.length >> 1);
        for (int vIdx = 1; vIdx < this.mapArr.length; vIdx += 2) {
            values.add(this.mapArr[vIdx]);
        }
        return Collections.unmodifiableList(values);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (this.isEmpty()) {
            return Collections.emptySet();
        }
        LinkedHashSet<SmallMapEntry> entries = new LinkedHashSet<SmallMapEntry>();
        for (int kIdx = 0; kIdx < this.mapArr.length; kIdx += 2) {
            entries.add(new SmallMapEntry(kIdx));
        }
        return Collections.unmodifiableSet(entries);
    }

    private class SmallMapEntry
    implements Map.Entry<K, V> {
        private final int keyIdx;

        SmallMapEntry(int keyInMapIdx) {
            this.keyIdx = keyInMapIdx;
        }

        @Override
        public K getKey() {
            return SmallMap.this.mapArr[this.keyIdx];
        }

        @Override
        public V getValue() {
            return SmallMap.this.mapArr[this.keyIdx + 1];
        }

        @Override
        public V setValue(V value) {
            if (value == null) {
                throw new NullPointerException("Key or value must not be null.");
            }
            Object oldValue = this.getValue();
            ((SmallMap)SmallMap.this).mapArr[this.keyIdx + 1] = value;
            return oldValue;
        }

        @Override
        public int hashCode() {
            return this.getKey().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SmallMapEntry)) {
                return false;
            }
            SmallMapEntry other = (SmallMapEntry)obj;
            return this.getKey().equals(other.getKey()) && this.getValue().equals(other.getValue());
        }
    }
}

