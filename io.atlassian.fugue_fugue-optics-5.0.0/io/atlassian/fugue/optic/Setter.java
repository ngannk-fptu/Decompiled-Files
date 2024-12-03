/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Either
 */
package io.atlassian.fugue.optic;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.optic.Iso;
import io.atlassian.fugue.optic.PSetter;
import io.atlassian.fugue.optic.Traversal;
import java.util.function.Function;

public final class Setter<S, A>
extends PSetter<S, S, A, A> {
    final PSetter<S, S, A, A> pSetter;

    public Setter(PSetter<S, S, A, A> pSetter) {
        this.pSetter = pSetter;
    }

    @Override
    public Function<S, S> modify(Function<A, A> f) {
        return this.pSetter.modify(f);
    }

    @Override
    public Function<S, S> set(A b) {
        return this.pSetter.set(b);
    }

    public final <S1> Setter<Either<S, S1>, A> sum(Setter<S1, A> other) {
        return new Setter<Either<S, S>, A>(this.pSetter.sum(other.pSetter));
    }

    public final <C> Setter<S, C> composeSetter(Setter<A, C> other) {
        return new Setter<S, A>(this.pSetter.composeSetter(other.pSetter));
    }

    public final <C> Setter<S, C> composeTraversal(Traversal<A, C> other) {
        return new Setter(this.pSetter.composeTraversal(other.pTraversal));
    }

    public final <C> Setter<S, C> composeIso(Iso<A, C> other) {
        return new Setter(this.pSetter.composeIso(other.pIso));
    }

    public static <S> Setter<S, S> id() {
        return new Setter(Setter.pId());
    }

    public static <S> Setter<Either<S, S>, S> codiagonal() {
        return new Setter(Setter.pCodiagonal());
    }

    public static <S, A> Setter<S, A> setter(Function<Function<A, A>, Function<S, S>> modify) {
        return new Setter(Setter.pSetter(modify));
    }
}

