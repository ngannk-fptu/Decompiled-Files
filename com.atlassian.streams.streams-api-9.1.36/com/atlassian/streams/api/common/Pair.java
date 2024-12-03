/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.streams.api.common;

import com.google.common.base.Preconditions;

public final class Pair<A, B> {
    private final A first;
    private final B second;

    Pair(A first, B second) {
        this.first = Preconditions.checkNotNull(first, (Object)"first");
        this.second = Preconditions.checkNotNull(second, (Object)"second");
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
}

