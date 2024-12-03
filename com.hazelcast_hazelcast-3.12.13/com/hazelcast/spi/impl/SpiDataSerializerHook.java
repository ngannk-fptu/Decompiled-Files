/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl;

import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.DistributedObjectNamespace;
import com.hazelcast.spi.OperationControl;
import com.hazelcast.spi.impl.BinaryOperationFactory;
import com.hazelcast.spi.impl.SerializableList;
import com.hazelcast.spi.impl.UnmodifiableLazyList;
import com.hazelcast.spi.impl.eventservice.impl.EventEnvelope;
import com.hazelcast.spi.impl.eventservice.impl.TrueEventFilter;
import com.hazelcast.spi.impl.eventservice.impl.operations.DeregistrationOperation;
import com.hazelcast.spi.impl.eventservice.impl.operations.OnJoinRegistrationOperation;
import com.hazelcast.spi.impl.eventservice.impl.operations.RegistrationOperation;
import com.hazelcast.spi.impl.eventservice.impl.operations.SendEventOperation;
import com.hazelcast.spi.impl.operationservice.impl.operations.Backup;
import com.hazelcast.spi.impl.operationservice.impl.operations.PartitionIteratingOperation;
import com.hazelcast.spi.impl.operationservice.impl.responses.BackupAckResponse;
import com.hazelcast.spi.impl.operationservice.impl.responses.CallTimeoutResponse;
import com.hazelcast.spi.impl.operationservice.impl.responses.ErrorResponse;
import com.hazelcast.spi.impl.operationservice.impl.responses.NormalResponse;
import com.hazelcast.spi.impl.proxyservice.impl.operations.DistributedObjectDestroyOperation;
import com.hazelcast.spi.impl.proxyservice.impl.operations.InitializeDistributedObjectOperation;
import com.hazelcast.spi.impl.proxyservice.impl.operations.PostJoinProxyOperation;

public final class SpiDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.spi", -1);
    public static final int NORMAL_RESPONSE = 0;
    public static final int BACKUP = 1;
    public static final int BACKUP_ACK_RESPONSE = 2;
    public static final int PARTITION_ITERATOR = 3;
    public static final int PARTITION_RESPONSE = 4;
    public static final int PARALLEL_OPERATION_FACTORY = 5;
    public static final int EVENT_ENVELOPE = 6;
    public static final int COLLECTION = 7;
    public static final int CALL_TIMEOUT_RESPONSE = 8;
    public static final int ERROR_RESPONSE = 9;
    public static final int DEREGISTRATION = 10;
    public static final int ON_JOIN_REGISTRATION = 11;
    public static final int REGISTRATION = 12;
    public static final int SEND_EVENT = 13;
    public static final int DIST_OBJECT_INIT = 14;
    public static final int DIST_OBJECT_DESTROY = 15;
    public static final int POST_JOIN_PROXY = 16;
    public static final int TRUE_EVENT_FILTER = 17;
    public static final int UNMODIFIABLE_LAZY_LIST = 18;
    public static final int OPERATION_CONTROL = 19;
    public static final int DISTRIBUTED_OBJECT_NS = 20;
    private static final DataSerializableFactory FACTORY = SpiDataSerializerHook.createFactoryInternal();

    @Override
    public DataSerializableFactory createFactory() {
        return FACTORY;
    }

    private static DataSerializableFactory createFactoryInternal() {
        return new DataSerializableFactory(){

            @Override
            public IdentifiedDataSerializable create(int typeId) {
                switch (typeId) {
                    case 0: {
                        return new NormalResponse();
                    }
                    case 1: {
                        return new Backup();
                    }
                    case 2: {
                        return new BackupAckResponse();
                    }
                    case 3: {
                        return new PartitionIteratingOperation();
                    }
                    case 4: {
                        return new PartitionIteratingOperation.PartitionResponse();
                    }
                    case 5: {
                        return new BinaryOperationFactory();
                    }
                    case 6: {
                        return new EventEnvelope();
                    }
                    case 7: {
                        return new SerializableList();
                    }
                    case 8: {
                        return new CallTimeoutResponse();
                    }
                    case 9: {
                        return new ErrorResponse();
                    }
                    case 10: {
                        return new DeregistrationOperation();
                    }
                    case 11: {
                        return new OnJoinRegistrationOperation();
                    }
                    case 12: {
                        return new RegistrationOperation();
                    }
                    case 13: {
                        return new SendEventOperation();
                    }
                    case 14: {
                        return new InitializeDistributedObjectOperation();
                    }
                    case 15: {
                        return new DistributedObjectDestroyOperation();
                    }
                    case 16: {
                        return new PostJoinProxyOperation();
                    }
                    case 17: {
                        return new TrueEventFilter();
                    }
                    case 18: {
                        return new UnmodifiableLazyList();
                    }
                    case 19: {
                        return new OperationControl();
                    }
                    case 20: {
                        return new DistributedObjectNamespace();
                    }
                }
                return null;
            }
        };
    }

    @Override
    public int getFactoryId() {
        return F_ID;
    }
}

