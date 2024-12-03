/*
 * Decompiled with CFR 0.152.
 */
package com.mysema.commons.lang;

public class Pair<F, S> {
    private final F first;
    private final S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public static <F, S> Pair<F, S> of(F f, S s) {
        return new Pair<F, S>(f, s);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Pair) {
            Pair p = (Pair)o;
            return Pair.equals(this.first, p.first) && Pair.equals(this.second, p.second);
        }
        return false;
    }

    private static boolean equals(Object a, Object b) {
        return a == b || a != null && a.equals(b);
    }

    public F getFirst() {
        return this.first;
    }

    public S getSecond() {
        return this.second;
    }

    public int hashCode() {
        return 31 * (this.first != null ? this.first.hashCode() : 0) + (this.second != null ? this.second.hashCode() : 0);
    }
}

