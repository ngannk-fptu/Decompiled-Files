/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl.coll;

import com.ibm.icu.impl.coll.CollationData;
import com.ibm.icu.impl.coll.CollationIterator;

public class UTF16CollationIterator
extends CollationIterator {
    protected CharSequence seq;
    protected int start;
    protected int pos;
    protected int limit;

    public UTF16CollationIterator(CollationData d) {
        super(d);
    }

    public UTF16CollationIterator(CollationData d, boolean numeric, CharSequence s, int p) {
        super(d, numeric);
        this.seq = s;
        this.start = 0;
        this.pos = p;
        this.limit = s.length();
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }
        UTF16CollationIterator o = (UTF16CollationIterator)other;
        return this.pos - this.start == o.pos - o.start;
    }

    @Override
    public int hashCode() {
        assert (false) : "hashCode not designed";
        return 42;
    }

    @Override
    public void resetToOffset(int newOffset) {
        this.reset();
        this.pos = this.start + newOffset;
    }

    @Override
    public int getOffset() {
        return this.pos - this.start;
    }

    public void setText(boolean numeric, CharSequence s, int p) {
        this.reset(numeric);
        this.seq = s;
        this.start = 0;
        this.pos = p;
        this.limit = s.length();
    }

    @Override
    public int nextCodePoint() {
        char trail;
        char c;
        if (this.pos == this.limit) {
            return -1;
        }
        if (Character.isHighSurrogate(c = this.seq.charAt(this.pos++)) && this.pos != this.limit && Character.isLowSurrogate(trail = this.seq.charAt(this.pos))) {
            ++this.pos;
            return Character.toCodePoint(c, trail);
        }
        return c;
    }

    @Override
    public int previousCodePoint() {
        char lead;
        char c;
        if (this.pos == this.start) {
            return -1;
        }
        if (Character.isLowSurrogate(c = this.seq.charAt(--this.pos)) && this.pos != this.start && Character.isHighSurrogate(lead = this.seq.charAt(this.pos - 1))) {
            --this.pos;
            return Character.toCodePoint(lead, c);
        }
        return c;
    }

    @Override
    protected long handleNextCE32() {
        if (this.pos == this.limit) {
            return -4294967104L;
        }
        char c = this.seq.charAt(this.pos++);
        return this.makeCodePointAndCE32Pair(c, this.trie.getFromU16SingleLead(c));
    }

    @Override
    protected char handleGetTrailSurrogate() {
        if (this.pos == this.limit) {
            return '\u0000';
        }
        char trail = this.seq.charAt(this.pos);
        if (Character.isLowSurrogate(trail)) {
            ++this.pos;
        }
        return trail;
    }

    @Override
    protected void forwardNumCodePoints(int num) {
        while (num > 0 && this.pos != this.limit) {
            char c = this.seq.charAt(this.pos++);
            --num;
            if (!Character.isHighSurrogate(c) || this.pos == this.limit || !Character.isLowSurrogate(this.seq.charAt(this.pos))) continue;
            ++this.pos;
        }
    }

    @Override
    protected void backwardNumCodePoints(int num) {
        while (num > 0 && this.pos != this.start) {
            char c = this.seq.charAt(--this.pos);
            --num;
            if (!Character.isLowSurrogate(c) || this.pos == this.start || !Character.isHighSurrogate(this.seq.charAt(this.pos - 1))) continue;
            --this.pos;
        }
    }
}

