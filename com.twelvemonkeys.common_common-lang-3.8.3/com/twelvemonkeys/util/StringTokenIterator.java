/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util;

import com.twelvemonkeys.util.AbstractTokenIterator;
import java.util.NoSuchElementException;

public class StringTokenIterator
extends AbstractTokenIterator {
    private final String string;
    private final char[] delimiters;
    private int position;
    private final int maxPosition;
    private String next;
    private String nextDelimiter;
    private final boolean includeDelimiters;
    private final boolean includeEmpty;
    private final boolean reverse;
    public static final int FORWARD = 1;
    public static final int REVERSE = -1;
    private final char maxDelimiter;

    public StringTokenIterator(String string) {
        this(string, " \t\n\r\f".toCharArray(), 1, false, false);
    }

    public StringTokenIterator(String string, String string2) {
        this(string, StringTokenIterator.toCharArray(string2), 1, false, false);
    }

    public StringTokenIterator(String string, String string2, int n) {
        this(string, StringTokenIterator.toCharArray(string2), n, false, false);
    }

    public StringTokenIterator(String string, String string2, boolean bl) {
        this(string, StringTokenIterator.toCharArray(string2), 1, bl, false);
    }

    public StringTokenIterator(String string, String string2, int n, boolean bl, boolean bl2) {
        this(string, StringTokenIterator.toCharArray(string2), n, bl, bl2);
    }

    private StringTokenIterator(String string, char[] cArray, int n, boolean bl, boolean bl2) {
        if (string == null) {
            throw new IllegalArgumentException("string == null");
        }
        this.string = string;
        this.maxPosition = string.length();
        this.delimiters = cArray;
        this.includeDelimiters = bl;
        this.reverse = n == -1;
        this.includeEmpty = bl2;
        this.maxDelimiter = StringTokenIterator.initMaxDelimiter(cArray);
        this.reset();
    }

    private static char[] toCharArray(String string) {
        if (string == null) {
            throw new IllegalArgumentException("delimiters == null");
        }
        return string.toCharArray();
    }

    private static char initMaxDelimiter(char[] cArray) {
        if (cArray == null) {
            return '\u0000';
        }
        char c = '\u0000';
        for (char c2 : cArray) {
            if (c >= c2) continue;
            c = c2;
        }
        return c;
    }

    @Override
    public void reset() {
        this.position = 0;
        this.next = null;
        this.nextDelimiter = null;
    }

    @Override
    public boolean hasNext() {
        return this.next != null || this.fetchNext() != null;
    }

    private String fetchNext() {
        if (this.nextDelimiter != null) {
            this.next = this.nextDelimiter;
            this.nextDelimiter = null;
            return this.next;
        }
        if (this.position >= this.maxPosition) {
            return null;
        }
        return this.reverse ? this.fetchReverse() : this.fetchForward();
    }

    private String fetchReverse() {
        int n = this.scanForPrev();
        this.next = this.string.substring(n + 1, this.maxPosition - this.position);
        if (this.includeDelimiters && n >= 0 && n < this.maxPosition) {
            this.nextDelimiter = this.string.substring(n, n + 1);
        }
        this.position = this.maxPosition - n;
        if (this.next.length() == 0 && !this.includeEmpty) {
            return this.fetchNext();
        }
        return this.next;
    }

    private String fetchForward() {
        int n = this.scanForNext();
        this.next = this.string.substring(this.position, n);
        if (this.includeDelimiters && n >= 0 && n < this.maxPosition) {
            this.nextDelimiter = this.string.substring(n, n + 1);
        }
        this.position = ++n;
        if (this.next.length() == 0 && !this.includeEmpty) {
            return this.fetchNext();
        }
        return this.next;
    }

    private int scanForNext() {
        int n;
        for (n = this.position; n < this.maxPosition; ++n) {
            char c = this.string.charAt(n);
            if (c > this.maxDelimiter) continue;
            for (char c2 : this.delimiters) {
                if (c != c2) continue;
                return n;
            }
        }
        return n;
    }

    private int scanForPrev() {
        int n;
        for (n = this.maxPosition - 1 - this.position; n >= 0; --n) {
            char c = this.string.charAt(n);
            if (c > this.maxDelimiter) continue;
            for (char c2 : this.delimiters) {
                if (c != c2) continue;
                return n;
            }
        }
        return n;
    }

    @Override
    public String next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        String string = this.next;
        this.next = this.fetchNext();
        return string;
    }
}

