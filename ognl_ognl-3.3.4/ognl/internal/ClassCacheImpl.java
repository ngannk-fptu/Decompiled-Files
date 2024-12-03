/*
 * Decompiled with CFR 0.152.
 */
package ognl.internal;

import java.util.Arrays;
import ognl.ClassCacheInspector;
import ognl.internal.ClassCache;
import ognl.internal.Entry;

public class ClassCacheImpl
implements ClassCache {
    private static final int TABLE_SIZE = 512;
    private static final int TABLE_SIZE_MASK = 511;
    private Entry[] _table = new Entry[512];
    private ClassCacheInspector _classInspector;
    private int _size = 0;

    @Override
    public void setClassInspector(ClassCacheInspector inspector) {
        this._classInspector = inspector;
    }

    @Override
    public void clear() {
        for (int i = 0; i < this._table.length; ++i) {
            this._table[i] = null;
        }
        this._size = 0;
    }

    @Override
    public int getSize() {
        return this._size;
    }

    @Override
    public final Object get(Class key) {
        Object result = null;
        int i = key.hashCode() & 0x1FF;
        Entry entry = this._table[i];
        while (entry != null) {
            if (entry.key == key) {
                result = entry.value;
                break;
            }
            entry = entry.next;
        }
        return result;
    }

    @Override
    public final Object put(Class key, Object value) {
        if (this._classInspector != null && !this._classInspector.shouldCache(key)) {
            return value;
        }
        Object result = null;
        int i = key.hashCode() & 0x1FF;
        Entry entry = this._table[i];
        if (entry == null) {
            this._table[i] = new Entry(key, value);
            ++this._size;
        } else if (entry.key == key) {
            result = entry.value;
            entry.value = value;
        } else {
            while (true) {
                if (entry.key == key) {
                    result = entry.value;
                    entry.value = value;
                    break;
                }
                if (entry.next == null) {
                    entry.next = new Entry(key, value);
                    break;
                }
                entry = entry.next;
            }
        }
        return result;
    }

    public String toString() {
        return "ClassCacheImpl[_table=" + (this._table == null ? null : Arrays.asList(this._table)) + '\n' + ", _classInspector=" + this._classInspector + '\n' + ", _size=" + this._size + '\n' + ']';
    }
}

