/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 */
package com.atlassian.fugue;

import com.atlassian.fugue.Function2;
import com.atlassian.fugue.Functions;
import com.atlassian.fugue.Option;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class Options {
    private Options() {
        throw new UnsupportedOperationException("This class is not instantiable.");
    }

    public static <A> Option<A> find(Iterable<Option<A>> options) {
        for (Option<A> option : options) {
            if (!option.isDefined()) continue;
            return option;
        }
        return Option.none();
    }

    public static <A> Iterable<Option<A>> filterNone(Iterable<Option<A>> options) {
        return Iterables.filter(options, Option.defined());
    }

    public static <A> Iterable<A> flatten(Iterable<Option<A>> options) {
        return Iterables.transform(Options.filterNone(options), new SomeAccessor());
    }

    public static <AA, A extends AA> Option<AA> upcast(Option<A> o) {
        return o.map(Functions.identity());
    }

    public static <A, B> Function<Option<A>, Option<B>> lift(final Function<A, B> f) {
        Preconditions.checkNotNull(f);
        return new Function<Option<A>, Option<B>>(){

            public Option<B> apply(Option<A> oa) {
                return oa.map(f);
            }
        };
    }

    public static <A, B> Function<Function<A, B>, Function<Option<A>, Option<B>>> lift() {
        return new Function<Function<A, B>, Function<Option<A>, Option<B>>>(){

            public Function<Option<A>, Option<B>> apply(Function<A, B> f) {
                return Options.lift(f);
            }
        };
    }

    public static <A> Predicate<Option<A>> lift(final Predicate<? super A> pred) {
        Preconditions.checkNotNull(pred);
        return new Predicate<Option<A>>(){

            public boolean apply(Option<A> oa) {
                return oa.exists(pred);
            }
        };
    }

    public static <A, B> Option<B> ap(Option<A> oa, Option<Function<A, B>> of) {
        return of.fold(Option.noneSupplier(), com.google.common.base.Functions.compose(Functions.apply(oa), Options.lift()));
    }

    public static <A, B, C> Function2<Option<A>, Option<B>, Option<C>> lift2(Function2<A, B, C> f2) {
        Function<A, Function<B, C>> curried = Functions.curried(f2);
        final Function<Option<A>, Option<Function<B, C>>> lifted = Options.lift(curried);
        return new Function2<Option<A>, Option<B>, Option<C>>(){

            @Override
            public Option<C> apply(Option<A> oa, Option<B> ob) {
                Option ofbc = (Option)lifted.apply(oa);
                return Options.ap(ob, ofbc);
            }
        };
    }

    public static <A, B, C> Function<Function2<A, B, C>, Function2<Option<A>, Option<B>, Option<C>>> lift2() {
        return new Function<Function2<A, B, C>, Function2<Option<A>, Option<B>, Option<C>>>(){

            public Function2<Option<A>, Option<B>, Option<C>> apply(Function2<A, B, C> f2) {
                return Options.lift2(f2);
            }
        };
    }

    static class SomeAccessor<A>
    implements Function<Option<A>, A> {
        SomeAccessor() {
        }

        public A apply(Option<A> from) {
            return from.get();
        }
    }
}

