/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.codegen;

public class IntegerCache {
    public int[] keyTable;
    public int[] valueTable;
    int elementSize = 0;
    int threshold;

    public IntegerCache() {
        this(13);
    }

    public IntegerCache(int initialCapacity) {
        this.threshold = (int)((double)initialCapacity * 0.66);
        this.keyTable = new int[initialCapacity];
        this.valueTable = new int[initialCapacity];
    }

    public void clear() {
        int i = this.keyTable.length;
        while (--i >= 0) {
            this.keyTable[i] = 0;
            this.valueTable[i] = 0;
        }
        this.elementSize = 0;
    }

    public boolean containsKey(int key) {
        int index = this.hash(key);
        int length = this.keyTable.length;
        while (this.keyTable[index] != 0 || this.keyTable[index] == 0 && this.valueTable[index] != 0) {
            if (this.keyTable[index] == key) {
                return true;
            }
            if (++index != length) continue;
            index = 0;
        }
        return false;
    }

    public int hash(int key) {
        return (key & Integer.MAX_VALUE) % this.keyTable.length;
    }

    public int put(int key, int value) {
        int index = this.hash(key);
        int length = this.keyTable.length;
        while (this.keyTable[index] != 0 || this.keyTable[index] == 0 && this.valueTable[index] != 0) {
            if (this.keyTable[index] == key) {
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

    public int putIfAbsent(int key, int value) {
        int index = this.hash(key);
        int length = this.keyTable.length;
        while (this.keyTable[index] != 0 || this.keyTable[index] == 0 && this.valueTable[index] != 0) {
            if (this.keyTable[index] == key) {
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
        return -value;
    }

    private void rehash() {
        IntegerCache newHashtable = new IntegerCache(this.keyTable.length * 2);
        int i = this.keyTable.length;
        while (--i >= 0) {
            int key = this.keyTable[i];
            int value = this.valueTable[i];
            if (key == 0 && (key != 0 || value == 0)) continue;
            newHashtable.put(key, value);
        }
        this.keyTable = newHashtable.keyTable;
        this.valueTable = newHashtable.valueTable;
        this.threshold = newHashtable.threshold;
    }

    public int size() {
        return this.elementSize;
    }

    public String toString() {
        int max = this.size();
        StringBuffer buf = new StringBuffer();
        buf.append("{");
        int i = 0;
        while (i < max) {
            if (this.keyTable[i] != 0 || this.keyTable[i] == 0 && this.valueTable[i] != 0) {
                buf.append(this.keyTable[i]).append("->").append(this.valueTable[i]);
            }
            if (i < max) {
                buf.append(", ");
            }
            ++i;
        }
        buf.append("}");
        return buf.toString();
    }
}

