/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Either
 *  io.atlassian.fugue.Iterables
 *  io.atlassian.fugue.Monoid
 *  io.atlassian.fugue.Option
 *  io.atlassian.fugue.Pair
 *  io.atlassian.fugue.Suppliers
 */
package io.atlassian.fugue.optic;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Iterables;
import io.atlassian.fugue.Monoid;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Pair;
import io.atlassian.fugue.Suppliers;
import io.atlassian.fugue.optic.Fold;
import io.atlassian.fugue.optic.Getter;
import io.atlassian.fugue.optic.PIso;
import io.atlassian.fugue.optic.POptional;
import io.atlassian.fugue.optic.PPrism;
import io.atlassian.fugue.optic.PSetter;
import io.atlassian.fugue.optic.PTraversal;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class PLens<S, T, A, B> {
    PLens() {
    }

    public abstract A get(S var1);

    public abstract Function<S, T> set(B var1);

    public abstract <C> Function<S, Function<C, T>> modifyFunctionF(Function<A, Function<C, B>> var1);

    public abstract <L> Function<S, Either<L, T>> modifyEitherF(Function<A, Either<L, B>> var1);

    public abstract Function<S, Option<T>> modifyOptionF(Function<A, Option<B>> var1);

    public abstract Function<S, Iterable<T>> modifyIterableF(Function<A, Iterable<B>> var1);

    public abstract Function<S, Supplier<T>> modifySupplierF(Function<A, Supplier<B>> var1);

    public abstract Function<S, Pair<T, T>> modifyPairF(Function<A, Pair<B, B>> var1);

    public abstract Function<S, T> modify(Function<A, B> var1);

    public final <S1, T1> PLens<Either<S, S1>, Either<T, T1>, A, B> sum(PLens<S1, T1, A, B> other) {
        return PLens.pLens(e -> e.fold(this::get, other::get), b -> e -> e.bimap(this.set(b), other.set(b)));
    }

    public final <C> Fold<S, C> composeFold(Fold<A, C> other) {
        return this.asFold().composeFold(other);
    }

    public final <C> Getter<S, C> composeGetter(Getter<A, C> other) {
        return this.asGetter().composeGetter(other);
    }

    public final <C, D> PSetter<S, T, C, D> composeSetter(PSetter<A, B, C, D> other) {
        return this.asSetter().composeSetter(other);
    }

    public final <C, D> PTraversal<S, T, C, D> composeTraversal(PTraversal<A, B, C, D> other) {
        return this.asTraversal().composeTraversal(other);
    }

    public final <C, D> POptional<S, T, C, D> composeOptional(POptional<A, B, C, D> other) {
        return this.asOptional().composeOptional(other);
    }

    public final <C, D> POptional<S, T, C, D> composePrism(PPrism<A, B, C, D> other) {
        return this.asOptional().composeOptional(other.asOptional());
    }

    public final <C, D> PLens<S, T, C, D> composeLens(final PLens<A, B, C, D> other) {
        final PLens self = this;
        return new PLens<S, T, C, D>(){

            @Override
            public C get(S s) {
                return other.get(self.get(s));
            }

            @Override
            public Function<S, T> set(D d) {
                return self.modify(other.set(d));
            }

            @Override
            public <G> Function<S, Function<G, T>> modifyFunctionF(Function<C, Function<G, D>> f) {
                return self.modifyFunctionF(other.modifyFunctionF(f));
            }

            @Override
            public <L> Function<S, Either<L, T>> modifyEitherF(Function<C, Either<L, D>> f) {
                return self.modifyEitherF(other.modifyEitherF(f));
            }

            @Override
            public Function<S, Option<T>> modifyOptionF(Function<C, Option<D>> f) {
                return self.modifyOptionF(other.modifyOptionF(f));
            }

            @Override
            public Function<S, Iterable<T>> modifyIterableF(Function<C, Iterable<D>> f) {
                return self.modifyIterableF(other.modifyIterableF(f));
            }

            @Override
            public Function<S, Supplier<T>> modifySupplierF(Function<C, Supplier<D>> f) {
                return self.modifySupplierF(other.modifySupplierF(f));
            }

            @Override
            public Function<S, Pair<T, T>> modifyPairF(Function<C, Pair<D, D>> f) {
                return self.modifyPairF(other.modifyPairF(f));
            }

            @Override
            public Function<S, T> modify(Function<C, D> f) {
                return self.modify(other.modify(f));
            }
        };
    }

    public final <C, D> PLens<S, T, C, D> composeIso(PIso<A, B, C, D> other) {
        return this.composeLens(other.asLens());
    }

    public final Fold<S, A> asFold() {
        return new Fold<S, A>(){

            @Override
            public <M> Function<S, M> foldMap(Monoid<M> monoid, Function<A, M> f) {
                return s -> f.apply(PLens.this.get(s));
            }
        };
    }

    public final Getter<S, A> asGetter() {
        return new Getter<S, A>(){

            @Override
            public A get(S s) {
                return PLens.this.get(s);
            }
        };
    }

    public PSetter<S, T, A, B> asSetter() {
        return new PSetter<S, T, A, B>(){

            @Override
            public Function<S, T> modify(Function<A, B> f) {
                return PLens.this.modify(f);
            }

            @Override
            public Function<S, T> set(B b) {
                return PLens.this.set(b);
            }
        };
    }

    public PTraversal<S, T, A, B> asTraversal() {
        final PLens self = this;
        return new PTraversal<S, T, A, B>(){

            @Override
            public <C> Function<S, Function<C, T>> modifyFunctionF(Function<A, Function<C, B>> f) {
                return self.modifyFunctionF(f);
            }

            @Override
            public <L> Function<S, Either<L, T>> modifyEitherF(Function<A, Either<L, B>> f) {
                return self.modifyEitherF(f);
            }

            @Override
            public Function<S, Option<T>> modifyOptionF(Function<A, Option<B>> f) {
                return self.modifyOptionF(f);
            }

            @Override
            public Function<S, Iterable<T>> modifyIterableF(Function<A, Iterable<B>> f) {
                return self.modifyIterableF(f);
            }

            @Override
            public Function<S, Supplier<T>> modifySupplierF(Function<A, Supplier<B>> f) {
                return self.modifySupplierF(f);
            }

            @Override
            public Function<S, Pair<T, T>> modifyPairF(Function<A, Pair<B, B>> f) {
                return self.modifyPairF(f);
            }

            @Override
            public <M> Function<S, M> foldMap(Monoid<M> monoid, Function<A, M> f) {
                return s -> f.apply(PLens.this.get(s));
            }
        };
    }

    public POptional<S, T, A, B> asOptional() {
        final PLens self = this;
        return new POptional<S, T, A, B>(){

            @Override
            public Either<T, A> getOrModify(S s) {
                return Either.right(self.get(s));
            }

            @Override
            public Function<S, T> set(B b) {
                return self.set(b);
            }

            @Override
            public Option<A> getOption(S s) {
                return Option.some(self.get(s));
            }

            @Override
            public <C> Function<S, Function<C, T>> modifyFunctionF(Function<A, Function<C, B>> f) {
                return self.modifyFunctionF(f);
            }

            @Override
            public <L> Function<S, Either<L, T>> modifyEitherF(Function<A, Either<L, B>> f) {
                return self.modifyEitherF(f);
            }

            @Override
            public Function<S, Option<T>> modifyOptionF(Function<A, Option<B>> f) {
                return self.modifyOptionF(f);
            }

            @Override
            public Function<S, Iterable<T>> modifyIterableF(Function<A, Iterable<B>> f) {
                return self.modifyIterableF(f);
            }

            @Override
            public Function<S, Supplier<T>> modifySupplierF(Function<A, Supplier<B>> f) {
                return self.modifySupplierF(f);
            }

            @Override
            public Function<S, Pair<T, T>> modifyPairF(Function<A, Pair<B, B>> f) {
                return self.modifyPairF(f);
            }

            @Override
            public Function<S, T> modify(Function<A, B> f) {
                return self.modify(f);
            }
        };
    }

    public static <S, T> PLens<S, T, S, T> pId() {
        return PIso.pId().asLens();
    }

    public static <S, T, A, B> PLens<S, T, A, B> pLens(final Function<S, A> get, final Function<B, Function<S, T>> set) {
        return new PLens<S, T, A, B>(){

            @Override
            public A get(S s) {
                return get.apply(s);
            }

            @Override
            public Function<S, T> set(B b) {
                return (Function)set.apply(b);
            }

            @Override
            public <C> Function<S, Function<C, T>> modifyFunctionF(Function<A, Function<C, B>> f) {
                return s -> ((Function)f.apply(get.apply(s))).andThen(b -> ((Function)set.apply(b)).apply(s));
            }

            @Override
            public <L> Function<S, Either<L, T>> modifyEitherF(Function<A, Either<L, B>> f) {
                return s -> ((Either)f.apply(get.apply(s))).right().map(a -> ((Function)set.apply(a)).apply(s));
            }

            @Override
            public Function<S, Option<T>> modifyOptionF(Function<A, Option<B>> f) {
                return s -> ((Option)f.apply(get.apply(s))).map(a -> ((Function)set.apply(a)).apply(s));
            }

            @Override
            public Function<S, Iterable<T>> modifyIterableF(Function<A, Iterable<B>> f) {
                return s -> Iterables.map((Iterable)((Iterable)f.apply(get.apply(s))), a -> ((Function)set.apply(a)).apply(s));
            }

            @Override
            public Function<S, Supplier<T>> modifySupplierF(Function<A, Supplier<B>> f) {
                return s -> Suppliers.compose(a -> ((Function)set.apply(a)).apply(s), (Supplier)((Supplier)f.apply(get.apply(s))));
            }

            @Override
            public Function<S, Pair<T, T>> modifyPairF(Function<A, Pair<B, B>> f) {
                return s -> Pair.map((Pair)((Pair)f.apply(get.apply(s))), a -> ((Function)set.apply(a)).apply(s));
            }

            @Override
            public Function<S, T> modify(Function<A, B> f) {
                return s -> ((Function)set.apply(f.apply(get.apply(s)))).apply(s);
            }
        };
    }
}

