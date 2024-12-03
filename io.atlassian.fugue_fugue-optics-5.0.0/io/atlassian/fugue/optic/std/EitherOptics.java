/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Either
 */
package io.atlassian.fugue.optic.std;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.optic.PPrism;
import io.atlassian.fugue.optic.Prism;
import java.util.function.Function;

public final class EitherOptics {
    private EitherOptics() {
    }

    public static <A, B, C> PPrism<Either<A, B>, Either<C, B>, A, C> pLeft() {
        return PPrism.pPrism(ab -> ab.swap().bimap(Either::right, Function.identity()), Either::left);
    }

    public static <A, B> Prism<Either<A, B>, A> left() {
        return Prism.prism(ab -> ab.left().toOption(), Either::left);
    }

    public static <A, B, C> PPrism<Either<A, B>, Either<A, C>, B, C> pRight() {
        return PPrism.pPrism(ab -> ab.bimap(Either::left, Function.identity()), Either::right);
    }

    public static <A, B> Prism<Either<A, B>, B> right() {
        return Prism.prism(ab -> ab.right().toOption(), Either::right);
    }
}

