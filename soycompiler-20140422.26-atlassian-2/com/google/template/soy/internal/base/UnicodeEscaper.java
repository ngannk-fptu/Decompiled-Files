/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.google.template.soy.internal.base;

import com.google.common.base.Preconditions;
import com.google.template.soy.internal.base.Escaper;
import java.io.IOException;

public abstract class UnicodeEscaper
implements Escaper {
    private static final int DEST_PAD = 32;
    private static final ThreadLocal<char[]> DEST_TL = new ThreadLocal<char[]>(){

        @Override
        protected char[] initialValue() {
            return new char[1024];
        }
    };

    protected abstract char[] escape(int var1);

    protected int nextEscapeIndex(CharSequence csq, int start, int end) {
        int index;
        int cp;
        for (index = start; index < end && (cp = UnicodeEscaper.codePointAt(csq, index, end)) >= 0 && this.escape(cp) == null; index += Character.isSupplementaryCodePoint(cp) ? 2 : 1) {
        }
        return index;
    }

    @Override
    public String escape(String string) {
        Preconditions.checkNotNull((Object)string);
        int end = string.length();
        int index = this.nextEscapeIndex(string, 0, end);
        return index == end ? string : this.escapeSlow(string, index);
    }

    protected final String escapeSlow(String s, int index) {
        int end = s.length();
        char[] dest = DEST_TL.get();
        int destIndex = 0;
        int unescapedChunkStart = 0;
        while (index < end) {
            int cp = UnicodeEscaper.codePointAt(s, index, end);
            if (cp < 0) {
                throw new IllegalArgumentException("Trailing high surrogate at end of input");
            }
            char[] escaped = this.escape(cp);
            int nextIndex = index + (Character.isSupplementaryCodePoint(cp) ? 2 : 1);
            if (escaped != null) {
                int charsSkipped = index - unescapedChunkStart;
                int sizeNeeded = destIndex + charsSkipped + escaped.length;
                if (dest.length < sizeNeeded) {
                    int destLength = sizeNeeded + (end - index) + 32;
                    dest = UnicodeEscaper.growBuffer(dest, destIndex, destLength);
                }
                if (charsSkipped > 0) {
                    s.getChars(unescapedChunkStart, index, dest, destIndex);
                    destIndex += charsSkipped;
                }
                if (escaped.length > 0) {
                    System.arraycopy(escaped, 0, dest, destIndex, escaped.length);
                    destIndex += escaped.length;
                }
                unescapedChunkStart = nextIndex;
            }
            index = this.nextEscapeIndex(s, nextIndex, end);
        }
        int charsSkipped = end - unescapedChunkStart;
        if (charsSkipped > 0) {
            int endIndex = destIndex + charsSkipped;
            if (dest.length < endIndex) {
                dest = UnicodeEscaper.growBuffer(dest, destIndex, endIndex);
            }
            s.getChars(unescapedChunkStart, end, dest, destIndex);
            destIndex = endIndex;
        }
        return new String(dest, 0, destIndex);
    }

    @Override
    public Appendable escape(final Appendable out) {
        Preconditions.checkNotNull((Object)out);
        return new Appendable(){
            char pendingHighSurrogate = '\u0000';

            @Override
            public Appendable append(CharSequence csq) throws IOException {
                return this.append(csq, 0, csq.length());
            }

            @Override
            public Appendable append(CharSequence csq, int start, int end) throws IOException {
                Preconditions.checkNotNull((Object)csq);
                Preconditions.checkPositionIndexes((int)start, (int)end, (int)csq.length());
                if (this.pendingHighSurrogate != '\u0000' && start < end) {
                    this.completeSurrogatePair(csq.charAt(start++));
                }
                if (start < end) {
                    char last = csq.charAt(end - 1);
                    if (Character.isHighSurrogate(last)) {
                        this.pendingHighSurrogate = last;
                        --end;
                    }
                    out.append(UnicodeEscaper.this.escape(csq.subSequence(start, end).toString()));
                }
                return this;
            }

            @Override
            public Appendable append(char c) throws IOException {
                if (this.pendingHighSurrogate != '\u0000') {
                    this.completeSurrogatePair(c);
                } else if (Character.isHighSurrogate(c)) {
                    this.pendingHighSurrogate = c;
                } else {
                    if (Character.isLowSurrogate(c)) {
                        throw new IllegalArgumentException("Unexpected low surrogate character '" + c + "' with value " + c);
                    }
                    char[] escaped = UnicodeEscaper.this.escape(c);
                    if (escaped != null) {
                        this.outputChars(escaped);
                    } else {
                        out.append(c);
                    }
                }
                return this;
            }

            private void completeSurrogatePair(char c) throws IOException {
                if (!Character.isLowSurrogate(c)) {
                    throw new IllegalArgumentException("Expected low surrogate character but got '" + c + "' with value " + c);
                }
                char[] escaped = UnicodeEscaper.this.escape(Character.toCodePoint(this.pendingHighSurrogate, c));
                if (escaped != null) {
                    this.outputChars(escaped);
                } else {
                    out.append(this.pendingHighSurrogate);
                    out.append(c);
                }
                this.pendingHighSurrogate = '\u0000';
            }

            private void outputChars(char[] chars) throws IOException {
                for (int n = 0; n < chars.length; ++n) {
                    out.append(chars[n]);
                }
            }
        };
    }

    protected static final int codePointAt(CharSequence seq, int index, int end) {
        if (index < end) {
            char c1;
            if ((c1 = seq.charAt(index++)) < '\ud800' || c1 > '\udfff') {
                return c1;
            }
            if (c1 <= '\udbff') {
                if (index == end) {
                    return -c1;
                }
                char c2 = seq.charAt(index);
                if (Character.isLowSurrogate(c2)) {
                    return Character.toCodePoint(c1, c2);
                }
                throw new IllegalArgumentException("Expected low surrogate but got char '" + c2 + "' with value " + c2 + " at index " + index);
            }
            throw new IllegalArgumentException("Unexpected low surrogate character '" + c1 + "' with value " + c1 + " at index " + (index - 1));
        }
        throw new IndexOutOfBoundsException("Index exceeds specified range");
    }

    private static final char[] growBuffer(char[] dest, int index, int size) {
        char[] copy = new char[size];
        if (index > 0) {
            System.arraycopy(dest, 0, copy, 0, index);
        }
        return copy;
    }
}

