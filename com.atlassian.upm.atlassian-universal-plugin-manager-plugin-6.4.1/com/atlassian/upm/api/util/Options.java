/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 */
package com.atlassian.upm.api.util;

import com.atlassian.upm.api.util.Option;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Deprecated
public final class Options {
    private Options() {
    }

    @Deprecated
    public static <A> List<A> catOptions(Iterable<Option<A>> as) {
        return StreamSupport.stream(as.spliterator(), false).filter(Option::isDefined).map(Option::get).collect(Collectors.toList());
    }

    @Deprecated
    public static <A> Predicate<Option<A>> isDefined() {
        return Option::isDefined;
    }

    @Deprecated
    public static <A, B> Function<A, Option<B>> asNone() {
        return ignored -> Option.none();
    }

    @Deprecated
    public static <A> Function<A, Option<A>> asSome() {
        return Option::some;
    }
}

