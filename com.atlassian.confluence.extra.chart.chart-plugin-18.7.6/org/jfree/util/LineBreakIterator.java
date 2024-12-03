/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.util;

import java.util.Iterator;

public class LineBreakIterator
implements Iterator {
    public static final int DONE = -1;
    private char[] text;
    private int position;

    public LineBreakIterator() {
        this.setText("");
    }

    public LineBreakIterator(String text) {
        this.setText(text);
    }

    public synchronized int nextPosition() {
        char c;
        boolean eol;
        if (this.text == null) {
            return -1;
        }
        if (this.position == -1) {
            return -1;
        }
        int nChars = this.text.length;
        int nextChar = this.position;
        do {
            int i;
            if (nextChar >= nChars) {
                this.position = -1;
                return -1;
            }
            eol = false;
            c = '\u0000';
            for (i = nextChar; i < nChars; ++i) {
                c = this.text[i];
                if (c != '\n' && c != '\r') continue;
                eol = true;
                break;
            }
            nextChar = i;
        } while (!eol);
        if (c == '\r' && ++nextChar < nChars && this.text[nextChar] == '\n') {
            ++nextChar;
        }
        this.position = nextChar;
        return this.position;
    }

    public int nextWithEnd() {
        int pos = this.position;
        if (pos == -1) {
            return -1;
        }
        if (pos == this.text.length) {
            this.position = -1;
            return -1;
        }
        int retval = this.nextPosition();
        if (retval == -1) {
            return this.text.length;
        }
        return retval;
    }

    public String getText() {
        return new String(this.text);
    }

    public void setText(String text) {
        this.position = 0;
        this.text = text.toCharArray();
    }

    public boolean hasNext() {
        return this.position != -1;
    }

    public Object next() {
        if (this.position == -1) {
            return null;
        }
        int lastFound = this.position;
        int pos = this.nextWithEnd();
        if (pos == -1) {
            return new String(this.text, lastFound, this.text.length - lastFound);
        }
        if (pos > 0) {
            int end = lastFound;
            while (pos > end && (this.text[pos - 1] == '\n' || this.text[pos - 1] == '\r')) {
                --pos;
            }
        }
        return new String(this.text, lastFound, pos - lastFound);
    }

    public void remove() {
        throw new UnsupportedOperationException("This iterator is read-only.");
    }
}

