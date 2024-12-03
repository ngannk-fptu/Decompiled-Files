/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Either
 *  io.atlassian.fugue.Eithers
 *  io.atlassian.fugue.Iterables
 *  io.atlassian.fugue.Option
 *  io.atlassian.fugue.Options
 *  io.atlassian.fugue.Pair
 *  io.atlassian.fugue.Suppliers
 *  io.atlassian.fugue.law.IsEq
 */
package io.atlassian.fugue.optic.law;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Eithers;
import io.atlassian.fugue.Iterables;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Options;
import io.atlassian.fugue.Pair;
import io.atlassian.fugue.Suppliers;
import io.atlassian.fugue.law.IsEq;
import io.atlassian.fugue.optic.PTraversal;
import java.util.Collections;
import java.util.List;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class TraversalLaws<S, A> {
    private final PTraversal<S, S, A, A> traversal;

    public TraversalLaws(PTraversal<S, S, A, A> traversal) {
        this.traversal = traversal;
    }

    public IsEq<List<A>> setGetAll(S s, A a) {
        return IsEq.isEq(StreamSupport.stream(this.traversal.getAll(this.traversal.set(a).apply(s)).spliterator(), false).collect(Collectors.toList()), StreamSupport.stream(Iterables.map(this.traversal.getAll(s), __ -> a).spliterator(), false).collect(Collectors.toList()));
    }

    public IsEq<S> setIdempotent(S s, A a) {
        return IsEq.isEq(this.traversal.set(a).apply(this.traversal.set(a).apply(s)), this.traversal.set(a).apply(s));
    }

    public IsEq<S> modifyIdentity(S s) {
        return IsEq.isEq(this.traversal.modify(Function.identity()).apply(s), s);
    }

    public IsEq<S> modifySupplierFPoint(S s) {
        return IsEq.isEq(this.traversal.modifySupplierF(Suppliers::ofInstance).apply(s).get(), s);
    }

    public IsEq<Either<String, S>> modifyEitherFPoint(S s) {
        return IsEq.isEq(this.traversal.modifyEitherF(Eithers.toRight()).apply(s), (Object)Either.right(s));
    }

    public IsEq<Option<S>> modifyOptionFPoint(S s) {
        return IsEq.isEq(this.traversal.modifyOptionF(Options.toOption()).apply(s), (Object)Option.some(s));
    }

    public IsEq<Pair<S, S>> modifyPairFPoint(S s) {
        return IsEq.isEq(this.traversal.modifyPairF(a -> Pair.pair((Object)a, (Object)a)).apply(s), (Object)Pair.pair(s, s));
    }

    public IsEq<S> modifyFunctionFPoint(S s) {
        return IsEq.isEq(this.traversal.modifyFunctionF(a -> __ -> a).apply(s).apply(""), s);
    }

    public IsEq<List<S>> modifyIterableFPoint(S s) {
        return IsEq.isEq(StreamSupport.stream(Spliterators.spliteratorUnknownSize(this.traversal.modifyIterableF(Collections::singleton).apply(s).iterator(), 16), false).collect(Collectors.toList()), Collections.singletonList(s));
    }

    public IsEq<Option<A>> headOption(S s) {
        return IsEq.isEq(this.traversal.headOption(s), (Object)Iterables.first(this.traversal.getAll(s)));
    }
}

