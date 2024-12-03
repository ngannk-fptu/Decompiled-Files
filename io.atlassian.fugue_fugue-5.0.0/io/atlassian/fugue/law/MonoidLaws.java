/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue.law;

import io.atlassian.fugue.Functions;
import io.atlassian.fugue.Iterables;
import io.atlassian.fugue.Monoid;
import io.atlassian.fugue.law.IsEq;

public final class MonoidLaws<A> {
    private final Monoid<A> monoid;

    public MonoidLaws(Monoid<A> monoid) {
        this.monoid = monoid;
    }

    public IsEq<A> semigroupAssociative(A x, A y, A z) {
        return IsEq.isEq(this.monoid.append(this.monoid.append(x, y), z), this.monoid.append(x, this.monoid.append(y, z)));
    }

    public IsEq<A> monoidLeftIdentity(A x) {
        return IsEq.isEq(x, this.monoid.append(this.monoid.zero(), x));
    }

    public IsEq<A> monoidRightIdentity(A x) {
        return IsEq.isEq(x, this.monoid.append(x, this.monoid.zero()));
    }

    public IsEq<A> sumEqualFold(Iterable<A> as) {
        return IsEq.isEq(this.monoid.sum(as), Functions.fold(this.monoid::append, this.monoid.zero(), as));
    }

    public IsEq<A> multiplyEqualRepeatedAppend(int n, A a) {
        return IsEq.isEq(this.monoid.multiply(n, a), this.monoid.sum(Iterables.take(n, Iterables.cycle(a))));
    }
}

