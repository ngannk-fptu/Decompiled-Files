/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl;

import com.hazelcast.client.impl.operations.ClientDisconnectionOperation;
import com.hazelcast.client.impl.operations.ClientReAuthOperation;
import com.hazelcast.client.impl.operations.GetConnectedClientsOperation;
import com.hazelcast.client.impl.operations.OnJoinClientOperation;
import com.hazelcast.client.impl.operations.OperationFactoryWrapper;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class ClientDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.client", -3);
    public static final int CLIENT_DISCONNECT = 0;
    public static final int RE_AUTH = 1;
    public static final int GET_CONNECTED_CLIENTS = 2;
    public static final int ON_JOIN = 3;
    public static final int OP_FACTORY_WRAPPER = 4;

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
                        return new ClientDisconnectionOperation();
                    }
                    case 1: {
                        return new ClientReAuthOperation();
                    }
                    case 2: {
                        return new GetConnectedClientsOperation();
                    }
                    case 3: {
                        return new OnJoinClientOperation();
                    }
                    case 4: {
                        return new OperationFactoryWrapper();
                    }
                }
                return null;
            }
        };
    }
}

