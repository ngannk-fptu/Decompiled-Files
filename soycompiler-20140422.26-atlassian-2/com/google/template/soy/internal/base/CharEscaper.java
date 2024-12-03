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

public abstract class CharEscaper
implements Escaper {
    private static final int DEST_PAD = 32;
    private static final ThreadLocal<char[]> DEST_TL = new ThreadLocal<char[]>(){

        @Override
        protected char[] initialValue() {
            return new char[1024];
        }
    };

    @Override
    public String escape(String string) {
        Preconditions.checkNotNull((Object)string);
        int length = string.length();
        for (int index = 0; index < length; ++index) {
            if (this.escape(string.charAt(index)) == null) continue;
            return this.escapeSlow(string, index);
        }
        return string;
    }

    @Override
    public Appendable escape(final Appendable out) {
        Preconditions.checkNotNull((Object)out);
        return new Appendable(){

            @Override
            public Appendable append(CharSequence csq) throws IOException {
                out.append(CharEscaper.this.escape(csq.toString()));
                return this;
            }

            @Override
            public Appendable append(CharSequence csq, int start, int end) throws IOException {
                out.append(CharEscaper.this.escape(csq.subSequence(start, end).toString()));
                return this;
            }

            @Override
            public Appendable append(char c) throws IOException {
                char[] escaped = CharEscaper.this.escape(c);
                if (escaped == null) {
                    out.append(c);
                } else {
                    for (char e : escaped) {
                        out.append(e);
                    }
                }
                return this;
            }
        };
    }

    protected String escapeSlow(String s, int index) {
        int slen = s.length();
        char[] dest = DEST_TL.get();
        int destSize = dest.length;
        int destIndex = 0;
        int lastEscape = 0;
        while (index < slen) {
            char[] r = this.escape(s.charAt(index));
            if (r != null) {
                int charsSkipped = index - lastEscape;
                int rlen = r.length;
                int sizeNeeded = destIndex + charsSkipped + rlen;
                if (destSize < sizeNeeded) {
                    destSize = sizeNeeded + (slen - index) + 32;
                    dest = CharEscaper.growBuffer(dest, destIndex, destSize);
                }
                if (charsSkipped > 0) {
                    s.getChars(lastEscape, index, dest, destIndex);
                    destIndex += charsSkipped;
                }
                if (rlen > 0) {
                    System.arraycopy(r, 0, dest, destIndex, rlen);
                    destIndex += rlen;
                }
                lastEscape = index + 1;
            }
            ++index;
        }
        int charsLeft = slen - lastEscape;
        if (charsLeft > 0) {
            int sizeNeeded = destIndex + charsLeft;
            if (destSize < sizeNeeded) {
                dest = CharEscaper.growBuffer(dest, destIndex, sizeNeeded);
            }
            s.getChars(lastEscape, slen, dest, destIndex);
            destIndex = sizeNeeded;
        }
        return new String(dest, 0, destIndex);
    }

    protected abstract char[] escape(char var1);

    private static char[] growBuffer(char[] dest, int index, int size) {
        char[] copy = new char[size];
        if (index > 0) {
            System.arraycopy(dest, 0, copy, 0, index);
        }
        return copy;
    }
}

