/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.spi;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.hibernate.HibernateException;
import org.hibernate.Incubating;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.query.spi.DoubleStreamDecorator;
import org.hibernate.query.spi.IntStreamDecorator;
import org.hibernate.query.spi.LongStreamDecorator;

@Incubating
public class StreamDecorator<R>
implements Stream<R> {
    private final Stream<R> delegate;
    private final Runnable closeHandler;

    public StreamDecorator(Stream<R> delegate, Runnable closeHandler) {
        this.closeHandler = closeHandler;
        this.delegate = (Stream)delegate.onClose(closeHandler);
    }

    @Override
    public Stream<R> filter(Predicate<? super R> predicate) {
        return new StreamDecorator<R>(this.delegate.filter(predicate), this.closeHandler);
    }

    @Override
    public <R1> Stream<R1> map(Function<? super R, ? extends R1> mapper) {
        return new StreamDecorator<R1>(this.delegate.map(mapper), this.closeHandler);
    }

    @Override
    public IntStream mapToInt(ToIntFunction<? super R> mapper) {
        return new IntStreamDecorator(this.delegate.mapToInt(mapper), this.closeHandler);
    }

    @Override
    public LongStream mapToLong(ToLongFunction<? super R> mapper) {
        return new LongStreamDecorator(this.delegate.mapToLong(mapper), this.closeHandler);
    }

    @Override
    public DoubleStream mapToDouble(ToDoubleFunction<? super R> mapper) {
        return new DoubleStreamDecorator(this.delegate.mapToDouble(mapper), this.closeHandler);
    }

    @Override
    public <R1> Stream<R1> flatMap(Function<? super R, ? extends Stream<? extends R1>> mapper) {
        return new StreamDecorator(this.delegate.flatMap(mapper), this.closeHandler);
    }

    @Override
    public IntStream flatMapToInt(Function<? super R, ? extends IntStream> mapper) {
        return new IntStreamDecorator(this.delegate.flatMapToInt(mapper), this.closeHandler);
    }

    @Override
    public LongStream flatMapToLong(Function<? super R, ? extends LongStream> mapper) {
        return new LongStreamDecorator(this.delegate.flatMapToLong(mapper), this.closeHandler);
    }

    @Override
    public DoubleStream flatMapToDouble(Function<? super R, ? extends DoubleStream> mapper) {
        return new DoubleStreamDecorator(this.delegate.flatMapToDouble(mapper), this.closeHandler);
    }

    @Override
    public Stream<R> distinct() {
        return new StreamDecorator<R>(this.delegate.distinct(), this.closeHandler);
    }

    @Override
    public Stream<R> sorted() {
        return new StreamDecorator<R>(this.delegate.sorted(), this.closeHandler);
    }

    @Override
    public Stream<R> sorted(Comparator<? super R> comparator) {
        return new StreamDecorator<R>(this.delegate.sorted(comparator), this.closeHandler);
    }

    @Override
    public Stream<R> peek(Consumer<? super R> action) {
        return new StreamDecorator<R>(this.delegate.peek(action), this.closeHandler);
    }

    @Override
    public Stream<R> limit(long maxSize) {
        return new StreamDecorator<R>(this.delegate.limit(maxSize), this.closeHandler);
    }

    @Override
    public Stream<R> skip(long n) {
        return new StreamDecorator<R>(this.delegate.skip(n), this.closeHandler);
    }

    @Override
    public void forEach(Consumer<? super R> action) {
        this.delegate.forEach(action);
        this.close();
    }

    @Override
    public void forEachOrdered(Consumer<? super R> action) {
        this.delegate.forEachOrdered(action);
        this.close();
    }

    @Override
    public Object[] toArray() {
        Object[] result = this.delegate.toArray();
        this.close();
        return result;
    }

    @Override
    public <A> A[] toArray(IntFunction<A[]> generator) {
        A[] result = this.delegate.toArray(generator);
        this.close();
        return result;
    }

    @Override
    public R reduce(R identity, BinaryOperator<R> accumulator) {
        R result = this.delegate.reduce(identity, accumulator);
        this.close();
        return result;
    }

    @Override
    public Optional<R> reduce(BinaryOperator<R> accumulator) {
        Optional<R> result = this.delegate.reduce(accumulator);
        this.close();
        return result;
    }

    @Override
    public <U> U reduce(U identity, BiFunction<U, ? super R, U> accumulator, BinaryOperator<U> combiner) {
        U result = this.delegate.reduce(identity, accumulator, combiner);
        this.close();
        return result;
    }

    @Override
    public <R1> R1 collect(Supplier<R1> supplier, BiConsumer<R1, ? super R> accumulator, BiConsumer<R1, R1> combiner) {
        R1 result = this.delegate.collect(supplier, accumulator, combiner);
        this.close();
        return result;
    }

    @Override
    public <R1, A> R1 collect(Collector<? super R, A, R1> collector) {
        R1 result = this.delegate.collect(collector);
        this.close();
        return result;
    }

    @Override
    public Optional<R> min(Comparator<? super R> comparator) {
        Optional<? super R> result = this.delegate.min(comparator);
        this.close();
        return result;
    }

    @Override
    public Optional<R> max(Comparator<? super R> comparator) {
        Optional<? super R> result = this.delegate.max(comparator);
        this.close();
        return result;
    }

    @Override
    public long count() {
        long result = this.delegate.count();
        this.close();
        return result;
    }

    @Override
    public boolean anyMatch(Predicate<? super R> predicate) {
        boolean result = this.delegate.anyMatch(predicate);
        this.close();
        return result;
    }

    @Override
    public boolean allMatch(Predicate<? super R> predicate) {
        boolean result = this.delegate.allMatch(predicate);
        this.close();
        return result;
    }

    @Override
    public boolean noneMatch(Predicate<? super R> predicate) {
        boolean result = this.delegate.noneMatch(predicate);
        this.close();
        return result;
    }

    @Override
    public Optional<R> findFirst() {
        Optional<R> result = this.delegate.findFirst();
        this.close();
        return result;
    }

    @Override
    public Optional<R> findAny() {
        Optional<R> result = this.delegate.findAny();
        this.close();
        return result;
    }

    @Override
    public Iterator<R> iterator() {
        return this.delegate.iterator();
    }

    @Override
    public Spliterator<R> spliterator() {
        return this.delegate.spliterator();
    }

    @Override
    public boolean isParallel() {
        return this.delegate.isParallel();
    }

    @Override
    public Stream<R> sequential() {
        return new StreamDecorator<R>((Stream)this.delegate.sequential(), this.closeHandler);
    }

    @Override
    public Stream<R> parallel() {
        return new StreamDecorator<R>((Stream)this.delegate.parallel(), this.closeHandler);
    }

    @Override
    public Stream<R> unordered() {
        return new StreamDecorator<R>((Stream)this.delegate.unordered(), this.closeHandler);
    }

    @Override
    public Stream<R> onClose(Runnable closeHandler) {
        this.delegate.onClose(closeHandler);
        return this;
    }

    @Override
    public void close() {
        this.delegate.close();
    }

    @Override
    public Stream<R> takeWhile(Predicate<? super R> predicate) {
        try {
            Stream result = (Stream)ReflectHelper.getMethod(Stream.class, "takeWhile", Predicate.class).invoke(this.delegate, predicate);
            return new StreamDecorator<R>(result, this.closeHandler);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new HibernateException(e);
        }
    }

    @Override
    public Stream<R> dropWhile(Predicate<? super R> predicate) {
        try {
            Stream result = (Stream)ReflectHelper.getMethod(Stream.class, "dropWhile", Predicate.class).invoke(this.delegate, predicate);
            return new StreamDecorator<R>(result, this.closeHandler);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new HibernateException(e);
        }
    }
}

