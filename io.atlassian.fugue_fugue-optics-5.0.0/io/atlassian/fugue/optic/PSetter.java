/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Either
 */
package io.atlassian.fugue.optic;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.optic.PIso;
import io.atlassian.fugue.optic.PLens;
import io.atlassian.fugue.optic.POptional;
import io.atlassian.fugue.optic.PPrism;
import io.atlassian.fugue.optic.PTraversal;
import java.util.function.Function;

public abstract class PSetter<S, T, A, B> {
    PSetter() {
    }

    public abstract Function<S, T> modify(Function<A, B> var1);

    public abstract Function<S, T> set(B var1);

    public final <S1, T1> PSetter<Either<S, S1>, Either<T, T1>, A, B> sum(PSetter<S1, T1, A, B> other) {
        return PSetter.pSetter(f -> e -> e.bimap(this.modify((Function<A, B>)f), other.modify((Function<A, B>)f)));
    }

    public final <C, D> PSetter<S, T, C, D> composeSetter(final PSetter<A, B, C, D> other) {
        final PSetter self = this;
        return new PSetter<S, T, C, D>(){

            @Override
            public Function<S, T> modify(Function<C, D> f) {
                return self.modify(other.modify(f));
            }

            @Override
            public Function<S, T> set(D d) {
                return self.modify(other.set(d));
            }
        };
    }

    public final <C, D> PSetter<S, T, C, D> composeTraversal(PTraversal<A, B, C, D> other) {
        return this.composeSetter(other.asSetter());
    }

    public final <C, D> PSetter<S, T, C, D> composeOptional(POptional<A, B, C, D> other) {
        return this.composeSetter(other.asSetter());
    }

    public final <C, D> PSetter<S, T, C, D> composePrism(PPrism<A, B, C, D> other) {
        return this.composeSetter(other.asSetter());
    }

    public final <C, D> PSetter<S, T, C, D> composeLens(PLens<A, B, C, D> other) {
        return this.composeSetter(other.asSetter());
    }

    public final <C, D> PSetter<S, T, C, D> composeIso(PIso<A, B, C, D> other) {
        return this.composeSetter(other.asSetter());
    }

    public static <S, T> PSetter<S, T, S, T> pId() {
        return PIso.pId().asSetter();
    }

    public static <S, T> PSetter<Either<S, S>, Either<T, T>, S, T> pCodiagonal() {
        return PSetter.pSetter(f -> e -> e.bimap(f, f));
    }

    public static <S, T, A, B> PSetter<S, T, A, B> pSetter(final Function<Function<A, B>, Function<S, T>> modify) {
        return new PSetter<S, T, A, B>(){

            @Override
            public Function<S, T> modify(Function<A, B> f) {
                return (Function)modify.apply(f);
            }

            @Override
            public Function<S, T> set(B b) {
                return (Function)modify.apply(__ -> b);
            }
        };
    }
}

