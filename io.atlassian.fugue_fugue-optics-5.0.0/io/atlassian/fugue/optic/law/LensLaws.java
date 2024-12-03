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
import io.atlassian.fugue.optic.PLens;
import java.util.Collections;
import java.util.List;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class LensLaws<S, A> {
    private final PLens<S, S, A, A> lens;

    public LensLaws(PLens<S, S, A, A> lens) {
        this.lens = lens;
    }

    public IsEq<S> getSet(S s) {
        return IsEq.isEq(this.lens.set(this.lens.get(s)).apply(s), s);
    }

    public IsEq<A> setGet(S s, A a) {
        return IsEq.isEq(this.lens.get(this.lens.set(a).apply(s)), a);
    }

    public IsEq<S> setIdempotent(S s, A a) {
        return IsEq.isEq(this.lens.set(a).apply(this.lens.set(a).apply(s)), this.lens.set(a).apply(s));
    }

    public IsEq<S> modifyIdentity(S s) {
        return IsEq.isEq(this.lens.modify(Function.identity()).apply(s), s);
    }

    public IsEq<S> modifySupplierFPoint(S s) {
        return IsEq.isEq(this.lens.modifySupplierF(Suppliers::ofInstance).apply(s).get(), s);
    }

    public IsEq<Either<String, S>> modifyEitherFPoint(S s) {
        return IsEq.isEq(this.lens.modifyEitherF(Eithers.toRight()).apply(s), (Object)Either.right(s));
    }

    public IsEq<Option<S>> modifyOptionFPoint(S s) {
        return IsEq.isEq(this.lens.modifyOptionF(Options.toOption()).apply(s), (Object)Option.some(s));
    }

    public IsEq<Pair<S, S>> modifyPairFPoint(S s) {
        return IsEq.isEq(this.lens.modifyPairF(a -> Pair.pair((Object)a, (Object)a)).apply(s), (Object)Pair.pair(s, s));
    }

    public IsEq<S> modifyFunctionFPoint(S s) {
        return IsEq.isEq(this.lens.modifyFunctionF(a -> __ -> a).apply(s).apply(""), s);
    }

    public IsEq<List<S>> modifyIterableFPoint(S s) {
        return IsEq.isEq(StreamSupport.stream(Spliterators.spliteratorUnknownSize(this.lens.modifyIterableF(Collections::singleton).apply(s).iterator(), 16), false).collect(Collectors.toList()), Collections.singletonList(s));
    }
}

