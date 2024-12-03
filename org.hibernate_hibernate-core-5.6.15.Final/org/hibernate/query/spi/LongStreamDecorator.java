/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.spi;

import java.util.LongSummaryStatistics;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.hibernate.Incubating;
import org.hibernate.query.spi.DoubleStreamDecorator;
import org.hibernate.query.spi.IntStreamDecorator;
import org.hibernate.query.spi.StreamDecorator;

@Incubating
public class LongStreamDecorator
implements LongStream {
    private final LongStream delegate;
    private final Runnable closeHandler;

    public LongStreamDecorator(LongStream delegate, Runnable closeHandler) {
        this.closeHandler = closeHandler;
        this.delegate = (LongStream)delegate.onClose(closeHandler);
    }

    @Override
    public LongStream filter(LongPredicate predicate) {
        return new LongStreamDecorator(this.delegate.filter(predicate), this.closeHandler);
    }

    @Override
    public LongStream map(LongUnaryOperator mapper) {
        return new LongStreamDecorator(this.delegate.map(mapper), this.closeHandler);
    }

    @Override
    public <U> Stream<U> mapToObj(LongFunction<? extends U> mapper) {
        return new StreamDecorator<U>(this.delegate.mapToObj(mapper), this.closeHandler);
    }

    @Override
    public IntStream mapToInt(LongToIntFunction mapper) {
        return new IntStreamDecorator(this.delegate.mapToInt(mapper), this.closeHandler);
    }

    @Override
    public DoubleStream mapToDouble(LongToDoubleFunction mapper) {
        return new DoubleStreamDecorator(this.delegate.mapToDouble(mapper), this.closeHandler);
    }

    @Override
    public LongStream flatMap(LongFunction<? extends LongStream> mapper) {
        return new LongStreamDecorator(this.delegate.flatMap(mapper), this.closeHandler);
    }

    @Override
    public LongStream distinct() {
        return new LongStreamDecorator(this.delegate.distinct(), this.closeHandler);
    }

    @Override
    public LongStream sorted() {
        return new LongStreamDecorator(this.delegate.sorted(), this.closeHandler);
    }

    @Override
    public LongStream peek(LongConsumer action) {
        return new LongStreamDecorator(this.delegate.peek(action), this.closeHandler);
    }

    @Override
    public LongStream limit(long maxSize) {
        return new LongStreamDecorator(this.delegate.limit(maxSize), this.closeHandler);
    }

    @Override
    public LongStream skip(long n) {
        return new LongStreamDecorator(this.delegate.skip(n), this.closeHandler);
    }

    @Override
    public void forEach(LongConsumer action) {
        this.delegate.forEach(action);
        this.close();
    }

    @Override
    public void forEachOrdered(LongConsumer action) {
        this.delegate.forEachOrdered(action);
        this.close();
    }

    @Override
    public long[] toArray() {
        long[] result = this.delegate.toArray();
        this.close();
        return result;
    }

    @Override
    public long reduce(long identity, LongBinaryOperator op) {
        long result = this.delegate.reduce(identity, op);
        this.close();
        return result;
    }

    @Override
    public OptionalLong reduce(LongBinaryOperator op) {
        OptionalLong result = this.delegate.reduce(op);
        this.close();
        return result;
    }

    @Override
    public <R> R collect(Supplier<R> supplier, ObjLongConsumer<R> accumulator, BiConsumer<R, R> combiner) {
        R result = this.delegate.collect(supplier, accumulator, combiner);
        this.close();
        return result;
    }

    @Override
    public long sum() {
        long result = this.delegate.sum();
        this.close();
        return result;
    }

    @Override
    public OptionalLong min() {
        OptionalLong result = this.delegate.min();
        this.close();
        return result;
    }

    @Override
    public OptionalLong max() {
        OptionalLong result = this.delegate.max();
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
    public LongSummaryStatistics summaryStatistics() {
        LongSummaryStatistics result = this.delegate.summaryStatistics();
        this.close();
        return result;
    }

    @Override
    public boolean anyMatch(LongPredicate predicate) {
        boolean result = this.delegate.anyMatch(predicate);
        this.close();
        return result;
    }

    @Override
    public boolean allMatch(LongPredicate predicate) {
        boolean result = this.delegate.allMatch(predicate);
        this.close();
        return result;
    }

    @Override
    public boolean noneMatch(LongPredicate predicate) {
        boolean result = this.delegate.noneMatch(predicate);
        this.close();
        return result;
    }

    @Override
    public OptionalLong findFirst() {
        OptionalLong result = this.delegate.findFirst();
        this.close();
        return result;
    }

    @Override
    public OptionalLong findAny() {
        OptionalLong result = this.delegate.findAny();
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
    public Stream<Long> boxed() {
        return new StreamDecorator<Long>(this.delegate.boxed(), this.closeHandler);
    }

    @Override
    public LongStream sequential() {
        return new LongStreamDecorator(this.delegate.sequential(), this.closeHandler);
    }

    @Override
    public LongStream parallel() {
        return new LongStreamDecorator(this.delegate.parallel(), this.closeHandler);
    }

    @Override
    public LongStream unordered() {
        return new LongStreamDecorator((LongStream)this.delegate.unordered(), this.closeHandler);
    }

    @Override
    public LongStream onClose(Runnable closeHandler) {
        this.delegate.onClose(closeHandler);
        return this;
    }

    @Override
    public void close() {
        this.delegate.close();
    }

    @Override
    public PrimitiveIterator.OfLong iterator() {
        return this.delegate.iterator();
    }

    @Override
    public Spliterator.OfLong spliterator() {
        return this.delegate.spliterator();
    }

    @Override
    public boolean isParallel() {
        return this.delegate.isParallel();
    }
}

