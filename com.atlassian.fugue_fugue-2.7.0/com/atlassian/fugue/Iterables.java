/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.concurrent.LazyReference
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.base.Supplier
 *  com.google.common.collect.AbstractIterator
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Ordering
 *  com.google.common.collect.PeekingIterator
 *  com.google.common.collect.Sets
 *  com.google.common.collect.UnmodifiableIterator
 */
package com.atlassian.fugue;

import com.atlassian.fugue.Function2;
import com.atlassian.fugue.Functions;
import com.atlassian.fugue.Option;
import com.atlassian.fugue.Pair;
import com.atlassian.fugue.Suppliers;
import com.atlassian.util.concurrent.LazyReference;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Ordering;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeSet;

public class Iterables {
    static final Iterable<?> EMPTY = new Iterable<Object>(){

        @Override
        public Iterator<Object> iterator() {
            return Collections.emptyIterator();
        }

        public String toString() {
            return "[]";
        }
    };

    private Iterables() {
        throw new UnsupportedOperationException("This class is not instantiable.");
    }

    public static <T> Iterable<T> emptyIterable() {
        Iterable<?> result = EMPTY;
        return result;
    }

    public static <A> Iterable<A> iterable(A ... as) {
        return Collections.unmodifiableCollection(Arrays.asList(as));
    }

    public static <T> Option<T> findFirst(Iterable<? extends T> elements, Predicate<? super T> predicate) {
        Iterator iterator = com.google.common.collect.Iterables.filter(elements, predicate).iterator();
        if (iterator.hasNext()) {
            Object t = iterator.next();
            return Option.some(t);
        }
        return Option.none();
    }

    public static <A> Function<Iterable<A>, Option<A>> findFirst(final Predicate<? super A> predicate) {
        return new Function<Iterable<A>, Option<A>>(){

            public Option<A> apply(Iterable<A> input) {
                return Iterables.findFirst(input, predicate);
            }
        };
    }

    public static <A> Option<A> first(Iterable<A> as) {
        Iterator<A> iterator = as.iterator();
        if (iterator.hasNext()) {
            A a = iterator.next();
            return Option.some(a);
        }
        return Option.none();
    }

    public static <A, B> Iterable<B> flatMap(Iterable<A> collection, Function<? super A, ? extends Iterable<? extends B>> f) {
        return com.google.common.collect.Iterables.concat((Iterable)com.google.common.collect.Iterables.transform(collection, f));
    }

    public static <A, B> Iterable<B> revMap(Iterable<? extends Function<A, B>> fs, A arg) {
        return com.google.common.collect.Iterables.transform(fs, Functions.apply(arg));
    }

    public static Predicate<Iterable<?>> isEmpty() {
        return new Predicate<Iterable<?>>(){

            public boolean apply(Iterable<?> i) {
                return com.google.common.collect.Iterables.isEmpty(i);
            }
        };
    }

    public static <A, B> Iterable<B> collect(Iterable<? extends A> from, Function<? super A, Option<B>> partial) {
        return new CollectingIterable<A, B>(from, partial);
    }

    public static <A extends Comparable<A>> Iterable<A> mergeSorted(Iterable<? extends Iterable<A>> xss) {
        return Iterables.mergeSorted(xss, Ordering.natural());
    }

    public static <A> Pair<Iterable<A>, Iterable<A>> partition(Iterable<A> iterable, Predicate<? super A> predicate) {
        return Pair.pair(com.google.common.collect.Iterables.filter(iterable, predicate), com.google.common.collect.Iterables.filter(iterable, (Predicate)Predicates.not(predicate)));
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

    public static <A> Iterable<A> iterate(Function<? super A, ? extends A> f, A start) {
        return new IteratingIterable(f, start);
    }

    public static <A, B> Iterable<A> unfold(Function<? super B, Option<Pair<A, B>>> f, B seed) {
        return new UnfoldingIterable(f, seed);
    }

    public static <A> Iterable<A> mergeSorted(Iterable<? extends Iterable<A>> xss, Ordering<A> ordering) {
        return new MergeSortedIterable<A>(xss, ordering);
    }

    public static <A> Iterable<A> memoize(Iterable<A> xs) {
        return new Memoizer<A>(xs);
    }

    public static <A, B> Iterable<Pair<A, B>> zip(Iterable<A> as, Iterable<B> bs) {
        return Iterables.zipWith(Pair.pairs()).apply(as, bs);
    }

    public static <A, B, C> Function2<Iterable<A>, Iterable<B>, Iterable<C>> zipWith(final Function2<A, B, C> f) {
        return new Function2<Iterable<A>, Iterable<B>, Iterable<C>>(){

            @Override
            public Iterable<C> apply(Iterable<A> as, Iterable<B> bs) {
                return new Zipper(as, bs, f);
            }
        };
    }

    public static <A> Iterable<Pair<A, Integer>> zipWithIndex(Iterable<A> as) {
        return Iterables.zip(as, Iterables.rangeTo(0, Integer.MAX_VALUE));
    }

    public static <A, B> Pair<Iterable<A>, Iterable<B>> unzip(Iterable<Pair<A, B>> pairs) {
        return Pair.pair(com.google.common.collect.Iterables.transform(pairs, Pair.leftValue()), com.google.common.collect.Iterables.transform(pairs, Pair.rightValue()));
    }

    public static Iterable<Integer> rangeUntil(int start, int end) {
        return Iterables.rangeUntil(start, end, start > end ? -1 : 1);
    }

    public static Iterable<Integer> rangeUntil(int start, int end, int step) {
        Preconditions.checkArgument((step != 0 ? 1 : 0) != 0, (Object)"Step must not be zero");
        return Iterables.rangeTo(start, end - Math.abs(step) / step, step);
    }

    public static Iterable<Integer> rangeTo(int start, int end) {
        return Iterables.rangeTo(start, end, start > end ? -1 : 1);
    }

    public static Iterable<Integer> rangeTo(final int start, final int end, final int step) {
        Preconditions.checkArgument((step != 0 ? 1 : 0) != 0, (Object)"Step must not be zero");
        if (step > 0) {
            Preconditions.checkArgument((start <= end ? 1 : 0) != 0, (String)"Start %s must not be greater than end %s with step %s", (Object[])new Object[]{start, end, step});
        } else {
            Preconditions.checkArgument((start >= end ? 1 : 0) != 0, (String)"Start %s must not be less than end %s with step %s", (Object[])new Object[]{start, end, step});
        }
        return new Iterable<Integer>(){

            @Override
            public Iterator<Integer> iterator() {
                return new UnmodifiableIterator<Integer>(){
                    private int i;
                    {
                        this.i = start;
                    }

                    public boolean hasNext() {
                        return step > 0 ? this.i <= end : this.i >= end;
                    }

                    public Integer next() {
                        try {
                            Integer n = this.i;
                            return n;
                        }
                        finally {
                            this.i += step;
                        }
                    }
                };
            }
        };
    }

    public static <A> Iterable<A> intersperse(Iterable<? extends A> as, A a) {
        return Iterables.intersperse(as, Suppliers.ofInstance(a));
    }

    public static <A> Iterable<A> intersperse(Iterable<? extends A> as, Supplier<A> a) {
        return new Intersperse<A>(as, a);
    }

    static <A> int size(Iterable<A> as) {
        return com.google.common.collect.Iterables.size(as);
    }

    static final class Intersperse<A>
    implements Iterable<A> {
        private final Iterable<? extends A> as;
        private final Supplier<A> a;

        Intersperse(Iterable<? extends A> as, Supplier<A> a) {
            this.as = as;
            this.a = a;
        }

        @Override
        public Iterator<A> iterator() {
            return new AbstractIterator<A>(){
                private final Iterator<? extends A> it;
                private boolean inter;
                {
                    this.it = Intersperse.this.as.iterator();
                    this.inter = false;
                }

                protected A computeNext() {
                    Object object;
                    if (!this.it.hasNext()) {
                        return this.endOfData();
                    }
                    try {
                        object = this.inter ? Intersperse.this.a.get() : this.it.next();
                        this.inter = !this.inter;
                    }
                    catch (Throwable throwable) {
                        this.inter = !this.inter;
                        throw throwable;
                    }
                    return object;
                }
            };
        }
    }

    static class Zipper<A, B, C>
    extends IterableToString<C> {
        private final Iterable<A> as;
        private final Iterable<B> bs;
        private final Function2<A, B, C> f;

        Zipper(Iterable<A> as, Iterable<B> bs, Function2<A, B, C> f) {
            this.as = (Iterable)Preconditions.checkNotNull(as, (Object)"as must not be null.");
            this.bs = (Iterable)Preconditions.checkNotNull(bs, (Object)"bs must not be null.");
            this.f = (Function2)Preconditions.checkNotNull(f, (Object)"f must not be null.");
        }

        @Override
        public Iterator<C> iterator() {
            return new Iter();
        }

        class Iter
        implements Iterator<C> {
            private final Iterator<A> a;
            private final Iterator<B> b;

            Iter() {
                this.a = (Iterator)Preconditions.checkNotNull(Zipper.this.as.iterator(), (Object)"as iterator must not be null.");
                this.b = (Iterator)Preconditions.checkNotNull(Zipper.this.bs.iterator(), (Object)"bs iterator must not be null.");
            }

            @Override
            public boolean hasNext() {
                return this.a.hasNext() && this.b.hasNext();
            }

            @Override
            public C next() {
                return Zipper.this.f.apply(this.a.next(), this.b.next());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
    }

    static final class Memoizer<A>
    extends IterableToString<A> {
        private final Node<A> head;

        Memoizer(Iterable<A> delegate) {
            this.head = Memoizer.nextNode(delegate.iterator());
        }

        @Override
        public Iterator<A> iterator() {
            return new Iter<A>(this.head);
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

    static class CollectingIterable<A, B>
    extends IterableToString<B> {
        private final Iterable<? extends A> delegate;
        private final Function<? super A, Option<B>> partial;

        CollectingIterable(Iterable<? extends A> delegate, Function<? super A, Option<B>> partial) {
            this.delegate = (Iterable)Preconditions.checkNotNull(delegate);
            this.partial = (Function)Preconditions.checkNotNull(partial);
        }

        @Override
        public Iterator<B> iterator() {
            return new Iter();
        }

        final class Iter
        extends AbstractIterator<B> {
            private final Iterator<? extends A> it;

            Iter() {
                this.it = CollectingIterable.this.delegate.iterator();
            }

            protected B computeNext() {
                while (this.it.hasNext()) {
                    Option result = (Option)CollectingIterable.this.partial.apply(this.it.next());
                    if (!result.isDefined()) continue;
                    return result.get();
                }
                return this.endOfData();
            }
        }
    }

    static final class MergeSortedIterable<A>
    extends IterableToString<A> {
        private final Iterable<? extends Iterable<A>> xss;
        private final Ordering<A> ordering;

        MergeSortedIterable(Iterable<? extends Iterable<A>> xss, Ordering<A> ordering) {
            this.xss = (Iterable)Preconditions.checkNotNull(xss, (Object)"xss");
            this.ordering = (Ordering)Preconditions.checkNotNull(ordering, (Object)"ordering");
        }

        @Override
        public Iterator<A> iterator() {
            return new Iter(this.xss, this.ordering);
        }

        private static final class Iter<A>
        extends AbstractIterator<A> {
            private final TreeSet<PeekingIterator<A>> xss;

            private Iter(Iterable<? extends Iterable<A>> xss, Ordering<A> ordering) {
                this.xss = Sets.newTreeSet(this.peekingIteratorOrdering(ordering));
                com.google.common.collect.Iterables.addAll(this.xss, (Iterable)com.google.common.collect.Iterables.transform((Iterable)com.google.common.collect.Iterables.filter(xss, (Predicate)Predicates.not(Iterables.isEmpty())), this.peekingIterator()));
            }

            protected A computeNext() {
                Option<PeekingIterator<A>> currFirstOption = Iterables.first(this.xss);
                if (!currFirstOption.isDefined()) {
                    return (A)this.endOfData();
                }
                PeekingIterator currFirst = (PeekingIterator)currFirstOption.get();
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

            private Ordering<? super PeekingIterator<A>> peekingIteratorOrdering(final Ordering<A> ordering) {
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

    static final class UnfoldingIterable<A, B>
    extends IterableToString<A> {
        private final Function<? super B, Option<Pair<A, B>>> f;
        private final B seed;

        private UnfoldingIterable(Function<? super B, Option<Pair<A, B>>> f, B seed) {
            this.f = (Function)Preconditions.checkNotNull(f);
            this.seed = seed;
        }

        @Override
        public Iterator<A> iterator() {
            return new Iter<A, B>(this.f, this.seed);
        }

        static final class Iter<A, B>
        extends AbstractIterator<A> {
            private final Function<? super B, Option<Pair<A, B>>> f;
            private B current;

            Iter(Function<? super B, Option<Pair<A, B>>> f, B seed) {
                this.f = f;
                this.current = seed;
            }

            protected A computeNext() {
                Option option = (Option)this.f.apply(this.current);
                if (option.isDefined()) {
                    Pair pair = (Pair)option.get();
                    this.current = pair.right();
                    return pair.left();
                }
                return (A)this.endOfData();
            }
        }
    }

    static final class IteratingIterable<A>
    implements Iterable<A> {
        private final Function<? super A, ? extends A> f;
        private final A start;

        private IteratingIterable(Function<? super A, ? extends A> f, A start) {
            this.f = (Function)Preconditions.checkNotNull(f);
            this.start = start;
        }

        @Override
        public Iterator<A> iterator() {
            return new Iter<A>(this.f, this.start);
        }

        static final class Iter<A>
        extends UnmodifiableIterator<A> {
            private final Function<? super A, ? extends A> f;
            private A current;

            Iter(Function<? super A, ? extends A> f, A start) {
                this.f = f;
                this.current = start;
            }

            public boolean hasNext() {
                return true;
            }

            public A next() {
                A value = this.current;
                this.current = this.f.apply(this.current);
                return value;
            }
        }
    }

    static final class Range<A>
    extends IterableToString<A> {
        private final Iterable<A> delegate;
        private final int drop;
        private final int size;

        private Range(int drop, int size, Iterable<A> delegate) {
            this.delegate = (Iterable)Preconditions.checkNotNull(delegate);
            this.drop = drop;
            this.size = size;
        }

        @Override
        public Iterator<A> iterator() {
            return new Iter<A>(this.drop, this.size, this.delegate.iterator());
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

    static abstract class IterableToString<A>
    implements Iterable<A> {
        IterableToString() {
        }

        public final String toString() {
            return com.google.common.collect.Iterables.toString((Iterable)this);
        }
    }
}

