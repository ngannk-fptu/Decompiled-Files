/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.idgen;

import com.hazelcast.concurrent.idgen.IdGeneratorImpl;
import com.hazelcast.concurrent.idgen.IdGeneratorService;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IdGenerator;
import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.NodeEngine;

public class IdGeneratorProxy
extends AbstractDistributedObject<IdGeneratorService>
implements IdGenerator {
    private final String name;
    private final IdGeneratorImpl idGeneratorImpl;

    public IdGeneratorProxy(IAtomicLong blockGenerator, String name, NodeEngine nodeEngine, IdGeneratorService service) {
        super(nodeEngine, service);
        this.name = name;
        this.idGeneratorImpl = new IdGeneratorImpl(blockGenerator);
    }

    @Override
    public boolean init(long id) {
        return this.idGeneratorImpl.init(id);
    }

    @Override
    public long newId() {
        return this.idGeneratorImpl.newId();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:idGeneratorService";
    }

    @Override
    protected void postDestroy() {
        this.idGeneratorImpl.destroy();
    }
}

