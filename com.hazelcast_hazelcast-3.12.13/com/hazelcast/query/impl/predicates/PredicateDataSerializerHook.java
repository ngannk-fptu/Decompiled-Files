/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.ArrayDataSerializableFactory;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.PartitionPredicate;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.query.TruePredicate;
import com.hazelcast.query.impl.CompositeValue;
import com.hazelcast.query.impl.FalsePredicate;
import com.hazelcast.query.impl.IndexImpl;
import com.hazelcast.query.impl.predicates.AndPredicate;
import com.hazelcast.query.impl.predicates.BetweenPredicate;
import com.hazelcast.query.impl.predicates.EqualPredicate;
import com.hazelcast.query.impl.predicates.GreaterLessPredicate;
import com.hazelcast.query.impl.predicates.ILikePredicate;
import com.hazelcast.query.impl.predicates.InPredicate;
import com.hazelcast.query.impl.predicates.InstanceOfPredicate;
import com.hazelcast.query.impl.predicates.LikePredicate;
import com.hazelcast.query.impl.predicates.NotEqualPredicate;
import com.hazelcast.query.impl.predicates.NotPredicate;
import com.hazelcast.query.impl.predicates.OrPredicate;
import com.hazelcast.query.impl.predicates.RegexPredicate;
import com.hazelcast.util.ConstructorFunction;

public class PredicateDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.predicate", -32);
    public static final int SQL_PREDICATE = 0;
    public static final int AND_PREDICATE = 1;
    public static final int BETWEEN_PREDICATE = 2;
    public static final int EQUAL_PREDICATE = 3;
    public static final int GREATERLESS_PREDICATE = 4;
    public static final int LIKE_PREDICATE = 5;
    public static final int ILIKE_PREDICATE = 6;
    public static final int IN_PREDICATE = 7;
    public static final int INSTANCEOF_PREDICATE = 8;
    public static final int NOTEQUAL_PREDICATE = 9;
    public static final int NOT_PREDICATE = 10;
    public static final int OR_PREDICATE = 11;
    public static final int REGEX_PREDICATE = 12;
    public static final int FALSE_PREDICATE = 13;
    public static final int TRUE_PREDICATE = 14;
    public static final int PAGING_PREDICATE = 15;
    public static final int PARTITION_PREDICATE = 16;
    public static final int NULL_OBJECT = 17;
    public static final int COMPOSITE_VALUE = 18;
    public static final int NEGATIVE_INFINITY = 19;
    public static final int POSITIVE_INFINITY = 20;
    public static final int LEN = 21;

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public DataSerializableFactory createFactory() {
        ConstructorFunction[] constructors = new ConstructorFunction[]{new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new SqlPredicate();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new AndPredicate();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new BetweenPredicate();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new EqualPredicate();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new GreaterLessPredicate();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new LikePredicate();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ILikePredicate();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new InPredicate();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new InstanceOfPredicate();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new NotEqualPredicate();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new NotPredicate();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new OrPredicate();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new RegexPredicate();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return FalsePredicate.INSTANCE;
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return TruePredicate.INSTANCE;
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PagingPredicate();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PartitionPredicate();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return IndexImpl.NULL;
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CompositeValue();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return CompositeValue.NEGATIVE_INFINITY;
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return CompositeValue.POSITIVE_INFINITY;
            }
        }};
        return new ArrayDataSerializableFactory(constructors);
    }
}

