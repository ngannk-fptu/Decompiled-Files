/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.codegen;

public class FloatCache {
    private float[] keyTable;
    private int[] valueTable;
    private int elementSize = 0;

    public FloatCache() {
        this(13);
    }

    public FloatCache(int initialCapacity) {
        this.keyTable = new float[initialCapacity];
        this.valueTable = new int[initialCapacity];
    }

    public void clear() {
        int i = this.keyTable.length;
        while (--i >= 0) {
            this.keyTable[i] = 0.0f;
            this.valueTable[i] = 0;
        }
        this.elementSize = 0;
    }

    public boolean containsKey(float key) {
        if (key == 0.0f) {
            int i = 0;
            int max = this.elementSize;
            while (i < max) {
                if (this.keyTable[i] == 0.0f) {
                    int value1 = Float.floatToIntBits(key);
                    int value2 = Float.floatToIntBits(this.keyTable[i]);
                    if (value1 == Integer.MIN_VALUE && value2 == Integer.MIN_VALUE) {
                        return true;
                    }
                    if (value1 == 0 && value2 == 0) {
                        return true;
                    }
                }
                ++i;
            }
        } else {
            int i = 0;
            int max = this.elementSize;
            while (i < max) {
                if (this.keyTable[i] == key) {
                    return true;
                }
                ++i;
            }
        }
        return false;
    }

    public int put(float key, int value) {
        if (this.elementSize == this.keyTable.length) {
            this.keyTable = new float[this.elementSize * 2];
            System.arraycopy(this.keyTable, 0, this.keyTable, 0, this.elementSize);
            this.valueTable = new int[this.elementSize * 2];
            System.arraycopy(this.valueTable, 0, this.valueTable, 0, this.elementSize);
        }
        this.keyTable[this.elementSize] = key;
        this.valueTable[this.elementSize] = value;
        ++this.elementSize;
        return value;
    }

    public int putIfAbsent(float key, int value) {
        if (key == 0.0f) {
            int i = 0;
            int max = this.elementSize;
            while (i < max) {
                if (this.keyTable[i] == 0.0f) {
                    int value1 = Float.floatToIntBits(key);
                    int value2 = Float.floatToIntBits(this.keyTable[i]);
                    if (value1 == Integer.MIN_VALUE && value2 == Integer.MIN_VALUE) {
                        return this.valueTable[i];
                    }
                    if (value1 == 0 && value2 == 0) {
                        return this.valueTable[i];
                    }
                }
                ++i;
            }
        } else {
            int i = 0;
            int max = this.elementSize;
            while (i < max) {
                if (this.keyTable[i] == key) {
                    return this.valueTable[i];
                }
                ++i;
            }
        }
        if (this.elementSize == this.keyTable.length) {
            this.keyTable = new float[this.elementSize * 2];
            System.arraycopy(this.keyTable, 0, this.keyTable, 0, this.elementSize);
            this.valueTable = new int[this.elementSize * 2];
            System.arraycopy(this.valueTable, 0, this.valueTable, 0, this.elementSize);
        }
        this.keyTable[this.elementSize] = key;
        this.valueTable[this.elementSize] = value;
        ++this.elementSize;
        return -value;
    }

    public String toString() {
        int max = this.elementSize;
        StringBuffer buf = new StringBuffer();
        buf.append("{");
        int i = 0;
        while (i < max) {
            if (this.keyTable[i] != 0.0f || this.keyTable[i] == 0.0f && this.valueTable[i] != 0) {
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

