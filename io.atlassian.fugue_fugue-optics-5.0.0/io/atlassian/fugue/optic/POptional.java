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
import io.atlassian.fugue.optic.PPrism;
import io.atlassian.fugue.optic.PSetter;
import io.atlassian.fugue.optic.PTraversal;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class POptional<S, T, A, B> {
    POptional() {
    }

    public abstract Either<T, A> getOrModify(S var1);

    public abstract Function<S, T> set(B var1);

    public abstract Option<A> getOption(S var1);

    public abstract <C> Function<S, Function<C, T>> modifyFunctionF(Function<A, Function<C, B>> var1);

    public abstract <L> Function<S, Either<L, T>> modifyEitherF(Function<A, Either<L, B>> var1);

    public abstract Function<S, Option<T>> modifyOptionF(Function<A, Option<B>> var1);

    public abstract Function<S, Pair<T, T>> modifyPairF(Function<A, Pair<B, B>> var1);

    public abstract Function<S, Supplier<T>> modifySupplierF(Function<A, Supplier<B>> var1);

    public abstract Function<S, Iterable<T>> modifyIterableF(Function<A, Iterable<B>> var1);

    public abstract Function<S, T> modify(Function<A, B> var1);

    public final Function<S, Option<T>> modifyOption(Function<A, B> f) {
        return s -> this.getOption(s).map(__ -> this.modify(f).apply(s));
    }

    public final Function<S, Option<T>> setOption(B b) {
        return this.modifyOption(__ -> b);
    }

    public final boolean isMatching(S s) {
        return this.getOption(s).isDefined();
    }

    public final <S1, T1> POptional<Either<S, S1>, Either<T, T1>, A, B> sum(POptional<S1, T1, A, B> other) {
        return POptional.pOptional(e -> (Either)e.fold(s -> this.getOrModify(s).left().map(Eithers.toLeft()), s1 -> other.getOrModify(s1).left().map(Eithers.toRight())), b -> e -> e.bimap(this.set(b), other.set(b)));
    }

    public <C> POptional<Pair<S, C>, Pair<T, C>, Pair<A, C>, Pair<B, C>> first() {
        return POptional.pOptional(sc -> this.getOrModify(sc.left()).bimap(t -> Pair.pair((Object)t, (Object)sc.right()), a -> Pair.pair((Object)a, (Object)sc.right())), bc -> s_ -> Pair.pair(this.set(bc.left()).apply(s_.left()), (Object)bc.right()));
    }

    public <C> POptional<Pair<C, S>, Pair<C, T>, Pair<C, A>, Pair<C, B>> second() {
        return POptional.pOptional(cs -> this.getOrModify(cs.right()).bimap(t -> Pair.pair((Object)cs.left(), (Object)t), a -> Pair.pair((Object)cs.left(), (Object)a)), cb -> _s -> Pair.pair((Object)cb.left(), this.set(cb.right()).apply(_s.right())));
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

    public final <C, D> POptional<S, T, C, D> composeOptional(final POptional<A, B, C, D> other) {
        final POptional self = this;
        return new POptional<S, T, C, D>(){

            @Override
            public Either<T, C> getOrModify(S s) {
                return self.getOrModify(s).right().flatMap(a -> other.getOrModify(a).bimap(b -> POptional.this.set(b).apply(s), Function.identity()));
            }

            @Override
            public Function<S, T> set(D d) {
                return self.modify(other.set(d));
            }

            @Override
            public Option<C> getOption(S s) {
                return self.getOption(s).flatMap(other::getOption);
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

    public final <C, D> POptional<S, T, C, D> composePrism(PPrism<A, B, C, D> other) {
        return this.composeOptional(other.asOptional());
    }

    public final <C, D> POptional<S, T, C, D> composeLens(PLens<A, B, C, D> other) {
        return this.composeOptional(other.asOptional());
    }

    public final <C, D> POptional<S, T, C, D> composeIso(PIso<A, B, C, D> other) {
        return this.composeOptional(other.asOptional());
    }

    public final Fold<S, A> asFold() {
        return new Fold<S, A>(){

            @Override
            public <M> Function<S, M> foldMap(Monoid<M> monoid, Function<A, M> f) {
                return s -> POptional.this.getOption(s).map(f).getOrElse(monoid.zero());
            }
        };
    }

    public PSetter<S, T, A, B> asSetter() {
        return new PSetter<S, T, A, B>(){

            @Override
            public Function<S, T> modify(Function<A, B> f) {
                return POptional.this.modify(f);
            }

            @Override
            public Function<S, T> set(B b) {
                return POptional.this.set(b);
            }
        };
    }

    public PTraversal<S, T, A, B> asTraversal() {
        final POptional self = this;
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
                return s -> self.getOption(s).map(f).getOrElse(monoid.zero());
            }
        };
    }

    public static <S, T> POptional<S, T, S, T> pId() {
        return PIso.pId().asOptional();
    }

    public static <S, T, A, B> POptional<S, T, A, B> pOptional(final Function<S, Either<T, A>> getOrModify, final Function<B, Function<S, T>> set) {
        return new POptional<S, T, A, B>(){

            @Override
            public Either<T, A> getOrModify(S s) {
                return (Either)getOrModify.apply(s);
            }

            @Override
            public Function<S, T> set(B b) {
                return (Function)set.apply(b);
            }

            @Override
            public Option<A> getOption(S s) {
                return ((Either)getOrModify.apply(s)).right().toOption();
            }

            @Override
            public <C> Function<S, Function<C, T>> modifyFunctionF(Function<A, Function<C, B>> f) {
                return s -> (Function)((Either)getOrModify.apply(s)).fold(t -> __ -> t, a -> ((Function)f.apply(a)).andThen(b -> ((Function)set.apply(b)).apply(s)));
            }

            @Override
            public <L> Function<S, Either<L, T>> modifyEitherF(Function<A, Either<L, B>> f) {
                return s -> (Either)((Either)getOrModify.apply(s)).fold(Eithers.toRight(), t -> ((Either)f.apply(t)).right().map(b -> ((Function)set.apply(b)).apply(s)));
            }

            @Override
            public Function<S, Option<T>> modifyOptionF(Function<A, Option<B>> f) {
                return s -> (Option)((Either)getOrModify.apply(s)).fold(Option::some, t -> ((Option)f.apply(t)).map(b -> ((Function)set.apply(b)).apply(s)));
            }

            @Override
            public Function<S, Iterable<T>> modifyIterableF(Function<A, Iterable<B>> f) {
                return s -> (Iterable)((Either)getOrModify.apply(s)).fold(Collections::singleton, t -> Iterables.map((Iterable)((Iterable)f.apply(t)), b -> ((Function)set.apply(b)).apply(s)));
            }

            @Override
            public Function<S, Supplier<T>> modifySupplierF(Function<A, Supplier<B>> f) {
                return s -> (Supplier)((Either)getOrModify.apply(s)).fold(Suppliers::ofInstance, t -> Suppliers.compose(b -> ((Function)set.apply(b)).apply(s), (Supplier)((Supplier)f.apply(t))));
            }

            @Override
            public Function<S, Pair<T, T>> modifyPairF(Function<A, Pair<B, B>> f) {
                return s -> (Pair)((Either)getOrModify.apply(s)).fold(t -> Pair.pair((Object)t, (Object)t), t -> Pair.map((Pair)((Pair)f.apply(t)), b -> ((Function)set.apply(b)).apply(s)));
            }

            @Override
            public Function<S, T> modify(Function<A, B> f) {
                return s -> ((Either)getOrModify.apply(s)).fold(Function.identity(), a -> ((Function)set.apply(f.apply(a))).apply(s));
            }
        };
    }
}

