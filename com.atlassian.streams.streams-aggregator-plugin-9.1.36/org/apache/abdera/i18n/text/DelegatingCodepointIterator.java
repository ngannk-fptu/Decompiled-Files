/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.text;

import org.apache.abdera.i18n.text.Codepoint;
import org.apache.abdera.i18n.text.CodepointIterator;

public abstract class DelegatingCodepointIterator
extends CodepointIterator {
    private CodepointIterator internal;

    protected DelegatingCodepointIterator(CodepointIterator internal) {
        this.internal = internal;
    }

    protected char get() {
        return this.internal.get();
    }

    protected char get(int index) {
        return this.internal.get(index);
    }

    public boolean hasNext() {
        return this.internal.hasNext();
    }

    public boolean isHigh(int index) {
        return this.internal.isHigh(index);
    }

    public boolean isLow(int index) {
        return this.internal.isLow(index);
    }

    public int limit() {
        return this.internal.limit();
    }

    public Codepoint next() {
        return this.internal.next();
    }

    public char[] nextChars() {
        return this.internal.nextChars();
    }

    public Codepoint peek() {
        return this.internal.peek();
    }

    public Codepoint peek(int index) {
        return this.internal.peek(index);
    }

    public char[] peekChars() {
        return this.internal.peekChars();
    }

    public int position() {
        return this.internal.position();
    }

    public int remaining() {
        return this.internal.remaining();
    }

    public void position(int position) {
        this.internal.position(position);
    }
}

