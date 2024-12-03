/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

public interface Bits {
    public static final Bits[] EMPTY_ARRAY = new Bits[0];

    public boolean get(int var1);

    public int length();

    public static class MatchNoBits
    implements Bits {
        final int len;

        public MatchNoBits(int len) {
            this.len = len;
        }

        public boolean get(int index) {
            return false;
        }

        public int length() {
            return this.len;
        }
    }

    public static class MatchAllBits
    implements Bits {
        final int len;

        public MatchAllBits(int len) {
            this.len = len;
        }

        public boolean get(int index) {
            return true;
        }

        public int length() {
            return this.len;
        }
    }
}

