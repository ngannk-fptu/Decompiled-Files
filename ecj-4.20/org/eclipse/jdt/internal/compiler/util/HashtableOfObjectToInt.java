/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.util;

public final class HashtableOfObjectToInt
implements Cloneable {
    public Object[] keyTable;
    public int[] valueTable;
    public int elementSize = 0;
    int threshold;

    public HashtableOfObjectToInt() {
        this(13);
    }

    public HashtableOfObjectToInt(int size) {
        this.threshold = size;
        int extraRoom = (int)((float)size * 1.75f);
        if (this.threshold == extraRoom) {
            ++extraRoom;
        }
        this.keyTable = new Object[extraRoom];
        this.valueTable = new int[extraRoom];
    }

    public Object clone() throws CloneNotSupportedException {
        HashtableOfObjectToInt result = (HashtableOfObjectToInt)super.clone();
        result.elementSize = this.elementSize;
        result.threshold = this.threshold;
        int length = this.keyTable.length;
        result.keyTable = new Object[length];
        System.arraycopy(this.keyTable, 0, result.keyTable, 0, length);
        length = this.valueTable.length;
        result.valueTable = new int[length];
        System.arraycopy(this.valueTable, 0, result.valueTable, 0, length);
        return result;
    }

    public boolean containsKey(Object key) {
        Object currentKey;
        int length = this.keyTable.length;
        int index = (key.hashCode() & Integer.MAX_VALUE) % length;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.equals(key)) {
                return true;
            }
            if (++index != length) continue;
            index = 0;
        }
        return false;
    }

    public int get(Object key) {
        Object currentKey;
        int length = this.keyTable.length;
        int index = (key.hashCode() & Integer.MAX_VALUE) % length;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.equals(key)) {
                return this.valueTable[index];
            }
            if (++index != length) continue;
            index = 0;
        }
        return -1;
    }

    public void keysToArray(Object[] array) {
        int index = 0;
        int i = 0;
        int length = this.keyTable.length;
        while (i < length) {
            if (this.keyTable[i] != null) {
                array[index++] = this.keyTable[i];
            }
            ++i;
        }
    }

    public int put(Object key, int value) {
        Object currentKey;
        int length = this.keyTable.length;
        int index = (key.hashCode() & Integer.MAX_VALUE) % length;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.equals(key)) {
                this.valueTable[index] = value;
                return this.valueTable[index];
            }
            if (++index != length) continue;
            index = 0;
        }
        this.keyTable[index] = key;
        this.valueTable[index] = value;
        if (++this.elementSize > this.threshold) {
            this.rehash();
        }
        return value;
    }

    public int removeKey(Object key) {
        Object currentKey;
        int length = this.keyTable.length;
        int index = (key.hashCode() & Integer.MAX_VALUE) % length;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.equals(key)) {
                int value = this.valueTable[index];
                --this.elementSize;
                this.keyTable[index] = null;
                this.rehash();
                return value;
            }
            if (++index != length) continue;
            index = 0;
        }
        return -1;
    }

    private void rehash() {
        HashtableOfObjectToInt newHashtable = new HashtableOfObjectToInt(this.elementSize * 2);
        int i = this.keyTable.length;
        while (--i >= 0) {
            Object currentKey = this.keyTable[i];
            if (currentKey == null) continue;
            newHashtable.put(currentKey, this.valueTable[i]);
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
        int length = this.keyTable.length;
        while (i < length) {
            Object key = this.keyTable[i];
            if (key != null) {
                s = String.valueOf(s) + key + " -> " + this.valueTable[i] + "\n";
            }
            ++i;
        }
        return s;
    }
}

