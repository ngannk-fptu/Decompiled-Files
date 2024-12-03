/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue.law;

import java.util.function.BiFunction;

public final class IsEq<A> {
    private final A lhs;
    private final A rhs;

    public IsEq(A lhs, A rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public <R> R match(BiFunction<A, A, R> cases) {
        return cases.apply(this.lhs, this.rhs);
    }

    public A lhs() {
        return this.lhs;
    }

    public A rhs() {
        return this.rhs;
    }

    public static <A> IsEq<A> isEq(A lhs, A rhs) {
        return new IsEq<A>(lhs, rhs);
    }
}

