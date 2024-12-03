/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.core.Member;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.impl.AbstractNamedOperation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;
import java.util.Set;

public class CacheDestroyOperation
extends AbstractNamedOperation
implements IdentifiedDataSerializable,
MutatingOperation {
    private boolean isLocal;

    public CacheDestroyOperation() {
    }

    public CacheDestroyOperation(String name) {
        this(name, false);
    }

    public CacheDestroyOperation(String name, boolean isLocal) {
        super(name);
        this.isLocal = isLocal;
    }

    @Override
    public void run() throws Exception {
        ICacheService service = (ICacheService)this.getService();
        service.deleteCache(this.name, this.getCallerUuid(), true);
        if (!this.isLocal) {
            this.destroyCacheOnAllMembers(this.name, this.getCallerUuid());
        }
    }

    private void destroyCacheOnAllMembers(String name, String callerUuid) {
        NodeEngine nodeEngine = this.getNodeEngine();
        OperationService operationService = nodeEngine.getOperationService();
        Set<Member> members = nodeEngine.getClusterService().getMembers();
        for (Member member : members) {
            if (member.localMember() || member.getUuid().equals(callerUuid)) continue;
            CacheDestroyOperation op = new CacheDestroyOperation(name, true);
            operationService.invokeOnTarget("hz:impl:cacheService", op, member.getAddress());
        }
    }

    @Override
    public int getId() {
        return 30;
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeBoolean(this.isLocal);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.isLocal = in.readBoolean();
    }
}

