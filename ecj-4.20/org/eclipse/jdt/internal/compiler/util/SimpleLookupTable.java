/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.util;

public final class SimpleLookupTable
implements Cloneable {
    public Object[] keyTable;
    public Object[] valueTable;
    public int elementSize = 0;
    public int threshold;

    public SimpleLookupTable() {
        this(13);
    }

    public SimpleLookupTable(int size) {
        this.threshold = size;
        int extraRoom = (int)((float)size * 1.5f);
        if (this.threshold == extraRoom) {
            ++extraRoom;
        }
        this.keyTable = new Object[extraRoom];
        this.valueTable = new Object[extraRoom];
    }

    public Object clone() throws CloneNotSupportedException {
        SimpleLookupTable result = (SimpleLookupTable)super.clone();
        result.elementSize = this.elementSize;
        result.threshold = this.threshold;
        int length = this.keyTable.length;
        result.keyTable = new Object[length];
        System.arraycopy(this.keyTable, 0, result.keyTable, 0, length);
        length = this.valueTable.length;
        result.valueTable = new Object[length];
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

    public Object get(Object key) {
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
        return null;
    }

    public Object getKey(Object key) {
        Object currentKey;
        int length = this.keyTable.length;
        int index = (key.hashCode() & Integer.MAX_VALUE) % length;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.equals(key)) {
                return currentKey;
            }
            if (++index != length) continue;
            index = 0;
        }
        return key;
    }

    public Object keyForValue(Object valueToMatch) {
        if (valueToMatch != null) {
            int i = 0;
            int l = this.keyTable.length;
            while (i < l) {
                if (this.keyTable[i] != null && valueToMatch.equals(this.valueTable[i])) {
                    return this.keyTable[i];
                }
                ++i;
            }
        }
        return null;
    }

    public Object put(Object key, Object value) {
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

    public Object removeKey(Object key) {
        Object currentKey;
        int length = this.keyTable.length;
        int index = (key.hashCode() & Integer.MAX_VALUE) % length;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.equals(key)) {
                --this.elementSize;
                Object oldValue = this.valueTable[index];
                this.keyTable[index] = null;
                this.valueTable[index] = null;
                if (this.keyTable[index + 1 == length ? 0 : index + 1] != null) {
                    this.rehash();
                }
                return oldValue;
            }
            if (++index != length) continue;
            index = 0;
        }
        return null;
    }

    public void removeValue(Object valueToRemove) {
        boolean rehash = false;
        int i = 0;
        int l = this.valueTable.length;
        while (i < l) {
            Object value = this.valueTable[i];
            if (value != null && value.equals(valueToRemove)) {
                --this.elementSize;
                this.keyTable[i] = null;
                this.valueTable[i] = null;
                if (!rehash && this.keyTable[i + 1 == l ? 0 : i + 1] != null) {
                    rehash = true;
                }
            }
            ++i;
        }
        if (rehash) {
            this.rehash();
        }
    }

    private void rehash() {
        SimpleLookupTable newLookupTable = new SimpleLookupTable(this.elementSize * 2);
        int i = this.keyTable.length;
        while (--i >= 0) {
            Object currentKey = this.keyTable[i];
            if (currentKey == null) continue;
            newLookupTable.put(currentKey, this.valueTable[i]);
        }
        this.keyTable = newLookupTable.keyTable;
        this.valueTable = newLookupTable.valueTable;
        this.elementSize = newLookupTable.elementSize;
        this.threshold = newLookupTable.threshold;
    }

    public String toString() {
        String s = "";
        int i = 0;
        int l = this.valueTable.length;
        while (i < l) {
            Object object = this.valueTable[i];
            if (object != null) {
                s = String.valueOf(s) + this.keyTable[i].toString() + " -> " + object.toString() + "\n";
            }
            ++i;
        }
        return s;
    }
}

