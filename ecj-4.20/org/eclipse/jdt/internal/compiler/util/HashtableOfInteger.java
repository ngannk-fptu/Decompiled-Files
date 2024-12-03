/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.util;

public final class HashtableOfInteger {
    public Integer[] keyTable;
    public Object[] valueTable;
    public int elementSize = 0;
    int threshold;

    public HashtableOfInteger() {
        this(13);
    }

    public HashtableOfInteger(int size) {
        this.threshold = size;
        int extraRoom = (int)((float)size * 1.75f);
        if (this.threshold == extraRoom) {
            ++extraRoom;
        }
        this.keyTable = new Integer[extraRoom];
        this.valueTable = new Object[extraRoom];
    }

    public void clear() {
        int i = this.keyTable.length;
        while (--i >= 0) {
            this.keyTable[i] = null;
            this.valueTable[i] = null;
        }
        this.elementSize = 0;
    }

    public Object clone() throws CloneNotSupportedException {
        HashtableOfInteger result = (HashtableOfInteger)super.clone();
        result.elementSize = this.elementSize;
        result.threshold = this.threshold;
        int length = this.keyTable.length;
        result.keyTable = new Integer[length];
        System.arraycopy(this.keyTable, 0, result.keyTable, 0, length);
        length = this.valueTable.length;
        result.valueTable = new Object[length];
        System.arraycopy(this.valueTable, 0, result.valueTable, 0, length);
        return result;
    }

    public boolean containsKey(int key) {
        Integer currentKey;
        Integer intKey = key;
        int length = this.keyTable.length;
        int index = intKey.hashCode() % length;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.equals(intKey)) {
                return true;
            }
            if (++index != length) continue;
            index = 0;
        }
        return false;
    }

    public Object get(int key) {
        Integer currentKey;
        Integer intKey = key;
        int length = this.keyTable.length;
        int index = intKey.hashCode() % length;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.equals(intKey)) {
                return this.valueTable[index];
            }
            if (++index != length) continue;
            index = 0;
        }
        return null;
    }

    public Object put(int key, Object value) {
        Integer currentKey;
        Integer intKey = key;
        int length = this.keyTable.length;
        int index = intKey.hashCode() % length;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.equals(intKey)) {
                this.valueTable[index] = value;
                return this.valueTable[index];
            }
            if (++index != length) continue;
            index = 0;
        }
        this.keyTable[index] = intKey;
        this.valueTable[index] = value;
        if (++this.elementSize > this.threshold) {
            this.rehash();
        }
        return value;
    }

    public void putUnsafely(int key, Object value) {
        Integer intKey = key;
        int length = this.keyTable.length;
        int index = intKey.hashCode() % length;
        while (this.keyTable[index] != null) {
            if (++index != length) continue;
            index = 0;
        }
        this.keyTable[index] = intKey;
        this.valueTable[index] = value;
        if (++this.elementSize > this.threshold) {
            this.rehash();
        }
    }

    public Object removeKey(int key) {
        Integer currentKey;
        Integer intKey = key;
        int length = this.keyTable.length;
        int index = intKey.hashCode() % length;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.equals(intKey)) {
                Object value = this.valueTable[index];
                --this.elementSize;
                this.keyTable[index] = null;
                this.valueTable[index] = null;
                this.rehash();
                return value;
            }
            if (++index != length) continue;
            index = 0;
        }
        return null;
    }

    private void rehash() {
        HashtableOfInteger newHashtable = new HashtableOfInteger(this.elementSize * 2);
        int i = this.keyTable.length;
        while (--i >= 0) {
            Integer currentKey = this.keyTable[i];
            if (currentKey == null) continue;
            newHashtable.putUnsafely(currentKey, this.valueTable[i]);
        }
        this.keyTable = newHashtable.keyTable;
        this.valueTable = newHashtable.valueTable;
        this.threshold = newHashtable.threshold;
    }

    public int size() {
        return this.elementSize;
    }

    public String toString() {
        String s = "";
        int i = 0;
        int length = this.valueTable.length;
        while (i < length) {
            Object object = this.valueTable[i];
            if (object != null) {
                s = String.valueOf(s) + this.keyTable[i] + " -> " + object.toString() + "\n";
            }
            ++i;
        }
        return s;
    }
}

