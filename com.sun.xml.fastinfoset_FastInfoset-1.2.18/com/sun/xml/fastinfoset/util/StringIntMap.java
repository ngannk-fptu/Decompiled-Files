/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.util;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import com.sun.xml.fastinfoset.util.KeyIntMap;

public class StringIntMap
extends KeyIntMap {
    protected static final Entry NULL_ENTRY = new Entry(null, 0, -1, null);
    protected StringIntMap _readOnlyMap;
    protected Entry _lastEntry = NULL_ENTRY;
    protected Entry[] _table = new Entry[this._capacity];
    protected int _index;
    protected int _totalCharacterCount;

    public StringIntMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public StringIntMap(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    public StringIntMap() {
        this(16, 0.75f);
    }

    @Override
    public void clear() {
        for (int i = 0; i < this._table.length; ++i) {
            this._table[i] = null;
        }
        this._lastEntry = NULL_ENTRY;
        this._size = 0;
        this._index = this._readOnlyMapSize;
        this._totalCharacterCount = 0;
    }

    @Override
    public void setReadOnlyMap(KeyIntMap readOnlyMap, boolean clear) {
        if (!(readOnlyMap instanceof StringIntMap)) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[]{readOnlyMap}));
        }
        this.setReadOnlyMap((StringIntMap)readOnlyMap, clear);
    }

    public final void setReadOnlyMap(StringIntMap readOnlyMap, boolean clear) {
        this._readOnlyMap = readOnlyMap;
        if (this._readOnlyMap != null) {
            this._readOnlyMapSize = this._readOnlyMap.size();
            this._index = this._size + this._readOnlyMapSize;
            if (clear) {
                this.clear();
            }
        } else {
            this._readOnlyMapSize = 0;
            this._index = this._size;
        }
    }

    public final int getNextIndex() {
        return this._index++;
    }

    public final int getIndex() {
        return this._index;
    }

    public final int obtainIndex(String key) {
        int index;
        int hash = StringIntMap.hashHash(key.hashCode());
        if (this._readOnlyMap != null && (index = this._readOnlyMap.get(key, hash)) != -1) {
            return index;
        }
        int tableIndex = StringIntMap.indexFor(hash, this._table.length);
        Entry e = this._table[tableIndex];
        while (e != null) {
            if (e._hash == hash && this.eq(key, e._key)) {
                return e._value;
            }
            e = e._next;
        }
        this.addEntry(key, hash, tableIndex);
        return -1;
    }

    public final void add(String key) {
        int hash = StringIntMap.hashHash(key.hashCode());
        int tableIndex = StringIntMap.indexFor(hash, this._table.length);
        this.addEntry(key, hash, tableIndex);
    }

    public final int get(String key) {
        if (key == this._lastEntry._key) {
            return this._lastEntry._value;
        }
        return this.get(key, StringIntMap.hashHash(key.hashCode()));
    }

    public final int getTotalCharacterCount() {
        return this._totalCharacterCount;
    }

    private final int get(String key, int hash) {
        int i;
        if (this._readOnlyMap != null && (i = this._readOnlyMap.get(key, hash)) != -1) {
            return i;
        }
        int tableIndex = StringIntMap.indexFor(hash, this._table.length);
        Entry e = this._table[tableIndex];
        while (e != null) {
            if (e._hash == hash && this.eq(key, e._key)) {
                this._lastEntry = e;
                return e._value;
            }
            e = e._next;
        }
        return -1;
    }

    private final void addEntry(String key, int hash, int bucketIndex) {
        Entry e = this._table[bucketIndex];
        this._table[bucketIndex] = new Entry(key, hash, this._index++, e);
        this._totalCharacterCount += key.length();
        if (this._size++ >= this._threshold) {
            this.resize(2 * this._table.length);
        }
    }

    protected final void resize(int newCapacity) {
        this._capacity = newCapacity;
        Entry[] oldTable = this._table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == 0x100000) {
            this._threshold = Integer.MAX_VALUE;
            return;
        }
        Entry[] newTable = new Entry[this._capacity];
        this.transfer(newTable);
        this._table = newTable;
        this._threshold = (int)((float)this._capacity * this._loadFactor);
    }

    private final void transfer(Entry[] newTable) {
        Entry[] src = this._table;
        int newCapacity = newTable.length;
        for (int j = 0; j < src.length; ++j) {
            Entry next;
            Entry e = src[j];
            if (e == null) continue;
            src[j] = null;
            do {
                next = e._next;
                int i = StringIntMap.indexFor(e._hash, newCapacity);
                e._next = newTable[i];
                newTable[i] = e;
            } while ((e = next) != null);
        }
    }

    private final boolean eq(String x, String y) {
        return x == y || x.equals(y);
    }

    protected static class Entry
    extends KeyIntMap.BaseEntry {
        final String _key;
        Entry _next;

        public Entry(String key, int hash, int value, Entry next) {
            super(hash, value);
            this._key = key;
            this._next = next;
        }
    }
}

