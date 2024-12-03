/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Either
 *  io.atlassian.fugue.Option
 *  io.atlassian.fugue.Pair
 */
package io.atlassian.fugue.optic;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Pair;
import io.atlassian.fugue.optic.Iso;
import io.atlassian.fugue.optic.Optional;
import io.atlassian.fugue.optic.PLens;
import io.atlassian.fugue.optic.Prism;
import io.atlassian.fugue.optic.Setter;
import io.atlassian.fugue.optic.Traversal;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Lens<S, A>
extends PLens<S, S, A, A> {
    final PLens<S, S, A, A> pLens;

    public Lens(PLens<S, S, A, A> pLens) {
        this.pLens = pLens;
    }

    @Override
    public A get(S s) {
        return this.pLens.get(s);
    }

    @Override
    public Function<S, S> set(A a) {
        return this.pLens.set(a);
    }

    @Override
    public <C> Function<S, Function<C, S>> modifyFunctionF(Function<A, Function<C, A>> f) {
        return this.pLens.modifyFunctionF(f);
    }

    @Override
    public <L> Function<S, Either<L, S>> modifyEitherF(Function<A, Either<L, A>> f) {
        return this.pLens.modifyEitherF(f);
    }

    @Override
    public Function<S, Option<S>> modifyOptionF(Function<A, Option<A>> f) {
        return this.pLens.modifyOptionF(f);
    }

    @Override
    public Function<S, Iterable<S>> modifyIterableF(Function<A, Iterable<A>> f) {
        return this.pLens.modifyIterableF(f);
    }

    @Override
    public Function<S, Supplier<S>> modifySupplierF(Function<A, Supplier<A>> f) {
        return this.pLens.modifySupplierF(f);
    }

    @Override
    public Function<S, Pair<S, S>> modifyPairF(Function<A, Pair<A, A>> f) {
        return this.pLens.modifyPairF(f);
    }

    @Override
    public Function<S, S> modify(Function<A, A> f) {
        return this.pLens.modify(f);
    }

    public final <S1> Lens<Either<S, S1>, A> sum(Lens<S1, A> other) {
        return new Lens<Either<S, S>, A>(this.pLens.sum(other.pLens));
    }

    public final <C> Setter<S, C> composeSetter(Setter<A, C> other) {
        return new Setter(this.pLens.composeSetter(other.pSetter));
    }

    public final <C> Traversal<S, C> composeTraversal(Traversal<A, C> other) {
        return new Traversal(this.pLens.composeTraversal(other.pTraversal));
    }

    public final <C> Optional<S, C> composeOptional(Optional<A, C> other) {
        return new Optional(this.pLens.composeOptional(other.pOptional));
    }

    public final <C> Optional<S, C> composePrism(Prism<A, C> other) {
        return new Optional(this.pLens.composePrism(other.pPrism));
    }

    public final <C> Lens<S, C> composeLens(Lens<A, C> other) {
        return new Lens<S, A>(this.pLens.composeLens(other.pLens));
    }

    public final <C> Lens<S, C> composeIso(Iso<A, C> other) {
        return new Lens(this.pLens.composeIso(other.pIso));
    }

    public Setter<S, A> asSetter() {
        return new Setter<S, A>(this.pLens.asSetter());
    }

    public final Traversal<S, A> asTraversal() {
        return new Traversal<S, A>(this.pLens.asTraversal());
    }

    public final Optional<S, A> asOptional() {
        return new Optional<S, A>(this.pLens.asOptional());
    }

    public static <S> Lens<S, S> id() {
        return new Lens(PLens.pId());
    }

    public static <S, A> Lens<S, A> lens(Function<S, A> get, Function<A, Function<S, S>> set) {
        return new Lens<S, A>(PLens.pLens(get, set));
    }
}

