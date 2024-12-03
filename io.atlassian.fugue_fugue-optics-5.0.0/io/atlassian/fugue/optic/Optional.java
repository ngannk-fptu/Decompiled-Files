/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Either
 *  io.atlassian.fugue.Eithers
 *  io.atlassian.fugue.Iterables
 *  io.atlassian.fugue.Option
 *  io.atlassian.fugue.Pair
 *  io.atlassian.fugue.Suppliers
 */
package io.atlassian.fugue.optic;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Eithers;
import io.atlassian.fugue.Iterables;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Pair;
import io.atlassian.fugue.Suppliers;
import io.atlassian.fugue.optic.Iso;
import io.atlassian.fugue.optic.Lens;
import io.atlassian.fugue.optic.POptional;
import io.atlassian.fugue.optic.Prism;
import io.atlassian.fugue.optic.Setter;
import io.atlassian.fugue.optic.Traversal;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Optional<S, A>
extends POptional<S, S, A, A> {
    final POptional<S, S, A, A> pOptional;

    public Optional(POptional<S, S, A, A> pOptional) {
        this.pOptional = pOptional;
    }

    @Override
    public Function<S, S> set(A a) {
        return this.pOptional.set(a);
    }

    @Override
    public Function<S, Supplier<S>> modifySupplierF(Function<A, Supplier<A>> f) {
        return this.pOptional.modifySupplierF(f);
    }

    @Override
    public Function<S, Option<S>> modifyOptionF(Function<A, Option<A>> f) {
        return this.pOptional.modifyOptionF(f);
    }

    @Override
    public <C> Function<S, Function<C, S>> modifyFunctionF(Function<A, Function<C, A>> f) {
        return this.pOptional.modifyFunctionF(f);
    }

    @Override
    public <L> Function<S, Either<L, S>> modifyEitherF(Function<A, Either<L, A>> f) {
        return this.pOptional.modifyEitherF(f);
    }

    @Override
    public Function<S, Iterable<S>> modifyIterableF(Function<A, Iterable<A>> f) {
        return this.pOptional.modifyIterableF(f);
    }

    @Override
    public Function<S, Pair<S, S>> modifyPairF(Function<A, Pair<A, A>> f) {
        return this.pOptional.modifyPairF(f);
    }

    @Override
    public Function<S, S> modify(Function<A, A> f) {
        return this.pOptional.modify(f);
    }

    @Override
    public Either<S, A> getOrModify(S s) {
        return this.pOptional.getOrModify(s);
    }

    @Override
    public Option<A> getOption(S s) {
        return this.pOptional.getOption(s);
    }

    public final <S1> Optional<Either<S, S1>, A> sum(Optional<S1, A> other) {
        return new Optional<Either<S, S>, A>(this.pOptional.sum(other.pOptional));
    }

    public final <C> Optional<Pair<S, C>, Pair<A, C>> first() {
        return new Optional(this.pOptional.first());
    }

    public final <C> Optional<Pair<C, S>, Pair<C, A>> second() {
        return new Optional(this.pOptional.second());
    }

    public final <C> Setter<S, C> composeSetter(Setter<A, C> other) {
        return new Setter(this.pOptional.composeSetter(other.pSetter));
    }

    public final <C> Traversal<S, C> composeTraversal(Traversal<A, C> other) {
        return new Traversal(this.pOptional.composeTraversal(other.pTraversal));
    }

    public final <C> Optional<S, C> composeOptional(Optional<A, C> other) {
        return new Optional<S, A>(this.pOptional.composeOptional(other.pOptional));
    }

    public final <C> Optional<S, C> composePrism(Prism<A, C> other) {
        return new Optional(this.pOptional.composePrism(other.pPrism));
    }

    public final <C> Optional<S, C> composeLens(Lens<A, C> other) {
        return new Optional(this.pOptional.composeLens(other.pLens));
    }

    public final <C> Optional<S, C> composeIso(Iso<A, C> other) {
        return new Optional(this.pOptional.composeIso(other.pIso));
    }

    public final Setter<S, A> asSetter() {
        return new Setter<S, A>(this.pOptional.asSetter());
    }

    public final Traversal<S, A> asTraversal() {
        return new Traversal<S, A>(this.pOptional.asTraversal());
    }

    public static <S> Optional<S, S> id() {
        return new Optional(POptional.pId());
    }

    public static <S, A> Optional<S, A> optional(final Function<S, Option<A>> getOption, final Function<A, Function<S, S>> set) {
        return new Optional<S, A>(new POptional<S, S, A, A>(){

            @Override
            public Either<S, A> getOrModify(S s) {
                return (Either)((Option)getOption.apply(s)).fold(() -> Either.left((Object)s), Eithers.toRight());
            }

            @Override
            public Function<S, S> set(A a) {
                return (Function)set.apply(a);
            }

            @Override
            public Option<A> getOption(S s) {
                return (Option)getOption.apply(s);
            }

            @Override
            public <C> Function<S, Function<C, S>> modifyFunctionF(Function<A, Function<C, A>> f) {
                return s -> (Function)((Option)getOption.apply(s)).fold(() -> __ -> s, a -> ((Function)f.apply(a)).andThen(b -> ((Function)set.apply(b)).apply(s)));
            }

            @Override
            public <L> Function<S, Either<L, S>> modifyEitherF(Function<A, Either<L, A>> f) {
                return s -> (Either)((Option)getOption.apply(s)).fold(() -> Either.right((Object)s), t -> ((Either)f.apply(t)).right().map(b -> ((Function)set.apply(b)).apply(s)));
            }

            @Override
            public Function<S, Option<S>> modifyOptionF(Function<A, Option<A>> f) {
                return s -> (Option)((Option)getOption.apply(s)).fold(() -> Option.some((Object)s), t -> ((Option)f.apply(t)).map(b -> ((Function)set.apply(b)).apply(s)));
            }

            @Override
            public Function<S, Iterable<S>> modifyIterableF(Function<A, Iterable<A>> f) {
                return s -> (Iterable)((Option)getOption.apply(s)).fold(() -> Collections.singleton(s), t -> Iterables.map((Iterable)((Iterable)f.apply(t)), b -> ((Function)set.apply(b)).apply(s)));
            }

            @Override
            public Function<S, Supplier<S>> modifySupplierF(Function<A, Supplier<A>> f) {
                return s -> (Supplier)((Option)getOption.apply(s)).fold(() -> Suppliers.ofInstance((Object)s), t -> Suppliers.compose(b -> ((Function)set.apply(b)).apply(s), (Supplier)((Supplier)f.apply(t))));
            }

            @Override
            public Function<S, Pair<S, S>> modifyPairF(Function<A, Pair<A, A>> f) {
                return s -> (Pair)((Option)getOption.apply(s)).fold(() -> Pair.pair((Object)s, (Object)s), t -> Pair.map((Pair)((Pair)f.apply(t)), b -> ((Function)set.apply(b)).apply(s)));
            }

            @Override
            public Function<S, S> modify(Function<A, A> f) {
                return s -> ((Option)getOption.apply(s)).fold(() -> s, a -> ((Function)set.apply(f.apply(a))).apply(s));
            }
        });
    }
}

