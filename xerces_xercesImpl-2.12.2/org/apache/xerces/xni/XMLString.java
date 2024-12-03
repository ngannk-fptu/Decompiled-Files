/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xni;

public class XMLString {
    public char[] ch;
    public int offset;
    public int length;

    public XMLString() {
    }

    public XMLString(char[] cArray, int n, int n2) {
        this.setValues(cArray, n, n2);
    }

    public XMLString(XMLString xMLString) {
        this.setValues(xMLString);
    }

    public void setValues(char[] cArray, int n, int n2) {
        this.ch = cArray;
        this.offset = n;
        this.length = n2;
    }

    public void setValues(XMLString xMLString) {
        this.setValues(xMLString.ch, xMLString.offset, xMLString.length);
    }

    public void clear() {
        this.ch = null;
        this.offset = 0;
        this.length = -1;
    }

    public boolean equals(char[] cArray, int n, int n2) {
        if (cArray == null) {
            return false;
        }
        if (this.length != n2) {
            return false;
        }
        for (int i = 0; i < n2; ++i) {
            if (this.ch[this.offset + i] == cArray[n + i]) continue;
            return false;
        }
        return true;
    }

    public boolean equals(String string) {
        if (string == null) {
            return false;
        }
        if (this.length != string.length()) {
            return false;
        }
        for (int i = 0; i < this.length; ++i) {
            if (this.ch[this.offset + i] == string.charAt(i)) continue;
            return false;
        }
        return true;
    }

    public String toString() {
        return this.length > 0 ? new String(this.ch, this.offset, this.length) : "";
    }
}

