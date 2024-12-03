/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Either
 *  io.atlassian.fugue.Eithers
 *  io.atlassian.fugue.Iterables
 *  io.atlassian.fugue.Monoid
 *  io.atlassian.fugue.Option
 *  io.atlassian.fugue.Pair
 *  io.atlassian.fugue.Suppliers
 */
package io.atlassian.fugue.optic;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Eithers;
import io.atlassian.fugue.Iterables;
import io.atlassian.fugue.Monoid;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Pair;
import io.atlassian.fugue.Suppliers;
import io.atlassian.fugue.optic.Fold;
import io.atlassian.fugue.optic.Getter;
import io.atlassian.fugue.optic.PIso;
import io.atlassian.fugue.optic.PLens;
import io.atlassian.fugue.optic.POptional;
import io.atlassian.fugue.optic.PSetter;
import io.atlassian.fugue.optic.PTraversal;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class PPrism<S, T, A, B> {
    PPrism() {
    }

    public abstract Either<T, A> getOrModify(S var1);

    public abstract T reverseGet(B var1);

    public abstract Option<A> getOption(S var1);

    public final <C> Function<S, Function<C, T>> modifyFunctionF(Function<A, Function<C, B>> f) {
        return s -> (Function)this.getOrModify(s).fold(t -> __ -> t, a -> ((Function)f.apply(a)).andThen(this::reverseGet));
    }

    public final <L> Function<S, Either<L, T>> modifyEitherF(Function<A, Either<L, B>> f) {
        return s -> (Either)this.getOrModify(s).fold(Eithers.toRight(), t -> ((Either)f.apply(t)).right().map(this::reverseGet));
    }

    public final Function<S, Option<T>> modifyOptionF(Function<A, Option<B>> f) {
        return s -> (Option)this.getOrModify(s).fold(Option::some, t -> ((Option)f.apply(t)).map(this::reverseGet));
    }

    public final Function<S, Pair<T, T>> modifyPairF(Function<A, Pair<B, B>> f) {
        return s -> (Pair)this.getOrModify(s).fold(t -> Pair.pair((Object)t, (Object)t), t -> Pair.map((Pair)((Pair)f.apply(t)), this::reverseGet));
    }

    public final Function<S, Supplier<T>> modifySupplierF(Function<A, Supplier<B>> f) {
        return s -> (Supplier)this.getOrModify(s).fold(Suppliers::ofInstance, t -> Suppliers.compose(this::reverseGet, (Supplier)((Supplier)f.apply(t))));
    }

    public final Function<S, Iterable<T>> modifyIterableF(Function<A, Iterable<B>> f) {
        return s -> (Iterable)this.getOrModify(s).fold(Collections::singleton, t -> Iterables.map((Iterable)((Iterable)f.apply(t)), this::reverseGet));
    }

    public final Function<S, T> modify(Function<A, B> f) {
        return s -> this.getOrModify(s).fold(Function.identity(), a -> this.reverseGet(f.apply(a)));
    }

    public final Function<S, Option<T>> modifyOption(Function<A, B> f) {
        return s -> this.getOption(s).map(a -> this.reverseGet(f.apply(a)));
    }

    public final Function<S, T> set(B b) {
        return this.modify(__ -> b);
    }

    public final Function<S, Option<T>> setOption(B b) {
        return this.modifyOption(__ -> b);
    }

    public final boolean isMatching(S s) {
        return this.getOption(s).isDefined();
    }

    public final Getter<B, T> re() {
        return Getter.getter(this::reverseGet);
    }

    public final <C> Fold<S, C> composeFold(Fold<A, C> other) {
        return this.asFold().composeFold(other);
    }

    public final <C> Fold<S, C> composeGetter(Getter<A, C> other) {
        return this.asFold().composeGetter(other);
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

    public final <C, D> POptional<S, T, C, D> composeLens(PLens<A, B, C, D> other) {
        return this.asOptional().composeOptional(other.asOptional());
    }

    public final <C, D> PPrism<S, T, C, D> composePrism(final PPrism<A, B, C, D> other) {
        return new PPrism<S, T, C, D>(){

            @Override
            public Either<T, C> getOrModify(S s) {
                return PPrism.this.getOrModify(s).right().flatMap(a -> other.getOrModify(a).bimap(b -> PPrism.this.set(b).apply(s), Function.identity()));
            }

            @Override
            public T reverseGet(D d) {
                return PPrism.this.reverseGet(other.reverseGet(d));
            }

            @Override
            public Option<C> getOption(S s) {
                return PPrism.this.getOption(s).flatMap(other::getOption);
            }
        };
    }

    public final <C, D> PPrism<S, T, C, D> composeIso(PIso<A, B, C, D> other) {
        return this.composePrism(other.asPrism());
    }

    public final Fold<S, A> asFold() {
        return new Fold<S, A>(){

            @Override
            public <M> Function<S, M> foldMap(Monoid<M> monoid, Function<A, M> f) {
                return s -> PPrism.this.getOption(s).map(f).getOrElse(monoid.zero());
            }
        };
    }

    public PSetter<S, T, A, B> asSetter() {
        return new PSetter<S, T, A, B>(){

            @Override
            public Function<S, T> modify(Function<A, B> f) {
                return PPrism.this.modify(f);
            }

            @Override
            public Function<S, T> set(B b) {
                return PPrism.this.set(b);
            }
        };
    }

    public PTraversal<S, T, A, B> asTraversal() {
        final PPrism self = this;
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
                return s -> PPrism.this.getOption(s).map(f).getOrElse(monoid.zero());
            }
        };
    }

    public POptional<S, T, A, B> asOptional() {
        final PPrism self = this;
        return new POptional<S, T, A, B>(){

            @Override
            public Either<T, A> getOrModify(S s) {
                return self.getOrModify(s);
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
                return self.getOption(s);
            }

            @Override
            public Function<S, T> modify(Function<A, B> f) {
                return self.modify(f);
            }
        };
    }

    public static <S, T> PPrism<S, T, S, T> pId() {
        return PIso.pId().asPrism();
    }

    public static <S, T, A, B> PPrism<S, T, A, B> pPrism(final Function<S, Either<T, A>> getOrModify, final Function<B, T> reverseGet) {
        return new PPrism<S, T, A, B>(){

            @Override
            public Either<T, A> getOrModify(S s) {
                return (Either)getOrModify.apply(s);
            }

            @Override
            public T reverseGet(B b) {
                return reverseGet.apply(b);
            }

            @Override
            public Option<A> getOption(S s) {
                return ((Either)getOrModify.apply(s)).right().toOption();
            }
        };
    }
}

