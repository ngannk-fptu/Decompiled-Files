/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.AbstractIterator
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Ordering
 *  com.google.common.collect.PeekingIterator
 *  com.google.common.collect.Sets
 *  io.atlassian.util.concurrent.LazyReference
 */
package com.atlassian.streams.api.common;

import com.atlassian.streams.api.common.Functions;
import com.atlassian.streams.api.common.Option;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Ordering;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.Sets;
import io.atlassian.util.concurrent.LazyReference;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeSet;

public final class Iterables {
    private Iterables() {
        throw new UnsupportedOperationException("Cannot be instantiated");
    }

    public static <T> Iterable<T> take(int n, Iterable<T> xs) {
        Preconditions.checkArgument((n >= 0 ? 1 : 0) != 0, (Object)"Cannot take a negative number of elements");
        if (xs instanceof List) {
            List list;
            return list.subList(0, n < (list = (List)xs).size() ? n : list.size());
        }
        return new Range(0, n, xs);
    }

    public static <T> Iterable<T> drop(int n, Iterable<T> xs) {
        Preconditions.checkArgument((n >= 0 ? 1 : 0) != 0, (Object)"Cannot drop a negative number of elements");
        if (xs instanceof List) {
            List list = (List)xs;
            if (n > list.size() - 1) {
                return ImmutableList.of();
            }
            return ((List)xs).subList(n, list.size());
        }
        return new Range(n, Integer.MAX_VALUE, xs);
    }

    @Deprecated
    public static <A, B> Iterable<B> flatMap(Iterable<A> collection, Function<A, Iterable<B>> f) {
        return com.google.common.collect.Iterables.concat((Iterable)com.google.common.collect.Iterables.transform(collection, f));
    }

    @Deprecated
    public static <A, B> Iterable<B> revMap(Iterable<? extends Function<A, B>> fs, A a) {
        return com.google.common.collect.Iterables.transform(fs, Functions.apply(a));
    }

    public static <A> Option<A> first(Iterable<A> as) {
        Iterator<A> i = as.iterator();
        if (!i.hasNext()) {
            return Option.none();
        }
        return Option.some(i.next());
    }

    @Deprecated
    public static Predicate<Iterable<?>> isEmpty() {
        return new Predicate<Iterable<?>>(){

            public boolean apply(Iterable<?> i) {
                return com.google.common.collect.Iterables.isEmpty(i);
            }
        };
    }

    public static <A extends Comparable<A>> Iterable<A> mergeSorted(Iterable<? extends Iterable<A>> xss) {
        return Iterables.mergeSorted(xss, Ordering.natural());
    }

    public static <A> Iterable<A> mergeSorted(Iterable<? extends Iterable<A>> xss, Ordering<A> ordering) {
        return new MergeSortedIterable<A>(xss, ordering);
    }

    public static <A> Iterable<A> mergeSorted(Iterable<? extends Iterable<A>> xss, Comparator<A> comparator) {
        return new MergeSortedIterable<A>(xss, comparator);
    }

    public static <A> Iterable<A> memoize(Iterable<A> xs) {
        return new Memoizer<A>(xs);
    }

    static final class Memoizer<A>
    implements Iterable<A> {
        private final Node<A> head;

        Memoizer(Iterable<A> delegate) {
            this.head = Memoizer.nextNode(delegate.iterator());
        }

        @Override
        public Iterator<A> iterator() {
            return new Iter<A>(this.head);
        }

        public String toString() {
            return com.google.common.collect.Iterables.toString((Iterable)this);
        }

        private static <A> Node<A> nextNode(Iterator<A> delegate) {
            return delegate.hasNext() ? new Lazy<A>(delegate) : new End();
        }

        static class Iter<A>
        extends AbstractIterator<A> {
            Node<A> node;

            Iter(Node<A> node) {
                this.node = node;
            }

            protected A computeNext() {
                if (this.node.isEnd()) {
                    return (A)this.endOfData();
                }
                try {
                    A a = this.node.value();
                    return a;
                }
                finally {
                    this.node = this.node.next();
                }
            }
        }

        static class End<A>
        implements Node<A> {
            End() {
            }

            @Override
            public boolean isEnd() {
                return true;
            }

            @Override
            public Node<A> next() {
                throw new NoSuchElementException();
            }

            @Override
            public A value() {
                throw new NoSuchElementException();
            }
        }

        static class Lazy<A>
        extends LazyReference<Node<A>>
        implements Node<A> {
            private final Iterator<A> delegate;
            private final A value;

            Lazy(Iterator<A> delegate) {
                this.delegate = delegate;
                this.value = delegate.next();
            }

            protected Node<A> create() throws Exception {
                return Memoizer.nextNode(this.delegate);
            }

            @Override
            public Node<A> next() throws NoSuchElementException {
                return (Node)this.get();
            }

            @Override
            public boolean isEnd() {
                return false;
            }

            @Override
            public A value() {
                return this.value;
            }
        }

        static interface Node<A> {
            public boolean isEnd();

            public A value();

            public Node<A> next() throws NoSuchElementException;
        }
    }

    private static final class MergeSortedIterable<A>
    implements Iterable<A> {
        private final Iterable<? extends Iterable<A>> xss;
        private final Comparator<A> ordering;

        @Deprecated
        public MergeSortedIterable(Iterable<? extends Iterable<A>> xss, Ordering<A> ordering) {
            this.xss = (Iterable)Preconditions.checkNotNull(xss, (Object)"xss");
            this.ordering = (Comparator)Preconditions.checkNotNull(ordering, (Object)"ordering");
        }

        public MergeSortedIterable(Iterable<? extends Iterable<A>> xss, Comparator<A> ordering) {
            this.xss = (Iterable)Preconditions.checkNotNull(xss, (Object)"xss");
            this.ordering = (Comparator)Preconditions.checkNotNull(ordering, (Object)"ordering");
        }

        @Override
        public Iterator<A> iterator() {
            return new Iter(this.xss, this.ordering);
        }

        public String toString() {
            return com.google.common.collect.Iterables.toString((Iterable)this);
        }

        private static final class Iter<A>
        extends AbstractIterator<A> {
            private final TreeSet<PeekingIterator<A>> xss;

            private Iter(Iterable<? extends Iterable<A>> xss, Comparator<A> ordering) {
                this.xss = Sets.newTreeSet(this.peekingIteratorOrdering(ordering));
                com.google.common.collect.Iterables.addAll(this.xss, (Iterable)com.google.common.collect.Iterables.transform((Iterable)com.google.common.collect.Iterables.filter(xss, (Predicate)Predicates.not(Iterables.isEmpty())), this.peekingIterator()));
            }

            protected A computeNext() {
                Option<PeekingIterator<A>> currFirstOption = Iterables.first(this.xss);
                if (!currFirstOption.isDefined()) {
                    return (A)this.endOfData();
                }
                PeekingIterator<A> currFirst = currFirstOption.get();
                this.xss.remove(currFirst);
                Object next = currFirst.next();
                if (currFirst.hasNext()) {
                    this.xss.add(currFirst);
                }
                return (A)next;
            }

            private Function<? super Iterable<A>, ? extends PeekingIterator<A>> peekingIterator() {
                return new Function<Iterable<A>, PeekingIterator<A>>(){

                    public PeekingIterator<A> apply(Iterable<A> i) {
                        return Iterators.peekingIterator(i.iterator());
                    }
                };
            }

            private Ordering<? super PeekingIterator<A>> peekingIteratorOrdering(final Comparator<A> ordering) {
                return new Ordering<PeekingIterator<A>>(){

                    public int compare(PeekingIterator<A> lhs, PeekingIterator<A> rhs) {
                        if (lhs == rhs) {
                            return 0;
                        }
                        return ordering.compare(lhs.peek(), rhs.peek());
                    }
                };
            }
        }
    }

    static final class Range<T>
    implements Iterable<T> {
        private final Iterable<T> delegate;
        private final int drop;
        private final int size;

        private Range(int drop, int size, Iterable<T> delegate) {
            this.delegate = (Iterable)Preconditions.checkNotNull(delegate);
            this.drop = drop;
            this.size = size;
        }

        @Override
        public Iterator<T> iterator() {
            return new Iter<T>(this.drop, this.size, this.delegate.iterator());
        }

        public String toString() {
            return com.google.common.collect.Iterables.toString((Iterable)this);
        }

        static final class Iter<T>
        extends AbstractIterator<T> {
            private final Iterator<T> it;
            private int remaining;

            Iter(int drop, int size, Iterator<T> it) {
                this.it = it;
                this.remaining = size;
                for (int i = 0; i < drop && it.hasNext(); ++i) {
                    it.next();
                }
            }

            protected T computeNext() {
                if (this.remaining > 0 && this.it.hasNext()) {
                    --this.remaining;
                    return this.it.next();
                }
                return (T)this.endOfData();
            }
        }
    }
}

