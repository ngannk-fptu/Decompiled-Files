/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.core.Member;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.impl.AbstractNamedOperation;
import com.hazelcast.spi.impl.SimpleExecutionCallback;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Deprecated
public class CacheCreateConfigOperation
extends AbstractNamedOperation
implements IdentifiedDataSerializable {
    private CacheConfig config;
    private boolean createAlsoOnOthers = true;
    private boolean ignoreLocal;
    private boolean returnsResponse = true;
    private transient Object response;

    public CacheCreateConfigOperation() {
    }

    public CacheCreateConfigOperation(CacheConfig config, boolean createAlsoOnOthers) {
        this(config, createAlsoOnOthers, false);
    }

    public CacheCreateConfigOperation(CacheConfig config, boolean createAlsoOnOthers, boolean ignoreLocal) {
        super(config.getNameWithPrefix());
        this.config = config;
        this.createAlsoOnOthers = createAlsoOnOthers;
        this.ignoreLocal = ignoreLocal;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public void run() throws Exception {
        NodeEngine nodeEngine;
        Set<Member> members;
        int remoteNodeCount;
        ICacheService service = (ICacheService)this.getService();
        if (!this.ignoreLocal) {
            this.response = service.putCacheConfigIfAbsent(this.config);
        }
        if (this.createAlsoOnOthers && (remoteNodeCount = (members = (nodeEngine = this.getNodeEngine()).getClusterService().getMembers()).size() - 1) > 0) {
            this.postponeReturnResponse();
            CacheConfigCreateCallback callback = new CacheConfigCreateCallback(this, remoteNodeCount);
            OperationService operationService = nodeEngine.getOperationService();
            for (Member member : members) {
                if (member.localMember()) continue;
                CacheCreateConfigOperation op = new CacheCreateConfigOperation(this.config, false);
                operationService.createInvocationBuilder("hz:impl:cacheService", (Operation)op, member.getAddress()).setExecutionCallback(callback).invoke();
            }
        }
    }

    private void postponeReturnResponse() {
        this.returnsResponse = false;
    }

    @Override
    public void onExecutionFailure(Throwable e) {
        this.returnsResponse = true;
        super.onExecutionFailure(e);
    }

    @Override
    public Object getResponse() {
        return this.response;
    }

    @Override
    public boolean returnsResponse() {
        return this.returnsResponse;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.config);
        out.writeBoolean(this.createAlsoOnOthers);
        out.writeBoolean(this.ignoreLocal);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.config = (CacheConfig)in.readObject();
        this.createAlsoOnOthers = in.readBoolean();
        this.ignoreLocal = in.readBoolean();
    }

    @Override
    public int getId() {
        return 26;
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    private static class CacheConfigCreateCallback
    extends SimpleExecutionCallback<Object> {
        final AtomicInteger counter;
        final CacheCreateConfigOperation operation;

        public CacheConfigCreateCallback(CacheCreateConfigOperation op, int count) {
            this.operation = op;
            this.counter = new AtomicInteger(count);
        }

        @Override
        public void notify(Object object) {
            if (this.counter.decrementAndGet() == 0) {
                this.operation.sendResponse(null);
            }
        }
    }
}

