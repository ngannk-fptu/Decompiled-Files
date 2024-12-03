/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 */
package com.atlassian.upm.api.util;

import com.google.common.base.Function;
import java.util.Map;
import java.util.Objects;

public final class Pair<A, B> {
    private final A first;
    private final B second;

    Pair(A first, B second) {
        this.first = Objects.requireNonNull(first, "first");
        this.second = Objects.requireNonNull(second, "second");
    }

    public A first() {
        return this.first;
    }

    public B second() {
        return this.second;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        Pair pair = (Pair)Pair.class.cast(o);
        return this.first.equals(pair.first) && this.second.equals(pair.second);
    }

    public int hashCode() {
        return 31 * this.first.hashCode() + this.second.hashCode();
    }

    public String toString() {
        return "(" + this.first + ", " + this.second + ")";
    }

    public static <A, B> Pair<A, B> pair(A first, B second) {
        return new Pair<A, B>(first, second);
    }

    public static <A, B> Pair<A, B> fromMapEntry(Map.Entry<A, B> entry) {
        return Pair.pair(entry.getKey(), entry.getValue());
    }

    @Deprecated
    public static <A, B> Function<Map.Entry<A, B>, Pair<A, B>> fromMapEntry() {
        return from -> Pair.pair(from.getKey(), from.getValue());
    }

    @Deprecated
    public static <A, B> Function<Pair<A, B>, A> toFirst() {
        return Pair::first;
    }

    @Deprecated
    public static <A, B> Function<Pair<A, B>, B> toSecond() {
        return Pair::second;
    }
}

