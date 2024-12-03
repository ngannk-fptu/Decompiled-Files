/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Either
 *  io.atlassian.fugue.Monoid
 *  io.atlassian.fugue.Pair
 */
package io.atlassian.fugue.optic;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Monoid;
import io.atlassian.fugue.Pair;
import io.atlassian.fugue.optic.Fold;
import io.atlassian.fugue.optic.PIso;
import io.atlassian.fugue.optic.PLens;
import io.atlassian.fugue.optic.POptional;
import io.atlassian.fugue.optic.PPrism;
import java.util.function.Function;

public abstract class Getter<S, A> {
    Getter() {
    }

    public abstract A get(S var1);

    public final <S1> Getter<Either<S, S1>, A> sum(Getter<S1, A> other) {
        return Getter.getter(e -> e.fold(this::get, other::get));
    }

    public final <S1, A1> Getter<Pair<S, S1>, Pair<A, A1>> product(Getter<S1, A1> other) {
        return Getter.getter(p2 -> Pair.pair(this.get(p2.left()), other.get(p2.right())));
    }

    public final <B> Getter<Pair<S, B>, Pair<A, B>> first() {
        return Getter.getter(p -> Pair.pair(this.get(p.left()), (Object)p.right()));
    }

    public final <B> Getter<Pair<B, S>, Pair<B, A>> second() {
        return Getter.getter(p -> Pair.pair((Object)p.left(), this.get(p.right())));
    }

    public final <B> Fold<S, B> composeFold(Fold<A, B> other) {
        return this.asFold().composeFold(other);
    }

    public final <B> Getter<S, B> composeGetter(Getter<A, B> other) {
        return Getter.getter(s -> other.get(this.get(s)));
    }

    public final <B, C, D> Fold<S, C> composeOptional(POptional<A, B, C, D> other) {
        return this.asFold().composeOptional(other);
    }

    public final <B, C, D> Fold<S, C> composePrism(PPrism<A, B, C, D> other) {
        return this.asFold().composePrism(other);
    }

    public final <B, C, D> Getter<S, C> composeLens(PLens<A, B, C, D> other) {
        return this.composeGetter(other.asGetter());
    }

    public final <B, C, D> Getter<S, C> composeIso(PIso<A, B, C, D> other) {
        return this.composeGetter(other.asGetter());
    }

    public final Fold<S, A> asFold() {
        return new Fold<S, A>(){

            @Override
            public <B> Function<S, B> foldMap(Monoid<B> monoid, Function<A, B> f) {
                return s -> f.apply(Getter.this.get(s));
            }
        };
    }

    public static <A> Getter<A, A> id() {
        return PIso.pId().asGetter();
    }

    public static <A> Getter<Either<A, A>, A> codiagonal() {
        return Getter.getter(e -> e.fold(Function.identity(), Function.identity()));
    }

    public static <S, A> Getter<S, A> getter(final Function<S, A> get) {
        return new Getter<S, A>(){

            @Override
            public A get(S s) {
                return get.apply(s);
            }
        };
    }
}

