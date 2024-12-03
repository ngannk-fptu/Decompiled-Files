/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Either
 *  io.atlassian.fugue.Monoid
 *  io.atlassian.fugue.Monoids
 *  io.atlassian.fugue.Option
 */
package io.atlassian.fugue.optic;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Monoid;
import io.atlassian.fugue.Monoids;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.optic.Getter;
import io.atlassian.fugue.optic.PIso;
import io.atlassian.fugue.optic.PLens;
import io.atlassian.fugue.optic.POptional;
import io.atlassian.fugue.optic.PPrism;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class Fold<S, A> {
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
        return this.find(__ -> true).apply(s);
    }

    public final Predicate<S> exist(Predicate<A> p) {
        return this.foldMap(Monoids.disjunction, p::test)::apply;
    }

    public final Function<S, Boolean> all(Predicate<A> p) {
        return this.foldMap(Monoids.conjunction, p::test)::apply;
    }

    public final <S1> Fold<Either<S, S1>, A> sum(final Fold<S1, A> other) {
        return new Fold<Either<S, S1>, A>(){

            @Override
            public <B> Function<Either<S, S1>, B> foldMap(Monoid<B> monoid, Function<A, B> f) {
                return s -> s.fold(Fold.this.foldMap(monoid, f), other.foldMap(monoid, f));
            }
        };
    }

    public final <B> Fold<S, B> composeFold(final Fold<A, B> other) {
        return new Fold<S, B>(){

            @Override
            public <C> Function<S, C> foldMap(Monoid<C> monoid, Function<B, C> f) {
                return Fold.this.foldMap(monoid, other.foldMap(monoid, f));
            }
        };
    }

    public final <C> Fold<S, C> composeGetter(Getter<A, C> other) {
        return this.composeFold(other.asFold());
    }

    public final <B, C, D> Fold<S, C> composeOptional(POptional<A, B, C, D> other) {
        return this.composeFold(other.asFold());
    }

    public final <B, C, D> Fold<S, C> composePrism(PPrism<A, B, C, D> other) {
        return this.composeFold(other.asFold());
    }

    public final <B, C, D> Fold<S, C> composeLens(PLens<A, B, C, D> other) {
        return this.composeFold(other.asFold());
    }

    public final <B, C, D> Fold<S, C> composeIso(PIso<A, B, C, D> other) {
        return this.composeFold(other.asFold());
    }

    public static <A> Fold<A, A> id() {
        return PIso.pId().asFold();
    }

    public static <A> Fold<Either<A, A>, A> codiagonal() {
        return new Fold<Either<A, A>, A>(){

            @Override
            public <B> Function<Either<A, A>, B> foldMap(Monoid<B> monoid, Function<A, B> f) {
                return e -> e.fold(f, f);
            }
        };
    }
}

