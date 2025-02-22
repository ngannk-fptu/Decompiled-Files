/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

import com.ibm.icu.text.Replaceable;
import com.ibm.icu.text.ReplaceableString;
import com.ibm.icu.text.UCharacterIterator;
import com.ibm.icu.text.UTF16;

public class ReplaceableUCharacterIterator
extends UCharacterIterator {
    private Replaceable replaceable;
    private int currentIndex;

    public ReplaceableUCharacterIterator(Replaceable replaceable) {
        if (replaceable == null) {
            throw new IllegalArgumentException();
        }
        this.replaceable = replaceable;
        this.currentIndex = 0;
    }

    public ReplaceableUCharacterIterator(String str) {
        if (str == null) {
            throw new IllegalArgumentException();
        }
        this.replaceable = new ReplaceableString(str);
        this.currentIndex = 0;
    }

    public ReplaceableUCharacterIterator(StringBuffer buf) {
        if (buf == null) {
            throw new IllegalArgumentException();
        }
        this.replaceable = new ReplaceableString(buf);
        this.currentIndex = 0;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public int current() {
        if (this.currentIndex < this.replaceable.length()) {
            return this.replaceable.charAt(this.currentIndex);
        }
        return -1;
    }

    @Override
    public int currentCodePoint() {
        int ch = this.current();
        if (UTF16.isLeadSurrogate(ch)) {
            this.next();
            int ch2 = this.current();
            this.previous();
            if (UTF16.isTrailSurrogate(ch2)) {
                return Character.toCodePoint((char)ch, (char)ch2);
            }
        }
        return ch;
    }

    @Override
    public int getLength() {
        return this.replaceable.length();
    }

    @Override
    public int getIndex() {
        return this.currentIndex;
    }

    @Override
    public int next() {
        if (this.currentIndex < this.replaceable.length()) {
            return this.replaceable.charAt(this.currentIndex++);
        }
        return -1;
    }

    @Override
    public int previous() {
        if (this.currentIndex > 0) {
            return this.replaceable.charAt(--this.currentIndex);
        }
        return -1;
    }

    @Override
    public void setIndex(int currentIndex) throws IndexOutOfBoundsException {
        if (currentIndex < 0 || currentIndex > this.replaceable.length()) {
            throw new IndexOutOfBoundsException();
        }
        this.currentIndex = currentIndex;
    }

    @Override
    public int getText(char[] fillIn, int offset) {
        int length = this.replaceable.length();
        if (offset < 0 || offset + length > fillIn.length) {
            throw new IndexOutOfBoundsException(Integer.toString(length));
        }
        this.replaceable.getChars(0, length, fillIn, offset);
        return length;
    }
}

