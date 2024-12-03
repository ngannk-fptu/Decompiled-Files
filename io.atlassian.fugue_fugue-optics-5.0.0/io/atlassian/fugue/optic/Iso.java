/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Pair
 */
package io.atlassian.fugue.optic;

import io.atlassian.fugue.Pair;
import io.atlassian.fugue.optic.Lens;
import io.atlassian.fugue.optic.Optional;
import io.atlassian.fugue.optic.PIso;
import io.atlassian.fugue.optic.Prism;
import io.atlassian.fugue.optic.Setter;
import io.atlassian.fugue.optic.Traversal;
import java.util.function.Function;

public final class Iso<S, A>
extends PIso<S, S, A, A> {
    final PIso<S, S, A, A> pIso;

    public Iso(PIso<S, S, A, A> pIso) {
        this.pIso = pIso;
    }

    @Override
    public A get(S s) {
        return this.pIso.get(s);
    }

    @Override
    public S reverseGet(A a) {
        return this.pIso.reverseGet(a);
    }

    public Iso<A, S> reverse() {
        return new Iso<A, S>(this.pIso.reverse());
    }

    public <S1, A1> Iso<Pair<S, S1>, Pair<A, A1>> product(Iso<S1, A1> other) {
        return new Iso<Pair<S, S1>, Pair<A, A1>>(this.pIso.product(other.pIso));
    }

    public <C> Iso<Pair<S, C>, Pair<A, C>> first() {
        return new Iso(this.pIso.first());
    }

    public <C> Iso<Pair<C, S>, Pair<C, A>> second() {
        return new Iso(this.pIso.second());
    }

    public final <C> Setter<S, C> composeSetter(Setter<A, C> other) {
        return new Setter(this.pIso.composeSetter(other.pSetter));
    }

    public final <C> Traversal<S, C> composeTraversal(Traversal<A, C> other) {
        return new Traversal(this.pIso.composeTraversal(other.pTraversal));
    }

    public final <C> Optional<S, C> composeOptional(Optional<A, C> other) {
        return new Optional(this.pIso.composeOptional(other.pOptional));
    }

    public final <C> Prism<S, C> composePrism(Prism<A, C> other) {
        return new Prism(this.pIso.composePrism(other.pPrism));
    }

    public final <C> Lens<S, C> composeLens(Lens<A, C> other) {
        return ((Lens)this.asLens()).composeLens(other);
    }

    public final <C> Iso<S, C> composeIso(Iso<A, C> other) {
        return new Iso<S, A>(this.pIso.composeIso(other.pIso));
    }

    public final Setter<S, A> asSetter() {
        return new Setter<S, A>(this.pIso.asSetter());
    }

    public final Traversal<S, A> asTraversal() {
        return new Traversal<S, A>(this.pIso.asTraversal());
    }

    public final Optional<S, A> asOptional() {
        return new Optional<S, A>(this.pIso.asOptional());
    }

    public final Prism<S, A> asPrism() {
        return new Prism<S, A>(this.pIso.asPrism());
    }

    public final Lens<S, A> asLens() {
        return new Lens<S, A>(this.pIso.asLens());
    }

    public static <S, A> Iso<S, A> iso(Function<S, A> get, Function<A, S> reverseGet) {
        return new Iso<S, A>(PIso.pIso(get, reverseGet));
    }

    public static <S> Iso<S, S> id() {
        return new Iso(PIso.pId());
    }
}

