/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.cache.impl.JCacheDetector;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.exception.ServiceNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OnJoinCacheOperation
extends Operation
implements IdentifiedDataSerializable {
    private List<CacheConfig> configs = new ArrayList<CacheConfig>();

    public void addCacheConfig(CacheConfig cacheConfig) {
        this.configs.add(cacheConfig);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public void run() throws Exception {
        if (JCacheDetector.isJCacheAvailable(this.getNodeEngine().getConfigClassLoader())) {
            ICacheService cacheService = (ICacheService)this.getService();
            for (CacheConfig cacheConfig : this.configs) {
                cacheService.putCacheConfigIfAbsent(cacheConfig);
            }
        } else if (this.configs.isEmpty()) {
            this.getLogger().warning("This member is joining a cluster whose members support JCache, however the cache-api artifact is missing from this member's classpath. In case JCache API will be used, add cache-api artifact in this member's classpath and restart the member.");
        } else {
            this.getLogger().severe("This member cannot support JCache because the cache-api artifact is missing from its classpath. Add the JCache API JAR in the classpath and restart the member.");
            throw new HazelcastException("Service with name 'hz:impl:cacheService' not found!", new ServiceNotFoundException("Service with name 'hz:impl:cacheService' not found!"));
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.configs.size());
        for (CacheConfig config : this.configs) {
            out.writeObject(config);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int confSize = in.readInt();
        for (int i = 0; i < confSize; ++i) {
            CacheConfig config = (CacheConfig)in.readObject();
            this.configs.add(config);
        }
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 46;
    }
}

