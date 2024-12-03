/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.util.BitUtil;
import com.atlassian.lucene36.util.StringInterner;

public class SimpleStringInterner
extends StringInterner {
    private final Entry[] cache;
    private final int maxChainLength;

    public SimpleStringInterner(int tableSize, int maxChainLength) {
        this.cache = new Entry[Math.max(1, BitUtil.nextHighestPowerOfTwo(tableSize))];
        this.maxChainLength = Math.max(2, maxChainLength);
    }

    public String intern(String s) {
        int h = s.hashCode();
        int slot = h & this.cache.length - 1;
        Entry first = this.cache[slot];
        Entry nextToLast = null;
        int chainLength = 0;
        Entry e = first;
        while (e != null) {
            if (e.hash == h && (e.str == s || e.str.compareTo(s) == 0)) {
                return e.str;
            }
            ++chainLength;
            if (e.next != null) {
                nextToLast = e;
            }
            e = e.next;
        }
        s = s.intern();
        this.cache[slot] = new Entry(s, h, first);
        if (chainLength >= this.maxChainLength) {
            nextToLast.next = null;
        }
        return s;
    }

    private static class Entry {
        private final String str;
        private final int hash;
        private Entry next;

        private Entry(String str, int hash, Entry next) {
            this.str = str;
            this.hash = hash;
            this.next = next;
        }
    }
}

