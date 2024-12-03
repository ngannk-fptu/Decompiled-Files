/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Either
 *  io.atlassian.fugue.Eithers
 *  io.atlassian.fugue.Option
 *  io.atlassian.fugue.Options
 *  io.atlassian.fugue.Pair
 *  io.atlassian.fugue.Suppliers
 *  io.atlassian.fugue.law.IsEq
 */
package io.atlassian.fugue.optic.law;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Eithers;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Options;
import io.atlassian.fugue.Pair;
import io.atlassian.fugue.Suppliers;
import io.atlassian.fugue.law.IsEq;
import io.atlassian.fugue.optic.PIso;
import java.util.Collections;
import java.util.List;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class IsoLaws<S, A> {
    private final PIso<S, S, A, A> iso;

    public IsoLaws(PIso<S, S, A, A> iso) {
        this.iso = iso;
    }

    public IsEq<S> roundTripOneWay(S s) {
        return IsEq.isEq(this.iso.reverseGet(this.iso.get(s)), s);
    }

    public IsEq<A> roundTripOtherWay(A a) {
        return IsEq.isEq(this.iso.get(this.iso.reverseGet(a)), a);
    }

    public IsEq<S> set(S s, A a) {
        return IsEq.isEq(this.iso.set(a).apply(s), this.iso.reverseGet(a));
    }

    public IsEq<S> modifyIdentity(S s) {
        return IsEq.isEq(this.iso.modify(Function.identity()).apply(s), s);
    }

    public IsEq<S> modifySupplierFPoint(S s) {
        return IsEq.isEq(this.iso.modifySupplierF(Suppliers::ofInstance).apply(s).get(), s);
    }

    public IsEq<Either<String, S>> modifyEitherFPoint(S s) {
        return IsEq.isEq(this.iso.modifyEitherF(Eithers.toRight()).apply(s), (Object)Either.right(s));
    }

    public IsEq<Option<S>> modifyOptionFPoint(S s) {
        return IsEq.isEq(this.iso.modifyOptionF(Options.toOption()).apply(s), (Object)Option.some(s));
    }

    public IsEq<Pair<S, S>> modifyPairFPoint(S s) {
        return IsEq.isEq(this.iso.modifyPairF(a -> Pair.pair((Object)a, (Object)a)).apply(s), (Object)Pair.pair(s, s));
    }

    public IsEq<S> modifyFunctionFPoint(S s) {
        return IsEq.isEq(this.iso.modifyFunctionF(a -> __ -> a).apply(s).apply(""), s);
    }

    public IsEq<List<S>> modifyIterableFPoint(S s) {
        return IsEq.isEq(StreamSupport.stream(Spliterators.spliteratorUnknownSize(this.iso.modifyIterableF(Collections::singleton).apply(s).iterator(), 16), false).collect(Collectors.toList()), Collections.singletonList(s));
    }
}

