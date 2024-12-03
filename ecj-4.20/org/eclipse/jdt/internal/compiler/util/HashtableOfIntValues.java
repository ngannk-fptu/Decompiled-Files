/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.util;

import org.eclipse.jdt.core.compiler.CharOperation;

public final class HashtableOfIntValues
implements Cloneable {
    public static final int NO_VALUE = Integer.MIN_VALUE;
    public char[][] keyTable;
    public int[] valueTable;
    public int elementSize = 0;
    int threshold;

    public HashtableOfIntValues() {
        this(13);
    }

    public HashtableOfIntValues(int size) {
        this.threshold = size;
        int extraRoom = (int)((float)size * 1.75f);
        if (this.threshold == extraRoom) {
            ++extraRoom;
        }
        this.keyTable = new char[extraRoom][];
        this.valueTable = new int[extraRoom];
    }

    public Object clone() throws CloneNotSupportedException {
        HashtableOfIntValues result = (HashtableOfIntValues)super.clone();
        result.elementSize = this.elementSize;
        result.threshold = this.threshold;
        int length = this.keyTable.length;
        result.keyTable = new char[length][];
        System.arraycopy(this.keyTable, 0, result.keyTable, 0, length);
        length = this.valueTable.length;
        result.valueTable = new int[length];
        System.arraycopy(this.valueTable, 0, result.valueTable, 0, length);
        return result;
    }

    public boolean containsKey(char[] key) {
        char[] currentKey;
        int length = this.keyTable.length;
        int index = CharOperation.hashCode(key) % length;
        int keyLength = key.length;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.length == keyLength && CharOperation.equals(currentKey, key)) {
                return true;
            }
            if (++index != length) continue;
            index = 0;
        }
        return false;
    }

    public int get(char[] key) {
        char[] currentKey;
        int length = this.keyTable.length;
        int index = CharOperation.hashCode(key) % length;
        int keyLength = key.length;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.length == keyLength && CharOperation.equals(currentKey, key)) {
                return this.valueTable[index];
            }
            if (++index != length) continue;
            index = 0;
        }
        return Integer.MIN_VALUE;
    }

    public int put(char[] key, int value) {
        char[] currentKey;
        int length = this.keyTable.length;
        int index = CharOperation.hashCode(key) % length;
        int keyLength = key.length;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.length == keyLength && CharOperation.equals(currentKey, key)) {
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

    public int removeKey(char[] key) {
        char[] currentKey;
        int length = this.keyTable.length;
        int index = CharOperation.hashCode(key) % length;
        int keyLength = key.length;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.length == keyLength && CharOperation.equals(currentKey, key)) {
                int value = this.valueTable[index];
                --this.elementSize;
                this.keyTable[index] = null;
                this.valueTable[index] = Integer.MIN_VALUE;
                this.rehash();
                return value;
            }
            if (++index != length) continue;
            index = 0;
        }
        return Integer.MIN_VALUE;
    }

    private void rehash() {
        HashtableOfIntValues newHashtable = new HashtableOfIntValues(this.elementSize * 2);
        int i = this.keyTable.length;
        while (--i >= 0) {
            char[] currentKey = this.keyTable[i];
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
        int length = this.valueTable.length;
        while (i < length) {
            char[] key = this.keyTable[i];
            if (key != null) {
                s = String.valueOf(s) + new String(key) + " -> " + this.valueTable[i] + "\n";
            }
            ++i;
        }
        return s;
    }
}

