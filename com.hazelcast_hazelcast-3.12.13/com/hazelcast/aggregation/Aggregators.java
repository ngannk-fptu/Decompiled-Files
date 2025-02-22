/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.aggregation;

import com.hazelcast.aggregation.Aggregator;
import com.hazelcast.aggregation.impl.BigDecimalAverageAggregator;
import com.hazelcast.aggregation.impl.BigDecimalSumAggregator;
import com.hazelcast.aggregation.impl.BigIntegerAverageAggregator;
import com.hazelcast.aggregation.impl.BigIntegerSumAggregator;
import com.hazelcast.aggregation.impl.CountAggregator;
import com.hazelcast.aggregation.impl.DistinctValuesAggregator;
import com.hazelcast.aggregation.impl.DoubleAverageAggregator;
import com.hazelcast.aggregation.impl.DoubleSumAggregator;
import com.hazelcast.aggregation.impl.FixedSumAggregator;
import com.hazelcast.aggregation.impl.FloatingPointSumAggregator;
import com.hazelcast.aggregation.impl.IntegerAverageAggregator;
import com.hazelcast.aggregation.impl.IntegerSumAggregator;
import com.hazelcast.aggregation.impl.LongAverageAggregator;
import com.hazelcast.aggregation.impl.LongSumAggregator;
import com.hazelcast.aggregation.impl.MaxAggregator;
import com.hazelcast.aggregation.impl.MaxByAggregator;
import com.hazelcast.aggregation.impl.MinAggregator;
import com.hazelcast.aggregation.impl.MinByAggregator;
import com.hazelcast.aggregation.impl.NumberAverageAggregator;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

public final class Aggregators {
    private Aggregators() {
    }

    public static <I> Aggregator<I, Long> count() {
        return new CountAggregator();
    }

    public static <I> Aggregator<I, Long> count(String attributePath) {
        return new CountAggregator(attributePath);
    }

    public static <I, R> Aggregator<I, Set<R>> distinct() {
        return new DistinctValuesAggregator();
    }

    public static <I, R> Aggregator<I, Set<R>> distinct(String attributePath) {
        return new DistinctValuesAggregator(attributePath);
    }

    public static <I> Aggregator<I, BigDecimal> bigDecimalAvg() {
        return new BigDecimalAverageAggregator();
    }

    public static <I> Aggregator<I, BigDecimal> bigDecimalAvg(String attributePath) {
        return new BigDecimalAverageAggregator(attributePath);
    }

    public static <I> Aggregator<I, BigDecimal> bigIntegerAvg() {
        return new BigIntegerAverageAggregator();
    }

    public static <I> Aggregator<I, BigDecimal> bigIntegerAvg(String attributePath) {
        return new BigIntegerAverageAggregator(attributePath);
    }

    public static <I> Aggregator<I, Double> doubleAvg() {
        return new DoubleAverageAggregator();
    }

    public static <I> Aggregator<I, Double> doubleAvg(String attributePath) {
        return new DoubleAverageAggregator(attributePath);
    }

    public static <I> Aggregator<I, Double> integerAvg() {
        return new IntegerAverageAggregator();
    }

    public static <I> Aggregator<I, Double> integerAvg(String attributePath) {
        return new IntegerAverageAggregator(attributePath);
    }

    public static <I> Aggregator<I, Double> longAvg() {
        return new LongAverageAggregator();
    }

    public static <I> Aggregator<I, Double> longAvg(String attributePath) {
        return new LongAverageAggregator(attributePath);
    }

    public static <I> Aggregator<I, Double> numberAvg() {
        return new NumberAverageAggregator();
    }

    public static <I> Aggregator<I, Double> numberAvg(String attributePath) {
        return new NumberAverageAggregator(attributePath);
    }

    public static <I> Aggregator<I, BigDecimal> bigDecimalMax() {
        return new MaxAggregator();
    }

    public static <I> Aggregator<I, BigDecimal> bigDecimalMax(String attributePath) {
        return new MaxAggregator(attributePath);
    }

    public static <I> Aggregator<I, BigInteger> bigIntegerMax() {
        return new MaxAggregator();
    }

    public static <I> Aggregator<I, BigInteger> bigIntegerMax(String attributePath) {
        return new MaxAggregator(attributePath);
    }

    public static <I> Aggregator<I, Double> doubleMax() {
        return new MaxAggregator();
    }

    public static <I> Aggregator<I, Double> doubleMax(String attributePath) {
        return new MaxAggregator(attributePath);
    }

    public static <I> Aggregator<I, Integer> integerMax() {
        return new MaxAggregator();
    }

    public static <I> Aggregator<I, Integer> integerMax(String attributePath) {
        return new MaxAggregator(attributePath);
    }

    public static <I> Aggregator<I, Long> longMax() {
        return new MaxAggregator();
    }

    public static <I> Aggregator<I, Long> longMax(String attributePath) {
        return new MaxAggregator(attributePath);
    }

    public static <I, R extends Comparable> Aggregator<I, R> comparableMax() {
        return new MaxAggregator();
    }

    public static <I, R extends Comparable> Aggregator<I, R> comparableMax(String attributePath) {
        return new MaxAggregator(attributePath);
    }

    public static <I> Aggregator<I, I> maxBy(String attributePath) {
        return new MaxByAggregator(attributePath);
    }

    public static <I> Aggregator<I, BigDecimal> bigDecimalMin() {
        return new MinAggregator();
    }

    public static <I> Aggregator<I, BigDecimal> bigDecimalMin(String attributePath) {
        return new MinAggregator(attributePath);
    }

    public static <I> Aggregator<I, BigInteger> bigIntegerMin() {
        return new MinAggregator();
    }

    public static <I> Aggregator<I, BigInteger> bigIntegerMin(String attributePath) {
        return new MinAggregator(attributePath);
    }

    public static <I> Aggregator<I, Double> doubleMin() {
        return new MinAggregator();
    }

    public static <I> Aggregator<I, Double> doubleMin(String attributePath) {
        return new MinAggregator(attributePath);
    }

    public static <I> Aggregator<I, Integer> integerMin() {
        return new MinAggregator();
    }

    public static <I> Aggregator<I, Integer> integerMin(String attributePath) {
        return new MinAggregator(attributePath);
    }

    public static <I> Aggregator<I, Long> longMin() {
        return new MinAggregator();
    }

    public static <I> Aggregator<I, Long> longMin(String attributePath) {
        return new MinAggregator(attributePath);
    }

    public static <I, R extends Comparable> Aggregator<I, R> comparableMin() {
        return new MinAggregator();
    }

    public static <I, R extends Comparable> Aggregator<I, R> comparableMin(String attributePath) {
        return new MinAggregator(attributePath);
    }

    public static <I> Aggregator<I, I> minBy(String attributePath) {
        return new MinByAggregator(attributePath);
    }

    public static <I> Aggregator<I, BigDecimal> bigDecimalSum() {
        return new BigDecimalSumAggregator();
    }

    public static <I> Aggregator<I, BigDecimal> bigDecimalSum(String attributePath) {
        return new BigDecimalSumAggregator(attributePath);
    }

    public static <I> Aggregator<I, BigInteger> bigIntegerSum() {
        return new BigIntegerSumAggregator();
    }

    public static <I> Aggregator<I, BigInteger> bigIntegerSum(String attributePath) {
        return new BigIntegerSumAggregator(attributePath);
    }

    public static <I> Aggregator<I, Double> doubleSum() {
        return new DoubleSumAggregator();
    }

    public static <I> Aggregator<I, Double> doubleSum(String attributePath) {
        return new DoubleSumAggregator(attributePath);
    }

    public static <I> Aggregator<I, Long> integerSum() {
        return new IntegerSumAggregator();
    }

    public static <I> Aggregator<I, Long> integerSum(String attributePath) {
        return new IntegerSumAggregator(attributePath);
    }

    public static <I> Aggregator<I, Long> longSum() {
        return new LongSumAggregator();
    }

    public static <I> Aggregator<I, Long> longSum(String attributePath) {
        return new LongSumAggregator(attributePath);
    }

    public static <I> Aggregator<I, Long> fixedPointSum() {
        return new FixedSumAggregator();
    }

    public static <I> Aggregator<I, Long> fixedPointSum(String attributePath) {
        return new FixedSumAggregator(attributePath);
    }

    public static <I> Aggregator<I, Double> floatingPointSum() {
        return new FloatingPointSumAggregator();
    }

    public static <I> Aggregator<I, Double> floatingPointSum(String attributePath) {
        return new FloatingPointSumAggregator(attributePath);
    }
}

