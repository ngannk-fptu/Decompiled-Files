/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class Pair<FIRST, SECOND> {
    public final FIRST first;
    public final SECOND second;

    public Pair(FIRST first, SECOND second) {
        this.first = first;
        this.second = second;
    }

    public int hashCode() {
        return 17 * (this.first != null ? this.first.hashCode() : 0) + 17 * (this.second != null ? this.second.hashCode() : 0);
    }

    public boolean equals(Object o) {
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair that = (Pair)o;
        return Pair.equal(this.first, that.first) && Pair.equal(this.second, that.second);
    }

    private static boolean equal(Object a, Object b) {
        return a == b || a != null && a.equals(b);
    }

    public String toString() {
        return String.format("{%s,%s}", this.first, this.second);
    }
}

