/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.spi;

import java.util.IntSummaryStatistics;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.hibernate.Incubating;
import org.hibernate.query.spi.DoubleStreamDecorator;
import org.hibernate.query.spi.LongStreamDecorator;
import org.hibernate.query.spi.StreamDecorator;

@Incubating
public class IntStreamDecorator
implements IntStream {
    private final IntStream delegate;
    private final Runnable closeHandler;

    public IntStreamDecorator(IntStream delegate, Runnable closeHandler) {
        this.closeHandler = closeHandler;
        this.delegate = (IntStream)delegate.onClose(closeHandler);
    }

    @Override
    public IntStream filter(IntPredicate predicate) {
        return new IntStreamDecorator(this.delegate.filter(predicate), this.closeHandler);
    }

    @Override
    public IntStream map(IntUnaryOperator mapper) {
        return new IntStreamDecorator(this.delegate.map(mapper), this.closeHandler);
    }

    @Override
    public <U> Stream<U> mapToObj(IntFunction<? extends U> mapper) {
        return new StreamDecorator<U>(this.delegate.mapToObj(mapper), this.closeHandler);
    }

    @Override
    public LongStream mapToLong(IntToLongFunction mapper) {
        return new LongStreamDecorator(this.delegate.mapToLong(mapper), this.closeHandler);
    }

    @Override
    public DoubleStream mapToDouble(IntToDoubleFunction mapper) {
        return new DoubleStreamDecorator(this.delegate.mapToDouble(mapper), this.closeHandler);
    }

    @Override
    public IntStream flatMap(IntFunction<? extends IntStream> mapper) {
        return new IntStreamDecorator(this.delegate.flatMap(mapper), this.closeHandler);
    }

    @Override
    public IntStream distinct() {
        return new IntStreamDecorator(this.delegate.distinct(), this.closeHandler);
    }

    @Override
    public IntStream sorted() {
        return new IntStreamDecorator(this.delegate.sorted(), this.closeHandler);
    }

    @Override
    public IntStream peek(IntConsumer action) {
        return new IntStreamDecorator(this.delegate.peek(action), this.closeHandler);
    }

    @Override
    public IntStream limit(long maxSize) {
        return new IntStreamDecorator(this.delegate.limit(maxSize), this.closeHandler);
    }

    @Override
    public IntStream skip(long n) {
        return new IntStreamDecorator(this.delegate.skip(n), this.closeHandler);
    }

    @Override
    public void forEach(IntConsumer action) {
        this.delegate.forEach(action);
        this.close();
    }

    @Override
    public void forEachOrdered(IntConsumer action) {
        this.delegate.forEachOrdered(action);
        this.close();
    }

    @Override
    public int[] toArray() {
        int[] result = this.delegate.toArray();
        this.close();
        return result;
    }

    @Override
    public int reduce(int identity, IntBinaryOperator op) {
        int result = this.delegate.reduce(identity, op);
        this.close();
        return result;
    }

    @Override
    public OptionalInt reduce(IntBinaryOperator op) {
        OptionalInt result = this.delegate.reduce(op);
        this.close();
        return result;
    }

    @Override
    public <R> R collect(Supplier<R> supplier, ObjIntConsumer<R> accumulator, BiConsumer<R, R> combiner) {
        R result = this.delegate.collect(supplier, accumulator, combiner);
        this.close();
        return result;
    }

    @Override
    public int sum() {
        int result = this.delegate.sum();
        this.close();
        return result;
    }

    @Override
    public OptionalInt min() {
        OptionalInt result = this.delegate.min();
        this.close();
        return result;
    }

    @Override
    public OptionalInt max() {
        OptionalInt result = this.delegate.max();
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
    public OptionalDouble average() {
        OptionalDouble result = this.delegate.average();
        this.close();
        return result;
    }

    @Override
    public IntSummaryStatistics summaryStatistics() {
        IntSummaryStatistics result = this.delegate.summaryStatistics();
        this.close();
        return result;
    }

    @Override
    public boolean anyMatch(IntPredicate predicate) {
        boolean result = this.delegate.anyMatch(predicate);
        this.close();
        return result;
    }

    @Override
    public boolean allMatch(IntPredicate predicate) {
        boolean result = this.delegate.allMatch(predicate);
        this.close();
        return result;
    }

    @Override
    public boolean noneMatch(IntPredicate predicate) {
        boolean result = this.delegate.noneMatch(predicate);
        this.close();
        return result;
    }

    @Override
    public OptionalInt findFirst() {
        OptionalInt result = this.delegate.findFirst();
        this.close();
        return result;
    }

    @Override
    public OptionalInt findAny() {
        OptionalInt result = this.delegate.findAny();
        this.close();
        return result;
    }

    @Override
    public LongStream asLongStream() {
        LongStream result = this.delegate.asLongStream();
        this.close();
        return result;
    }

    @Override
    public DoubleStream asDoubleStream() {
        DoubleStream result = this.delegate.asDoubleStream();
        this.close();
        return result;
    }

    @Override
    public Stream<Integer> boxed() {
        return new StreamDecorator<Integer>(this.delegate.boxed(), this.closeHandler);
    }

    @Override
    public IntStream sequential() {
        return new IntStreamDecorator(this.delegate.sequential(), this.closeHandler);
    }

    @Override
    public IntStream parallel() {
        return new IntStreamDecorator(this.delegate.parallel(), this.closeHandler);
    }

    @Override
    public IntStream unordered() {
        return new IntStreamDecorator((IntStream)this.delegate.unordered(), this.closeHandler);
    }

    @Override
    public IntStream onClose(Runnable closeHandler) {
        this.delegate.onClose(closeHandler);
        return this;
    }

    @Override
    public void close() {
        this.delegate.close();
    }

    @Override
    public PrimitiveIterator.OfInt iterator() {
        return this.delegate.iterator();
    }

    @Override
    public Spliterator.OfInt spliterator() {
        return this.delegate.spliterator();
    }

    @Override
    public boolean isParallel() {
        return this.delegate.isParallel();
    }
}

