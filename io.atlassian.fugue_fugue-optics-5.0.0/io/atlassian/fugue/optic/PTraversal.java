/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Either
 *  io.atlassian.fugue.Eithers
 *  io.atlassian.fugue.Functions
 *  io.atlassian.fugue.Iterables
 *  io.atlassian.fugue.Monoid
 *  io.atlassian.fugue.Monoids
 *  io.atlassian.fugue.Option
 *  io.atlassian.fugue.Options
 *  io.atlassian.fugue.Pair
 *  io.atlassian.fugue.Suppliers
 */
package io.atlassian.fugue.optic;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Eithers;
import io.atlassian.fugue.Functions;
import io.atlassian.fugue.Iterables;
import io.atlassian.fugue.Monoid;
import io.atlassian.fugue.Monoids;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Options;
import io.atlassian.fugue.Pair;
import io.atlassian.fugue.Suppliers;
import io.atlassian.fugue.optic.Fold;
import io.atlassian.fugue.optic.Getter;
import io.atlassian.fugue.optic.PIso;
import io.atlassian.fugue.optic.PLens;
import io.atlassian.fugue.optic.POptional;
import io.atlassian.fugue.optic.PPrism;
import io.atlassian.fugue.optic.PSetter;
import java.util.Collections;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class PTraversal<S, T, A, B> {
    public abstract <C> Function<S, Function<C, T>> modifyFunctionF(Function<A, Function<C, B>> var1);

    public abstract <L> Function<S, Either<L, T>> modifyEitherF(Function<A, Either<L, B>> var1);

    public abstract Function<S, Option<T>> modifyOptionF(Function<A, Option<B>> var1);

    public abstract Function<S, Iterable<T>> modifyIterableF(Function<A, Iterable<B>> var1);

    public abstract Function<S, Supplier<T>> modifySupplierF(Function<A, Supplier<B>> var1);

    public abstract Function<S, Pair<T, T>> modifyPairF(Function<A, Pair<B, B>> var1);

    public abstract <M> Function<S, M> foldMap(Monoid<M> var1, Function<A, M> var2);

    public final Function<S, A> fold(Monoid<A> monoid) {
        return this.foldMap(monoid, Function.identity());
    }

    public final Iterable<A> getAll(S s) {
        return this.foldMap(Monoids.iterable(), Collections::singleton).apply(s);
    }

    public final Function<S, Option<A>> find(Predicate<A> p) {
        return this.foldMap(Monoids.firstOption(), a -> p.test(a) ? Option.some((Object)a) : Option.none());
    }

    public final Option<A> headOption(S s) {
        return this.find(__ -> Boolean.TRUE).apply(s);
    }

    public final Predicate<S> exist(Predicate<A> p) {
        return this.foldMap(Monoids.disjunction, p::test)::apply;
    }

    public final Predicate<S> all(Predicate<A> p) {
        return this.foldMap(Monoids.conjunction, p::test)::apply;
    }

    public final Function<S, T> modify(Function<A, B> f) {
        return s -> this.modifySupplierF(a -> Suppliers.ofInstance(f.apply(a))).apply(s).get();
    }

    public final Function<S, T> set(B b) {
        return this.modify(__ -> b);
    }

    public final <S1, T1> PTraversal<Either<S, S1>, Either<T, T1>, A, B> sum(final PTraversal<S1, T1, A, B> other) {
        final PTraversal self = this;
        return new PTraversal<Either<S, S1>, Either<T, T1>, A, B>(){

            @Override
            public <C> Function<Either<S, S1>, Function<C, Either<T, T1>>> modifyFunctionF(Function<A, Function<C, B>> f) {
                return ss1 -> (Function)ss1.fold(s -> self.modifyFunctionF(f).apply(s).andThen(Eithers.toLeft()), s1 -> other.modifyFunctionF(f).apply(s1).andThen(Eithers.toRight()));
            }

            @Override
            public <L> Function<Either<S, S1>, Either<L, Either<T, T1>>> modifyEitherF(Function<A, Either<L, B>> f) {
                return ss1 -> (Either)ss1.fold(s -> self.modifyEitherF(f).apply(s).right().map(Eithers.toLeft()), s1 -> other.modifyEitherF(f).apply(s1).right().map(Eithers.toRight()));
            }

            @Override
            public Function<Either<S, S1>, Option<Either<T, T1>>> modifyOptionF(Function<A, Option<B>> f) {
                return ss1 -> (Option)ss1.fold(s -> self.modifyOptionF(f).apply(s).map(Eithers.toLeft()), s1 -> other.modifyOptionF(f).apply(s1).map(Eithers.toRight()));
            }

            @Override
            public Function<Either<S, S1>, Iterable<Either<T, T1>>> modifyIterableF(Function<A, Iterable<B>> f) {
                return ss1 -> (Iterable)ss1.fold(s -> Iterables.map(self.modifyIterableF(f).apply(s), (Function)Eithers.toLeft()), s1 -> Iterables.map(other.modifyIterableF(f).apply(s1), (Function)Eithers.toRight()));
            }

            @Override
            public Function<Either<S, S1>, Supplier<Either<T, T1>>> modifySupplierF(Function<A, Supplier<B>> f) {
                return ss1 -> (Supplier)ss1.fold(s -> Suppliers.compose((Function)Eithers.toLeft(), self.modifySupplierF(f).apply(s)), s1 -> Suppliers.compose((Function)Eithers.toRight(), other.modifySupplierF(f).apply(s1)));
            }

            @Override
            public Function<Either<S, S1>, Pair<Either<T, T1>, Either<T, T1>>> modifyPairF(Function<A, Pair<B, B>> f) {
                return ss1 -> (Pair)ss1.fold(s -> Pair.map(self.modifyPairF(f).apply(s), (Function)Eithers.toLeft()), s1 -> Pair.map(other.modifyPairF(f).apply(s1), (Function)Eithers.toRight()));
            }

            @Override
            public <M> Function<Either<S, S1>, M> foldMap(Monoid<M> monoid, Function<A, M> f) {
                return ss1 -> ss1.fold(self.foldMap(monoid, f), other.foldMap(monoid, f));
            }
        };
    }

    public final <C> Fold<S, C> composeFold(Fold<A, C> other) {
        return this.asFold().composeFold(other);
    }

    public final <C> Fold<S, C> composeFold(Getter<A, C> other) {
        return this.asFold().composeGetter(other);
    }

    public final <C, D> PSetter<S, T, C, D> composeSetter(PSetter<A, B, C, D> other) {
        return this.asSetter().composeSetter(other);
    }

    public final <C, D> PTraversal<S, T, C, D> composeTraversal(final PTraversal<A, B, C, D> other) {
        final PTraversal self = this;
        return new PTraversal<S, T, C, D>(){

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
            public <M> Function<S, M> foldMap(Monoid<M> monoid, Function<C, M> f) {
                return self.foldMap(monoid, other.foldMap(monoid, f));
            }
        };
    }

    public final <C, D> PTraversal<S, T, C, D> composeOptional(POptional<A, B, C, D> other) {
        return this.composeTraversal(other.asTraversal());
    }

    public final <C, D> PTraversal<S, T, C, D> composePrism(PPrism<A, B, C, D> other) {
        return this.composeTraversal(other.asTraversal());
    }

    public final <C, D> PTraversal<S, T, C, D> composeLens(PLens<A, B, C, D> other) {
        return this.composeTraversal(other.asTraversal());
    }

    public final <C, D> PTraversal<S, T, C, D> composeIso(PIso<A, B, C, D> other) {
        return this.composeTraversal(other.asTraversal());
    }

    public final Fold<S, A> asFold() {
        return new Fold<S, A>(){

            @Override
            public <M> Function<S, M> foldMap(Monoid<M> monoid, Function<A, M> f) {
                return PTraversal.this.foldMap(monoid, f);
            }
        };
    }

    public PSetter<S, T, A, B> asSetter() {
        return PSetter.pSetter(this::modify);
    }

    public static <S, T> PTraversal<S, T, S, T> pId() {
        return PIso.pId().asTraversal();
    }

    public static <S, T> PTraversal<Either<S, S>, Either<T, T>, S, T> pCodiagonal() {
        return new PTraversal<Either<S, S>, Either<T, T>, S, T>(){

            @Override
            public <C> Function<Either<S, S>, Function<C, Either<T, T>>> modifyFunctionF(Function<S, Function<C, T>> f) {
                return s -> (Function)s.bimap(f, f).fold(f1 -> f1.andThen(Eithers.toLeft()), f1 -> f1.andThen(Eithers.toRight()));
            }

            @Override
            public <L> Function<Either<S, S>, Either<L, Either<T, T>>> modifyEitherF(Function<S, Either<L, T>> f) {
                return s -> (Either)s.bimap(f, f).fold(e -> e.right().map(Eithers.toLeft()), e -> e.right().map(Eithers.toRight()));
            }

            @Override
            public Function<Either<S, S>, Option<Either<T, T>>> modifyOptionF(Function<S, Option<T>> f) {
                return s -> (Option)s.bimap(f, f).fold(o -> o.map(Eithers.toLeft()), o -> o.map(Eithers.toRight()));
            }

            @Override
            public Function<Either<S, S>, Iterable<Either<T, T>>> modifyIterableF(Function<S, Iterable<T>> f) {
                return s -> (Iterable)s.bimap(f, f).fold(ts -> Iterables.map((Iterable)ts, (Function)Eithers.toLeft()), ts -> Iterables.map((Iterable)ts, (Function)Eithers.toRight()));
            }

            @Override
            public Function<Either<S, S>, Supplier<Either<T, T>>> modifySupplierF(Function<S, Supplier<T>> f) {
                return s -> (Supplier)s.bimap(f, f).fold(p1 -> Suppliers.compose((Function)Eithers.toLeft(), (Supplier)p1), p1 -> Suppliers.compose((Function)Eithers.toRight(), (Supplier)p1));
            }

            @Override
            public Function<Either<S, S>, Pair<Either<T, T>, Either<T, T>>> modifyPairF(Function<S, Pair<T, T>> f) {
                return s -> (Pair)s.bimap(f, f).fold(tt -> Pair.map((Pair)tt, (Function)Eithers.toLeft()), tt -> Pair.map((Pair)tt, (Function)Eithers.toRight()));
            }

            @Override
            public <M> Function<Either<S, S>, M> foldMap(Monoid<M> monoid, Function<S, M> f) {
                return s -> s.fold(f, f);
            }
        };
    }

    public static <S, T, A, B> PTraversal<S, T, A, B> pTraversal(final Function<S, A> get1, final Function<S, A> get2, final BiFunction<B, B, Function<S, T>> set) {
        return new PTraversal<S, T, A, B>(){

            @Override
            public <C> Function<S, Function<C, T>> modifyFunctionF(Function<A, Function<C, B>> f) {
                return s -> Functions.ap((Function)((Function)f.apply(get2.apply(s))), ((Function)f.apply(get1.apply(s))).andThen(b1 -> b2 -> ((Function)set.apply(b1, b2)).apply(s)));
            }

            @Override
            public <L> Function<S, Either<L, T>> modifyEitherF(Function<A, Either<L, B>> f) {
                return s -> ((Either)f.apply(get2.apply(s))).right().ap(((Either)f.apply(get1.apply(s))).right().map(b1 -> b2 -> ((Function)set.apply(b1, b2)).apply(s)));
            }

            @Override
            public Function<S, Option<T>> modifyOptionF(Function<A, Option<B>> f) {
                return s -> Options.ap((Option)((Option)f.apply(get2.apply(s))), (Option)((Option)f.apply(get1.apply(s))).map(b1 -> b2 -> ((Function)set.apply(b1, b2)).apply(s)));
            }

            @Override
            public Function<S, Iterable<T>> modifyIterableF(Function<A, Iterable<B>> f) {
                return s -> Iterables.ap((Iterable)((Iterable)f.apply(get2.apply(s))), (Iterable)Iterables.map((Iterable)((Iterable)f.apply(get1.apply(s))), b1 -> b2 -> ((Function)set.apply(b1, b2)).apply(s)));
            }

            @Override
            public Function<S, Supplier<T>> modifySupplierF(Function<A, Supplier<B>> f) {
                return s -> Suppliers.ap((Supplier)((Supplier)f.apply(get2.apply(s))), (Supplier)Suppliers.compose(b1 -> b2 -> ((Function)set.apply(b1, b2)).apply(s), (Supplier)((Supplier)f.apply(get1.apply(s)))));
            }

            @Override
            public Function<S, Pair<T, T>> modifyPairF(Function<A, Pair<B, B>> f) {
                return s -> Pair.ap((Pair)((Pair)f.apply(get2.apply(s))), (Pair)Pair.map((Pair)((Pair)f.apply(get1.apply(s))), b1 -> b2 -> ((Function)set.apply(b1, b2)).apply(s)));
            }

            @Override
            public <M> Function<S, M> foldMap(Monoid<M> monoid, Function<A, M> f) {
                return s -> monoid.append(f.apply(get1.apply(s)), f.apply(get2.apply(s)));
            }
        };
    }

    public static <S, T, A, B> PTraversal<S, T, A, B> pTraversal(Function<S, A> get1, Function<S, A> get2, Function<S, A> get3, Function<B, Function<B, Function<B, Function<S, T>>>> set) {
        return PTraversal.fromCurried(PTraversal.pTraversal(get1, get2, (b1, b2) -> s -> b3 -> ((Function)((Function)((Function)set.apply(b1)).apply(b2)).apply(b3)).apply(s)), get3);
    }

    public static <S, T, A, B> PTraversal<S, T, A, B> pTraversal(Function<S, A> get1, Function<S, A> get2, Function<S, A> get3, Function<S, A> get4, Function<B, Function<B, Function<B, Function<B, Function<S, T>>>>> set) {
        return PTraversal.fromCurried(PTraversal.pTraversal(get1, get2, get3, b1 -> b2 -> b3 -> s -> b4 -> ((Function)((Function)((Function)((Function)set.apply(b1)).apply(b2)).apply(b3)).apply(b4)).apply(s)), get4);
    }

    public static <S, T, A, B> PTraversal<S, T, A, B> pTraversal(Function<S, A> get1, Function<S, A> get2, Function<S, A> get3, Function<S, A> get4, Function<S, A> get5, Function<B, Function<B, Function<B, Function<B, Function<B, Function<S, T>>>>>> set) {
        return PTraversal.fromCurried(PTraversal.pTraversal(get1, get2, get3, get4, b1 -> b2 -> b3 -> b4 -> s -> b5 -> ((Function)((Function)((Function)((Function)((Function)set.apply(b1)).apply(b2)).apply(b3)).apply(b4)).apply(b5)).apply(s)), get5);
    }

    public static <S, T, A, B> PTraversal<S, T, A, B> pTraversal(Function<S, A> get1, Function<S, A> get2, Function<S, A> get3, Function<S, A> get4, Function<S, A> get5, Function<S, A> get6, Function<B, Function<B, Function<B, Function<B, Function<B, Function<B, Function<S, T>>>>>>> set) {
        return PTraversal.fromCurried(PTraversal.pTraversal(get1, get2, get3, get4, get5, b1 -> b2 -> b3 -> b4 -> b5 -> s -> b6 -> ((Function)((Function)((Function)((Function)((Function)((Function)set.apply(b1)).apply(b2)).apply(b3)).apply(b4)).apply(b5)).apply(b6)).apply(s)), get6);
    }

    private static <S, T, A, B> PTraversal<S, T, A, B> fromCurried(final PTraversal<S, Function<B, T>, A, B> curriedTraversal, final Function<S, A> lastGet) {
        return new PTraversal<S, T, A, B>(){

            @Override
            public <C> Function<S, Function<C, T>> modifyFunctionF(Function<A, Function<C, B>> f) {
                return s -> Functions.ap((Function)((Function)f.apply(lastGet.apply(s))), curriedTraversal.modifyFunctionF(f).apply(s));
            }

            @Override
            public <L> Function<S, Either<L, T>> modifyEitherF(Function<A, Either<L, B>> f) {
                return s -> ((Either)f.apply(lastGet.apply(s))).right().ap(curriedTraversal.modifyEitherF(f).apply(s));
            }

            @Override
            public Function<S, Option<T>> modifyOptionF(Function<A, Option<B>> f) {
                return s -> Options.ap((Option)((Option)f.apply(lastGet.apply(s))), curriedTraversal.modifyOptionF(f).apply(s));
            }

            @Override
            public Function<S, Iterable<T>> modifyIterableF(Function<A, Iterable<B>> f) {
                return s -> Iterables.ap((Iterable)((Iterable)f.apply(lastGet.apply(s))), curriedTraversal.modifyIterableF(f).apply(s));
            }

            @Override
            public Function<S, Supplier<T>> modifySupplierF(Function<A, Supplier<B>> f) {
                return s -> Suppliers.ap((Supplier)((Supplier)f.apply(lastGet.apply(s))), curriedTraversal.modifySupplierF(f).apply(s));
            }

            @Override
            public Function<S, Pair<T, T>> modifyPairF(Function<A, Pair<B, B>> f) {
                return s -> Pair.ap((Pair)((Pair)f.apply(lastGet.apply(s))), curriedTraversal.modifyPairF(f).apply(s));
            }

            @Override
            public <M> Function<S, M> foldMap(Monoid<M> monoid, Function<A, M> f) {
                return s -> monoid.append(curriedTraversal.foldMap(monoid, f).apply(s), f.apply(lastGet.apply(s)));
            }
        };
    }
}

