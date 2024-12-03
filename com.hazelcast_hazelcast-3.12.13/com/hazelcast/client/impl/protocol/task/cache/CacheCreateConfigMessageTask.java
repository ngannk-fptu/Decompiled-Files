/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.cache.impl.PreJoinCacheConfig;
import com.hazelcast.cache.impl.merge.policy.CacheMergePolicyProvider;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheCreateConfigCodec;
import com.hazelcast.client.impl.protocol.task.AbstractMessageTask;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.config.LegacyCacheConfig;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.config.ConfigValidator;
import com.hazelcast.internal.config.MergePolicyValidator;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.properties.GroupProperty;
import java.security.Permission;

public class CacheCreateConfigMessageTask
extends AbstractMessageTask<CacheCreateConfigCodec.RequestParameters>
implements ExecutionCallback {
    public CacheCreateConfigMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        CacheConfig cacheConfig = this.extractCacheConfigFromMessage();
        CacheService cacheService = (CacheService)this.getService("hz:impl:cacheService");
        if (cacheConfig != null) {
            CacheMergePolicyProvider mergePolicyProvider = cacheService.getMergePolicyProvider();
            ConfigValidator.checkCacheConfig(cacheConfig, mergePolicyProvider);
            Object mergePolicy = mergePolicyProvider.getMergePolicy(cacheConfig.getMergePolicy());
            MergePolicyValidator.checkMergePolicySupportsInMemoryFormat(cacheConfig.getName(), mergePolicy, cacheConfig.getInMemoryFormat(), true, this.logger);
            ICompletableFuture future = cacheService.createCacheConfigOnAllMembersAsync(PreJoinCacheConfig.of(cacheConfig));
            future.andThen(this);
        } else {
            this.sendResponse(null);
        }
    }

    private CacheConfig extractCacheConfigFromMessage() {
        boolean compatibilityEnabled;
        int clientVersion = this.endpoint.getClientVersion();
        if (-1 == clientVersion && (compatibilityEnabled = this.nodeEngine.getProperties().getBoolean(GroupProperty.COMPATIBILITY_3_6_CLIENT_ENABLED))) {
            LegacyCacheConfig legacyCacheConfig = (LegacyCacheConfig)this.nodeEngine.toObject(((CacheCreateConfigCodec.RequestParameters)this.parameters).cacheConfig, LegacyCacheConfig.class);
            if (null == legacyCacheConfig) {
                return null;
            }
            return legacyCacheConfig.getConfigAndReset();
        }
        return (CacheConfig)this.nodeEngine.toObject(((CacheCreateConfigCodec.RequestParameters)this.parameters).cacheConfig);
    }

    @Override
    protected CacheCreateConfigCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheCreateConfigCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        Data responseData = this.serializeCacheConfig(response);
        return CacheCreateConfigCodec.encodeResponse(responseData);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getDistributedObjectName() {
        return null;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    public void onResponse(Object response) {
        this.sendResponse(response);
    }

    @Override
    public void onFailure(Throwable t) {
        this.handleProcessingFailure(t);
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

