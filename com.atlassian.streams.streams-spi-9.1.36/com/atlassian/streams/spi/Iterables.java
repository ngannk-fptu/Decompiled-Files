/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.collect.AbstractIterator
 *  com.google.common.collect.Iterables
 */
package com.atlassian.streams.spi;

import com.atlassian.streams.spi.Evictor;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import java.util.Iterator;

public final class Iterables {
    public static <T> Iterable<T> filterOrEvict(final Evictor<? super T> evictor, Iterable<T> unfiltered, Predicate<? super T> predicate) {
        Preconditions.checkNotNull(unfiltered);
        Preconditions.checkNotNull(predicate);
        Preconditions.checkNotNull(evictor);
        return new FilteringIterable<T>(unfiltered, predicate, new Function<T, Void>(){

            public Void apply(T instance) {
                evictor.apply(instance);
                return null;
            }
        });
    }

    private static class FilteringIterable<T>
    implements Iterable<T> {
        private final Iterable<T> unfiltered;
        private final Predicate<? super T> predicate;
        private final Function<T, Void> filterFail;

        public FilteringIterable(Iterable<T> unfiltered, Predicate<? super T> predicate, Function<T, Void> filterFail) {
            this.unfiltered = unfiltered;
            this.predicate = predicate;
            this.filterFail = filterFail;
        }

        public String toString() {
            return com.google.common.collect.Iterables.toString((Iterable)this);
        }

        @Override
        public Iterator<T> iterator() {
            final Iterator<T> unfilteredIterator = this.unfiltered.iterator();
            return new AbstractIterator<T>(){

                protected T computeNext() {
                    while (unfilteredIterator.hasNext()) {
                        Object element = unfilteredIterator.next();
                        if (predicate.apply(element)) {
                            return element;
                        }
                        filterFail.apply(element);
                    }
                    return this.endOfData();
                }
            };
        }
    }
}

