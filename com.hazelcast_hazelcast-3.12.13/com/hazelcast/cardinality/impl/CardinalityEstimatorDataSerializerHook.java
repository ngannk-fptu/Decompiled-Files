/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cardinality.impl;

import com.hazelcast.cardinality.impl.CardinalityEstimatorContainer;
import com.hazelcast.cardinality.impl.hyperloglog.impl.DenseHyperLogLogEncoder;
import com.hazelcast.cardinality.impl.hyperloglog.impl.HyperLogLogImpl;
import com.hazelcast.cardinality.impl.hyperloglog.impl.SparseHyperLogLogEncoder;
import com.hazelcast.cardinality.impl.operations.AggregateBackupOperation;
import com.hazelcast.cardinality.impl.operations.AggregateOperation;
import com.hazelcast.cardinality.impl.operations.EstimateOperation;
import com.hazelcast.cardinality.impl.operations.MergeBackupOperation;
import com.hazelcast.cardinality.impl.operations.MergeOperation;
import com.hazelcast.cardinality.impl.operations.ReplicationOperation;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public final class CardinalityEstimatorDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.cardinality_estimator", -33);
    public static final int ADD = 0;
    public static final int ESTIMATE = 1;
    public static final int AGGREGATE_BACKUP = 2;
    public static final int REPLICATION = 3;
    public static final int CARDINALITY_EST_CONTAINER = 4;
    public static final int HLL = 5;
    public static final int HLL_DENSE_ENC = 6;
    public static final int HLL_SPARSE_ENC = 7;
    public static final int MERGE = 8;
    public static final int MERGE_BACKUP = 9;

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public DataSerializableFactory createFactory() {
        return new DataSerializableFactory(){

            @Override
            public IdentifiedDataSerializable create(int typeId) {
                switch (typeId) {
                    case 0: {
                        return new AggregateOperation();
                    }
                    case 1: {
                        return new EstimateOperation();
                    }
                    case 2: {
                        return new AggregateBackupOperation();
                    }
                    case 3: {
                        return new ReplicationOperation();
                    }
                    case 4: {
                        return new CardinalityEstimatorContainer();
                    }
                    case 5: {
                        return new HyperLogLogImpl();
                    }
                    case 6: {
                        return new DenseHyperLogLogEncoder();
                    }
                    case 7: {
                        return new SparseHyperLogLogEncoder();
                    }
                    case 8: {
                        return new MergeOperation();
                    }
                    case 9: {
                        return new MergeBackupOperation();
                    }
                }
                return null;
            }
        };
    }
}

