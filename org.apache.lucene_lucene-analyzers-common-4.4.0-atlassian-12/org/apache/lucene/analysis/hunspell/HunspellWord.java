/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.hunspell;

import java.util.Arrays;

public class HunspellWord {
    private final char[] flags;

    public HunspellWord() {
        this.flags = null;
    }

    public HunspellWord(char[] flags) {
        this.flags = flags;
    }

    public boolean hasFlag(char flag) {
        return this.flags != null && Arrays.binarySearch(this.flags, flag) >= 0;
    }

    public char[] getFlags() {
        return this.flags;
    }
}

