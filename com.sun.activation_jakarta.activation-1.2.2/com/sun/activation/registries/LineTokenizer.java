/*
 * Decompiled with CFR 0.152.
 */
package com.sun.activation.registries;

import java.util.NoSuchElementException;
import java.util.Vector;

class LineTokenizer {
    private int currentPosition = 0;
    private int maxPosition;
    private String str;
    private Vector stack = new Vector();
    private static final String singles = "=";

    public LineTokenizer(String str) {
        this.str = str;
        this.maxPosition = str.length();
    }

    private void skipWhiteSpace() {
        while (this.currentPosition < this.maxPosition && Character.isWhitespace(this.str.charAt(this.currentPosition))) {
            ++this.currentPosition;
        }
    }

    public boolean hasMoreTokens() {
        if (this.stack.size() > 0) {
            return true;
        }
        this.skipWhiteSpace();
        return this.currentPosition < this.maxPosition;
    }

    public String nextToken() {
        int start;
        char c;
        int size = this.stack.size();
        if (size > 0) {
            String t = (String)this.stack.elementAt(size - 1);
            this.stack.removeElementAt(size - 1);
            return t;
        }
        this.skipWhiteSpace();
        if (this.currentPosition >= this.maxPosition) {
            throw new NoSuchElementException();
        }
        if ((c = this.str.charAt(start = this.currentPosition++)) == '\"') {
            boolean filter = false;
            while (this.currentPosition < this.maxPosition) {
                String s;
                if ((c = this.str.charAt(this.currentPosition++)) == '\\') {
                    ++this.currentPosition;
                    filter = true;
                    continue;
                }
                if (c != '\"') continue;
                if (filter) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = start + 1; i < this.currentPosition - 1; ++i) {
                        c = this.str.charAt(i);
                        if (c == '\\') continue;
                        sb.append(c);
                    }
                    s = sb.toString();
                } else {
                    s = this.str.substring(start + 1, this.currentPosition - 1);
                }
                return s;
            }
        } else if (singles.indexOf(c) >= 0) {
            ++this.currentPosition;
        } else {
            while (this.currentPosition < this.maxPosition && singles.indexOf(this.str.charAt(this.currentPosition)) < 0 && !Character.isWhitespace(this.str.charAt(this.currentPosition))) {
                ++this.currentPosition;
            }
        }
        return this.str.substring(start, this.currentPosition);
    }

    public void pushToken(String token) {
        this.stack.addElement(token);
    }
}

