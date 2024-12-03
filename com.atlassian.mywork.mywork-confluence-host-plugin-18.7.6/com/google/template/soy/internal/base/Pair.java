/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  javax.annotation.Nullable
 */
package com.google.template.soy.internal.base;

import com.google.common.base.Objects;
import javax.annotation.Nullable;

public class Pair<A, B> {
    public final A first;
    public final B second;

    public static <A, B> Pair<A, B> of(@Nullable A first, @Nullable B second) {
        return new Pair<A, B>(first, second);
    }

    public Pair(@Nullable A first, @Nullable B second) {
        this.first = first;
        this.second = second;
    }

    public A getFirst() {
        return this.first;
    }

    public B getSecond() {
        return this.second;
    }

    public boolean equals(@Nullable Object object) {
        if (object instanceof Pair) {
            Pair that = (Pair)object;
            return Objects.equal(this.first, that.first) && Objects.equal(this.second, that.second);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.first, this.second});
    }

    public String toString() {
        return "(" + this.first + ", " + this.second + ")";
    }
}

