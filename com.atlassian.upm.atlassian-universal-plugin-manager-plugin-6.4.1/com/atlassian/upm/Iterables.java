/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm;

import com.atlassian.upm.api.util.Option;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Iterables {
    public static <T> Option<T> findOption(Iterable<T> ts, Predicate<T> p) {
        return Option.option(Iterables.toStream(ts).filter(p).findFirst().orElse(null));
    }

    public static <T> boolean none(Iterable<T> ts, Predicate<T> p) {
        return !Iterables.findOption(ts, p).isDefined();
    }

    public static <T> Option<T> toOption(Iterable<T> ts) {
        return Option.option(Iterables.toStream(ts).findFirst().orElse(null));
    }

    public static <T> Stream<T> toStream(Iterable<T> ts) {
        return StreamSupport.stream(ts.spliterator(), false);
    }

    public static <T> List<T> toList(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }

    public static <T> T getOnlyElement(Iterable<T> iterable) {
        Iterator<T> iterator = iterable.iterator();
        T first = iterator.next();
        if (!iterator.hasNext()) {
            return first;
        }
        StringBuilder sb = new StringBuilder().append("expected one element but was: <").append(first);
        for (int i = 0; i < 4 && iterator.hasNext(); ++i) {
            sb.append(", ").append(iterator.next());
        }
        if (iterator.hasNext()) {
            sb.append(", ...");
        }
        sb.append('>');
        throw new IllegalArgumentException(sb.toString());
    }
}

