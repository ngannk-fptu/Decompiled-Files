/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.operation.CacheGetConfigOperation;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheGetConfigCodec;
import com.hazelcast.client.impl.protocol.task.AbstractAddressMessageTask;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.config.LegacyCacheConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.properties.GroupProperty;
import java.security.Permission;

public class CacheGetConfigMessageTask
extends AbstractAddressMessageTask<CacheGetConfigCodec.RequestParameters> {
    public CacheGetConfigMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Address getAddress() {
        return this.nodeEngine.getThisAddress();
    }

    @Override
    protected Operation prepareOperation() {
        return new CacheGetConfigOperation(((CacheGetConfigCodec.RequestParameters)this.parameters).name, ((CacheGetConfigCodec.RequestParameters)this.parameters).simpleName);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    protected CacheGetConfigCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheGetConfigCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        Data responseData = this.serializeCacheConfig(response);
        return CacheGetConfigCodec.encodeResponse(responseData);
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheGetConfigCodec.RequestParameters)this.parameters).name;
    }

    private Data serializeCacheConfig(Object response) {
        boolean compatibilityEnabled;
        Data responseData = null;
        if (-1 == this.endpoint.getClientVersion() && (compatibilityEnabled = this.nodeEngine.getProperties().getBoolean(GroupProperty.COMPATIBILITY_3_6_CLIENT_ENABLED))) {
            responseData = this.nodeEngine.toData(response == null ? null : new LegacyCacheConfig((CacheConfig)response));
        }
        if (null == responseData) {
            responseData = this.nodeEngine.toData(response);
        }
        return responseData;
    }
}

