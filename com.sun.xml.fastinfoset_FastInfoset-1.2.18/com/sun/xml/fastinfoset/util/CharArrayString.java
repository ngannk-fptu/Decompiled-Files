/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.util;

import com.sun.xml.fastinfoset.util.CharArray;

public class CharArrayString
extends CharArray {
    protected String _s;

    public CharArrayString(String s) {
        this(s, true);
    }

    public CharArrayString(String s, boolean createArray) {
        this._s = s;
        if (createArray) {
            this.ch = this._s.toCharArray();
            this.start = 0;
            this.length = this.ch.length;
        }
    }

    @Override
    public String toString() {
        return this._s;
    }

    @Override
    public int hashCode() {
        return this._s.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof CharArrayString) {
            CharArrayString chas = (CharArrayString)obj;
            return this._s.equals(chas._s);
        }
        if (obj instanceof CharArray) {
            CharArray cha = (CharArray)obj;
            if (this.length == cha.length) {
                int n = this.length;
                int i = this.start;
                int j = cha.start;
                while (n-- != 0) {
                    if (this.ch[i++] == cha.ch[j++]) continue;
                    return false;
                }
                return true;
            }
        }
        return false;
    }
}

