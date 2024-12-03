/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.aggregation.impl;

import com.hazelcast.aggregation.impl.BigDecimalAverageAggregator;
import com.hazelcast.aggregation.impl.BigDecimalSumAggregator;
import com.hazelcast.aggregation.impl.BigIntegerAverageAggregator;
import com.hazelcast.aggregation.impl.BigIntegerSumAggregator;
import com.hazelcast.aggregation.impl.CanonicalizingHashSet;
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
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.ArrayDataSerializableFactory;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.ConstructorFunction;

public final class AggregatorDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.aggregator", -41);
    public static final int BIG_DECIMAL_AVG = 0;
    public static final int BIG_DECIMAL_SUM = 1;
    public static final int BIG_INT_AVG = 2;
    public static final int BIG_INT_SUM = 3;
    public static final int COUNT = 4;
    public static final int DISTINCT_VALUES = 5;
    public static final int DOUBLE_AVG = 6;
    public static final int DOUBLE_SUM = 7;
    public static final int FIXED_SUM = 8;
    public static final int FLOATING_POINT_SUM = 9;
    public static final int INT_AVG = 10;
    public static final int INT_SUM = 11;
    public static final int LONG_AVG = 12;
    public static final int LONG_SUM = 13;
    public static final int MAX = 14;
    public static final int MIN = 15;
    public static final int NUMBER_AVG = 16;
    public static final int MAX_BY = 17;
    public static final int MIN_BY = 18;
    public static final int CANONICALIZING_SET = 19;
    private static final int LEN = 20;

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public DataSerializableFactory createFactory() {
        ConstructorFunction[] constructors = new ConstructorFunction[]{new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new BigDecimalAverageAggregator();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new BigDecimalSumAggregator();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new BigIntegerAverageAggregator();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new BigIntegerSumAggregator();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CountAggregator();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new DistinctValuesAggregator();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new DoubleAverageAggregator();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new DoubleSumAggregator();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new FixedSumAggregator();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new FloatingPointSumAggregator();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new IntegerAverageAggregator();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new IntegerSumAggregator();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new LongAverageAggregator();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new LongSumAggregator();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MaxAggregator();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MinAggregator();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new NumberAverageAggregator();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MaxByAggregator();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MinByAggregator();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CanonicalizingHashSet();
            }
        }};
        return new ArrayDataSerializableFactory(constructors);
    }
}

