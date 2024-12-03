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
import io.atlassian.fugue.optic.PLens;
import io.atlassian.fugue.optic.POptional;
import io.atlassian.fugue.optic.PPrism;
import io.atlassian.fugue.optic.PSetter;
import io.atlassian.fugue.optic.PTraversal;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class PIso<S, T, A, B> {
    PIso() {
    }

    public abstract A get(S var1);

    public abstract T reverseGet(B var1);

    public abstract PIso<B, A, T, S> reverse();

    public final <C> Function<S, Function<C, T>> modifyFunctionF(Function<A, Function<C, B>> f) {
        return s -> ((Function)f.apply(this.get(s))).andThen(this::reverseGet);
    }

    public final <L> Function<S, Either<L, T>> modifyEitherF(Function<A, Either<L, B>> f) {
        return s -> ((Either)f.apply(this.get(s))).right().map(this::reverseGet);
    }

    public final Function<S, Option<T>> modifyOptionF(Function<A, Option<B>> f) {
        return s -> ((Option)f.apply(this.get(s))).map(this::reverseGet);
    }

    public final Function<S, Iterable<T>> modifyIterableF(Function<A, Iterable<B>> f) {
        return s -> Iterables.map((Iterable)((Iterable)f.apply(this.get(s))), this::reverseGet);
    }

    public final Function<S, Supplier<T>> modifySupplierF(Function<A, Supplier<B>> f) {
        return s -> Suppliers.compose(this::reverseGet, (Supplier)((Supplier)f.apply(this.get(s))));
    }

    public final Function<S, Pair<T, T>> modifyPairF(Function<A, Pair<B, B>> f) {
        return s -> Pair.map((Pair)((Pair)f.apply(this.get(s))), this::reverseGet);
    }

    public final Function<S, T> modify(Function<A, B> f) {
        return s -> this.reverseGet(f.apply(this.get(s)));
    }

    public final Function<S, T> set(B b) {
        return __ -> this.reverseGet(b);
    }

    public <S1, T1, A1, B1> PIso<Pair<S, S1>, Pair<T, T1>, Pair<A, A1>, Pair<B, B1>> product(PIso<S1, T1, A1, B1> other) {
        return PIso.pIso(ss1 -> Pair.pair(this.get(ss1.left()), other.get(ss1.right())), bb1 -> Pair.pair(this.reverseGet(bb1.left()), other.reverseGet(bb1.right())));
    }

    public <C> PIso<Pair<S, C>, Pair<T, C>, Pair<A, C>, Pair<B, C>> first() {
        return PIso.pIso(sc -> Pair.pair(this.get(sc.left()), (Object)sc.right()), bc -> Pair.pair(this.reverseGet(bc.left()), (Object)bc.right()));
    }

    public <C> PIso<Pair<C, S>, Pair<C, T>, Pair<C, A>, Pair<C, B>> second() {
        return PIso.pIso(cs -> Pair.pair((Object)cs.left(), this.get(cs.right())), cb -> Pair.pair((Object)cb.left(), this.reverseGet(cb.right())));
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

    public final <C, D> PPrism<S, T, C, D> composePrism(PPrism<A, B, C, D> other) {
        return this.asPrism().composePrism(other);
    }

    public final <C, D> PLens<S, T, C, D> composeLens(PLens<A, B, C, D> other) {
        return this.asLens().composeLens(other);
    }

    public final <C, D> PIso<S, T, C, D> composeIso(final PIso<A, B, C, D> other) {
        final PIso self = this;
        return new PIso<S, T, C, D>(){

            @Override
            public C get(S s) {
                return other.get(self.get(s));
            }

            @Override
            public T reverseGet(D d) {
                return self.reverseGet(other.reverseGet(d));
            }

            @Override
            public PIso<D, C, T, S> reverse() {
                final 1 composeSelf = this;
                return new PIso<D, C, T, S>(){

                    @Override
                    public T get(D d) {
                        return self.reverseGet(other.reverseGet(d));
                    }

                    @Override
                    public C reverseGet(S s) {
                        return other.get(self.get(s));
                    }

                    @Override
                    public PIso<S, T, C, D> reverse() {
                        return composeSelf;
                    }
                };
            }
        };
    }

    public final Fold<S, A> asFold() {
        return new Fold<S, A>(){

            @Override
            public <M> Function<S, M> foldMap(Monoid<M> monoid, Function<A, M> f) {
                return s -> f.apply(PIso.this.get(s));
            }
        };
    }

    public final Getter<S, A> asGetter() {
        return new Getter<S, A>(){

            @Override
            public A get(S s) {
                return PIso.this.get(s);
            }
        };
    }

    public PSetter<S, T, A, B> asSetter() {
        return new PSetter<S, T, A, B>(){

            @Override
            public Function<S, T> modify(Function<A, B> f) {
                return PIso.this.modify(f);
            }

            @Override
            public Function<S, T> set(B b) {
                return PIso.this.set(b);
            }
        };
    }

    public PTraversal<S, T, A, B> asTraversal() {
        final PIso self = this;
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
                return s -> f.apply(self.get(s));
            }
        };
    }

    public POptional<S, T, A, B> asOptional() {
        final PIso self = this;
        return new POptional<S, T, A, B>(){

            @Override
            public Either<T, A> getOrModify(S s) {
                return Either.right(self.get(s));
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
            public Function<S, T> set(B b) {
                return self.set(b);
            }

            @Override
            public Option<A> getOption(S s) {
                return Option.some(self.get(s));
            }

            @Override
            public Function<S, T> modify(Function<A, B> f) {
                return self.modify(f);
            }
        };
    }

    public PPrism<S, T, A, B> asPrism() {
        final PIso self = this;
        return new PPrism<S, T, A, B>(){

            @Override
            public Either<T, A> getOrModify(S s) {
                return Either.right(self.get(s));
            }

            @Override
            public T reverseGet(B b) {
                return self.reverseGet(b);
            }

            @Override
            public Option<A> getOption(S s) {
                return Option.some(self.get(s));
            }
        };
    }

    public PLens<S, T, A, B> asLens() {
        final PIso self = this;
        return new PLens<S, T, A, B>(){

            @Override
            public A get(S s) {
                return self.get(s);
            }

            @Override
            public Function<S, T> set(B b) {
                return self.set(b);
            }

            @Override
            public Function<S, T> modify(Function<A, B> f) {
                return self.modify(f);
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
        };
    }

    public static <S, T, A, B> PIso<S, T, A, B> pIso(final Function<S, A> get, final Function<B, T> reverseGet) {
        return new PIso<S, T, A, B>(){

            @Override
            public A get(S s) {
                return get.apply(s);
            }

            @Override
            public T reverseGet(B b) {
                return reverseGet.apply(b);
            }

            @Override
            public PIso<B, A, T, S> reverse() {
                final 9 self = this;
                return new PIso<B, A, T, S>(){

                    @Override
                    public T get(B b) {
                        return reverseGet.apply(b);
                    }

                    @Override
                    public A reverseGet(S s) {
                        return get.apply(s);
                    }

                    @Override
                    public PIso<S, T, A, B> reverse() {
                        return self;
                    }
                };
            }
        };
    }

    public static <S, T> PIso<S, T, S, T> pId() {
        return new PIso<S, T, S, T>(){

            @Override
            public S get(S s) {
                return s;
            }

            @Override
            public T reverseGet(T t) {
                return t;
            }

            @Override
            public PIso<T, S, T, S> reverse() {
                final 10 self = this;
                return new PIso<T, S, T, S>(){

                    @Override
                    public T get(T t) {
                        return t;
                    }

                    @Override
                    public S reverseGet(S s) {
                        return s;
                    }

                    @Override
                    public PIso<S, T, S, T> reverse() {
                        return self;
                    }
                };
            }
        };
    }
}

