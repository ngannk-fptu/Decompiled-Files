/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.util;

import java.io.PrintStream;

public final class IntegerArray
implements Cloneable {
    private static final int InitialSize = 32;
    private int[] _array;
    private int _size;
    private int _free = 0;

    public IntegerArray() {
        this(32);
    }

    public IntegerArray(int size) {
        this._size = size;
        this._array = new int[this._size];
    }

    public IntegerArray(int[] array) {
        this(array.length);
        this._free = this._size;
        System.arraycopy(array, 0, this._array, 0, this._free);
    }

    public void clear() {
        this._free = 0;
    }

    public Object clone() {
        IntegerArray clone = new IntegerArray(this._free > 0 ? this._free : 1);
        System.arraycopy(this._array, 0, clone._array, 0, this._free);
        clone._free = this._free;
        return clone;
    }

    public int[] toIntArray() {
        int[] result = new int[this.cardinality()];
        System.arraycopy(this._array, 0, result, 0, this.cardinality());
        return result;
    }

    public final int at(int index) {
        return this._array[index];
    }

    public final void set(int index, int value) {
        this._array[index] = value;
    }

    public int indexOf(int n) {
        for (int i = 0; i < this._free; ++i) {
            if (n != this._array[i]) continue;
            return i;
        }
        return -1;
    }

    public final void add(int value) {
        if (this._free == this._size) {
            this.growArray(this._size * 2);
        }
        this._array[this._free++] = value;
    }

    public void addNew(int value) {
        for (int i = 0; i < this._free; ++i) {
            if (this._array[i] != value) continue;
            return;
        }
        this.add(value);
    }

    public void reverse() {
        int left = 0;
        int right = this._free - 1;
        while (left < right) {
            int temp = this._array[left];
            this._array[left++] = this._array[right];
            this._array[right--] = temp;
        }
    }

    public void merge(IntegerArray other) {
        int newSize = this._free + other._free;
        int[] newArray = new int[newSize];
        int i = 0;
        int j = 0;
        int k = 0;
        while (i < this._free && j < other._free) {
            int x = this._array[i];
            int y = other._array[j];
            if (x < y) {
                newArray[k] = x;
                ++i;
            } else if (x > y) {
                newArray[k] = y;
                ++j;
            } else {
                newArray[k] = x;
                ++i;
                ++j;
            }
            ++k;
        }
        if (i >= this._free) {
            while (j < other._free) {
                newArray[k++] = other._array[j++];
            }
        } else {
            while (i < this._free) {
                newArray[k++] = this._array[i++];
            }
        }
        this._array = newArray;
        this._free = this._size = newSize;
    }

    public void sort() {
        IntegerArray.quicksort(this._array, 0, this._free - 1);
    }

    private static void quicksort(int[] array, int p, int r) {
        if (p < r) {
            int q = IntegerArray.partition(array, p, r);
            IntegerArray.quicksort(array, p, q);
            IntegerArray.quicksort(array, q + 1, r);
        }
    }

    private static int partition(int[] array, int p, int r) {
        int x = array[p + r >>> 1];
        int i = p - 1;
        int j = r + 1;
        while (true) {
            if (x < array[--j]) {
                continue;
            }
            while (x > array[++i]) {
            }
            if (i >= j) break;
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
        return j;
    }

    private void growArray(int size) {
        this._size = size;
        int[] newArray = new int[this._size];
        System.arraycopy(this._array, 0, newArray, 0, this._free);
        this._array = newArray;
    }

    public int popLast() {
        return this._array[--this._free];
    }

    public int last() {
        return this._array[this._free - 1];
    }

    public void setLast(int n) {
        this._array[this._free - 1] = n;
    }

    public void pop() {
        --this._free;
    }

    public void pop(int n) {
        this._free -= n;
    }

    public final int cardinality() {
        return this._free;
    }

    public void print(PrintStream out) {
        if (this._free > 0) {
            for (int i = 0; i < this._free - 1; ++i) {
                out.print(this._array[i]);
                out.print(' ');
            }
            out.println(this._array[this._free - 1]);
        } else {
            out.println("IntegerArray: empty");
        }
    }
}

