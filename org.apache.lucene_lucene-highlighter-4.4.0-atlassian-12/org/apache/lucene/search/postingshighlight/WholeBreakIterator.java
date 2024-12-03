/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.postingshighlight;

import java.text.BreakIterator;
import java.text.CharacterIterator;

public final class WholeBreakIterator
extends BreakIterator {
    private CharacterIterator text;
    private int start;
    private int end;
    private int current;

    @Override
    public int current() {
        return this.current;
    }

    @Override
    public int first() {
        this.current = this.start;
        return this.current;
    }

    @Override
    public int following(int pos) {
        if (pos < this.start || pos > this.end) {
            throw new IllegalArgumentException("offset out of bounds");
        }
        if (pos == this.end) {
            this.current = this.end;
            return -1;
        }
        return this.last();
    }

    @Override
    public CharacterIterator getText() {
        return this.text;
    }

    @Override
    public int last() {
        this.current = this.end;
        return this.current;
    }

    @Override
    public int next() {
        if (this.current == this.end) {
            return -1;
        }
        return this.last();
    }

    @Override
    public int next(int n) {
        if (n < 0) {
            for (int i = 0; i < -n; ++i) {
                this.previous();
            }
        } else {
            for (int i = 0; i < n; ++i) {
                this.next();
            }
        }
        return this.current();
    }

    @Override
    public int preceding(int pos) {
        if (pos < this.start || pos > this.end) {
            throw new IllegalArgumentException("offset out of bounds");
        }
        if (pos == this.start) {
            this.current = this.start;
            return -1;
        }
        return this.first();
    }

    @Override
    public int previous() {
        if (this.current == this.start) {
            return -1;
        }
        return this.first();
    }

    @Override
    public void setText(CharacterIterator newText) {
        this.start = newText.getBeginIndex();
        this.end = newText.getEndIndex();
        this.text = newText;
        this.current = this.start;
    }
}

