/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.codegen;

public class ObjectCache {
    public Object[] keyTable;
    public int[] valueTable;
    int elementSize = 0;
    int threshold;

    public ObjectCache() {
        this(13);
    }

    public ObjectCache(int initialCapacity) {
        this.threshold = (int)((float)initialCapacity * 0.66f);
        this.keyTable = new Object[initialCapacity];
        this.valueTable = new int[initialCapacity];
    }

    public void clear() {
        int i = this.keyTable.length;
        while (--i >= 0) {
            this.keyTable[i] = null;
            this.valueTable[i] = 0;
        }
        this.elementSize = 0;
    }

    public boolean containsKey(Object key) {
        int index = this.hashCode(key);
        int length = this.keyTable.length;
        while (this.keyTable[index] != null) {
            if (this.keyTable[index] == key) {
                return true;
            }
            if (++index != length) continue;
            index = 0;
        }
        return false;
    }

    public int get(Object key) {
        int index = this.hashCode(key);
        int length = this.keyTable.length;
        while (this.keyTable[index] != null) {
            if (this.keyTable[index] == key) {
                return this.valueTable[index];
            }
            if (++index != length) continue;
            index = 0;
        }
        return -1;
    }

    public int hashCode(Object key) {
        return (key.hashCode() & Integer.MAX_VALUE) % this.keyTable.length;
    }

    public int put(Object key, int value) {
        int index = this.hashCode(key);
        int length = this.keyTable.length;
        while (this.keyTable[index] != null) {
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

    private void rehash() {
        ObjectCache newHashtable = new ObjectCache(this.keyTable.length * 2);
        int i = this.keyTable.length;
        while (--i >= 0) {
            if (this.keyTable[i] == null) continue;
            newHashtable.put(this.keyTable[i], this.valueTable[i]);
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
            if (this.keyTable[i] != null) {
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

