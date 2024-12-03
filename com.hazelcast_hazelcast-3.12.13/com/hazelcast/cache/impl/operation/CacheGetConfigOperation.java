/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.AbstractCacheService;
import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.PreJoinCacheConfig;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.impl.AbstractNamedOperation;
import java.io.IOException;

public class CacheGetConfigOperation
extends AbstractNamedOperation
implements IdentifiedDataSerializable,
ReadonlyOperation {
    private volatile transient Object response;
    private transient ICompletableFuture createOnAllMembersFuture;
    private String simpleName;

    public CacheGetConfigOperation() {
    }

    public CacheGetConfigOperation(String name, String simpleName) {
        super(name);
        this.simpleName = simpleName;
    }

    @Override
    public void run() throws Exception {
        AbstractCacheService service = (AbstractCacheService)this.getService();
        CacheConfig cacheConfig = service.getCacheConfig(this.name);
        if (cacheConfig == null && (cacheConfig = service.findCacheConfig(this.simpleName)) != null) {
            cacheConfig.setManagerPrefix(this.name.substring(0, this.name.lastIndexOf(this.simpleName)));
            CacheConfig existingCacheConfig = service.putCacheConfigIfAbsent(cacheConfig);
            if (existingCacheConfig != null) {
                cacheConfig = existingCacheConfig;
            } else {
                this.createOnAllMembersFuture = service.createCacheConfigOnAllMembersAsync(PreJoinCacheConfig.of(cacheConfig));
            }
        }
        this.response = cacheConfig;
        if (this.createOnAllMembersFuture != null) {
            this.createOnAllMembersFuture.andThen(new ExecutionCallback(){

                public void onResponse(Object asyncResponse) {
                    CacheGetConfigOperation.this.sendResponse(CacheGetConfigOperation.this.response);
                }

                @Override
                public void onFailure(Throwable t) {
                    CacheGetConfigOperation.this.sendResponse(t);
                }
            });
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.simpleName);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.simpleName = in.readUTF();
    }

    @Override
    public boolean returnsResponse() {
        return this.createOnAllMembersFuture == null;
    }

    @Override
    public int getId() {
        return 27;
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public Object getResponse() {
        return this.response;
    }
}

