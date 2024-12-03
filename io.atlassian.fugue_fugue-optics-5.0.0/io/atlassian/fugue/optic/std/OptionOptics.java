/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Either
 *  io.atlassian.fugue.Option
 */
package io.atlassian.fugue.optic.std;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.optic.Iso;
import io.atlassian.fugue.optic.PPrism;
import io.atlassian.fugue.optic.Prism;
import java.util.Optional;

public class OptionOptics {
    private OptionOptics() {
    }

    public static <A, B> PPrism<Option<A>, Option<B>, A, B> pSome() {
        return PPrism.pPrism(oa -> (Either)oa.fold(() -> Either.left((Object)Option.none()), Either::right), Option::some);
    }

    public static <A> Prism<Option<A>, A> some() {
        return new Prism(OptionOptics.pSome());
    }

    public static <A> Iso<Option<A>, Optional<A>> optionToOptional() {
        return Iso.iso(Option::toOptional, Option::fromOptional);
    }
}

