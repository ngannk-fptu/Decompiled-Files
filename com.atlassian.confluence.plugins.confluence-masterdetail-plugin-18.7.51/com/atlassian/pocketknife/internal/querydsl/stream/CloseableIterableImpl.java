/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.google.common.base.Preconditions
 */
package com.atlassian.pocketknife.internal.querydsl.stream;

import com.atlassian.annotations.Internal;
import com.atlassian.pocketknife.api.querydsl.stream.ClosePromise;
import com.atlassian.pocketknife.api.querydsl.stream.CloseableIterable;
import com.atlassian.pocketknife.api.querydsl.tuple.Tuples;
import com.atlassian.pocketknife.internal.querydsl.util.fp.Fp;
import com.google.common.base.Preconditions;
import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Internal
public class CloseableIterableImpl<S, T>
implements CloseableIterable<T> {
    private final CloseableIteratorImpl<S, T> closeableIterator;
    private final ClosePromise closePromise;

    public CloseableIterableImpl(CloseableIterator<S> srcIterator, Function<S, T> mapper, ClosePromise parentPromise) {
        this(srcIterator, mapper, parentPromise, Fp.alwaysTrue(), Fp.alwaysTrue());
    }

    public CloseableIterableImpl(CloseableIterator<S> srcIterator, Function<S, T> mapper, ClosePromise parentPromise, Predicate<S> filterPredicate, Predicate<S> takeWhilePredicate) {
        IteratorInstructions<S, T> instructions = new IteratorInstructions<S, T>(srcIterator, mapper, parentPromise, filterPredicate, takeWhilePredicate);
        this.closeableIterator = new CloseableIteratorImpl<S, T>(instructions);
        this.closePromise = new ClosePromise(parentPromise, this::closeImpl);
    }

    public CloseableIterableImpl(CloseableIteratorImpl<S, T> closeableIterator) {
        this.closeableIterator = closeableIterator;
        this.closePromise = ((CloseableIteratorImpl)closeableIterator).closePromise;
    }

    @Override
    public CloseableIterator<T> iterator() {
        return this.closeableIterator;
    }

    @Override
    public CloseableIterable<T> take(int n) {
        Preconditions.checkArgument((n >= 0 ? 1 : 0) != 0, (Object)"take(n) argument must be >= 0");
        Preconditions.checkState((!this.closeableIterator.hasStarted() ? 1 : 0) != 0, (Object)"You cant take(n) from an iterable that has been read");
        this.ensureNotClosed();
        Predicate<T> nTaken = CloseableIterableImpl.nTakenPredicate(n);
        return this.takeWhile(nTaken);
    }

    @Override
    public CloseableIterable<T> takeWhile(Predicate<T> takeWhilePredicate) {
        Preconditions.checkNotNull(takeWhilePredicate);
        Preconditions.checkState((!this.closeableIterator.hasStarted() ? 1 : 0) != 0, (Object)"You cant takeWhile() from an iterable that has been read");
        this.ensureNotClosed();
        CloseableIteratorImpl<S, T> src = this.closeableIterator;
        Predicate composedTakeWhile = Fp.compose(takeWhilePredicate, ((CloseableIteratorImpl)src).mapper);
        IteratorInstructions instructions = new IteratorInstructions(((CloseableIteratorImpl)src).instructions);
        instructions.takeWhilePredicate = composedTakeWhile;
        CloseableIteratorImpl newIterator = new CloseableIteratorImpl(instructions);
        return new CloseableIterableImpl(newIterator);
    }

    @Override
    public CloseableIterable<T> filter(Predicate<T> filterPredicate) {
        Preconditions.checkNotNull(filterPredicate);
        this.ensureNotClosed();
        CloseableIteratorImpl<S, T> src = this.closeableIterator;
        Predicate composedFilterPredicate = Fp.compose(filterPredicate, ((CloseableIteratorImpl)src).mapper);
        IteratorInstructions instructions = new IteratorInstructions(((CloseableIteratorImpl)src).instructions);
        instructions.filterPredicate = composedFilterPredicate;
        CloseableIteratorImpl newIterator = new CloseableIteratorImpl(instructions);
        return new CloseableIterableImpl(newIterator);
    }

    public static <T> Predicate<T> nTakenPredicate(final int n) {
        return new Predicate<T>(){
            int takenSoFar = 0;

            @Override
            public boolean test(T t) {
                if (this.takenSoFar < n) {
                    ++this.takenSoFar;
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public <D> CloseableIterable<D> map(Function<T, D> mapper) {
        this.ensureNotClosed();
        CloseableIteratorImpl src = this.closeableIterator;
        Function<Object, Object> composedMapper = input -> Fp.compose(mapper, src.mapper).apply(input);
        IteratorInstructions<Object, Object> instructions = new IteratorInstructions<Object, Object>(src.srcIterator, composedMapper, this.closePromise, src.filterPredicate, src.takeWhilePredicate);
        CloseableIteratorImpl<Object, Object> composedIterator = new CloseableIteratorImpl<Object, Object>(instructions);
        return new CloseableIterableImpl<Object, Object>(composedIterator);
    }

    @Override
    public <D> CloseableIterable<D> map(Expression<D> expr) {
        this.ensureNotClosed();
        Function extractColumn = Tuples.column(expr);
        return this.map((T t) -> this.ensureTupleQuery(t, extractColumn));
    }

    private <D> D ensureTupleQuery(T t, Function<Tuple, D> extractColumn) {
        if (!(t instanceof Tuple)) {
            throw new IllegalStateException("The underlying query must be SQLQuery<Tuple> to call this method");
        }
        Tuple tuple = (Tuple)t;
        return extractColumn.apply(tuple);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <D> D foldLeft(D initial, BiFunction<D, T, D> combiningFunction) {
        this.ensureNotClosed();
        try {
            D accumulator = initial;
            while (this.closeableIterator.hasNext()) {
                accumulator = combiningFunction.apply(accumulator, this.closeableIterator.next());
            }
            D d = accumulator;
            return d;
        }
        finally {
            this.close();
        }
    }

    private void ensureNotClosed() {
        if (this.closePromise.isClosed()) {
            throw new IllegalStateException("This CloseableIterable has already been closed");
        }
    }

    @Override
    public Optional<T> fetchFirst() {
        this.ensureNotClosed();
        try {
            Iterator iterator = this.iterator();
            if (iterator.hasNext()) {
                Optional optional = Optional.of(iterator.next());
                return optional;
            }
            Optional optional = Optional.empty();
            return optional;
        }
        finally {
            this.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void foreach(Consumer<T> effect) {
        this.ensureNotClosed();
        try {
            for (Object t : this) {
                effect.accept(t);
            }
        }
        finally {
            this.close();
        }
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        this.foreach(action::accept);
    }

    @Override
    public Spliterator<T> spliterator() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void close() {
        this.closePromise.close();
    }

    private void closeImpl() {
        this.closeableIterator.close();
    }

    static class CloseableIteratorImpl<S, T>
    implements CloseableIterator<T> {
        private final CloseableIterator<S> srcIterator;
        private final Function<S, T> mapper;
        private final ClosePromise closePromise;
        private final Predicate<S> filterPredicate;
        private final Predicate<S> takeWhilePredicate;
        private final IteratorInstructions<S, T> instructions;
        int returnedSoFar;
        S nextObject;
        boolean nextObjectAccessed = true;

        CloseableIteratorImpl(IteratorInstructions<S, T> instructions) {
            this.instructions = instructions;
            this.srcIterator = ((IteratorInstructions)instructions).srcIterator;
            this.mapper = ((IteratorInstructions)instructions).mapper;
            this.closePromise = new ClosePromise(((IteratorInstructions)instructions).closePromise, this.srcIterator::close);
            this.filterPredicate = ((IteratorInstructions)instructions).filterPredicate;
            this.takeWhilePredicate = ((IteratorInstructions)instructions).takeWhilePredicate;
            this.returnedSoFar = 0;
        }

        boolean hasStarted() {
            return this.returnedSoFar > 0;
        }

        @Override
        public boolean hasNext() {
            if (!this.nextObjectAccessed) {
                return true;
            }
            if (this.closePromise.isClosed()) {
                return false;
            }
            boolean hasNext = this.srcIterator.hasNext();
            while (hasNext) {
                this.nextObject = this.nextImpl();
                this.nextObjectAccessed = false;
                if (this.filterPredicate.test(this.nextObject)) break;
                hasNext = this.srcIterator.hasNext();
            }
            if (hasNext && !this.takeWhilePredicate.test(this.nextObject)) {
                hasNext = false;
            }
            if (!hasNext) {
                this.nextObject = null;
                this.close();
            }
            return hasNext;
        }

        private S nextImpl() {
            Object next = this.srcIterator.next();
            ++this.returnedSoFar;
            return (S)next;
        }

        @Override
        public T next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.nextObjectAccessed = true;
            return this.mapper.apply(this.nextObject);
        }

        @Override
        public void close() {
            this.closePromise.close();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    static class IteratorInstructions<S, T> {
        private CloseableIterator<S> srcIterator;
        private Function<S, T> mapper;
        private ClosePromise closePromise;
        private Predicate<S> filterPredicate;
        private Predicate<S> takeWhilePredicate;

        public IteratorInstructions(CloseableIterator<S> srcIterator, Function<S, T> mapper, ClosePromise closePromise, Predicate<S> filterPredicate, Predicate<S> takeWhilePredicate) {
            this.srcIterator = srcIterator;
            this.mapper = mapper;
            this.closePromise = closePromise;
            this.filterPredicate = filterPredicate;
            this.takeWhilePredicate = takeWhilePredicate;
        }

        IteratorInstructions(IteratorInstructions<S, T> instructions) {
            this.srcIterator = instructions.srcIterator;
            this.mapper = instructions.mapper;
            this.closePromise = instructions.closePromise;
            this.takeWhilePredicate = instructions.takeWhilePredicate;
            this.filterPredicate = instructions.filterPredicate;
        }
    }
}

