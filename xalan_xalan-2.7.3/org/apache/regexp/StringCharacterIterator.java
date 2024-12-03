/*
 * Decompiled with CFR 0.152.
 */
package org.apache.regexp;

import org.apache.regexp.CharacterIterator;

public final class StringCharacterIterator
implements CharacterIterator {
    private final String src;

    public StringCharacterIterator(String string) {
        this.src = string;
    }

    public char charAt(int n) {
        return this.src.charAt(n);
    }

    public boolean isEnd(int n) {
        return n >= this.src.length();
    }

    public String substring(int n) {
        return this.src.substring(n);
    }

    public String substring(int n, int n2) {
        return this.src.substring(n, n2);
    }
}

