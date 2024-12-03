/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Either
 *  io.atlassian.fugue.Monoid
 *  io.atlassian.fugue.Option
 *  io.atlassian.fugue.Pair
 */
package io.atlassian.fugue.optic;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Monoid;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Pair;
import io.atlassian.fugue.optic.PTraversal;
import io.atlassian.fugue.optic.Setter;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Traversal<S, A>
extends PTraversal<S, S, A, A> {
    final PTraversal<S, S, A, A> pTraversal;

    public Traversal(PTraversal<S, S, A, A> pTraversal) {
        this.pTraversal = pTraversal;
    }

    @Override
    public <C> Function<S, Function<C, S>> modifyFunctionF(Function<A, Function<C, A>> f) {
        return this.pTraversal.modifyFunctionF(f);
    }

    @Override
    public <L> Function<S, Either<L, S>> modifyEitherF(Function<A, Either<L, A>> f) {
        return this.pTraversal.modifyEitherF(f);
    }

    @Override
    public Function<S, Option<S>> modifyOptionF(Function<A, Option<A>> f) {
        return this.pTraversal.modifyOptionF(f);
    }

    @Override
    public Function<S, Iterable<S>> modifyIterableF(Function<A, Iterable<A>> f) {
        return this.pTraversal.modifyIterableF(f);
    }

    @Override
    public Function<S, Supplier<S>> modifySupplierF(Function<A, Supplier<A>> f) {
        return this.pTraversal.modifySupplierF(f);
    }

    @Override
    public Function<S, Pair<S, S>> modifyPairF(Function<A, Pair<A, A>> f) {
        return this.pTraversal.modifyPairF(f);
    }

    @Override
    public <M> Function<S, M> foldMap(Monoid<M> monoid, Function<A, M> f) {
        return this.pTraversal.foldMap(monoid, f);
    }

    public final <S1> Traversal<Either<S, S1>, A> sum(Traversal<S1, A> other) {
        return new Traversal<Either<S, S>, A>(this.pTraversal.sum(other.pTraversal));
    }

    public final <C> Setter<S, C> composeSetter(Setter<A, C> other) {
        return new Setter(this.pTraversal.composeSetter(other.pSetter));
    }

    public final <C> Traversal<S, C> composeTraversal(Traversal<A, C> other) {
        return new Traversal<S, A>(this.pTraversal.composeTraversal(other.pTraversal));
    }

    public final Setter<S, A> asSetter() {
        return new Setter<S, A>(this.pTraversal.asSetter());
    }

    public static <S> Traversal<S, S> id() {
        return new Traversal(Traversal.pId());
    }

    public static <S> Traversal<Either<S, S>, S> codiagonal() {
        return new Traversal(Traversal.pCodiagonal());
    }

    public static <S, A> Traversal<S, A> traversal(Function<S, A> get1, Function<S, A> get2, BiFunction<A, A, Function<S, S>> set) {
        return new Traversal<S, A>(Traversal.pTraversal(get1, get2, set));
    }

    public static <S, A> Traversal<S, A> traversal(Function<S, A> get1, Function<S, A> get2, Function<S, A> get3, Function<A, Function<A, Function<A, Function<S, S>>>> set) {
        return new Traversal<S, A>(Traversal.pTraversal(get1, get2, get3, set));
    }

    public static <S, A> Traversal<S, A> traversal(Function<S, A> get1, Function<S, A> get2, Function<S, A> get3, Function<S, A> get4, Function<A, Function<A, Function<A, Function<A, Function<S, S>>>>> set) {
        return new Traversal<S, A>(Traversal.pTraversal(get1, get2, get3, get4, set));
    }

    public static <S, A> Traversal<S, A> traversal(Function<S, A> get1, Function<S, A> get2, Function<S, A> get3, Function<S, A> get4, Function<S, A> get5, Function<A, Function<A, Function<A, Function<A, Function<A, Function<S, S>>>>>> set) {
        return new Traversal<S, A>(Traversal.pTraversal(get1, get2, get3, get4, get5, set));
    }

    public static <S, A> Traversal<S, A> traversal(Function<S, A> get1, Function<S, A> get2, Function<S, A> get3, Function<S, A> get4, Function<S, A> get5, Function<S, A> get6, Function<A, Function<A, Function<A, Function<A, Function<A, Function<A, Function<S, S>>>>>>> set) {
        return new Traversal<S, A>(Traversal.pTraversal(get1, get2, get3, get4, get5, get6, set));
    }
}

