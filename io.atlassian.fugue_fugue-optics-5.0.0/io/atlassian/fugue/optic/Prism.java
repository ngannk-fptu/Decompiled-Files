/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Either
 *  io.atlassian.fugue.Eithers
 *  io.atlassian.fugue.Option
 */
package io.atlassian.fugue.optic;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Eithers;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.optic.Iso;
import io.atlassian.fugue.optic.Lens;
import io.atlassian.fugue.optic.Optional;
import io.atlassian.fugue.optic.PPrism;
import io.atlassian.fugue.optic.Setter;
import io.atlassian.fugue.optic.Traversal;
import java.util.function.Function;

public final class Prism<S, A>
extends PPrism<S, S, A, A> {
    final PPrism<S, S, A, A> pPrism;

    public Prism(PPrism<S, S, A, A> pPrism) {
        this.pPrism = pPrism;
    }

    @Override
    public Either<S, A> getOrModify(S s) {
        return this.pPrism.getOrModify(s);
    }

    @Override
    public S reverseGet(A a) {
        return this.pPrism.reverseGet(a);
    }

    @Override
    public Option<A> getOption(S s) {
        return this.pPrism.getOption(s);
    }

    public final <C, D> Setter<S, C> composeSetter(Setter<A, C> other) {
        return new Setter(this.pPrism.composeSetter(other.pSetter));
    }

    public final <C, D> Traversal<S, C> composeTraversal(Traversal<A, C> other) {
        return new Traversal(this.pPrism.composeTraversal(other.pTraversal));
    }

    public final <C, D> Optional<S, C> composeOptional(Optional<A, C> other) {
        return new Optional(this.pPrism.composeOptional(other.pOptional));
    }

    public final <C, D> Optional<S, C> composeLens(Lens<A, C> other) {
        return new Optional(this.pPrism.composeLens(other.pLens));
    }

    public final <C> Prism<S, C> composePrism(Prism<A, C> other) {
        return new Prism<S, A>(this.pPrism.composePrism(other.pPrism));
    }

    public final <C, D> Prism<S, C> composeIso(Iso<A, C> other) {
        return new Prism(this.pPrism.composeIso(other.pIso));
    }

    public final Setter<S, A> asSetter() {
        return new Setter<S, A>(this.pPrism.asSetter());
    }

    public final Traversal<S, A> asTraversal() {
        return new Traversal<S, A>(this.pPrism.asTraversal());
    }

    public final Optional<S, A> asOptional() {
        return new Optional<S, A>(this.pPrism.asOptional());
    }

    public static <S> Prism<S, S> id() {
        return new Prism(Prism.pId());
    }

    public static <S, A> Prism<S, A> prism(final Function<S, Option<A>> getOption, final Function<A, S> reverseGet) {
        return new Prism<S, A>(new PPrism<S, S, A, A>(){

            @Override
            public Either<S, A> getOrModify(S s) {
                return (Either)((Option)getOption.apply(s)).fold(() -> Either.left((Object)s), Eithers.toRight());
            }

            @Override
            public S reverseGet(A a) {
                return reverseGet.apply(a);
            }

            @Override
            public Option<A> getOption(S s) {
                return (Option)getOption.apply(s);
            }
        });
    }
}

