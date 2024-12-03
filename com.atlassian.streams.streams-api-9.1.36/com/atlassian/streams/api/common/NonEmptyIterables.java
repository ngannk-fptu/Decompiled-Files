/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 */
package com.atlassian.streams.api.common;

import com.atlassian.streams.api.common.NonEmptyIterable;
import com.atlassian.streams.api.common.Option;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.Iterator;

public class NonEmptyIterables {
    private NonEmptyIterables() {
        throw new UnsupportedOperationException("Cannot be instantiated");
    }

    public static <A> A first(NonEmptyIterable<A> as) {
        return (A)as.iterator().next();
    }

    public static <A> Option<NonEmptyIterable<A>> from(Iterable<A> it) {
        if (!Iterables.isEmpty(it)) {
            return Option.some(new NonEmptyForwardingIterable<A>(it));
        }
        return Option.none();
    }

    private static final class NonEmptyForwardingIterable<T>
    implements NonEmptyIterable<T> {
        private final Iterable<T> delegate;

        NonEmptyForwardingIterable(Iterable<T> delegate) {
            Preconditions.checkArgument((!Iterables.isEmpty(delegate) ? 1 : 0) != 0, (Object)"empty delegate");
            this.delegate = delegate;
        }

        @Override
        public Iterator<T> iterator() {
            return this.delegate.iterator();
        }
    }
}

