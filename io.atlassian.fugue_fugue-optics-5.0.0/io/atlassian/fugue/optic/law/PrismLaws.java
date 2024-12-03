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
import io.atlassian.fugue.optic.PPrism;
import java.util.Collections;
import java.util.List;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class PrismLaws<S, A> {
    private final PPrism<S, S, A, A> prism;

    public PrismLaws(PPrism<S, S, A, A> prism) {
        this.prism = prism;
    }

    public IsEq<S> partialRoundTripOneWay(S s) {
        return IsEq.isEq((Object)this.prism.getOrModify(s).fold(Function.identity(), this.prism::reverseGet), s);
    }

    public IsEq<Option<A>> roundTripOtherWay(A a) {
        return IsEq.isEq(this.prism.getOption(this.prism.reverseGet(a)), (Object)Option.some(a));
    }

    public IsEq<S> modifyIdentity(S s) {
        return IsEq.isEq(this.prism.modify(Function.identity()).apply(s), s);
    }

    public IsEq<S> modifySupplierFPoint(S s) {
        return IsEq.isEq(this.prism.modifySupplierF(Suppliers::ofInstance).apply(s).get(), s);
    }

    public IsEq<Either<String, S>> modifyEitherFPoint(S s) {
        return IsEq.isEq(this.prism.modifyEitherF(Eithers.toRight()).apply(s), (Object)Either.right(s));
    }

    public IsEq<Option<S>> modifyOptionFPoint(S s) {
        return IsEq.isEq(this.prism.modifyOptionF(Options.toOption()).apply(s), (Object)Option.some(s));
    }

    public IsEq<Pair<S, S>> modifyPairFPoint(S s) {
        return IsEq.isEq(this.prism.modifyPairF(a -> Pair.pair((Object)a, (Object)a)).apply(s), (Object)Pair.pair(s, s));
    }

    public IsEq<S> modifyFunctionFPoint(S s) {
        return IsEq.isEq(this.prism.modifyFunctionF(a -> __ -> a).apply(s).apply(""), s);
    }

    public IsEq<List<S>> modifyIterableFPoint(S s) {
        return IsEq.isEq(StreamSupport.stream(Spliterators.spliteratorUnknownSize(this.prism.modifyIterableF(Collections::singleton).apply(s).iterator(), 16), false).collect(Collectors.toList()), Collections.singletonList(s));
    }

    public IsEq<Option<S>> setOption(S s, A a) {
        return IsEq.isEq(this.prism.setOption(a).apply(s), (Object)this.prism.getOption(s).map(__ -> this.prism.set(a).apply(s)));
    }

    public IsEq<Option<S>> modifyOptionIdentity(S s) {
        return IsEq.isEq(this.prism.modifyOption(Function.identity()).apply(s), (Object)this.prism.getOption(s).map(__ -> s));
    }
}

