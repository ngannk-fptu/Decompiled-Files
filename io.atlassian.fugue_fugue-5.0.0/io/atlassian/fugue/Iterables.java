/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue;

import io.atlassian.fugue.Functions;
import io.atlassian.fugue.Iterators;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Pair;
import io.atlassian.fugue.Suppliers;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.StreamSupport;

public class Iterables {
    static final Iterable<?> EMPTY = new Iterable<Object>(){

        @Override
        public Iterator<Object> iterator() {
            return Iterators.emptyIterator();
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

    @SafeVarargs
    public static <A> Iterable<A> iterable(A ... as) {
        return Collections.unmodifiableCollection(Arrays.asList(as));
    }

    public static <T> Option<T> findFirst(Iterable<? extends T> elements, Predicate<? super T> p) {
        Iterator<T> iterator = Iterables.filter(elements, p).iterator();
        if (iterator.hasNext()) {
            T t = iterator.next();
            return Option.some(t);
        }
        return Option.none();
    }

    public static <A> Function<Iterable<A>, Option<A>> findFirst(Predicate<? super A> predicate) {
        return input -> Iterables.findFirst(input, predicate);
    }

    public static <A> Option<A> first(Iterable<A> as) {
        Iterator<A> iterator = as.iterator();
        if (iterator.hasNext()) {
            A a = iterator.next();
            return Option.some(a);
        }
        return Option.none();
    }

    public static <A, B> Iterable<B> ap(Iterable<A> as, Iterable<Function<A, B>> fs) {
        return Iterables.flatMap(fs, f -> Iterables.map(as, f));
    }

    public static <A, B> Iterable<B> flatMap(Iterable<A> collection, Function<? super A, ? extends Iterable<? extends B>> f) {
        return Iterables.join(Iterables.map(collection, f));
    }

    public static <A, B> Iterable<B> revMap(Iterable<? extends Function<A, B>> fs, A arg) {
        return Iterables.map(fs, Functions.apply(arg));
    }

    public static Predicate<Iterable<?>> isEmpty() {
        return it -> {
            if (it instanceof Collection) {
                return ((Collection)it).isEmpty();
            }
            return !it.iterator().hasNext();
        };
    }

    public static <A, B> Iterable<B> collect(Iterable<? extends A> from, Function<? super A, Option<B>> partial) {
        return new CollectingIterable<A, B>(from, partial);
    }

    public static <T, A, R> R collect(Iterable<T> elements, Collector<T, A, R> collector) {
        Objects.requireNonNull(elements, "elements is null.");
        Objects.requireNonNull(collector, "collector is null.");
        return StreamSupport.stream(elements.spliterator(), false).collect(collector);
    }

    public static <A> Pair<Iterable<A>, Iterable<A>> partition(Iterable<A> iterable, Predicate<? super A> p) {
        return Pair.pair(Iterables.filter(iterable, p), Iterables.filter(iterable, p.negate()));
    }

    public static <A> Iterable<A> take(int n, Iterable<A> as) {
        if (n < 0) {
            throw new IllegalArgumentException("Cannot take a negative number of elements");
        }
        if (as instanceof List) {
            List list;
            return list.subList(0, n < (list = (List)as).size() ? n : list.size());
        }
        return new Take(as, Functions.countingPredicate(n));
    }

    public static <A> Iterable<A> drop(int n, Iterable<A> as) {
        if (n < 0) {
            throw new IllegalArgumentException("Cannot drop a negative number of elements");
        }
        if (as instanceof List) {
            List list = (List)as;
            if (n > list.size() - 1) {
                return Iterables.emptyIterable();
            }
            return list.subList(n, list.size());
        }
        return new Drop(as, Functions.countingPredicate(n));
    }

    public static <A> Iterable<A> dropWhile(Iterable<A> as, Predicate<A> p) {
        return new Drop(as, p);
    }

    public static <A> Iterable<A> takeWhile(Iterable<A> as, Predicate<A> p) {
        return new Take(as, p);
    }

    public static <A, B> Iterable<Pair<A, B>> zip(Iterable<A> as, Iterable<B> bs) {
        return Iterables.zipWith(Pair.pairs()).apply(as, bs);
    }

    public static <A, B, C> BiFunction<Iterable<A>, Iterable<B>, Iterable<C>> zipWith(BiFunction<A, B, C> f) {
        return (as, bs) -> new Zipper(as, bs, f);
    }

    public static <A> Iterable<Pair<A, Integer>> zipWithIndex(Iterable<A> as) {
        return Iterables.zip(as, Iterables.rangeTo(0, Integer.MAX_VALUE));
    }

    public static <A, B> Pair<Iterable<A>, Iterable<B>> unzip(Iterable<Pair<A, B>> pairs) {
        return Pair.pair(Iterables.map(pairs, Pair.leftValue()), Iterables.map(pairs, Pair.rightValue()));
    }

    public static Iterable<Integer> rangeUntil(int start, int end) {
        return Iterables.rangeUntil(start, end, start > end ? -1 : 1);
    }

    public static Iterable<Integer> rangeUntil(int start, int end, int step) {
        if (step == 0) {
            throw new IllegalArgumentException("Step must not be zero");
        }
        return Iterables.rangeTo(start, end - Math.abs(step) / step, step);
    }

    public static Iterable<Integer> rangeTo(int start, int end) {
        return Iterables.rangeTo(start, end, start > end ? -1 : 1);
    }

    public static Iterable<Integer> rangeTo(final int start, final int end, final int step) {
        if (step == 0) {
            throw new IllegalArgumentException("Step must not be zero");
        }
        if (step > 0) {
            if (start > end) {
                throw new IllegalArgumentException(String.format("Start %s must not be greater than end %s with step %s", start, end, step));
            }
        } else if (start < end) {
            throw new IllegalArgumentException(String.format("Start %s must not be less than end %s with step %s", start, end, step));
        }
        return () -> new Iterators.Unmodifiable<Integer>(){
            private int i;
            private boolean reachedMinOrMax;
            {
                this.i = start;
                this.reachedMinOrMax = false;
            }

            @Override
            public boolean hasNext() {
                return (step > 0 ? this.i <= end : this.i >= end) && !this.reachedMinOrMax;
            }

            @Override
            public Integer next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                try {
                    Integer n = this.i;
                    return n;
                }
                finally {
                    int attempt = this.i + step;
                    if (((this.i ^ attempt) & (step ^ attempt)) < 0) {
                        this.reachedMinOrMax = true;
                    }
                    this.i = attempt;
                }
            }
        };
    }

    public static <A> Iterable<A> intersperse(Iterable<? extends A> as, A a) {
        return Iterables.intersperse(as, Suppliers.ofInstance(a));
    }

    public static <A> Iterable<A> intersperse(Iterable<? extends A> as, Supplier<A> a) {
        return new Intersperse<A>(as, a);
    }

    public static <A> int size(Iterable<A> as) {
        if (as instanceof Collection) {
            return ((Collection)as).size();
        }
        Iterator<A> iterator = as.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            ++count;
        }
        return count;
    }

    @Deprecated
    public static <A, B> Iterable<B> transform(Iterable<A> as, Function<? super A, ? extends B> f) {
        return Iterables.map(as, f);
    }

    public static <A, B> Iterable<B> map(Iterable<A> as, Function<? super A, ? extends B> f) {
        return new Mapped<A, B>(as, f);
    }

    public static <A> Iterable<A> filter(Iterable<A> as, Predicate<? super A> p) {
        return new Filter<A>(as, p);
    }

    public static <A> Iterable<A> join(Iterable<? extends Iterable<? extends A>> ias) {
        return new Join(ias);
    }

    @SafeVarargs
    public static <A> Iterable<A> concat(Iterable<? extends A> ... as) {
        return as.length > 0 ? Iterables.join(Arrays.asList(as)) : Iterables.emptyIterable();
    }

    public static <A> boolean any(Iterable<? extends A> as, Predicate<? super A> p) {
        return !Iterables.isEmpty().test(Iterables.filter(as, p));
    }

    public static <A> boolean all(Iterable<? extends A> as, Predicate<? super A> p) {
        return Iterables.isEmpty().test(Iterables.filter(as, p.negate()));
    }

    public static <A> Iterable<A> iterate(Function<? super A, ? extends A> f, A start) {
        return new IteratingIterable(f, start);
    }

    public static <A, B> Iterable<A> unfold(Function<? super B, Option<Pair<A, B>>> f, B seed) {
        return new UnfoldingIterable(f, seed);
    }

    public static <A extends Comparable<A>> Iterable<A> mergeSorted(Iterable<? extends Iterable<A>> xss) {
        return Iterables.mergeSorted(xss, Comparator.naturalOrder());
    }

    public static <A> Iterable<A> mergeSorted(Iterable<? extends Iterable<A>> xss, Comparator<A> ordering) {
        return new MergeSortedIterable<A>(xss, ordering);
    }

    public static <A> boolean addAll(Collection<A> collectionToModify, Iterable<? extends A> elementsToAdd) {
        if (elementsToAdd instanceof Collection) {
            return collectionToModify.addAll((Collection)elementsToAdd);
        }
        return Iterators.addAll(collectionToModify, Objects.requireNonNull(elementsToAdd).iterator());
    }

    @SafeVarargs
    public static <A> Iterable<A> cycle(A ... as) {
        if (as.length > 0) {
            return new Cycle<A>(Arrays.asList(as));
        }
        return Iterables.emptyIterable();
    }

    public static <A> Iterable<A> cycle(Iterable<? extends A> as) {
        return new Cycle<A>(as);
    }

    public static <A> String makeString(Iterable<? extends A> as, String start, String sep, String end, int maxLength) {
        StringBuilder b = new StringBuilder();
        b.append(start);
        Iterator<A> ias = Objects.requireNonNull(as).iterator();
        if (ias.hasNext()) {
            b.append(String.valueOf(ias.next()));
        }
        while (ias.hasNext() && b.length() < maxLength) {
            b.append(sep);
            String value = String.valueOf(ias.next());
            b.append(value);
        }
        if (ias.hasNext()) {
            b.append("...");
        }
        b.append(end);
        return b.toString();
    }

    public static <A> String makeString(Iterable<? extends A> as, String start, String sep, String end) {
        return Iterables.makeString(as, start, sep, end, 100);
    }

    public static <A> Iterable<A> memoize(Iterable<A> xs) {
        return new Memoizer<A>(xs);
    }

    static abstract class LazyReference<T>
    extends WeakReference<T>
    implements Supplier<T> {
        private final Sync sync = new Sync();

        public LazyReference() {
            super(null);
        }

        protected abstract T create() throws Exception;

        @Override
        public final T get() {
            boolean interrupted = false;
            while (true) {
                try {
                    T t = this.getInterruptibly();
                    return t;
                }
                catch (InterruptedException ignore) {
                    interrupted = true;
                    continue;
                }
                break;
            }
            finally {
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        public final T getInterruptibly() throws InterruptedException {
            if (!this.sync.isDone()) {
                this.sync.run();
            }
            try {
                return this.sync.get();
            }
            catch (ExecutionException e) {
                throw new InitializationException(e);
            }
        }

        public final boolean isInitialized() {
            return this.sync.isDone();
        }

        public final void cancel() {
            this.sync.cancel(true);
        }

        private final class Sync
        extends AbstractQueuedSynchronizer {
            static final int IGNORED = 0;
            private static final long serialVersionUID = -1645412544240373524L;
            private T result;
            private Throwable exception;
            private volatile Thread runner;

            private Sync() {
            }

            private boolean ranOrCancelled(int state) {
                return (state & 6) != 0;
            }

            @Override
            protected int tryAcquireShared(int ignore) {
                return this.isDone() ? 1 : -1;
            }

            @Override
            protected boolean tryReleaseShared(int ignore) {
                this.runner = null;
                return true;
            }

            boolean isDone() {
                return this.ranOrCancelled(this.getState()) && this.runner == null;
            }

            T get() throws InterruptedException, ExecutionException {
                this.acquireSharedInterruptibly(0);
                if (this.getState() == 4) {
                    throw new CancellationException();
                }
                if (this.exception != null) {
                    throw new ExecutionException(this.exception);
                }
                return this.result;
            }

            void set(T v) {
                int s;
                do {
                    if ((s = this.getState()) == 2) {
                        return;
                    }
                    if (s != 4) continue;
                    this.releaseShared(0);
                    return;
                } while (!this.compareAndSetState(s, 2));
                this.result = v;
                this.releaseShared(0);
            }

            void setException(Throwable t) {
                int s;
                do {
                    if ((s = this.getState()) == 2) {
                        return;
                    }
                    if (s != 4) continue;
                    this.releaseShared(0);
                    return;
                } while (!this.compareAndSetState(s, 2));
                this.exception = t;
                this.result = null;
                this.releaseShared(0);
            }

            void cancel(boolean mayInterruptIfRunning) {
                Thread r;
                int s;
                do {
                    if (!this.ranOrCancelled(s = this.getState())) continue;
                    return;
                } while (!this.compareAndSetState(s, 4));
                if (mayInterruptIfRunning && (r = this.runner) != null) {
                    r.interrupt();
                }
                this.releaseShared(0);
            }

            void run() {
                if (this.getState() != 0 || !this.compareAndSetState(0, 1)) {
                    if (this.runner == Thread.currentThread()) {
                        throw new IllegalMonitorStateException("Not reentrant!");
                    }
                    return;
                }
                try {
                    this.runner = Thread.currentThread();
                    this.set(LazyReference.this.create());
                }
                catch (Throwable ex) {
                    this.setException(ex);
                }
            }
        }

        static final class State {
            static final int INIT = 0;
            static final int RUNNING = 1;
            static final int RAN = 2;
            static final int CANCELLED = 4;

            State() {
            }
        }

        public static class InitializationException
        extends RuntimeException {
            private static final long serialVersionUID = 3638376010285456759L;

            InitializationException(ExecutionException e) {
                super(e.getCause() != null ? e.getCause() : e);
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
        extends Iterators.Abstract<A> {
            Node<A> node;

            Iter(Node<A> node) {
                this.node = node;
            }

            @Override
            protected A computeNext() {
                if (this.node.isEnd()) {
                    return this.endOfData();
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

            @Override
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

    static final class Cycle<A>
    implements Iterable<A> {
        final Iterable<? extends A> as;

        Cycle(Iterable<? extends A> as) {
            this.as = Objects.requireNonNull(as);
        }

        @Override
        public Iterator<A> iterator() {
            return new Iter<A>(this.as);
        }

        public String toString() {
            return Iterables.makeString(this.as, "[", ", ", "...]", 100);
        }

        static final class Iter<A>
        extends Iterators.Abstract<A> {
            Iterable<? extends A> as;
            Iterator<? extends A> ias;

            Iter(Iterable<? extends A> as) {
                this.as = as;
                this.ias = as.iterator();
            }

            @Override
            protected A computeNext() {
                if (!this.ias.hasNext() && !(this.ias = this.as.iterator()).hasNext()) {
                    return this.endOfData();
                }
                return this.ias.next();
            }
        }
    }

    static final class MergeSortedIterable<A>
    extends IterableToString<A> {
        private final Iterable<? extends Iterable<A>> xss;
        private final Comparator<A> comparator;

        MergeSortedIterable(Iterable<? extends Iterable<A>> xss, Comparator<A> comparator) {
            this.xss = Objects.requireNonNull(xss, "xss");
            this.comparator = Objects.requireNonNull(comparator, "comparator");
        }

        @Override
        public Iterator<A> iterator() {
            return new Iter(this.xss, this.comparator);
        }

        private static final class Iter<A>
        extends Iterators.Abstract<A> {
            private final TreeSet<Iterators.Peeking<A>> xss;

            private Iter(Iterable<? extends Iterable<A>> xss, Comparator<A> c) {
                this.xss = new TreeSet<Iterators.Peeking<A>>(this.peekingIteratorComparator(c));
                Iterables.addAll(this.xss, Iterables.map(Iterables.filter(xss, Iterables.isEmpty().negate()), i -> Iterators.peekingIterator(i.iterator())));
            }

            @Override
            protected A computeNext() {
                Option<Iterators.Peeking<A>> currFirstOption = Iterables.first(this.xss);
                if (!currFirstOption.isDefined()) {
                    return this.endOfData();
                }
                Iterators.Peeking currFirst = (Iterators.Peeking)currFirstOption.get();
                this.xss.remove(currFirst);
                Object next = currFirst.next();
                if (currFirst.hasNext()) {
                    this.xss.add(currFirst);
                }
                return (A)next;
            }

            private Comparator<? super Iterators.Peeking<A>> peekingIteratorComparator(Comparator<A> comparator) {
                return (lhs, rhs) -> lhs == rhs ? 0 : comparator.compare(lhs.peek(), rhs.peek());
            }
        }
    }

    static final class UnfoldingIterable<A, B>
    extends IterableToString<A> {
        private final Function<? super B, Option<Pair<A, B>>> f;
        private final B seed;

        private UnfoldingIterable(Function<? super B, Option<Pair<A, B>>> f, B seed) {
            this.f = Objects.requireNonNull(f, "f");
            this.seed = seed;
        }

        @Override
        public Iterator<A> iterator() {
            return new Iter<A, B>(this.f, this.seed);
        }

        static final class Iter<A, B>
        extends Iterators.Abstract<A> {
            private final Function<? super B, Option<Pair<A, B>>> f;
            private B current;

            Iter(Function<? super B, Option<Pair<A, B>>> f, B seed) {
                this.f = f;
                this.current = seed;
            }

            @Override
            protected A computeNext() {
                Option<Pair<A, B>> option = this.f.apply(this.current);
                if (option.isDefined()) {
                    Pair pair = (Pair)option.get();
                    this.current = pair.right();
                    return pair.left();
                }
                return this.endOfData();
            }
        }
    }

    static final class IteratingIterable<A>
    implements Iterable<A> {
        private final Function<? super A, ? extends A> f;
        private final A start;

        private IteratingIterable(Function<? super A, ? extends A> f, A start) {
            this.f = Objects.requireNonNull(f, "f");
            this.start = start;
        }

        @Override
        public Iterator<A> iterator() {
            return new Iter<A>(this.f, this.start);
        }

        static final class Iter<A>
        extends Iterators.Unmodifiable<A> {
            private final Function<? super A, ? extends A> f;
            private A current;

            Iter(Function<? super A, ? extends A> f, A start) {
                this.f = f;
                this.current = start;
            }

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public A next() {
                A value = this.current;
                this.current = this.f.apply(this.current);
                return value;
            }
        }
    }

    static final class Join<A>
    extends IterableToString<A> {
        private final Iterable<? extends Iterable<? extends A>> ias;

        public Join(Iterable<? extends Iterable<? extends A>> ias) {
            this.ias = ias;
        }

        @Override
        public Iterator<A> iterator() {
            return new Iter(this.ias);
        }

        static class Iter<A>
        extends Iterators.Abstract<A> {
            final Queue<Iterator<? extends A>> qas = new LinkedList<Iterator<? extends A>>();

            public Iter(Iterable<? extends Iterable<? extends A>> ias) {
                for (Iterable<A> a : ias) {
                    this.qas.add(Objects.requireNonNull(a.iterator()));
                }
            }

            @Override
            protected A computeNext() {
                while (!this.qas.isEmpty() && !this.qas.peek().hasNext()) {
                    this.qas.remove();
                }
                if (this.qas.isEmpty()) {
                    return this.endOfData();
                }
                return this.qas.peek().next();
            }
        }
    }

    static final class Filter<A>
    implements Iterable<A> {
        private final Iterable<? extends A> as;
        private final Predicate<? super A> p;

        Filter(Iterable<? extends A> as, Predicate<? super A> p) {
            this.as = as;
            this.p = p;
        }

        @Override
        public Iterator<A> iterator() {
            return new Iterators.Abstract<A>(){
                private final Iterator<? extends A> it;
                {
                    this.it = as.iterator();
                }

                @Override
                protected A computeNext() {
                    if (!this.it.hasNext()) {
                        return this.endOfData();
                    }
                    while (this.it.hasNext()) {
                        Object a = this.it.next();
                        if (!p.test(a)) continue;
                        return a;
                    }
                    return this.endOfData();
                }
            };
        }
    }

    static final class Mapped<A, B>
    implements Iterable<B> {
        private final Iterable<? extends A> as;
        private final Function<? super A, ? extends B> f;

        Mapped(Iterable<? extends A> as, Function<? super A, ? extends B> f) {
            this.as = as;
            this.f = f;
        }

        @Override
        public Iterator<B> iterator() {
            return new Iterators.Abstract<B>(){
                private final Iterator<? extends A> it;
                {
                    this.it = as.iterator();
                }

                @Override
                protected B computeNext() {
                    if (!this.it.hasNext()) {
                        return this.endOfData();
                    }
                    return f.apply(this.it.next());
                }
            };
        }
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
            return new Iterators.Abstract<A>(){
                private final Iterator<? extends A> it;
                private boolean inter;
                {
                    this.it = as.iterator();
                    this.inter = false;
                }

                @Override
                protected A computeNext() {
                    Object object;
                    if (!this.it.hasNext()) {
                        return this.endOfData();
                    }
                    try {
                        object = this.inter ? a.get() : this.it.next();
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
        private final BiFunction<A, B, C> f;

        Zipper(Iterable<A> as, Iterable<B> bs, BiFunction<A, B, C> f) {
            this.as = Objects.requireNonNull(as, "as must not be null.");
            this.bs = Objects.requireNonNull(bs, "bs must not be null.");
            this.f = Objects.requireNonNull(f, "f must not be null.");
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
                this.a = Objects.requireNonNull(Zipper.this.as.iterator(), "as iterator must not be null.");
                this.b = Objects.requireNonNull(Zipper.this.bs.iterator(), "bs iterator must not be null.");
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

    static class CollectingIterable<A, B>
    extends IterableToString<B> {
        private final Iterable<? extends A> delegate;
        private final Function<? super A, Option<B>> partial;

        CollectingIterable(Iterable<? extends A> delegate, Function<? super A, Option<B>> partial) {
            this.delegate = Objects.requireNonNull(delegate);
            this.partial = Objects.requireNonNull(partial);
        }

        @Override
        public Iterator<B> iterator() {
            return new Iter();
        }

        final class Iter
        extends Iterators.Abstract<B> {
            private final Iterator<? extends A> it;

            Iter() {
                this.it = CollectingIterable.this.delegate.iterator();
            }

            @Override
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

    static final class Drop<A>
    extends IterableToString<A> {
        private final Iterable<A> as;
        private final Predicate<A> p;

        private Drop(Iterable<A> as, Predicate<A> p) {
            this.p = Objects.requireNonNull(p);
            this.as = Objects.requireNonNull(as);
        }

        @Override
        public Iterator<A> iterator() {
            return new Iter<A>(this.as.iterator(), this.p);
        }

        static final class Iter<A>
        extends Iterators.Abstract<A> {
            private final Iterators.Peeking<A> as;

            Iter(Iterator<A> as, Predicate<A> p) {
                this.as = Iterators.peekingIterator(as);
                while (this.as.hasNext() && p.test(this.as.peek())) {
                    this.as.next();
                }
            }

            @Override
            protected A computeNext() {
                return (A)(this.as.hasNext() ? this.as.next() : this.endOfData());
            }
        }
    }

    static final class Take<A>
    extends IterableToString<A> {
        private final Iterable<A> as;
        private final Predicate<A> p;

        private Take(Iterable<A> as, Predicate<A> p) {
            this.p = Objects.requireNonNull(p);
            this.as = Objects.requireNonNull(as);
        }

        @Override
        public Iterator<A> iterator() {
            return new Iter<A>(this.as.iterator(), this.p);
        }

        static final class Iter<A>
        extends Iterators.Abstract<A> {
            private final Iterator<A> ias;
            private final Predicate<A> p;

            Iter(Iterator<A> ias, Predicate<A> p) {
                this.ias = ias;
                this.p = p;
            }

            @Override
            protected A computeNext() {
                if (this.ias.hasNext()) {
                    A a = this.ias.next();
                    return this.p.test(a) ? a : this.endOfData();
                }
                return this.endOfData();
            }
        }
    }

    static abstract class IterableToString<A>
    implements Iterable<A> {
        IterableToString() {
        }

        public final String toString() {
            return Iterables.makeString(this, "[", ", ", "]");
        }
    }
}

