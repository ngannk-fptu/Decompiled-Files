/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import org.apache.xerces.xni.XMLString;

public class XMLStringBuffer
extends XMLString {
    public static final int DEFAULT_SIZE = 32;

    public XMLStringBuffer() {
        this(32);
    }

    public XMLStringBuffer(int n) {
        this.ch = new char[n];
    }

    public XMLStringBuffer(char c) {
        this(1);
        this.append(c);
    }

    public XMLStringBuffer(String string) {
        this(string.length());
        this.append(string);
    }

    public XMLStringBuffer(char[] cArray, int n, int n2) {
        this(n2);
        this.append(cArray, n, n2);
    }

    public XMLStringBuffer(XMLString xMLString) {
        this(xMLString.length);
        this.append(xMLString);
    }

    @Override
    public void clear() {
        this.offset = 0;
        this.length = 0;
    }

    public void append(char c) {
        if (this.length + 1 > this.ch.length) {
            int n = this.ch.length * 2;
            if (n < this.ch.length + 32) {
                n = this.ch.length + 32;
            }
            char[] cArray = new char[n];
            System.arraycopy(this.ch, 0, cArray, 0, this.length);
            this.ch = cArray;
        }
        this.ch[this.length] = c;
        ++this.length;
    }

    public void append(String string) {
        int n = string.length();
        if (this.length + n > this.ch.length) {
            int n2 = this.ch.length * 2;
            if (n2 < this.length + n + 32) {
                n2 = this.ch.length + n + 32;
            }
            char[] cArray = new char[n2];
            System.arraycopy(this.ch, 0, cArray, 0, this.length);
            this.ch = cArray;
        }
        string.getChars(0, n, this.ch, this.length);
        this.length += n;
    }

    public void append(char[] cArray, int n, int n2) {
        if (this.length + n2 > this.ch.length) {
            int n3 = this.ch.length * 2;
            if (n3 < this.length + n2 + 32) {
                n3 = this.ch.length + n2 + 32;
            }
            char[] cArray2 = new char[n3];
            System.arraycopy(this.ch, 0, cArray2, 0, this.length);
            this.ch = cArray2;
        }
        System.arraycopy(cArray, n, this.ch, this.length, n2);
        this.length += n2;
    }

    public void append(XMLString xMLString) {
        this.append(xMLString.ch, xMLString.offset, xMLString.length);
    }
}

