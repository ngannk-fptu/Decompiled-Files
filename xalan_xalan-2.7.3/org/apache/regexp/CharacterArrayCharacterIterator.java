/*
 * Decompiled with CFR 0.152.
 */
package org.apache.regexp;

import org.apache.regexp.CharacterIterator;

public final class CharacterArrayCharacterIterator
implements CharacterIterator {
    private final char[] src;
    private final int off;
    private final int len;

    public CharacterArrayCharacterIterator(char[] cArray, int n, int n2) {
        this.src = cArray;
        this.off = n;
        this.len = n2;
    }

    public char charAt(int n) {
        return this.src[this.off + n];
    }

    public boolean isEnd(int n) {
        return n >= this.len;
    }

    public String substring(int n) {
        return new String(this.src, this.off + n, this.len);
    }

    public String substring(int n, int n2) {
        return new String(this.src, this.off + n, n2);
    }
}

