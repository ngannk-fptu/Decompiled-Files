/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.spi;

import java.util.DoubleSummaryStatistics;
import java.util.OptionalDouble;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.hibernate.Incubating;
import org.hibernate.query.spi.IntStreamDecorator;
import org.hibernate.query.spi.LongStreamDecorator;
import org.hibernate.query.spi.StreamDecorator;

@Incubating
public class DoubleStreamDecorator
implements DoubleStream {
    private final DoubleStream delegate;
    private final Runnable closeHandler;

    public DoubleStreamDecorator(DoubleStream delegate, Runnable closeHandler) {
        this.closeHandler = closeHandler;
        this.delegate = (DoubleStream)delegate.onClose(closeHandler);
    }

    @Override
    public DoubleStream filter(DoublePredicate predicate) {
        return new DoubleStreamDecorator(this.delegate.filter(predicate), this.closeHandler);
    }

    @Override
    public DoubleStream map(DoubleUnaryOperator mapper) {
        return new DoubleStreamDecorator(this.delegate.map(mapper), this.closeHandler);
    }

    @Override
    public <U> Stream<U> mapToObj(DoubleFunction<? extends U> mapper) {
        return new StreamDecorator<U>(this.delegate.mapToObj(mapper), this.closeHandler);
    }

    @Override
    public IntStream mapToInt(DoubleToIntFunction mapper) {
        return new IntStreamDecorator(this.delegate.mapToInt(mapper), this.closeHandler);
    }

    @Override
    public LongStream mapToLong(DoubleToLongFunction mapper) {
        return new LongStreamDecorator(this.delegate.mapToLong(mapper), this.closeHandler);
    }

    @Override
    public DoubleStream flatMap(DoubleFunction<? extends DoubleStream> mapper) {
        return new DoubleStreamDecorator(this.delegate.flatMap(mapper), this.closeHandler);
    }

    @Override
    public DoubleStream distinct() {
        return new DoubleStreamDecorator(this.delegate.distinct(), this.closeHandler);
    }

    @Override
    public DoubleStream sorted() {
        return new DoubleStreamDecorator(this.delegate.sorted(), this.closeHandler);
    }

    @Override
    public DoubleStream peek(DoubleConsumer action) {
        return new DoubleStreamDecorator(this.delegate.peek(action), this.closeHandler);
    }

    @Override
    public DoubleStream limit(long maxSize) {
        return new DoubleStreamDecorator(this.delegate.limit(maxSize), this.closeHandler);
    }

    @Override
    public DoubleStream skip(long n) {
        return new DoubleStreamDecorator(this.delegate.skip(n), this.closeHandler);
    }

    @Override
    public void forEach(DoubleConsumer action) {
        this.delegate.forEach(action);
        this.close();
    }

    @Override
    public void forEachOrdered(DoubleConsumer action) {
        this.delegate.forEachOrdered(action);
        this.close();
    }

    @Override
    public double[] toArray() {
        double[] result = this.delegate.toArray();
        this.close();
        return result;
    }

    @Override
    public double reduce(double identity, DoubleBinaryOperator op) {
        double result = this.delegate.reduce(identity, op);
        this.close();
        return result;
    }

    @Override
    public OptionalDouble reduce(DoubleBinaryOperator op) {
        OptionalDouble result = this.delegate.reduce(op);
        this.close();
        return result;
    }

    @Override
    public <R> R collect(Supplier<R> supplier, ObjDoubleConsumer<R> accumulator, BiConsumer<R, R> combiner) {
        R result = this.delegate.collect(supplier, accumulator, combiner);
        this.close();
        return result;
    }

    @Override
    public double sum() {
        double result = this.delegate.sum();
        this.close();
        return result;
    }

    @Override
    public OptionalDouble min() {
        OptionalDouble result = this.delegate.min();
        this.close();
        return result;
    }

    @Override
    public OptionalDouble max() {
        OptionalDouble result = this.delegate.max();
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
    public DoubleSummaryStatistics summaryStatistics() {
        DoubleSummaryStatistics result = this.delegate.summaryStatistics();
        this.close();
        return result;
    }

    @Override
    public boolean anyMatch(DoublePredicate predicate) {
        boolean result = this.delegate.anyMatch(predicate);
        this.close();
        return result;
    }

    @Override
    public boolean allMatch(DoublePredicate predicate) {
        boolean result = this.delegate.allMatch(predicate);
        this.close();
        return result;
    }

    @Override
    public boolean noneMatch(DoublePredicate predicate) {
        boolean result = this.delegate.noneMatch(predicate);
        this.close();
        return result;
    }

    @Override
    public OptionalDouble findFirst() {
        OptionalDouble result = this.delegate.findFirst();
        this.close();
        return result;
    }

    @Override
    public OptionalDouble findAny() {
        OptionalDouble result = this.delegate.findAny();
        this.close();
        return result;
    }

    @Override
    public Stream<Double> boxed() {
        return new StreamDecorator<Double>(this.delegate.boxed(), this.closeHandler);
    }

    @Override
    public DoubleStream sequential() {
        return new DoubleStreamDecorator(this.delegate.sequential(), this.closeHandler);
    }

    @Override
    public DoubleStream parallel() {
        return new DoubleStreamDecorator(this.delegate.parallel(), this.closeHandler);
    }

    @Override
    public DoubleStream unordered() {
        return new DoubleStreamDecorator((DoubleStream)this.delegate.unordered(), this.closeHandler);
    }

    @Override
    public DoubleStream onClose(Runnable closeHandler) {
        this.delegate.onClose(closeHandler);
        return this;
    }

    @Override
    public void close() {
        this.delegate.close();
    }

    @Override
    public PrimitiveIterator.OfDouble iterator() {
        return this.delegate.iterator();
    }

    @Override
    public Spliterator.OfDouble spliterator() {
        return this.delegate.spliterator();
    }

    @Override
    public boolean isParallel() {
        return this.delegate.isParallel();
    }
}

