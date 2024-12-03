/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.input.sax;

import org.jdom2.Verifier;
import org.jdom2.internal.ArrayCopy;

final class TextBuffer {
    private char[] array = new char[1024];
    private int arraySize = 0;

    TextBuffer() {
    }

    void append(char[] source, int start, int count) {
        if (count + this.arraySize > this.array.length) {
            this.array = ArrayCopy.copyOf(this.array, count + this.arraySize + (this.array.length >> 2));
        }
        System.arraycopy(source, start, this.array, this.arraySize, count);
        this.arraySize += count;
    }

    void clear() {
        this.arraySize = 0;
    }

    boolean isAllWhitespace() {
        int i = this.arraySize;
        while (--i >= 0) {
            if (Verifier.isXMLWhitespace(this.array[i])) continue;
            return false;
        }
        return true;
    }

    public String toString() {
        if (this.arraySize == 0) {
            return "";
        }
        return String.valueOf(this.array, 0, this.arraySize);
    }
}

