/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.aggregation.impl;

import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.ArrayDataSerializableFactory;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.mapreduce.aggregation.impl.AcceptAllSupplier;
import com.hazelcast.mapreduce.aggregation.impl.AvgTuple;
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
import com.hazelcast.mapreduce.aggregation.impl.KeyPredicateSupplier;
import com.hazelcast.mapreduce.aggregation.impl.LongAvgAggregation;
import com.hazelcast.mapreduce.aggregation.impl.LongMaxAggregation;
import com.hazelcast.mapreduce.aggregation.impl.LongMinAggregation;
import com.hazelcast.mapreduce.aggregation.impl.LongSumAggregation;
import com.hazelcast.mapreduce.aggregation.impl.PredicateSupplier;
import com.hazelcast.mapreduce.aggregation.impl.SetAdapter;
import com.hazelcast.mapreduce.aggregation.impl.SupplierConsumingMapper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.ConstructorFunction;

public class AggregationsDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.aggregations", -24);
    public static final int SUPPLIER_CONSUMING_MAPPER = 0;
    public static final int ACCEPT_ALL_SUPPLIER = 1;
    public static final int DISTINCT_VALUES_MAPPER = 2;
    public static final int SET_ADAPTER = 3;
    public static final int BIG_DECIMAL_AVG_COMBINER_FACTORY = 4;
    public static final int BIG_DECIMAL_AVG_REDUCER_FACTORY = 5;
    public static final int BIG_DECIMAL_MAX_COMBINER_FACTORY = 6;
    public static final int BIG_DECIMAL_MAX_REDUCER_FACTORY = 7;
    public static final int BIG_DECIMAL_MIN_COMBINER_FACTORY = 8;
    public static final int BIG_DECIMAL_MIN_REDUCER_FACTORY = 9;
    public static final int BIG_DECIMAL_SUM_COMBINER_FACTORY = 10;
    public static final int BIG_DECIMAL_SUM_REDUCER_FACTORY = 11;
    public static final int BIG_INTEGER_AVG_COMBINER_FACTORY = 12;
    public static final int BIG_INTEGER_AVG_REDUCER_FACTORY = 13;
    public static final int BIG_INTEGER_MAX_COMBINER_FACTORY = 14;
    public static final int BIG_INTEGER_MAX_REDUCER_FACTORY = 15;
    public static final int BIG_INTEGER_MIN_COMBINER_FACTORY = 16;
    public static final int BIG_INTEGER_MIN_REDUCER_FACTORY = 17;
    public static final int BIG_INTEGER_SUM_COMBINER_FACTORY = 18;
    public static final int BIG_INTEGER_SUM_REDUCER_FACTORY = 19;
    public static final int COMPARABLE_MAX_COMBINER_FACTORY = 20;
    public static final int COMPARABLE_MAX_REDUCER_FACTORY = 21;
    public static final int COMPARABLE_MIN_COMBINER_FACTORY = 22;
    public static final int COMPARABLE_MIN_REDUCER_FACTORY = 23;
    public static final int COUNT_COMBINER_FACTORY = 24;
    public static final int COUNT_REDUCER_FACTORY = 25;
    public static final int DISTINCT_VALUES_COMBINER_FACTORY = 26;
    public static final int DISTINCT_VALUES_REDUCER_FACTORY = 27;
    public static final int DOUBLE_AVG_COMBINER_FACTORY = 28;
    public static final int DOUBLE_AVG_REDUCER_FACTORY = 29;
    public static final int DOUBLE_MAX_COMBINER_FACTORY = 30;
    public static final int DOUBLE_MAX_REDUCER_FACTORY = 31;
    public static final int DOUBLE_MIN_COMBINER_FACTORY = 32;
    public static final int DOUBLE_MIN_REDUCER_FACTORY = 33;
    public static final int DOUBLE_SUM_COMBINER_FACTORY = 34;
    public static final int DOUBLE_SUM_REDUCER_FACTORY = 35;
    public static final int INTEGER_AVG_COMBINER_FACTORY = 36;
    public static final int INTEGER_AVG_REDUCER_FACTORY = 37;
    public static final int INTEGER_MAX_COMBINER_FACTORY = 38;
    public static final int INTEGER_MAX_REDUCER_FACTORY = 39;
    public static final int INTEGER_MIN_COMBINER_FACTORY = 40;
    public static final int INTEGER_MIN_REDUCER_FACTORY = 41;
    public static final int INTEGER_SUM_COMBINER_FACTORY = 42;
    public static final int INTEGER_SUM_REDUCER_FACTORY = 43;
    public static final int LONG_AVG_COMBINER_FACTORY = 44;
    public static final int LONG_AVG_REDUCER_FACTORY = 45;
    public static final int LONG_MAX_COMBINER_FACTORY = 46;
    public static final int LONG_MAX_REDUCER_FACTORY = 47;
    public static final int LONG_MIN_COMBINER_FACTORY = 48;
    public static final int LONG_MIN_REDUCER_FACTORY = 49;
    public static final int LONG_SUM_COMBINER_FACTORY = 50;
    public static final int LONG_SUM_REDUCER_FACTORY = 51;
    public static final int KEY_PREDICATE_SUPPLIER = 52;
    public static final int PREDICATE_SUPPLIER = 53;
    public static final int AVG_TUPLE = 54;
    private static final int LEN = 55;

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public DataSerializableFactory createFactory() {
        ConstructorFunction[] constructors = new ConstructorFunction[55];
        constructors[0] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new SupplierConsumingMapper();
            }
        };
        constructors[1] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new AcceptAllSupplier();
            }
        };
        constructors[2] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new DistinctValuesAggregation.DistinctValueMapper();
            }
        };
        constructors[3] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new SetAdapter();
            }
        };
        constructors[54] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new AvgTuple();
            }
        };
        constructors[4] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new BigDecimalAvgAggregation.BigDecimalAvgCombinerFactory();
            }
        };
        constructors[5] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new BigDecimalAvgAggregation.BigDecimalAvgReducerFactory();
            }
        };
        constructors[6] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new BigDecimalMaxAggregation.BigDecimalMaxCombinerFactory();
            }
        };
        constructors[7] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new BigDecimalMaxAggregation.BigDecimalMaxReducerFactory();
            }
        };
        constructors[8] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new BigDecimalMinAggregation.BigDecimalMinCombinerFactory();
            }
        };
        constructors[9] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new BigDecimalMinAggregation.BigDecimalMinReducerFactory();
            }
        };
        constructors[10] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new BigDecimalSumAggregation.BigDecimalSumCombinerFactory();
            }
        };
        constructors[11] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new BigDecimalSumAggregation.BigDecimalSumReducerFactory();
            }
        };
        constructors[12] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new BigIntegerAvgAggregation.BigIntegerAvgCombinerFactory();
            }
        };
        constructors[13] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new BigIntegerAvgAggregation.BigIntegerAvgReducerFactory();
            }
        };
        constructors[14] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new BigIntegerMaxAggregation.BigIntegerMaxCombinerFactory();
            }
        };
        constructors[15] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new BigIntegerMaxAggregation.BigIntegerMaxReducerFactory();
            }
        };
        constructors[16] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new BigIntegerMinAggregation.BigIntegerMinCombinerFactory();
            }
        };
        constructors[17] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new BigIntegerMinAggregation.BigIntegerMinReducerFactory();
            }
        };
        constructors[18] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new BigIntegerSumAggregation.BigIntegerSumCombinerFactory();
            }
        };
        constructors[19] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new BigIntegerSumAggregation.BigIntegerSumReducerFactory();
            }
        };
        constructors[20] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ComparableMaxAggregation.ComparableMaxCombinerFactory();
            }
        };
        constructors[21] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ComparableMaxAggregation.ComparableMaxReducerFactory();
            }
        };
        constructors[22] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ComparableMinAggregation.ComparableMinCombinerFactory();
            }
        };
        constructors[23] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ComparableMinAggregation.ComparableMinReducerFactory();
            }
        };
        constructors[24] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CountAggregation.CountCombinerFactory();
            }
        };
        constructors[25] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CountAggregation.CountReducerFactory();
            }
        };
        constructors[26] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new DistinctValuesAggregation.DistinctValuesCombinerFactory();
            }
        };
        constructors[27] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new DistinctValuesAggregation.DistinctValuesReducerFactory();
            }
        };
        constructors[28] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new DoubleAvgAggregation.DoubleAvgCombinerFactory();
            }
        };
        constructors[29] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new DoubleAvgAggregation.DoubleAvgReducerFactory();
            }
        };
        constructors[30] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new DoubleMaxAggregation.DoubleMaxCombinerFactory();
            }
        };
        constructors[31] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new DoubleMaxAggregation.DoubleMaxReducerFactory();
            }
        };
        constructors[32] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new DoubleMinAggregation.DoubleMinCombinerFactory();
            }
        };
        constructors[33] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new DoubleMinAggregation.DoubleMinReducerFactory();
            }
        };
        constructors[34] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new DoubleSumAggregation.DoubleSumCombinerFactory();
            }
        };
        constructors[35] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new DoubleSumAggregation.DoubleSumReducerFactory();
            }
        };
        constructors[36] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new IntegerAvgAggregation.IntegerAvgCombinerFactory();
            }
        };
        constructors[37] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new IntegerAvgAggregation.IntegerAvgReducerFactory();
            }
        };
        constructors[38] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new IntegerMaxAggregation.IntegerMaxCombinerFactory();
            }
        };
        constructors[39] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new IntegerMaxAggregation.IntegerMaxReducerFactory();
            }
        };
        constructors[40] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new IntegerMinAggregation.IntegerMinCombinerFactory();
            }
        };
        constructors[41] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new IntegerMinAggregation.IntegerMinReducerFactory();
            }
        };
        constructors[42] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new IntegerSumAggregation.IntegerSumCombinerFactory();
            }
        };
        constructors[43] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new IntegerSumAggregation.IntegerSumReducerFactory();
            }
        };
        constructors[44] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new LongAvgAggregation.LongAvgCombinerFactory();
            }
        };
        constructors[45] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new LongAvgAggregation.LongAvgReducerFactory();
            }
        };
        constructors[46] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new LongMaxAggregation.LongMaxCombinerFactory();
            }
        };
        constructors[47] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new LongMaxAggregation.LongMaxReducerFactory();
            }
        };
        constructors[48] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new LongMinAggregation.LongMinCombinerFactory();
            }
        };
        constructors[49] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new LongMinAggregation.LongMinReducerFactory();
            }
        };
        constructors[50] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new LongSumAggregation.LongSumCombinerFactory();
            }
        };
        constructors[51] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new LongSumAggregation.LongSumReducerFactory();
            }
        };
        constructors[52] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new KeyPredicateSupplier();
            }
        };
        constructors[53] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PredicateSupplier();
            }
        };
        return new ArrayDataSerializableFactory(constructors);
    }
}

