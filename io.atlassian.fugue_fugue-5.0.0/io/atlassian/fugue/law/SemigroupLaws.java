/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue.law;

import io.atlassian.fugue.Functions;
import io.atlassian.fugue.Iterables;
import io.atlassian.fugue.Semigroup;
import io.atlassian.fugue.law.IsEq;

public final class SemigroupLaws<A> {
    private final Semigroup<A> semigroup;

    public SemigroupLaws(Semigroup<A> semigroup) {
        this.semigroup = semigroup;
    }

    public IsEq<A> semigroupAssociative(A x, A y, A z) {
        return IsEq.isEq(this.semigroup.append(this.semigroup.append(x, y), z), this.semigroup.append(x, this.semigroup.append(y, z)));
    }

    public IsEq<A> sumNonEmptyEqualFold(A head, Iterable<A> tail) {
        return IsEq.isEq(this.semigroup.sumNonEmpty(head, tail), Functions.fold(this.semigroup::append, head, tail));
    }

    public IsEq<A> multiply1pEqualRepeatedAppend(int n, A a) {
        return IsEq.isEq(this.semigroup.multiply1p(n, a), this.semigroup.sumNonEmpty(a, Iterables.take(n, Iterables.cycle(a))));
    }
}

