/*
 * Decompiled with CFR 0.152.
 */
package org.jdom.input;

import org.jdom.Verifier;

class TextBuffer {
    private static final String CVS_ID = "@(#) $RCSfile: TextBuffer.java,v $ $Revision: 1.10 $ $Date: 2007/11/10 05:29:00 $ $Name:  $";
    private String prefixString;
    private char[] array = new char[4096];
    private int arraySize = 0;

    TextBuffer() {
    }

    void append(char[] source, int start, int count) {
        if (this.prefixString == null) {
            this.prefixString = new String(source, start, count);
        } else {
            this.ensureCapacity(this.arraySize + count);
            System.arraycopy(source, start, this.array, this.arraySize, count);
            this.arraySize += count;
        }
    }

    int size() {
        if (this.prefixString == null) {
            return 0;
        }
        return this.prefixString.length() + this.arraySize;
    }

    void clear() {
        this.arraySize = 0;
        this.prefixString = null;
    }

    boolean isAllWhitespace() {
        int i;
        if (this.prefixString == null || this.prefixString.length() == 0) {
            return true;
        }
        int size = this.prefixString.length();
        for (i = 0; i < size; ++i) {
            if (Verifier.isXMLWhitespace(this.prefixString.charAt(i))) continue;
            return false;
        }
        for (i = 0; i < this.arraySize; ++i) {
            if (Verifier.isXMLWhitespace(this.array[i])) continue;
            return false;
        }
        return true;
    }

    public String toString() {
        if (this.prefixString == null) {
            return "";
        }
        String str = "";
        str = this.arraySize == 0 ? this.prefixString : new StringBuffer(this.prefixString.length() + this.arraySize).append(this.prefixString).append(this.array, 0, this.arraySize).toString();
        return str;
    }

    private void ensureCapacity(int csize) {
        int capacity = this.array.length;
        if (csize > capacity) {
            int nsize;
            char[] old = this.array;
            for (nsize = capacity; csize > nsize; nsize += capacity / 2) {
            }
            this.array = new char[nsize];
            System.arraycopy(old, 0, this.array, 0, this.arraySize);
        }
    }
}

