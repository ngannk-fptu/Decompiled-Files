/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.aggregation;

import com.hazelcast.mapreduce.Collator;
import com.hazelcast.mapreduce.CombinerFactory;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.ReducerFactory;
import com.hazelcast.mapreduce.aggregation.Aggregation;
import com.hazelcast.mapreduce.aggregation.Supplier;
import com.hazelcast.mapreduce.aggregation.impl.AggType;
import com.hazelcast.mapreduce.aggregation.impl.BigDecimalAvgAggregation;
import com.hazelcast.mapreduce.aggregation.impl.BigDecimalMaxAggregation;
import com.hazelcast.mapreduce.aggregation.impl.BigDecimalMinAggregation;
import com.hazelcast.mapreduce.aggregation.impl.BigDecimalSumAggregation;
import com.hazelcast.mapreduce.aggregation.impl.BigIntegerAvgAggregation;
import com.hazelcast.mapreduce.aggregation.impl.BigIntegerMaxAggregation;
import com.hazelcast.mapreduce.aggregation.impl.BigIntegerMinAggregation;
import com.hazelcast.mapreduce.aggregation.impl.BigIntegerSumAggregation;
import com.hazelcast.mapreduce.aggregation.impl.ComparableMaxAggregation;
import com.hazelcast.mapreduce.aggregation.impl.ComparableMinAggregation;
import com.hazelcast.mapreduce.aggregation.impl.CountAggregation;
import com.hazelcast.mapreduce.aggregation.impl.DistinctValuesAggregation;
import com.hazelcast.mapreduce.aggregation.impl.DoubleAvgAggregation;
import com.hazelcast.mapreduce.aggregation.impl.DoubleMaxAggregation;
import com.hazelcast.mapreduce.aggregation.impl.DoubleMinAggregation;
import com.hazelcast.mapreduce.aggregation.impl.DoubleSumAggregation;
import com.hazelcast.mapreduce.aggregation.impl.IntegerAvgAggregation;
import com.hazelcast.mapreduce.aggregation.impl.IntegerMaxAggregation;
import com.hazelcast.mapreduce.aggregation.impl.IntegerMinAggregation;
import com.hazelcast.mapreduce.aggregation.impl.IntegerSumAggregation;
import com.hazelcast.mapreduce.aggregation.impl.LongAvgAggregation;
import com.hazelcast.mapreduce.aggregation.impl.LongMaxAggregation;
import com.hazelcast.mapreduce.aggregation.impl.LongMinAggregation;
import com.hazelcast.mapreduce.aggregation.impl.LongSumAggregation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

@Deprecated
public final class Aggregations {
    private Aggregations() {
    }

    public static <Key, Value> Aggregation<Key, Value, Long> count() {
        return new AggregationAdapter(new CountAggregation());
    }

    public static <Key, Value, DistinctType> Aggregation<Key, Value, Set<DistinctType>> distinctValues() {
        DistinctValuesAggregation aggType = new DistinctValuesAggregation();
        return new AggregationAdapter(aggType);
    }

    public static <Key, Value> Aggregation<Key, Value, Integer> integerAvg() {
        return new AggregationAdapter(new IntegerAvgAggregation());
    }

    public static <Key, Value> Aggregation<Key, Integer, Integer> integerSum() {
        return new AggregationAdapter(new IntegerSumAggregation());
    }

    public static <Key, Value> Aggregation<Key, Integer, Integer> integerMin() {
        return new AggregationAdapter(new IntegerMinAggregation());
    }

    public static <Key, Value> Aggregation<Key, Integer, Integer> integerMax() {
        return new AggregationAdapter(new IntegerMaxAggregation());
    }

    public static <Key, Value> Aggregation<Key, Long, Long> longAvg() {
        return new AggregationAdapter(new LongAvgAggregation());
    }

    public static <Key, Value> Aggregation<Key, Long, Long> longSum() {
        return new AggregationAdapter(new LongSumAggregation());
    }

    public static <Key, Value> Aggregation<Key, Long, Long> longMin() {
        return new AggregationAdapter(new LongMinAggregation());
    }

    public static <Key, Value> Aggregation<Key, Long, Long> longMax() {
        return new AggregationAdapter(new LongMaxAggregation());
    }

    public static <Key, Value> Aggregation<Key, Double, Double> doubleAvg() {
        return new AggregationAdapter(new DoubleAvgAggregation());
    }

    public static <Key, Value> Aggregation<Key, Double, Double> doubleSum() {
        return new AggregationAdapter(new DoubleSumAggregation());
    }

    public static <Key, Value> Aggregation<Key, Double, Double> doubleMin() {
        return new AggregationAdapter(new DoubleMinAggregation());
    }

    public static <Key, Value> Aggregation<Key, Double, Double> doubleMax() {
        return new AggregationAdapter(new DoubleMaxAggregation());
    }

    public static <Key, Value> Aggregation<Key, BigDecimal, BigDecimal> bigDecimalAvg() {
        return new AggregationAdapter(new BigDecimalAvgAggregation());
    }

    public static <Key, Value> Aggregation<Key, BigDecimal, BigDecimal> bigDecimalSum() {
        return new AggregationAdapter(new BigDecimalSumAggregation());
    }

    public static <Key, Value> Aggregation<Key, BigDecimal, BigDecimal> bigDecimalMin() {
        return new AggregationAdapter(new BigDecimalMinAggregation());
    }

    public static <Key, Value> Aggregation<Key, BigDecimal, BigDecimal> bigDecimalMax() {
        return new AggregationAdapter(new BigDecimalMaxAggregation());
    }

    public static <Key, Value> Aggregation<Key, BigInteger, BigInteger> bigIntegerAvg() {
        return new AggregationAdapter(new BigIntegerAvgAggregation());
    }

    public static <Key, Value> Aggregation<Key, BigInteger, BigInteger> bigIntegerSum() {
        return new AggregationAdapter(new BigIntegerSumAggregation());
    }

    public static <Key, Value> Aggregation<Key, BigInteger, BigInteger> bigIntegerMin() {
        return new AggregationAdapter(new BigIntegerMinAggregation());
    }

    public static <Key, Value> Aggregation<Key, BigInteger, BigInteger> bigIntegerMax() {
        return new AggregationAdapter(new BigIntegerMaxAggregation());
    }

    public static <Key, Value> Aggregation<Key, Comparable, Comparable> comparableMin() {
        return new AggregationAdapter(new ComparableMinAggregation());
    }

    public static <Key, Value> Aggregation<Key, Comparable, Comparable> comparableMax() {
        return new AggregationAdapter(new ComparableMaxAggregation());
    }

    private static final class AggregationAdapter<Key, Supplied, Result>
    implements Aggregation<Key, Supplied, Result> {
        private final AggType internalAggregationType;

        private AggregationAdapter(AggType internalAggregationType) {
            this.internalAggregationType = internalAggregationType;
        }

        @Override
        public Collator<Map.Entry, Result> getCollator() {
            return this.internalAggregationType.getCollator();
        }

        @Override
        public Mapper getMapper(Supplier<Key, ?, Supplied> supplier) {
            return this.internalAggregationType.getMapper(supplier);
        }

        @Override
        public CombinerFactory getCombinerFactory() {
            return this.internalAggregationType.getCombinerFactory();
        }

        @Override
        public ReducerFactory getReducerFactory() {
            return this.internalAggregationType.getReducerFactory();
        }
    }
}

