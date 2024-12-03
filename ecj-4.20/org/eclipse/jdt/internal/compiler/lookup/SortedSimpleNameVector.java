/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.Arrays;
import org.eclipse.jdt.internal.compiler.util.SortedCharArrays;

final class SortedSimpleNameVector {
    static int INITIAL_SIZE = 10;
    int size = 0;
    char[][] elements = new char[INITIAL_SIZE][];

    public boolean add(char[] newElement) {
        int idx = Arrays.binarySearch(this.elements, 0, this.size, newElement, SortedCharArrays.CHAR_ARR_COMPARATOR);
        if (idx < 0) {
            this.elements = SortedCharArrays.insertIntoArray(this.elements, this.size < this.elements.length ? this.elements : (char[][])new char[this.elements.length * 2][], newElement, -(idx + 1), this.size++);
            return true;
        }
        return false;
    }

    public char[] elementAt(int index) {
        return this.elements[index];
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        int i = 0;
        while (i < this.size) {
            buffer.append(this.elements[i]).append("\n");
            ++i;
        }
        return buffer.toString();
    }
}

